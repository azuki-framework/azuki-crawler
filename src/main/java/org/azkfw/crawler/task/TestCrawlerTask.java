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

/**
 * このクラスは、テスト用のクローラタスククラスです。
 * 
 * <p>
 * このクローラタスクのパラメータを下記に記す。
 * <ul>
 * <li>パラメータなし</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public class TestCrawlerTask extends AbstractCrawlerTask implements CrawlerTaskStateSupport {

	private float progress;

	public TestCrawlerTask() {
		super(TestCrawlerTask.class);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void doSetup() {

	}

	@Override
	protected void doInitialize() {
		progress = 0.0f;
	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected CrawlerTaskResult doExecute() {
		info("Environment : " + getParameter("environment", "none"));
		try {
			while (100.f > progress && !isRequestStop()) {
				progress += 1.f;
				Thread.sleep(600);
			}
		} catch (Exception ex) {

		}
		CrawlerTaskResult result = new CrawlerTaskResult();
		result.setResult(true);
		return result;
	}

	@Override
	public float getStateProgress() {
		if (0 > progress)
			return 0;
		if (100 < progress)
			return 100;
		return progress;
	}

	@Override
	public String getStateMessage() {
		return String.format("進捗状況 - %.2f%%", progress);
	}

}
