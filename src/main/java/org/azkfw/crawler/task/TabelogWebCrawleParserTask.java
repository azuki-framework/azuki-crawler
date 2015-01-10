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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.property.Property;
import org.azkfw.business.property.PropertyFile;
import org.azkfw.crawler.CrawlInfo;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.content.FileContent;
import org.azkfw.crawler.engine.CrawlerEngine;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.crawler.parser.engine.AbstractHtmlParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngineResult;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine.Counter;
import org.azkfw.parameter.Parameter;
import org.azkfw.store.Store;
import org.azkfw.util.MapUtility;
import org.azkfw.util.PathUtility;
import org.azkfw.util.StringUtility;
import org.azkfw.util.URLUtility;

/**
 * このクラスは、食べログWebクロールを行うクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
@PropertyFile("conf/TabelogWebCrawleParser.properties")
public final class TabelogWebCrawleParserTask extends StandAloneWebCrawleTask {

	/** 文字コード取得パターン */
	private static final Pattern PTN_GET_CHARSET = Pattern.compile("charset\\s*=\\s*([^;]+);*");

	/** base directory */
	private File baseDirectory;

	/** Crawler engine */
	private CrawlerEngine crawlerEngine;

	/**
	 * コンストラクタ
	 */
	public TabelogWebCrawleParserTask() {
		super(TabelogWebCrawleParserTask.class);
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		Property p = getProperty();

		String dir = p.getString("base.directory");
		baseDirectory = new File(dir);
		baseDirectory.mkdirs();

		info("Base Directory : " + baseDirectory.getAbsolutePath());

		Parameter param = getParameter();
		String crawlerEngineClassPath = param.getString("CrawlerEngine");
		if (StringUtility.isNotEmpty(crawlerEngineClassPath)) {
			try {
				@SuppressWarnings("rawtypes")
				Class clazz = Class.forName(crawlerEngineClassPath);
				Object obj = clazz.newInstance();
				if (crawlerEngine instanceof CrawlerEngine) {
					crawlerEngine = (CrawlerEngine) obj;
				} else {
					throw new CrawlerSetupException("No crawlerEngin class.");
				}
			} catch (ClassNotFoundException ex) {
				throw new CrawlerSetupException(ex);
			} catch (IllegalAccessException ex) {
				throw new CrawlerSetupException(ex);
			} catch (InstantiationException ex) {
				throw new CrawlerSetupException(ex);
			}
		} else {
			throw new CrawlerSetupException("Unset task parameter.[CrawlerEngine]");
		}
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

		Store<String, Object> session = getSession();
		Map<String, String> urlIdMap = (Map<String, String>) session.get("urlIdMap", new HashMap<String, String>());
		session.put("urlIdMap", urlIdMap);

		try {
			WebCrawlerManager manager = (WebCrawlerManager) getLogic("WebCrawlerManager");

			Map<String, Object> content = manager.getParseContent();
			if (MapUtility.isNotEmpty(content)) {

				String contentParseId = MapUtility.getString(content, "contentParseId");
				String hostProtocol = MapUtility.getString(content, "hostProtocol");
				String hostName = MapUtility.getString(content, "hostName");
				Integer hostPort = MapUtility.getInteger(content, "hostPort");
				String contentId = MapUtility.getString(content, "contentId");
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

					File dir = new File(PathUtility.cat(baseDirectory.getAbsolutePath(), "data", contentPath));
					String contentFilePath = PathUtility.cat(dir.getAbsolutePath(), "content.dat");
					String linkFilePath = PathUtility.cat(dir.getAbsolutePath(), "link.txt");

					// 解析
					ParseEngine engine = getParseEngine(absoluteUrl, new FileContent(new File(contentFilePath)));
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
						writeAnchors(new File(linkFilePath), urls);

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
							Map<URL, CrawlInfo> urlInfos = new HashMap<URL, CrawlInfo>();
							for (String str : bufUrls) {
								if (!urlIdMap.containsKey(str)) {
									//URLDecoder.decode(url, charset);
									URL url = new URL(str);
									CrawlInfo info = crawlerEngine.getCrawlInfo(url);

									urlInfos.put(url, info);
								}
							}

							Map<String, String> registUrlId = manager.registContents(bufHostId, urlInfos, contentId, date);
							urlIdMap.putAll(registUrlId);
						}
					}

					if (rslt.isResult()) {
						manager.parseContent(contentParseId);
					} else {
						manager.parseErrorContent(contentParseId);
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
		boolean result = crawlerEngine.isDownloadContent(aUrl);
		return result;
	}

	protected ParseEngine getParseEngine(final URL aUrl, final Content aContent) {
		return new SimpleHtmlParseEngine(aUrl, aContent);
	}
}
