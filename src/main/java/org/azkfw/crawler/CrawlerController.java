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
package org.azkfw.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.azkfw.crawler.config.CrawlerConfig.CrawlerControllerConfig;
import org.azkfw.crawler.logger.LoggerObject;

/**
 * このクラスは、クローラを制御するためのコントロールクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
class CrawlerController extends LoggerObject {

	/**
	 * 設定情報
	 */
	private CrawlerControllerConfig config;

	/**
	 * コンストラクタ
	 * 
	 * @param aConfig 設定情報
	 */
	CrawlerController(final CrawlerControllerConfig aConfig) {
		super(CrawlerController.class);

		config = aConfig;
	}

	/**
	 * クローラサーバへ停止依頼を行う。
	 * 
	 * @return 結果
	 */
	public boolean stop() {
		boolean result = false;

		info("Request stop crawler.");

		try {
			result = execCommand("stop");

			if (result) {
				while (true) {
					execCommand("active");
					Thread.sleep(1000);
				}
			}

		} catch (HttpHostConnectException ex) {
			info("Already stoped crawler.");
			result = true;
		} catch (ClientProtocolException ex) {
			fatal(ex);
			return false;
		} catch (IOException ex) {
			fatal(ex);
			return false;
		} catch (Exception ex) {
			fatal(ex);
			return false;
		}

		info("Stop crawler.");
		return result;
	}

	private boolean execCommand(final String aCommand) throws ClientProtocolException, IOException {
		return execCommand(aCommand, null);
	}

	private boolean execCommand(final String aCommand, final Map<String, String> aParameter) throws ClientProtocolException, IOException {
		boolean result = false;

		String userAgent = "Azuki crawler controller 1.0";
		RequestConfig requestConfig = RequestConfig.custom().build();
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Accept-Charset", config.getCharset()));
		headers.add(new BasicHeader("Accept-Language", "ja, en;q=0.8"));
		headers.add(new BasicHeader("User-Agent", userAgent));

		StringBuilder parameter = new StringBuilder();
		if (null != aParameter) {
			for (String key : aParameter.keySet()) {
				if (0 == parameter.length()) {
					parameter.append("?");
				} else {
					parameter.append("&");
				}
				parameter.append(key);
				parameter.append("=");
				parameter.append(aParameter.get(key));
			}
		}

		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();

		String url = String.format("http://localhost:%d%s/%s%s", config.getPort(), config.getContextpath(), aCommand, parameter.toString());

		HttpGet httpGet = null;
		BufferedReader reader = null;
		try {
			info("Command " + aCommand + " " + parameter.toString());

			httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();

			StringBuilder html = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), config.getCharset()));
			String line = null;
			while (null != (line = reader.readLine())) {
				html.append(line);
			}

			if (200 == statusCode) {
				result = true;
				info("Return " + html);
			} else {
				error("Error Code.[" + statusCode + "]");
				error(html.toString());
			}

		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
			if (null != httpGet) {
				httpGet.abort();
			}
		}

		return result;
	}
}
