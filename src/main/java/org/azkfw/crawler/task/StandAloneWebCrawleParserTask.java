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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.content.FileContent;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngineResult;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine;
import org.azkfw.persistence.proterty.Property;
import org.azkfw.persistence.proterty.PropertyFile;
import org.azkfw.util.MapUtility;
import org.azkfw.util.PathUtility;
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

	/** base directory */
	private File baseDirectory;

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

				String hostId = MapUtility.getString(content, "hostId");
				String hostProtocol = MapUtility.getString(content, "hostProtocol");
				String hostName = MapUtility.getString(content, "hostName");
				Integer hostPort = MapUtility.getInteger(content, "hostPort");
				String contentId = MapUtility.getString(content, "contentId");
				String contentAreas = MapUtility.getString(content, "contentAreas");

				try {
					URL url = URLUtility.toURL(hostProtocol, hostName, hostPort, contentAreas);

					File dir = new File(PathUtility.cat(baseDirectory.getAbsolutePath(), "data", hostId, contentId));
					dir.mkdirs();

					String filePath = PathUtility.cat(dir.getAbsolutePath(), "content.dat");

					ParseEngine engine = getParseEngine(url, new FileContent(new File(filePath)));
					engine.initialize();
					ParseEngineResult rslt = engine.parse(); // TODO: 解析の改修難易度
					engine.release();

					// TODO: 解析結果登録
					if (engine instanceof SimpleHtmlParseEngine) {
						SimpleHtmlParseEngine e = (SimpleHtmlParseEngine) engine;
						List<String> urls = e.getUrlList();
						for (String u : urls) {
							System.out.println("URL : " + u);
						}
					}

					if (rslt.isResult()) {
						manager.parseContent(contentId);
					} else {
						manager.parseErrorContent(contentId);
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

	protected boolean isDownloadContent(final URL aUrl) {
		return true;
	}

	protected ParseEngine getParseEngine(final URL aUrl, final Content aContent) {
		return new SimpleHtmlParseEngine(aUrl, aContent);
	}
}
