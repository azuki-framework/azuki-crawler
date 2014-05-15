/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.crawler.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.azkfw.crawler.CrawlerServer;
import org.azkfw.crawler.access.AccessControl;
import org.azkfw.crawler.config.CrawlerConfig.CrawlerManagerConfig;
import org.azkfw.crawler.localization.LocalizeTransform;
import org.azkfw.crawler.logger.LoggerObject;
import org.azkfw.crawler.schedule.CrawlerSchedule;
import org.azkfw.crawler.task.CrawlerTask;
import org.azkfw.crawler.task.CrawlerTaskStateSupport;
import org.azkfw.crawler.thread.CrawlerThread;
import org.azkfw.crawler.thread.CrawlerThread.Status;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * このクラスは、クローラのマネージャーサーバクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/14
 * @author Kawakicchi
 */
public class CrawlerManagerServer extends LoggerObject implements HttpHandler {

	private CrawlerServer server;
	private CrawlerManagerConfig config;

	private HttpServer httpServer;

	private AccessControl accessControl;

	public CrawlerManagerServer(final CrawlerServer aServer, final CrawlerManagerConfig aConfig) {
		super(CrawlerManagerServer.class);

		server = aServer;
		config = aConfig;

		accessControl = new AccessControl();
	}

	public boolean start() {
		boolean result = false;
		try {
			httpServer = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
			httpServer.createContext(config.getContextpath(), this);
			httpServer.start();

			result = true;
		} catch (IOException ex) {
			fatal(ex);
		}
		return result;
	}

	public void stop(final int sec) {
		httpServer.stop(sec);
	}

	private Message doStartThread(final CrawlerThread aThread) {
		Message message = null;

		Status status = aThread.getStatus();
		if (Status.stoped == status) {
			aThread.start();
			message = Message.createMessageSuccess(String.format("「%s」を起動しました。", aThread.getTitle()));
		} else if (Status.running == status || Status.sleeping == status) {
			message = Message.createMessageInfo(String.format("「%s」は既に起動しています。", aThread.getTitle()));
		} else if (Status.stoping == status) {
			message = Message.createMessageInfo(String.format("「%s」は停止処理中です。", aThread.getTitle()));
		} else if (Status.error == status) {
			message = Message.createMessageError(String.format("「%s」は問題が発生しているため起動できません。", aThread.getTitle()));
		}

		return message;
	}

	private Message doStopThread(final CrawlerThread aThread) {
		Message message = null;

		Status status = aThread.getStatus();
		if (Status.running == status || Status.sleeping == status) {
			aThread.requestStop();
			message = Message.createMessageSuccess(String.format("「%s」を停止しています。", aThread.getTitle()));
		} else if (Status.stoped == status) {
			message = Message.createMessageInfo(String.format("「%s」は既に停止しています。", aThread.getTitle()));
		} else if (Status.stoping == status) {
			message = Message.createMessageInfo(String.format("「%s」は停止処理中です。", aThread.getTitle()));
		} else if (Status.error == status) {
			message = Message.createMessageError(String.format("「%s」は問題が発生しているため停止できません。", aThread.getTitle()));
		}

		return message;
	}

	// HttpHandler /////////////////////////////////////////////////////////////////////////////////////

	private static final byte[] NOT_FOUND = "<h1>404 Not Found</h1>No context found for request".getBytes();
	private static final byte[] SERVER_ERROR = "<html><head><title>500 - Error</title></head><body>500 - Error</body></html>".getBytes();

