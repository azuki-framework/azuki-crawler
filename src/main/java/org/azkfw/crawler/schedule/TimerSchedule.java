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
package org.azkfw.crawler.schedule;

/**
 * このクラスは、一定時間単位にスケジュールするクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public class TimerSchedule extends AbstractCrawlerSchedule {

	/**
	 * 間隔(ミリ秒)
	 */
	private long interval;

	/**
	 * スリープ間隔(ミリ秒)
	 */
	private long sleepInterval;

	/**
	 * トータルスリープ時間(ミリ秒)
	 */
	private long sleepCount;

	@Override
	protected void doSetup() {
		interval = getParameter("interval", 60 * 1000);
	}

	@Override
	protected void doInitialize() {
		sleepCount = interval;
		if (interval >= 1000) {
			sleepInterval = 1000;
		} else {
			sleepInterval = interval;
		}
	}

	@Override
	protected void doRelease() {

	}

	@Override
	public String getOutline() {
		StringBuilder s = new StringBuilder();
		s.append(String.format("%dミリ秒間隔で実行", interval));
		return s.toString();
	}

	@Override
	public boolean check() {
		if (sleepCount >= interval) {
			sleepCount = 0;
			return true;
		} else {
			sleepCount += sleepInterval;
			return false;
		}
	}

	@Override
	public boolean isStop() {
		return false;
	}

	@Override
	public boolean isRun() {
		return true;
	}

	public void sleep() throws InterruptedException {
		Thread.sleep(sleepInterval);
	}

}
