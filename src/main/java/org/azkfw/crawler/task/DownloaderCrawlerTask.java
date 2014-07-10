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
import java.util.Map;

import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.downloader.engine.SampleDownloadEngine;
import org.azkfw.persistence.parameter.Parameter;
import org.azkfw.persistence.proterty.Property;
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

	private URL url;
	private File file;

	private String userAgent;

	/**
	 * コンストラクタ
	 */
	public DownloaderCrawlerTask() {
		super(DownloaderCrawlerTask.class);
	}

	@Override
	protected void doSetup() {
		Parameter p = getParameter();

		try {
			url = new URL(p.getString("url"));
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		file = new File(p.getString("file"));

		info(String.format("url  : %s", url.toExternalForm()));
		info(String.format("file : %s", file.getAbsolutePath()));

		Property pp = getProperty();
		userAgent = pp.getString("userAgent", "Azuki crawler task 1.0");
		info(String.format("userAgent : %s", userAgent));
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
	protected CrawlerTaskResult doExecute() {

		CrawlerTaskResult result = new CrawlerTaskResult();

		if (download(url, null, file)) {
			result.setResult(true);
			result.setStop(false);
		} else {
			result.setResult(false);
			result.setStop(true);
		}
		return result;
	}

	private boolean download(final URL aUrl, final Map<String, Object> aParam, final File aDestFile) {
		boolean result = false;

		DownloadEngine engine = new SampleDownloadEngine(url, file);

		engine.initialize();
		engine.download();
		engine.release();

		result = true;

		return result;
	}
}
