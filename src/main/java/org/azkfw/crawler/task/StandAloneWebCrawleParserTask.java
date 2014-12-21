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
package org.azkfw.crawler.task;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.property.Property;
import org.azkfw.business.property.PropertyFile;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.content.FileContent;
import org.azkfw.crawler.engine.CrawlerEngineController;
import org.azkfw.crawler.engine.CrawlerEngineControllerFactory;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.crawler.parser.engine.AbstractHtmlParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngineResult;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine.Counter;
import org.azkfw.util.MapUtility;
import org.azkfw.util.PathUtility;
import org.azkfw.util.StringUtility;
import org.azkfw.util.URLUtility;

/**
 * このクラスは、スタントアロンでWebクロールを行うクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
@PropertyFile("conf/StandAloneWebCrawleParser.properties")
public final class StandAloneWebCrawleParserTask extends StandAloneWebCrawleTask {

	public static void main(final String[] args) {
		try {
			URL url = null;
			url = new URL("http://localhost/aaaa/index.do?aaa=bbb&cc=cc#bottom");
			System.out.println(String.format("protocol  : %s", url.getProtocol())); // http
			System.out.println(String.format("host      : %s", url.getHost())); // localhost
			System.out.println(String.format("port      : %d", url.getPort())); // -1
			System.out.println(String.format("port(def) : %d", url.getDefaultPort())); // 80
			System.out.println(String.format("file      : %s", url.getFile())); // /aaa/index.do?aaa=bbb&cc=cc
			System.out.println(String.format("query     : %s", url.getQuery())); // aaa=bbb&cc=cc
			System.out.println(String.format("path      : %s", url.getPath())); // /aaa/index.do
			System.out.println(String.format("ref       : %s", url.getRef())); // bottom

			url = new URL("http://localhost");
			System.out.println(String.format("protocol  : %s", url.getProtocol())); // http
			System.out.println(String.format("host      : %s", url.getHost())); // localhost
			System.out.println(String.format("port      : %d", url.getPort())); // -1
			System.out.println(String.format("port(def) : %d", url.getDefaultPort())); // 80
			System.out.println(String.format("file      : %s", url.getFile())); //
			System.out.println(String.format("query     : %s", url.getQuery())); // null
			System.out.println(String.format("path      : %s", url.getPath())); // 
			System.out.println(String.format("ref       : %s", url.getRef())); // null
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
	}

	/** 文字コード取得パターン */
	private static final Pattern PTN_GET_CHARSET = Pattern.compile("charset\\s*=\\s*([^;]+);*");

	/** base directory */
	private File baseDirectory;

	/** Crawler engine controller */
	private CrawlerEngineController crawlerEngineController;

	/**
	 * コンストラクタ
	 */
	public StandAloneWebCrawleParserTask() {
		super(StandAloneWebCrawleParserTask.class);
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		Property p = getProperty();

		String dir = p.getString("base.directory");
		baseDirectory = new File(dir);
		baseDirectory.mkdirs();

		info("Base Directory : " + baseDirectory.getAbsolutePath());

		crawlerEngineController = CrawlerEngineControllerFactory.getDefaultController();
	}

	@Override
	protected void doStartup() {
	}

	@Override
	protected void doShutdown() {
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected CrawlerTaskResult doExecute() throws CrawlerServiceException {
		CrawlerTaskResult result = new CrawlerTaskResult();

		try {
			WebCrawlerManager manager = (WebCrawlerManager) getLogic("WebCrawlerManager");

			Map<String, Object> content = manager.getParseContent();
			if (MapUtility.isNotEmpty(content)) {

				String parseId = MapUtility.getString(content, "parseId");
				String historyId = MapUtility.getString(content, "historyId");
				String contentId = MapUtility.getString(content, "contentId");
				String hostProtocol = MapUtility.getString(content, "hostProtocol");
				String hostName = MapUtility.getString(content, "hostName");
				Integer hostPort = MapUtility.getInteger(content, "hostPort");
				String contentAreas = MapUtility.getString(content, "contentAreas");
				String contentPath = MapUtility.getString(content, "contentPath");
				String contentType = MapUtility.getString(content, "contentType");

				String charset = null;
				if (StringUtility.isNotEmpty(contentType)) {
					Matcher m = PTN_GET_CHARSET.matcher(contentType);
					if (m.find()) {
						charset = m.group(1);
					}
				}

				try {
					URL absoluteUrl = URLUtility.toURL(hostProtocol, hostName, hostPort, contentAreas);

					File dir = new File(PathUtility.cat(baseDirectory.getAbsolutePath(), "data", contentPath, historyId));
					String filePath = PathUtility.cat(dir.getAbsolutePath(), "content.dat");

					// 解析
					ParseEngine engine = getParseEngine(absoluteUrl, contentType, new FileContent(new File(filePath)));
					if (engine instanceof AbstractHtmlParseEngine) {
						AbstractHtmlParseEngine e = (AbstractHtmlParseEngine) engine;
						if (StringUtility.isNotEmpty(charset)) {
							e.setCharset(Charset.forName(charset));
						}
					}
					engine.initialize();
					ParseEngineResult rslt = engine.parse(); // TODO: 解析の改修難易度
					engine.release();

					// TODO: 解析結果登録
					if (engine instanceof SimpleHtmlParseEngine) {
						Map<String, String> hostKeyIdMap = new HashMap<String, String>();

						SimpleHtmlParseEngine e = (SimpleHtmlParseEngine) engine;

						Counter urls = e.getAnchors();
						Map<String, Set<String>> hostUrls = new HashMap<String, Set<String>>();

						for (String url : urls.keyset()) {
							try {
								URL bufUrl = new URL(url);

								if (isDownloadContent(bufUrl)) {
									// ダウンロード対象か判断

									String protocol = bufUrl.getProtocol();
									String name = bufUrl.getHost();
									int port = (-1 != bufUrl.getPort()) ? bufUrl.getPort() : bufUrl.getDefaultPort();

									// ホスト情報登録
									String hostKey = getHostKey(bufUrl);
									if (hostKeyIdMap.containsKey(hostKey)) {
										String bufHostId = hostKeyIdMap.get(hostKey);

										hostUrls.get(bufHostId).add(url);
									} else {
										String bufHostId = null;

										Map<String, Object> host = manager.getHost(name, protocol, port);
										if (MapUtility.isNotEmpty(host)) {
											bufHostId = MapUtility.getString(host, "id");
										} else {
											host = manager.registHost(name, protocol, port);
											bufHostId = MapUtility.getString(host, "id");

											info(String.format("New host.[%s]", hostKey));
										}
										hostKeyIdMap.put(hostKey, bufHostId);

										Set<String> bufUrls = new HashSet<String>();
										bufUrls.add(url);
										hostUrls.put(bufHostId, bufUrls);
									}

								} else {
									// ダウンロード対象じゃない
								}
							} catch (MalformedURLException ex) {
								ex.printStackTrace();
							}
						}

						// ホストごとのURL
						Date date = new Date();
						for (String bufHostId : hostUrls.keySet()) {
							Set<String> bufUrls = hostUrls.get(bufHostId);
							List<URL> urlList = new ArrayList<URL>();
							for (String url : bufUrls) {
								//URLDecoder.decode(url, charset);
								urlList.add(new URL(url));
							}
							manager.registContents(bufHostId, urlList, contentId, date);
						}
					}

					if (rslt.isResult()) {
						manager.parseContent(parseId);
					} else {
						manager.parseErrorContent(parseId);
					}

				} catch (MalformedURLException ex) {

				}

			}

			result.setResult(true);
			result.setStop(false);
		} catch (SQLException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		} catch (DataAccessServiceException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		} catch (BusinessServiceException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		}

		result.setResult(true);
		result.setStop(false);
		return result;
	}

	private String getHostKey(final URL aUrl) {
		String protocol = aUrl.getProtocol();
		String name = aUrl.getHost();
		int port = (-1 != aUrl.getPort()) ? aUrl.getPort() : aUrl.getDefaultPort();
		return getHostKey(protocol, name, port);
	}

	private String getHostKey(final String aProtocol, final String aName, final int aPort) {
		String hostKey = String.format("%s://%s:%d", aProtocol, aName, aPort);
		return hostKey;
	}

	protected boolean isDownloadContent(final URL aUrl) {
		boolean result = crawlerEngineController.isDownloadContent(aUrl);
		return result;
	}

	protected ParseEngine getParseEngine(final URL aUrl, final String aContentType, final Content aContent) {
		return crawlerEngineController.getParseEngine(aUrl, aContentType, aContent);
	}
}
