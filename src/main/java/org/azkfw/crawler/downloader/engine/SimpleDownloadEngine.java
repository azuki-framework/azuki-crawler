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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class SimpleDownloadEngine extends AbstractDownloadEngine {

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
	protected SimpleDownloadEngine(final String aName) {
		super(aName);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	protected SimpleDownloadEngine(final Class<?> aClass) {
		super(aClass);
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected final DownloadEngineResult doDownload(final URL aTargetUrl, final File aDestFile) {
		DownloadEngineResult result = new DownloadEngineResult();

		if (ObjectUtility.isNull(aTargetUrl)) {
			throw new NullPointerException("TargetUrl");
		}
		if (ObjectUtility.isNull(aDestFile)) {
			throw new NullPointerException("DestFile");
		}

		HttpClient httpClient = createClient();

		HttpGet httpGet = null;
		InputStream reader = null;
		FileOutputStream writer = null;
		try {
			httpGet = new HttpGet(aTargetUrl.toExternalForm());

			HttpResponse response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			result.setStatusCode(statusCode);

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				result.addHeader(header);
			}

			if (200 == statusCode) {
				HttpEntity httpEntity = response.getEntity();
				reader = httpEntity.getContent();

				writer = new FileOutputStream(aDestFile);
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
				result.setLength(length);
			}

			result.setResult(true);

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
		// chrom
		headers.add(new BasicHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.63 Safari/537.36"));
		// IOS5
		//headers.add(new BasicHeader("User-Agent",
		//		"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9A405 Safari/7534.48.3"));

		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		return httpClient;
	}
}
