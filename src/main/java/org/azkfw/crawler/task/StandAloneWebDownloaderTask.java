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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.azkfw.business.BusinessServiceException;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.persistence.proterty.Property;
import org.azkfw.persistence.proterty.PropertyFile;
import org.azkfw.util.ListUtility;
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
@PropertyFile("conf/StandAloneWebDownloader.properties")
public class StandAloneWebDownloaderTask extends AbstractBusinessCrawlerTask {

	private File baseDirectory;

	/**
	 * コンストラクタ
	 */
	public StandAloneWebDownloaderTask() {
		super(StandAloneWebDownloaderTask.class);
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

						String pageId = MapUtility.getString(page, "id");
						String pageAreas = MapUtility.getString(page, "areas");

						try {
							URL url = URLUtility.toURL(hostProtocol, hostName, hostPort, pageAreas);

							info("Download url : " + url.toExternalForm());

							File dir = new File(PathUtility.cat(baseDirectory.getAbsolutePath(), "data", hostId, pageId));
							dir.mkdirs();

							Map<String, Object> download = download(url, dir);

							boolean rslt = (Boolean) MapUtility.getObject(download, "result", false);

							if (rslt) {

								int statusCode = MapUtility.getInteger(download, "statusCode", Integer.valueOf(-1));
								info("Status code : " + statusCode);

								List<Header> headers = (List<Header>) MapUtility.getObject(download, "headers", new ArrayList<Header>());
								for (Header header : headers) {
									String name = header.getName().toLowerCase();
									String value = header.getValue();

									if (name.startsWith("content-type")) {
										info("Content type : " + value);
									}
								}
								
								if (200 == statusCode) {
									Long length = MapUtility.getLong(download, "length", Long.valueOf(-1));
									
									info("Length : " + length);
									
								} else {
									// Error
								}

							} else {
								// not found host
							}

						} catch (MalformedURLException ex) {
							// Error
						}
					}

				}

				manager.unlockHost(hostId, 0);
			}

			result.setResult(true);
			result.setStop(false);
		} catch (BusinessServiceException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		}

		return result;
	}

	/**
	 * <p>
	 * <ul>
	 * <li>result - 結果</li>
	 * <li>statusCode - ステータスコード</li>
	 * <li>headers - ヘッダーリスト</li>
	 * <li>length - </li>
	 * </ul>
	 * </p>
	 * 
	 * @param aURL
	 * @return
	 */
	private Map<String, Object> download(final URL aURL, final File aDirectory) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", false);
		result.put("statusCode", -1);

		String filePath = PathUtility.cat(aDirectory.getAbsolutePath(), "content.dat");

		HttpClient httpClient = createClient();

		HttpGet httpGet = null;
		InputStream reader = null;
		FileOutputStream writer = null;
		try {
			httpGet = new HttpGet(aURL.toExternalForm());

			HttpResponse response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			result.put("statusCode", statusCode);

			List<Header> hs = new ArrayList<Header>();
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				hs.add(header);
			}
			result.put("headers", hs);

			if (200 == statusCode) {
				HttpEntity httpEntity = response.getEntity();
				reader = httpEntity.getContent();

				writer = new FileOutputStream(filePath);
				byte[] buffer = new byte[512];
				int len;
				long length = 0;
				while (-1 != (len = reader.read(buffer, 0, 512))) {
					if (0 == len) {
						continue;
					}
					writer.write(buffer, 0, len);
					length += len;
				}
				
				result.put("length", length);
			}

			result.put("result", true);

		} catch (ClientProtocolException ex) {
			fatal(ex);
		} catch (IOException ex) {
			fatal(ex);
		} finally {
			if (null != writer) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (null != httpGet) {
				httpGet.abort();
			}
			// httpClient.getConnectionManager().shutdown();
		}

		return result;
	}

	/**
	 * HTTPクライアント情報を取得する。
	 * <p>
	 * このメソッドをオーバーライドすることでHTTPクライアント情報を変更できます。
	 * </p>
	 * 
	 * @return HTTPクライアント情報
	 */
	private HttpClient createClient() {
		// Ver old
		//HttpClient httpClient = new DefaultHttpClient();
		// Ver 4.3
		// int socketTimeout = 3;
		// int connectionTimeout = 3;
		// RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
		RequestConfig requestConfig = RequestConfig.custom().build();
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Accept-Charset", "utf-8"));
		headers.add(new BasicHeader("Accept-Language", "ja, en;q=0.8"));
		headers.add(new BasicHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36"));

		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		return httpClient;
	}

}
