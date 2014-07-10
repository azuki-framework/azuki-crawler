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
package org.azkfw.crawler.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azkfw.business.logic.AbstractPersistenceLogic;
import org.azkfw.crawler.logic.WebCrawlerManager;

/**
 * このクラスは、 Webクロール管理機能を実装するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public class WebCrawlerManagerImpl extends AbstractPersistenceLogic implements WebCrawlerManager {

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doDestroy() {

	}

	@Override
	public Map<String, Object> lockHost() {
		Map<String, Object> host = new HashMap<String, Object>();

		// host lock

		host.put("id", "host01");
		host.put("protocol", "http");
		host.put("name", "localhost");
		host.put("port", 8080);

		// commit

		return host;
	}

	@Override
	public void unlockHost(final String aHostId, final int aResultCode) {
		// result code

	}

	@Override
	public List<Map<String, Object>> getDownloadPages(final String aHostId, final int aPageSize) {
		List<Map<String, Object>> pages = new ArrayList<Map<String, Object>>();

		Map<String, Object> page = new HashMap<String, Object>();
		page.put("id", "page01");
		page.put("areas", "/");

		pages.add(page);

		return pages;
	}

}
