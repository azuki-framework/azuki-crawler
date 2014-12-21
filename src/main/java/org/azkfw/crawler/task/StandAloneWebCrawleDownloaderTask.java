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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.property.Property;
import org.azkfw.business.property.PropertyFile;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.downloader.engine.DownloadEngineCondition;
import org.azkfw.crawler.downloader.engine.DownloadEngineResult;
import org.azkfw.crawler.engine.CrawlerEngineController;
import org.azkfw.crawler.engine.CrawlerEngineControllerFactory;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.util.ListUtility;
import org.azkfw.util.MapUtility;
import org.azkfw.util.PathUtility;
import org.azkfw.util.StringUtility;
import org.azkfw.util.URLUtility;
import org.azkfw.util.UUIDUtility;

/**
 * このクラスは、スタントアロンでWebクロールを行うクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
@PropertyFile("conf/StandAloneWebCrawleDownloader.properties")
public final class StandAloneWebCrawleDownloaderTask extends StandAloneWebCrawleTask {

	/** base directory */
	private File baseDirectory;

	/** Crawler engine controller */
	private CrawlerEngineController crawlerEngineController;

	/**
	 * コンストラクタ
	 */
	public StandAloneWebCrawleDownloaderTask() {
		super(StandAloneWebCrawleDownloaderTask.class);
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

			Map<String, Object> host = manager.lockHost();
			if (MapUtility.isNotEmpty(host)) {

				String hostId = MapUtility.getString(host, "id");
				String hostName = MapUtility.getString(host, "name");
				String protocol = MapUtility.getString(host, "protocol");
				Integer port = MapUtility.getInteger(host, "port");

				debug(String.format("Target host %s://%s:%d", protocol, hostName, port));

				int status = doExecute(hostId, hostName, protocol, port);

				manager.unlockHost(hostId, status);
			} else {
				debug("Not found download target host.");
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

		return result;
	}

	private int doExecute(final String aHostId, final String aHostName, final String aProtocol, final Integer aPort) throws BusinessServiceException {
		int status = -1;

		WebCrawlerManager manager = (WebCrawlerManager) getLogic("WebCrawlerManager");

		try {
			Calendar cln = Calendar.getInstance();
			cln.setTime(new Date());

			List<Map<String, Object>> pages = manager.getDownloadPages(aHostId, 5);
			if (ListUtility.isNotEmpty(pages)) {

				for (int i = 0; i < pages.size(); i++) {
					Map<String, Object> page = pages.get(i);

					String contentId = MapUtility.getString(page, "id");
					String historyId = MapUtility.getString(page, "historyId");
					String contentAreas = MapUtility.getString(page, "areas");

					URL refererUrl = null;
					try {
						String refererHostProtocol = MapUtility.getString(page, "refererHostProtocol");
						String refererHostName = MapUtility.getString(page, "refererHostName");
						Integer refererHostPort = MapUtility.getInteger(page, "refererHostPort");
						String refererContentAreas = MapUtility.getString(page, "refererContentAreas");
						if (StringUtility.isNotEmpty(refererHostName)) {
							refererUrl = URLUtility.toURL(refererHostProtocol, refererHostName, refererHostPort, refererContentAreas);
						}
					} catch (MalformedURLException ex) {
						// ここに処理が来ることはない
					}

					try {
						URL url = URLUtility.toURL(aProtocol, aHostName, aPort, contentAreas);

						debug("Download url : " + url.toExternalForm());

						String contentPath = String.format("/%04d/%02d/%02d/%02d", cln.get(Calendar.YEAR), cln.get(Calendar.MONTH) + 1,
								cln.get(Calendar.DAY_OF_MONTH), cln.get(Calendar.HOUR_OF_DAY));
						File dir = new File(PathUtility.cat(baseDirectory.getAbsolutePath(), "data", contentPath, historyId));
						dir.mkdirs();

						String filePath = PathUtility.cat(dir.getAbsolutePath(), "content.dat");

						DownloadEngineCondition condition = new DownloadEngineCondition();
						condition.setContentURL(url);
						condition.setDestFile(new File(filePath));
						condition.setRefererURL(refererUrl);

						DownloadEngine engine = getDownloadEngine(url);
						engine.initialize();
						DownloadEngineResult rslt = engine.download(condition);
						engine.release();

						if (rslt.isResult()) {
							int statusCode = rslt.getStatusCode();
							debug("Status code : " + statusCode);

							String contentType = null;
							for (Header header : rslt.getHeaders()) {
								String name = header.getName().toLowerCase();
								String value = header.getValue();

								if (name.startsWith("content-type")) {
									debug("Content type : " + value);
									contentType = value;
								}
							}

							{ // write header
								BufferedWriter writer = null;
								try {
									String headerFilePath = PathUtility.cat(dir.getAbsolutePath(), "header.txt");
									writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(headerFilePath), "UTF-8"));
									for (Header header : rslt.getHeaders()) {
										String name = header.getName().toLowerCase();
										String value = header.getValue();
										writer.write(String.format("%s:%s\n", name, value));
									}
								} catch (IOException ex) {
								} finally {
									if (null != writer) {
										try {
											writer.close();
										} catch (IOException ex) {

										}
									}
								}
							}

							if (200 == statusCode) {
								long length = rslt.getLength();
																
								// Success
								manager.successDownloadContent(contentId, historyId, contentPath, statusCode, length, contentType);

								if (isParseContent(url, contentType)) {
									manager.requestContentParse(contentId, historyId);
								}
							} else {
								// Error
								manager.successDownloadContent(contentId, historyId, contentPath, statusCode);
							}

						} else {
							// not found host
							manager.errorDownloadContent(contentId);
						}

					} catch (MalformedURLException ex) {
						// Error
					}
				}

			} else {
				debug("Not found download content.");
			}

			status = 1;

		} catch (SQLException ex) {
			fatal(ex);
		} catch (DataAccessServiceException ex) {
			fatal(ex);
		}

		return status;
	}

	protected boolean isParseContent(final URL aURL, final String aContentType) {
		boolean result = crawlerEngineController.isParseContent(aURL, aContentType);
		return result;
	}

	protected DownloadEngine getDownloadEngine(final URL url) {
		return crawlerEngineController.getDownloadEngine(url);
	}
}