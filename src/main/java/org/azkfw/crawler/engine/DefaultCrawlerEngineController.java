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

import org.azkfw.crawler.engine.tabelog.TabelogCrawlerEngine;

/**
 * このクラスは、クローラエンジンのコントローラ機能を実装したクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/12/11
 * @author kawakicchi
 */
public class DefaultCrawlerEngineController implements CrawlerEngineController {

	private List<CrawlerEngine> engines;

	public DefaultCrawlerEngineController() {
		engines = new ArrayList<CrawlerEngine>();

		// TODO: debug
		engines.add(new TabelogCrawlerEngine());
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
	public boolean isParseContent(final URL url, final String contentType) {
		for (CrawlerEngine engine : engines) {
			if (engine.isParseContent(url, contentType)) {
				return true;
			}
		}
		return false;
	}

}