	@Override
	public void handle(final HttpExchange exchange) throws IOException {
		String url = exchange.getRequestURI().toString();
		url = url.substring(config.getContextpath().length());
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		int index = url.indexOf("?");
		String areas;
		Map<String, String> parameters = new HashMap<String, String>();
		if (-1 == index) {
			areas = url;
		} else {
			areas = url.substring(0, index);

			String prms = url.substring(index + 1);
			Pattern splitPattern = Pattern.compile("[&]+");
			String[] ss = splitPattern.split(prms);
			for (String s : ss) {
				System.out.println(s);
				index = s.indexOf("=");
				if (-1 != index) {
					String key = s.substring(0, index);
					String value = s.substring(index + 1);
					parameters.put(key, value);
				}
			}
		}

		Pattern threadPattern = Pattern.compile("^/thread/([0-9a-z]{8}\\-[0-9a-z]{4}\\-[0-9a-z]{4}\\-[0-9a-z]{4}\\-[0-9a-z]{12}){1}(/.*){0,}$");

		OutputStream out = exchange.getResponseBody();
		try {
			if (!accessControl.authentication(exchange.getRemoteAddress(), areas)) {
				exchange.sendResponseHeaders(404, NOT_FOUND.length);
				out.write(NOT_FOUND);
				out.flush();

			} else if ("/".equals(areas)) {
				String html = doIndex();
				byte[] buf = html.getBytes(config.getCharset());

				exchange.sendResponseHeaders(200, buf.length);
				out.write(buf);
				out.flush();

			} else if ("/thread".equals(areas)) {
				String ctrl = parameters.get("ctrl");
				String threadId = parameters.get("thread");

				Message message = null;
				if ("start".equals(ctrl)) {
					CrawlerThread thread = getThread(threadId);
					message = doStartThread(thread);
				} else if ("stop".equals(ctrl)) {
					CrawlerThread thread = getThread(threadId);
					message = doStopThread(thread);
				} else if (null != ctrl && 0 < ctrl.length()) {
					message = Message.createMessageWarning("Undefined command.");
				} else {

				}

				String html = doThreadList(message);
				byte[] buf = html.getBytes(config.getCharset());

				exchange.sendResponseHeaders(200, buf.length);
				out.write(buf);
				out.flush();
			} else if (areas.startsWith("/thread")) {
				Matcher matcher = threadPattern.matcher(areas);
				if (matcher.find()) {
					String threadId = matcher.group(1);
					String ar = matcher.group(2);
					if (null == ar)
						ar = "/";

					CrawlerThread thread = getThread(threadId);
					if (null != thread) {
						Message message = null;
						if ("/".equals(ar)) {
						} else if ("/start".equals(ar)) {
							message = doStartThread(thread);
						} else if ("/stop".equals(ar)) {
							message = doStopThread(thread);
						} else {
							message = Message.createMessageWarning("Undefined command.");
						}

						String html = doThread(thread, message);
						byte[] buf = html.getBytes(config.getCharset());

						exchange.sendResponseHeaders(200, buf.length);
						out.write(buf);
						out.flush();

					} else {
						exchange.sendResponseHeaders(404, NOT_FOUND.length);
						out.write(NOT_FOUND);
						out.flush();
					}

				} else {
					exchange.sendResponseHeaders(404, NOT_FOUND.length);
					out.write(NOT_FOUND);
					out.flush();

				}

			} else if (isFile(areas)) {
				File file = new File(getFilePath(areas));

				exchange.sendResponseHeaders(200, file.length());

				InputStream is = new FileInputStream(file);
				byte[] buf = new byte[1024];
				int length;
				while (-1 != (length = is.read(buf, 0, 1024))) {
					if (0 != length) {
						out.write(buf, 0, length);
					}
				}
				is.close();

				out.flush();

			} else {
				exchange.sendResponseHeaders(404, NOT_FOUND.length);
				out.write(NOT_FOUND);
				out.flush();

			}
		} catch (Exception ex) {
			fatal(ex);

			try {
				exchange.sendResponseHeaders(500, SERVER_ERROR.length);
				out.write(SERVER_ERROR);
				out.flush();

			} catch (IOException ex2) {
				fatal(ex2);
			}

		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
			exchange.close();
		}
	}

	private CrawlerThread getThread(final String aId) {
		CrawlerThread thread = null;
		for (CrawlerThread t : server.getThreads()) {
			if (t.getId().equals(aId)) {
				thread = t;
				break;
			}
		}
		return thread;
	}

	private boolean isFile(final String aAreas) {
		File file = new File(getFilePath(aAreas));
		return file.isFile();
	}

	private String getFilePath(final String aAreas) {
		String path = config.getBasedir() + aAreas;
		return path;
	}

	private String doIndex() {
		return getHTML(doIndexClient(), doIndexJavascript(), "");
	}

	private String doIndexJavascript() {
		return "";
	}

	private String doIndexClient() {
		return "";
	}

	private String doThreadList(final Message aMessage) {
		return getHTML(doThreadListClient(aMessage), doThreadListJavascript(), "thread");
	}

	private String doThreadListJavascript() {
		StringBuilder s = new StringBuilder();
		s.append(" function onClickThreadStart(threadId) {");
		s.append(" var url = '/manager/thread?ctrl=start&thread=' + threadId;");
		s.append(" document.location = url;");
		s.append(" }");
		s.append(" function onClickThreadStop(threadId) {");
		s.append(" var url = '/manager/thread?ctrl=stop&thread=' + threadId;");
		s.append(" document.location = url;");
		s.append(" }");
		return s.toString();
	}

