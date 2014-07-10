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
package org.azkfw.crawler.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * このクラスは、 日付文字列のマッチング機能を実装するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public class DateTimeMatcher implements org.azkfw.crawler.matcher.Matcher {

	private NumberMatcher nmMinute;
	private NumberMatcher nmHour;
	private NumberMatcher nmDay;
	private NumberMatcher nmMonth;
	private NumberMatcher nmWeek;

	@Override
	public boolean compile(final String aPattern) {
		//		String text = new String("* * * * *");
		//		text = new String("* 	* 	* 	* 	*");
		//		text = new String("0 0 1 1 0");
		//		text = new String("59 23 31 12 7");
		//		text = new String("0-59 0-23 1-31 1-12 0-7");
		//		text = new String("0,59 0,23 1,31 1,12 0,7");
		//		text = new String("0,0-59,0-59,59 0,0-23,0-23,23 1,1-31,1-31,31 1,1-12,1-12,12 0,0-7,0-7,7");
		//		text = new String("*/0 */0 */1 */1 */0");

		String min1 = "[0-9]{1}|[0-5]{1}[0-9]{1}"; // 0-59 ([0-9]{1}|[0-5]{1}[0-9]{1})
		String hor1 = "[0-9]{1}|[0-1]{1}[0-9]{1}|[2]{1}[0-3]{1}"; // 0-23 ([0-9]{1}|[0-1]{1}[0-9]{1}|[2]{1}[0-3]{1})
		String day1 = "[1-9]{1}|[0-2]{1}[0-9]{1}|[3]{1}[0-1]{1}"; // 1-31 ([1-9]{1}|[0-2]{1}[0-9]{1}|[3]{1}[0-1]{1})
		String mon1 = "[1-9]{1}|[0]{1}[1-9]{1}|[1]{1}[0-2]{1}"; // 1-12 ([1-9]{1}|[0]{1}[1-9]{1}|[1]{1}[0-2]{1})
		String wek1 = "[0-7]{1}"; // 0-7 ([0-7]{1})

		String min0 = "[1-9]{1}|[0-5]{1}[0-9]{1}|60"; // 1-60
		String hor0 = "[1-9]{1}|[0-1]{1}[0-9]{1}|[2]{1}[0-3]{1}"; // 1-24
		String day0 = "[1-9]{1}|[0-2]{1}[0-9]{1}|[3]{1}[0-1]{1}"; // 1-31
		String mon0 = "[1-9]{1}|[0]{1}[1-9]{1}|[1]{1}[0-2]{1}"; // 1-12
		String wek0 = "[1-7]{1}"; // 1-7

		String min2 = String.format("((%s){1}(-(%s)){0,1})", min1, min1);
		String hor2 = String.format("((%s){1}(-(%s)){0,1})", hor1, hor1);
		String day2 = String.format("((%s){1}(-(%s)){0,1})", day1, day1);
		String mon2 = String.format("((%s){1}(-(%s)){0,1})", mon1, mon1);
		String wek2 = String.format("((%s){1}(-(%s)){0,1})", wek1, wek1);

		String min3 = String.format("((\\*)|(\\*/(%s))|((%s){1}(,(%s)){0,}))", min0, min2, min2);
		String hor3 = String.format("((\\*)|(\\*/(%s))|((%s){1}(,(%s)){0,}))", hor0, hor2, hor2);
		String day3 = String.format("((\\*)|(\\*/(%s))|((%s){1}(,(%s)){0,}))", day0, day2, day2);
		String mon3 = String.format("((\\*)|(\\*/(%s))|((%s){1}(,(%s)){0,}))", mon0, mon2, mon2);
		String wek3 = String.format("((\\*)|(\\*/(%s))|((%s){1}(,(%s)){0,}))", wek0, wek2, wek2);

		String pattern = String.format("^%s[\\s]{1,}%s[\\s]{1,}%s[\\s]{1,}%s[\\s]{1,}%s$", min3, hor3, day3, mon3, wek3);
		Pattern pattern1 = Pattern.compile(pattern);
		Matcher matcher1 = pattern1.matcher(aPattern);

		if (matcher1.matches()) {
			Pattern splitPattern = Pattern.compile("[\\s]+");
			String[] ss = splitPattern.split(aPattern);
			String minute = ss[0];
			String hour = ss[1];
			String day = ss[2];
			String month = ss[3];
			String week = ss[4];

			nmMinute = new NumberMatcher();
			nmHour = new NumberMatcher();
			nmDay = new NumberMatcher();
			nmMonth = new NumberMatcher();
			nmWeek = new NumberMatcher();

			nmMinute.compile(minute);
			nmHour.compile(hour);
			nmDay.compile(day);
			nmMonth.compile(month);
			nmWeek.compile(week);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean match(final String aValue) {
		String min1 = "[0-9]{1}|[0-5]{1}[0-9]{1}"; // 0-59 
		String hor1 = "[0-9]{1}|[0-1]{1}[0-9]{1}|[2]{1}[0-3]{1}"; // 0-23
		String day1 = "[1-9]{1}|[0-2]{1}[0-9]{1}|[3]{1}[0-1]{1}"; // 1-31
		String mon1 = "[1-9]{1}|[0]{1}[1-9]{1}|[1]{1}[0-2]{1}"; // 1-12
		String wek1 = "[0-7]{1}"; // 0-7

		String pattern = String.format("^(%s){1}[\\s]{1,}(%s){1}[\\s]{1,}(%s){1}[\\s]{1,}(%s){1}[\\s]{1,}(%s){1}$", min1, hor1, day1, mon1, wek1);
		Pattern pattern1 = Pattern.compile(pattern);

		Matcher matcher1 = pattern1.matcher(aValue);
		if (matcher1.matches()) {
			Pattern splitPattern = Pattern.compile("[\\s]+");
			String[] ss = splitPattern.split(aValue);
			String minute = ss[0];
			String hour = ss[1];
			String day = ss[2];
			String month = ss[3];
			String week = ss[4];

			if (!nmMinute.match(minute))
				return false;
			if (!nmHour.match(hour))
				return false;
			if (!nmDay.match(day))
				return false;
			if (!nmMonth.match(month))
				return false;
			if (!nmWeek.match(week))
				return false;

			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getMatchString(final String aValue) {
		String min1 = "[0-9]{1}|[0-5]{1}[0-9]{1}"; // 0-59 
		String hor1 = "[0-9]{1}|[0-1]{1}[0-9]{1}|[2]{1}[0-3]{1}"; // 0-23
		String day1 = "[1-9]{1}|[0-2]{1}[0-9]{1}|[3]{1}[0-1]{1}"; // 1-31
		String mon1 = "[1-9]{1}|[0]{1}[1-9]{1}|[1]{1}[0-2]{1}"; // 1-12
		String wek1 = "[0-7]{1}"; // 0-7

		String pattern = String.format("^(%s){1}[\\s]{1,}(%s){1}[\\s]{1,}(%s){1}[\\s]{1,}(%s){1}[\\s]{1,}(%s){1}$", min1, hor1, day1, mon1, wek1);
		Pattern pattern1 = Pattern.compile(pattern);

		Matcher matcher1 = pattern1.matcher(aValue);
		if (matcher1.matches()) {
			Pattern splitPattern = Pattern.compile("[\\s]+");
			String[] ss = splitPattern.split(aValue);
			String minute = ss[0];
			String hour = ss[1];
			String day = ss[2];
			String month = ss[3];
			String week = ss[4];

			String mi = nmMinute.getMatchString(minute);
			String ho = nmHour.getMatchString(hour);
			String da = nmDay.getMatchString(day);
			String mo = nmMonth.getMatchString(month);
			String we = nmWeek.getMatchString(week);

			return mi + " " + ho + " " + da + " " + mo + " " + we;
		} else {
			return null;
		}
	}

}
