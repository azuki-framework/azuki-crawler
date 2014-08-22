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

import org.azkfw.parameter.Parameter;
import org.azkfw.util.StringUtility;

/**
 * このクラスは、一定時間単位に実行を行うスケジュールクラスです。
 * 
 * <p>
 * パラメータ一覧
 * <ul>
 * <li>interval - 実行する間隔(default:1min)</li>
 * </ul>
 * </p>
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
		Parameter p = getParameter();

		interval = toMillis(p.getString("interval", "1min"));
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

	private long toMillis(final String aString) {
		long ms = 0;
		if (StringUtility.isNotEmpty(aString)) {
			String buf = aString.trim().toLowerCase();

			if (buf.endsWith("ms")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 2).trim());

			} else if (buf.endsWith("sec")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 3).trim()) * 1000;
			} else if (buf.endsWith("min")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 3).trim()) * 1000 * 60;
			} else if (buf.endsWith("hor")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 3).trim()) * 1000 * 60 * 60;

			} else if (buf.endsWith("s")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 1).trim()) * 1000;
			} else if (buf.endsWith("m")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 1).trim()) * 1000 * 60;
			} else if (buf.endsWith("h")) {
				ms = Long.parseLong(buf.substring(0, buf.length() - 1).trim()) * 1000 * 60 * 60;
			} else {
				ms = Long.parseLong(buf);
			}
		}
		return ms;
	}
}
