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
package org.azkfw.crawler.downloader.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
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
import org.azkfw.util.ObjectUtility;

/**
 * このクラスは、簡易的なダウンロードを行うダウンロードエンジンです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/09
 * @author Kawakicchi
 */
public abstract class SimpleDownloadEngine extends AbstractDownloadEngine {

	/**
	 * コンストラクタ
	 */
	public SimpleDownloadEngine() {
		super(SimpleDownloadEngine.class);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 */
	public SimpleDownloadEngine(final String aName) {
		super(aName);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public SimpleDownloadEngine(final Class<?> aClass) {
		super(aClass);
	}

	/**
	 * HTTPクライアント情報を取得する。
	 * <p>
	 * このメソッドをオーバーライドすることでHTTPクライアント情報を変更できます。
	 * </p>
	 * 
	 * @return HTTPクライアント情報
	 */
	protected HttpClient createClient() {
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

	/**
	 * ダウンロード対象URLを取得する。
	 * 
	 * @return URL
	 */
	protected abstract URL getDownloadURL();

	/**
	 * ダウンロード保存ファイルを取得する。
	 * 
	 * @return
	 */
	protected abstract File getDownloadFile();

	@Override
	protected final boolean doDownload() {
		boolean result = false;
		
		URL url = getDownloadURL();
		File file = getDownloadFile();
		
		if (ObjectUtility.isAllNotNull(url, file)) {

			result = get(url, null, file);
		}

		return result;
	}

	private boolean get(final URL aUrl, final Map<String, String> aParams, final File aDestFile) {
		boolean result = false;

		// Create Parameter
		StringBuilder builder = new StringBuilder(aUrl.toExternalForm());

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

			bwInfo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aDestFile.getAbsolutePath() + ".info"), "UTF-8"));

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

	@SuppressWarnings("unused")
	private boolean post(final String aUrl, final Map<String, String> aParams) {
		boolean result = false;

		return result;
	}

}
