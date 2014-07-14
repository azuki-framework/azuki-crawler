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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

/**
 * このクラスは、ダウンロード結果情報を保持するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/14
 * @author Kawakicchi
 */
public class DownloadEngineResult {

	private boolean result;
	private int statusCode;
	private List<Header> headers;
	private long length;

	public DownloadEngineResult() {
		result = false;
		statusCode = -1;
		headers = new ArrayList<Header>();
		length = -1;
	}

	public void setResult(final boolean aResult) {
		result = aResult;
	}

	public boolean isResult() {
		return result;
	}

	public void setStatusCode(final int aCode) {
		statusCode = aCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void addHeader(final Header aHeader) {
		headers.add(aHeader);
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public long getLength() {
		return length;
	}

	public void setLength(final long aLength) {
		length = aLength;
	}

}
