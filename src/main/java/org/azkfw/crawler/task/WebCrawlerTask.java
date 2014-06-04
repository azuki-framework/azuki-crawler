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

import org.azkfw.crawler.lang.CrawlerSetupException;

/**
 * このクラスは、Webクロール機能を実装したクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/06/02
 * @author Kawakicchi
 */
public final class WebCrawlerTask extends AbstractWebCrawlerTask {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		Integer count = 0;

		getSession().put("Count", count);
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected void test() {
		Integer count = (Integer) getSession().get("Count");

		System.out.println("count : " + count);

		getSession().put("Count", count + 1);
	}

}
