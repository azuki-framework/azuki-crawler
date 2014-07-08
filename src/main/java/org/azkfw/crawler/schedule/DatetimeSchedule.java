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

import java.util.Calendar;
import java.util.Date;

import org.azkfw.crawler.matcher.DateTimeMatcher;
import org.azkfw.persistence.parameter.Parameter;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public class DatetimeSchedule extends AbstractCrawlerSchedule {

	private String pattern;
	private DateTimeMatcher matcher;

	private String lastMatchString;

	@Override
	protected void doSetup() {
		Parameter p = getParameter();

		pattern = p.getString("pattern", "* * * * *");
		matcher = new DateTimeMatcher();
		matcher.compile(pattern);
	}

	@Override
	protected void doInitialize() {
	}

	@Override
	protected void doRelease() {

	}

	@Override
	public String getOutline() {
		StringBuilder s = new StringBuilder();
		s.append(String.format("日付が「%s」に一致する時実行", pattern));
		return s.toString();
	}

	@Override
	public boolean check() {
		Calendar cln = Calendar.getInstance();
		cln.setTime(new Date());
		String str = String.format("%d %d %d %d %d", cln.get(Calendar.MINUTE), cln.get(Calendar.HOUR_OF_DAY), cln.get(Calendar.DAY_OF_MONTH),
				cln.get(Calendar.MONTH) + 1, cln.get(Calendar.DAY_OF_WEEK));

		if (matcher.match(str)) {
			String matchString = matcher.getMatchString(str);
			if (null == matchString) {
				return false;
			} else if (matchString.equals(lastMatchString)) {
				return false;
			} else {
				info("match " + matchString);
				lastMatchString = matchString;
				return true;
			}
		} else {
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
		Thread.sleep(1000);
	}

}
