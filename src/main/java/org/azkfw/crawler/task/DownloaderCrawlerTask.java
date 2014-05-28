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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
import org.azkfw.persistence.proterty.PropertyFile;

/**
 * このクラスは、ダウンロードを行うクローラタスククラスです。
 * 
 * <p>
 * このクローラタスクのパラメータを下記に記す。
 * <ul>
 * <li>url - ダウンロード対象のURL</li>
 * <li>file - ダウンロードファイルの保存先</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/14
 * @author Kawakicchi
 */
@PropertyFile("conf/DownloaderCrawlerTask.properties")
public class DownloaderCrawlerTask extends AbstractPersistenceCrawlerTask {

	private String url;
	private String file;

	public DownloaderCrawlerTask() {
		super(DownloaderCrawlerTask.class);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void doSetup() {
		url = getParameter("url", null);
		file = getParameter("file", null);
		info(String.format("url  : %s", url));
		info(String.format("file : %s", file));
		
		System.out.println(getProperty().getString("message", "None message."));
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected CrawlerTaskResult doExecute() {
		

		CrawlerTaskResult result = new CrawlerTaskResult();

		if (get(url, null, file)) {
			result.setResult(true);
			result.setStop(false);
		} else {
			result.setResult(false);
			result.setStop(true);
		}
		return result;
	}

	public boolean get(final String aUrl, final Map<String, String> aParams, final String aDestFile) {
		boolean result = false;

		// Create Parameter
		StringBuilder builder = new StringBuilder(aUrl);
		if (null != aParams && !aParams.isEmpty()) {
			int cnt = 0;
			for (Map.Entry<String, String> entry : aParams.entrySet()) {
				if (0 == cnt) {
					builder.append("?");
				} else {
					builder.append("&");
				}
				builder.append(entry.getKey());
				builder.append("=");
				// TODO: URLEncoding
				builder.append(entry.getValue());
			}
		}

		HttpClient httpClient = createClient();

		HttpGet httpGet = null;
		InputStream is = null;
		FileOutputStream fosData = null;
		BufferedWriter bwInfo = null;

		try {
			String url = builder.toString();

			httpGet = new HttpGet(url);
			System.out.println(httpGet.getURI());

			HttpResponse response = httpClient.execute(httpGet);

			bwInfo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aDestFile + ".info"), "UTF-8"));

			int statusCode = response.getStatusLine().getStatusCode();
			// InfoFile Write StatusCode
			bwInfo.write(String.format("%s : %d\n", "StatusCode", statusCode));
			// InfoFile Write Header
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				bwInfo.write(String.format("%s : %s\n", header.getName(), header.getValue()));
			}

			HttpEntity httpEntity = response.getEntity();
			is = httpEntity.getContent();
			fosData = new FileOutputStream(aDestFile);

			byte[] buffer = new byte[512];
			int len;
			while (-1 != (len = is.read(buffer, 0, 512))) {
				if (0 == len) {
					continue;
				}
				fosData.write(buffer, 0, len);
			}

			result = true;

		} catch (ClientProtocolException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != bwInfo) {
				try {
					bwInfo.flush();
					bwInfo.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (null != fosData) {
				try {
					fosData.flush();
					fosData.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
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

	public boolean post(final String aUrl, final Map<String, String> params) {
		boolean result = false;

		return result;
	}

	private HttpClient createClient() {
		// Ver old
		//HttpClient httpClient = new DefaultHttpClient();
		// Ver 4.3
		// int socketTimeout = 3;
		// int connectionTimeout = 3;
		String userAgent = "Azuki crawler task 1.0";
		// RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
		RequestConfig requestConfig = RequestConfig.custom().build();
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Accept-Charset", "utf-8"));
		headers.add(new BasicHeader("Accept-Language", "ja, en;q=0.8"));
		headers.add(new BasicHeader("User-Agent", userAgent));

		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		return httpClient;
	}
}
