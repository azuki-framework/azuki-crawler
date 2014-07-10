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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * このクラスは、 数値文字列のマッチング機能を実装するクラスです。
 * 
 * <p>
 * <ul>
 * <li>* - 必ずマッチする</li>
 * <li>*&frasl;10 - 10の倍数の場合マッチする</li>
 * <li>10 - 10の場合マッチする</li>
 * <li>10,15 - 10か15の場合マッチする</li>
 * <li>10,15-20 - 10か15から20の場合マッチする</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public class NumberMatcher implements Matcher {

	private int type;
	private int interval;
	private Set<Integer> numbers;

	@Override
	public boolean compile(final String aPattern) {
		if ("*".equals(aPattern)) {
			type = 0;
		} else if (aPattern.startsWith("*/")) {
			type = 1;
			interval = Integer.parseInt(aPattern.substring(2));
		} else {
			type = 2;
			numbers = new HashSet<Integer>();
			Pattern splitPattern = Pattern.compile("[,]+");
			String[] ss = splitPattern.split(aPattern);
			for (String s : ss) {
				int index = s.indexOf("-");
				if (-1 == index) {
					numbers.add(Integer.parseInt(s));
				} else {
					int start = Integer.parseInt(aPattern.substring(0, index));
					int end = Integer.parseInt(aPattern.substring(index + 1));
					if (start > end) {
						int buf = start;
						start = end;
						end = buf;
					}
					for (int i = start; i <= end; i++) {
						numbers.add(i);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean match(final String aValue) {
		int number = Integer.parseInt(aValue);
		if (0 == type) {
			return true;
		} else if (1 == type) {
			return (0 == number % interval);
		} else if (2 == type) {
			return (numbers.contains(number));
		}
		return false;
	}

	@Override
	public String getMatchString(final String aValue) {
		int number = Integer.parseInt(aValue);
		if (0 == type) {
			return Integer.toString(number);
		} else if (1 == type) {
			return Integer.toString(number);
		} else if (2 == type) {
			return Integer.toString(number);
		}
		return null;
	}
}
