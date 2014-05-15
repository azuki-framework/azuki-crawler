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
package org.azkfw.crawler.localization;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/05/15
 * @author Kawakicchi
 */
public class LocalizeTransform {

	public static String milliSec2Diaplay(final long aMilliSec) {
		String string = null;
		if (aMilliSec >= 1000 * 60 * 60 * 24) {
			string = String.format("%d日", (aMilliSec / (1000 * 60 * 60 * 24)));
		} else if (aMilliSec >= 1000 * 60 * 60) {
			string = String.format("%d時間", (aMilliSec / (1000 * 60 * 60)));
		} else if (aMilliSec >= 1000 * 60) {
			long sec = (long) ((double) aMilliSec / 1000.f);
			if (0 == sec % 60) {
				string = String.format("%d分", (sec / (60)));
			} else {
				string = String.format("%d分%d秒", (sec / (60)), sec % 60);
			}
		} else if (aMilliSec >= 1000) {
			string = String.format("%.1f秒", ((double) aMilliSec / (1000.f)));
		} else {
			string = String.format("%dミリ秒", aMilliSec);
		}
		return string;
	}

}
