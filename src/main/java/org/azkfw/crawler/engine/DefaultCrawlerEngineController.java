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
package org.azkfw.crawler.engine;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.downloader.engine.SimpleDownloadEngine;
import org.azkfw.crawler.engine.tabelog.TabelogCrawlerEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine;

/**
 * このクラスは、クローラエンジンのコントローラ機能を実装したクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/12/11
 * @author kawakicchi
 */
public class DefaultCrawlerEngineController implements CrawlerEngineController {

	/** クローラエンジンリスト */
	private List<CrawlerEngine> engines;

	/**
	 * コンストラクタ
	 */
	public DefaultCrawlerEngineController() {
		engines = new ArrayList<CrawlerEngine>();

		// TODO: debug
		engines.add(new TabelogCrawlerEngine());
	}

	public int getEngineNo() {
		return 0;
	}

	@Override
	public boolean isDownloadContent(final URL url) {
		for (CrawlerEngine engine : engines) {
			if (engine.isDownloadContent(url)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public DownloadEngine getDownloadEngine(final URL url) {
		for (CrawlerEngine engine : engines) {
			if (engine.isDownloadContent(url)) {
				DownloadEngine de = engine.getDownloadEngine(url);
				if (null != de) {
					return de;
				}
			}
		}
		return new SimpleDownloadEngine();
	}

	@Override
	public boolean isParseContent(final URL url, final String contentType) {
		for (CrawlerEngine engine : engines) {
			if (engine.isParseContent(url, contentType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ParseEngine getParseEngine(final URL aUrl, final String aContentType, final Content aContent) {
		for (CrawlerEngine engine : engines) {
			if (engine.isParseContent(aUrl, aContentType)) {
				ParseEngine pe = engine.getParseEngine(aUrl, aContentType, aContent);
				if (null != pe) {
					return pe;
				}
			}
		}
		return new SimpleHtmlParseEngine(aUrl, aContent);
	}
}
