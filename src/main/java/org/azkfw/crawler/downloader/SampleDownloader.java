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
package org.azkfw.crawler.downloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.downloader.engine.DownloadEngineResult;
import org.azkfw.crawler.downloader.engine.SimpleDownloadEngine;

/**
 * このクラスは、簡易的なダウンロードを行うダウンロードエンジンです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/09
 * @author Kawakicchi
 */
public class SampleDownloader {

	/**
	 * メイン関数
	 * 
	 * @param args 引数
	 */
	public static void main(final String[] args) {
		URL url = null;
		File file = null;
		try {
			url = new URL("http://yahoo.co.jp");
			file = new File("C:\\temp\\eee.html");
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			return;
		}

		DownloadEngine engin = new SimpleDownloadEngine();

		engin.initialize();
		DownloadEngineResult result = engin.download(url, file);
		engin.release();

		System.out.println("Result : " + result.isResult());
		if (result.isResult()) {
			System.out.println("Status : " + result.getStatusCode());
			System.out.println("Length : " + result.getLength());

			for (Header header : result.getHeaders()) {
				System.out.println("Header : " + header.getName() + " - " + header.getValue());
			}
		}
	}

}
