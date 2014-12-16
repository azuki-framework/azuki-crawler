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

	private HttpClient httpClient;

	/**
	 * コンストラクタ
	 */
	public SimpleDownloadEngine() {
		super(SimpleDownloadEngine.class);

		httpClient = defaultClient();
	}

	public SimpleDownloadEngine(final HttpClient aHttpClient) {
		super(SimpleDownloadEngine.class);

		httpClient = aHttpClient;
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected final DownloadEngineResult doDownload(final DownloadEngineCondition condition) {
		DownloadEngineResult result = new DownloadEngineResult();

		URL targetUrl = condition.getContentURL();
		File destFile = condition.getDestFile();

		if (ObjectUtility.isNull(targetUrl)) {
			throw new NullPointerException("TargetUrl");
		}
		if (ObjectUtility.isNull(destFile)) {
			throw new NullPointerException("DestFile");
		}

		HttpGet httpGet = null;
		InputStream reader = null;
		FileOutputStream writer = null;
		try {
			doBefore(condition);

			httpGet = new HttpGet(targetUrl.toExternalForm());

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

				writer = new FileOutputStream(destFile);
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

			doAfter(condition);

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

	protected void doBefore(final DownloadEngineCondition condition) {

	}

	protected void doAfter(final DownloadEngineCondition condition) {

	}

	/**
	 * HTTPクライアント情報を取得する。
	 * <p>
	 * このメソッドをオーバーライドすることでHTTPクライアント情報を変更できます。
	 * </p>
	 * 
	 * @return HTTPクライアント情報
	 */
	private HttpClient defaultClient() {
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
		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
		return httpClient;
	}
}
