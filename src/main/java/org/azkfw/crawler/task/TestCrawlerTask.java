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

import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.store.Store;

/**
 * このクラスは、テストを行うクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public class TestCrawlerTask extends AbstractPersistenceCrawlerTask {

	/**
	 * コンストラクタ
	 */
	public TestCrawlerTask() {
		super(TestCrawlerTask.class);
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {

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
	protected CrawlerTaskResult doExecute() throws CrawlerServiceException {
		Store<String, Object> session = getSession();
		Integer count = (Integer) session.get("count", Integer.valueOf(0));

		info(String.format("Count %4d", count));

		session.put("count", count + 1);

		CrawlerTaskResult result = new CrawlerTaskResult();
		result.setResult(true);
		result.setStop(false);
		return result;
	}

}
