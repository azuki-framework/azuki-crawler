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
package org.azkfw.crawler.engine.gnavi;

import java.net.URL;
import java.util.regex.Pattern;

import org.azkfw.crawler.CrawlInfo;
import org.azkfw.crawler.CrawlType;
import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.engine.AbstractCrawlerEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.util.StringUtility;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/12/29
 * @author Kawakicchi
 */
public class GnaviCrawlerEngine extends AbstractCrawlerEngine {

	// http://www.gnavi.co.jp/
	// http://r.gnavi.co.jp/4660m13p0000

	private static final Pattern PTN_ETS1 = Pattern.compile("^http://www\\.gnavi\\.co\\.jp/.*$");
	private static final Pattern PTN_ETS2 = Pattern.compile("^http://r\\.gnavi\\.co\\.jp/.*$");
	private static final Pattern PTN_SHOP = Pattern.compile("^http://r\\.gnavi\\.co\\.jp/[^/]+[/]{0,1}$");

	public GnaviCrawlerEngine() {

	}

	@Override
	public CrawlInfo getCrawlInfo(final URL url) {
		String str = url.toExternalForm();
		if (PTN_SHOP.matcher(str).matches()) {
			return new CrawlInfo(CrawlType.Once);
		}
		if (PTN_ETS1.matcher(str).matches()) {
			return new CrawlInfo(CrawlType.Loop);
		}
		if (PTN_ETS2.matcher(str).matches()) {
			return new CrawlInfo(CrawlType.Loop);
		}
		return null;
	}

	@Override
	public boolean isDownloadContent(URL url) {
		String str = url.toExternalForm();
		if (PTN_SHOP.matcher(str).matches()) {
			return true;
		}
		if (PTN_ETS1.matcher(str).matches()) {
			return true;
		}
		if (PTN_ETS2.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public DownloadEngine getDownloadEngine(URL url) {
		// デフォルトのダウンロードエンジンを使用する。
		return null;
	}

	@Override
	public boolean isParseContent(URL url, String contentType) {
		if (StringUtility.isEmpty(contentType)) {
			return false;
		}
		if (-1 == contentType.toLowerCase().indexOf("html")) {
			return false;
		}

		String str = url.toExternalForm();
		if (PTN_SHOP.matcher(str).matches()) {
			return true;
		}
		if (PTN_ETS1.matcher(str).matches()) {
			return true;
		}
		if (PTN_ETS2.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public ParseEngine getParseEngine(URL aUrl, String contentType, Content aContent) {
		// デフォルトのダウンロードエンジンを使用する。
		return null;
	}

}
