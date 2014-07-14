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
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.downloader.engine.DownloadEngineResult;
import org.azkfw.crawler.downloader.engine.SimpleDownloadEngine;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.persistence.proterty.Property;
import org.azkfw.persistence.proterty.PropertyFile;
import org.azkfw.util.ListUtility;
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
@PropertyFile("conf/StandAloneWebCrawleDownloader.properties")
public final class StandAloneWebCrawleDownloaderTask extends StandAloneWebCrawleTask {

	/** base directory */
	private File baseDirectory;

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
				String hostProtocol = MapUtility.getString(host, "protocol");
				String hostName = MapUtility.getString(host, "name");
				Integer hostPort = MapUtility.getInteger(host, "port");

				List<Map<String, Object>> pages = manager.getDownloadPages(hostId, 5);
				if (ListUtility.isNotEmpty(pages)) {

					for (int i = 0; i < pages.size(); i++) {
						Map<String, Object> page = pages.get(i);

						String contentId = MapUtility.getString(page, "id");
						String contentAreas = MapUtility.getString(page, "areas");

						try {
							URL url = URLUtility.toURL(hostProtocol, hostName, hostPort, contentAreas);

							info("Download url : " + url.toExternalForm());

							File dir = new File(PathUtility.cat(baseDirectory.getAbsolutePath(), "data", hostId, contentId));
							dir.mkdirs();

							String filePath = PathUtility.cat(dir.getAbsolutePath(), "content.dat");

							DownloadEngine engine = getDownloadEngine(url);
							engine.initialize();
							DownloadEngineResult rslt = engine.download(url, new File(filePath));
							engine.release();

							if (rslt.isResult()) {
								int statusCode = rslt.getStatusCode();
								info("Status code : " + statusCode);

								String contentType = null;
								for (Header header : rslt.getHeaders()) {
									String name = header.getName().toLowerCase();
									String value = header.getValue();

									if (name.startsWith("content-type")) {
										info("Content type : " + value);
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
									info("Length : " + length);

									manager.downloadContent(contentId, statusCode, length, contentType);

									if (isParseContent(url, contentType)) {
										manager.requestContentParse(contentId);
									}
								} else {
									// Error
									manager.downloadContent(contentId, statusCode);
								}

							} else {
								// not found host
								manager.downloadErrorContent(contentId);
							}

						} catch (MalformedURLException ex) {
							// Error
						}
					}

				} else {
					debug("Not found download content.");
				}

				manager.unlockHost(hostId, 0);
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

	protected boolean isParseContent(final URL aURL, final String aContentType) {
		if (StringUtility.isNotEmpty(aContentType)) {
			if (-1 != aContentType.toLowerCase().indexOf("html")) {
				return true;
			}
		}
		return false;
	}

	protected DownloadEngine getDownloadEngine(final URL aUrl) {
		return new SimpleDownloadEngine();
	}

}