	private String doThreadListClient(final Message aMessage) {
		Calendar cln = Calendar.getInstance();
		cln.setTime(new Date());
		String updateDate = String.format("Last update : %04d/%02d/%02d %02d:%02d:%02d", cln.get(Calendar.YEAR), cln.get(Calendar.MONTH) + 1,
				cln.get(Calendar.DAY_OF_MONTH), cln.get(Calendar.HOUR_OF_DAY), cln.get(Calendar.MINUTE), cln.get(Calendar.SECOND));

		StringBuilder s = new StringBuilder();
		s.append("<h1 class=\"page-header\">Thread</h1>");

		if (null != aMessage) {
			if (1 == aMessage.getType()) {
				s.append("<div class=\"alert alert-success alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Success!</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			} else if (2 == aMessage.getType()) {
				s.append("<div class=\"alert alert-info alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Information</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			} else if (3 == aMessage.getType()) {
				s.append("<div class=\"alert alert-warning alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Warning!</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			} else if (4 == aMessage.getType()) {
				s.append("<div class=\"alert alert-danger alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Error!</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			}
		}

		s.append("<div class=\"table-responsive\">");
		s.append("<table class=\"table table-striped\">");

		s.append("<thead>");
		s.append("<tr>");
		s.append("<th align=\"center\">Status</th>");
		s.append("<th>Title</th>");
		s.append("<th>Description</th>");
		s.append("<th>Start date</th>");
		s.append("<th>Stop date</th>");
		s.append("<th>time</th>");
		s.append("<th>Schedule</th>");
		s.append("<th>process</th>");
		s.append("<th>message</th>");
		s.append("<th></th>");
		s.append("</tr>");
		s.append("</thead>");
		s.append("<tbody>");
		List<CrawlerThread> threads = server.getThreads();
		for (CrawlerThread thread : threads) {
			s.append("<tr>");
			s.append("<td align=\"center\">");
			if (Status.running == thread.getStatus()) {
				s.append("<span class=\"label label-success\">");
			} else if (Status.sleeping == thread.getStatus()) {
				s.append("<span class=\"label label-primary\">");
			} else if (Status.stoping == thread.getStatus()) {
				s.append("<span class=\"label label-warning\">");
			} else if (Status.error == thread.getStatus()) {
				s.append("<span class=\"label label-danger\">");
			} else {
				s.append("<span class=\"label label-default\">");
			}
			s.append(es(thread.getStatus().getName()));
			s.append("</span>");
			s.append("</td>");
			s.append("<td>");
			s.append("<a href=\"").append(getUrl("/thread/")).append(thread.getId()).append("/\">");
			s.append(thread.getTitle());
			s.append("</a>");
			s.append("</td>");
			s.append("<td>").append(es(thread.getDescription())).append("</td>");

			// 起動日付
			Date startDate = thread.getStartDate();
			String sStartdate = null;
			if (null != startDate) {
				cln.setTime(startDate);
				sStartdate = String.format("%02d/%02d %02d:%02d", cln.get(Calendar.MONTH) + 1, cln.get(Calendar.DAY_OF_MONTH),
						cln.get(Calendar.HOUR_OF_DAY), cln.get(Calendar.MINUTE));
			}
			if (null != sStartdate) {
				s.append("<td>").append(sStartdate).append("</td>");
			} else {
				s.append("<td>").append("").append("</td>");
			}
			// 停止日付
			Date stopDate = thread.getStopDate();
			String sStopdate = null;
			if (null != stopDate) {
				cln.setTime(stopDate);
				sStopdate = String.format("%02d/%02d %02d:%02d", cln.get(Calendar.MONTH) + 1, cln.get(Calendar.DAY_OF_MONTH),
						cln.get(Calendar.HOUR_OF_DAY), cln.get(Calendar.MINUTE));
			}
			if (null != sStopdate) {
				s.append("<td>").append(sStopdate).append("</td>");
			} else {
				s.append("<td>").append("").append("</td>");
			}

			// 起動時間／処理時間
			if (null == startDate && null == stopDate) {
				// 未起動
				s.append("<td>").append("").append("</td>");
			} else if (null != startDate && null == stopDate) {
				// 起動中
				Date nowDate = new Date();
				long interval = nowDate.getTime() - startDate.getTime();
				s.append("<td>").append(LocalizeTransform.milliSec2Diaplay(interval)).append("</td>");
			} else {
				// 停止中
				long interval = stopDate.getTime() - startDate.getTime();
				s.append("<td>").append(LocalizeTransform.milliSec2Diaplay(interval)).append("</td>");
			}

			CrawlerSchedule schedule = thread.getSchedule();
			if (null != schedule) {
				s.append("<td>").append(es(schedule.getOutline())).append("</td>");
			} else {
				s.append("<td>").append("").append("</td>");
			}

			CrawlerTask task = thread.getTask();
			if (null != task) {
				if (task instanceof CrawlerTaskStateSupport) {
					CrawlerTaskStateSupport support = (CrawlerTaskStateSupport) task;
					s.append("<td>").append(String.format("%.2f%%", support.getStateProgress())).append("</td>");
					s.append("<td>").append(es(String.format("%s", support.getStateMessage()))).append("</td>");
				} else {
					s.append("<td>").append("").append("</td>");
					s.append("<td>").append("").append("</td>");
				}
			} else {
				s.append("<td>").append("").append("</td>");
				s.append("<td>").append("").append("</td>");
			}

			s.append("<td>");
			if (Status.stoped == thread.getStatus()) {
				s.append("<button type=\"button\" class=\"btn btn-default\" onclick=\"onClickThreadStart('" + thread.getId() + "');\">起動</button>");
			} else if (Status.running == thread.getStatus() || Status.sleeping == thread.getStatus()) {
				s.append("<button type=\"button\" class=\"btn btn-danger\" onclick=\"onClickThreadStop('" + thread.getId() + "');\">停止</button>");
			} else if (Status.stoping == thread.getStatus()) {
				s.append("<button type=\"button\" class=\"btn btn-danger disabled\">停止</button>");
			} else {

			}
			s.append("</td>");

			s.append("</tr>");
		}
		s.append("<tbody>");
		s.append("</table>");
		s.append("</div>");

		s.append("<br />");
		s.append(updateDate);

		return s.toString();
	}

	private String doThread(final CrawlerThread aThread, final Message aMessage) {
		return getHTML(doThreadClient(aThread, aMessage), doThreadJavascript(), "thread" + aThread.getId());
	}

	private String doThreadJavascript() {
		return "";
	}

	private String doThreadClient(final CrawlerThread aThread, final Message aMessage) {

		StringBuilder s = new StringBuilder();
		s.append("<h1 class=\"page-header\">").append(es(aThread.getTitle())).append("</h1>");

		if (null != aMessage) {
			if (1 == aMessage.getType()) {
				s.append("<div class=\"alert alert-success alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Success!</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			} else if (2 == aMessage.getType()) {
				s.append("<div class=\"alert alert-info alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Information</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			} else if (3 == aMessage.getType()) {
				s.append("<div class=\"alert alert-warning alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Warning!</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			} else if (4 == aMessage.getType()) {
				s.append("<div class=\"alert alert-danger alert-dismissable\">");
				s.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
				s.append("<strong>Error!</strong> ").append(es(aMessage.getMessage()));
				s.append("</div>");
			}
		}

		s.append("<div class=\"row placeholders\">");
		s.append("<p style=\"text-align: left;\">");
		s.append(es(aThread.getDescription()));
		s.append("</p>");
		s.append("</div>");

		s.append("<h3 class=\"sub-header\">Schedule</h3>");
		s.append("<div class=\"row placeholders\">");
		s.append("</div>");

		s.append("<h3 class=\"sub-header\">Task</h3>");
		s.append("<div class=\"row placeholders\">");
		s.append("</div>");

		return s.toString();
	}

	private String getHTML(final String aBody, final String aJavascript, final String aActiveSidebar) {
		StringBuilder s = new StringBuilder();

		s.append("<!DOCTYPE html>");
		s.append("<html lang=\"ja\">");

		s.append("<head>");
		s.append("");
		s.append("<meta charset=\"").append(config.getCharset()).append("\">");
		s.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
		s.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		s.append("<meta name=\"description\" content=\"\">");
		s.append("<meta name=\"author\" content=\"\">");
		s.append("<link rel=\"shortcut icon\" href=\"").append(getUrl("/icon/favicon.ico")).append("\">");
		s.append("");
		s.append("<title>").append("Crawler Manager").append("</title>");
		s.append("");
		s.append("<link href=\"").append(getUrl("/css/bootstrap.min.css")).append("\" rel=\"stylesheet\">");
		s.append("<link href=\"").append(getUrl("/css/bootstrap-theme.min.css")).append("\" rel=\"stylesheet\">");
		s.append("<link href=\"").append(getUrl("/css/custom.css")).append("\" rel=\"stylesheet\">");

		if (null != aJavascript && 0 < aJavascript.length()) {
			s.append("<script language=\"JavaScript\" type=\"text/javascript\"><!--\r\n");
			s.append(aJavascript);
			s.append("\r\n--></script>\r\n");
		}

		s.append("</head>");

		s.append("<body>");

		// Navbar
		s.append("<div class=\"navbar navbar-inverse navbar-fixed-top\" role=\"navigation\">");
		s.append("<div class=\"container-fluid\">");
		s.append("<div class=\"navbar-header\">");
		s.append("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">");
		s.append("<span class=\"sr-only\">Toggle navigation</span>");
		s.append("<span class=\"icon-bar\"></span>");
		s.append("<span class=\"icon-bar\"></span>");
		s.append("<span class=\"icon-bar\"></span>");
		s.append("</button>");
		s.append("<a class=\"navbar-brand\" href=\"").append(getUrl("/")).append("\">").append("Crawler Manager").append("</a>");
		s.append("</div>");
		s.append("<div class=\"navbar-collapse collapse\">");
		s.append("<ul class=\"nav navbar-nav navbar-right\">");
		//s.append("<li><a href=\"#\">Home</a></li>");
		//s.append("<li><a href=\"#about\">About</a></li>");
		//s.append("<li><a href=\"#contact\">Contact</a></li>");
		s.append("</ul>");
		s.append("</div><!--/.navbar-collapse -->");
		s.append("</div><!-- /.container -->");
		s.append("</div><!-- /.navbar -->");

		s.append("<div class=\"container-fluid\">");
		s.append("<div class=\"row\">");
		s.append("");

		// Sidebar
		s.append("<div class=\"col-sm-3 col-md-2 sidebar\">");

		s.append("<ul class=\"nav nav-sidebar\">");
		s.append("<li class=\"").append(isActive("", aActiveSidebar)).append("\"><a href=\"").append(getUrl("/")).append("/\">Overview</a></li>");
		s.append("<li class=\"").append(isActive("thread", aActiveSidebar)).append("\"><a href=\"").append(getUrl("/thread"))
				.append("\">Thread</a></li>");
		s.append("</ul>");

		s.append("<ul class=\"nav nav-sidebar-sub\">");
		List<CrawlerThread> threads = server.getThreads();
		for (int i = 0; i < threads.size(); i++) {
			CrawlerThread thread = threads.get(i);
			s.append("<li class=\"").append(isActive("thread" + thread.getId(), aActiveSidebar)).append("\">");
			s.append(" <a href=\"").append(getUrl("/thread/")).append(thread.getId()).append("/\">").append(es(thread.getTitle())).append("</a>");
			s.append("</li>");
		}
		s.append("</ul>");

		s.append("</div>");
		// !Sidebar

		s.append("<div class=\"container col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main\">");
		s.append(aBody);
		s.append("</div>");

		s.append("</div>");
		s.append("</div>");

		s.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>");
		s.append("<script src=\"").append(getUrl("/js/bootstrap.min.js")).append("\"></script>");
		s.append("<script src=\"").append(getUrl("/js/docs.min.js")).append("\"></script>");
		s.append("</body>");

		s.append("</html>");

		return s.toString();
	}

	public String isActive(final String aName, final String aValue) {
		if (aName.equals(aValue)) {
			return "active";
		} else {
			return "";
		}
	}

	private String getUrl(final String aName) {
		String url = null;
		if (aName.startsWith("/")) {
			// 絶対
			if (1 == aName.length()) {
				if (config.getContextpath().endsWith("/")) {
					url = config.getContextpath().substring(0, config.getContextpath().length() - 1);
				} else {
					url = config.getContextpath();
				}
			} else {
				if (config.getContextpath().endsWith("/")) {
					url = config.getContextpath() + aName.substring(1);
				} else {
					url = config.getContextpath() + aName;
				}
			}
		} else {
			// 相対
			url = aName;
		}
		return url;
	}

	private static String es(final String aString) {
		return aString;
	}

	private static class Message {
		private int type;
		private String message;

		private Message(final int aType, final String aMessage) {
			type = aType;
			message = aMessage;
		}

		public int getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}

		public static Message createMessageSuccess(final String aMessage) {
			return new Message(1, aMessage);
		}

		public static Message createMessageInfo(final String aMessage) {
			return new Message(2, aMessage);
		}

		public static Message createMessageWarning(final String aMessage) {
			return new Message(3, aMessage);
		}

		public static Message createMessageError(final String aMessage) {
			return new Message(4, aMessage);
		}
	}
}
