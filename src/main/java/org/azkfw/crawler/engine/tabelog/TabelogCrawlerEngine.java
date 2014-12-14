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
package org.azkfw.crawler.engine.tabelog;

import java.net.URL;
import java.util.regex.Pattern;

import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.engine.AbstractCrawlerEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.util.StringUtility;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/12/11
 * @author kawakicchi
 */
public final class TabelogCrawlerEngine extends AbstractCrawlerEngine {

	private static final Pattern PTN_SHOP = Pattern.compile("^http://tabelog\\.com/[^/]+/[^/]+/[^/]+/[^/]+/$");
	private static final Pattern PTN_SEARCH = Pattern.compile("^http://tabelog\\.com/[^/]+/[^/]+/[^/]+/[^/]+/rstLst/([0-9&&[^/]]+/){0,1}$");
	private static final Pattern PTN_CATEGORY = Pattern
			.compile("^http://tabelog\\.com/([^/]+/([A-Z]{1}[0-9&&[^/]]+/([A-Z]{1}[0-9&&[^/]]+/){0,1}){0,1}){0,1}$");

	public TabelogCrawlerEngine() {

	}

	@Override
	public boolean isDownloadContent(final URL url) {
		String str = url.toExternalForm();
		if (PTN_SHOP.matcher(str).matches()) {
			return true;
		}
		if (PTN_SEARCH.matcher(str).matches()) {
			return true;
		}
		if (PTN_CATEGORY.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public DownloadEngine getDownloadEngine(final URL url) {
		// デフォルトのダウンロードエンジンを使用する。
		return null;
	}

	@Override
	public boolean isParseContent(final URL url, final String contentType) {
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
		if (PTN_SEARCH.matcher(str).matches()) {
			return true;
		}
		if (PTN_CATEGORY.matcher(str).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public ParseEngine getParseEngine(final URL url, final String aContentType, final Content content) {
		// デフォルトのダウンロードエンジンを使用する。
		return null;
	}

}
