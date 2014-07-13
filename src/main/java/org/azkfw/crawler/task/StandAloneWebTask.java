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

import java.net.URL;

import org.azkfw.util.StringUtility;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/07/11
 * @author Kawakicchi
 */
public abstract class StandAloneWebTask extends AbstractBusinessCrawlerTask {

	public StandAloneWebTask(final Class<?> aClass) {
		super(aClass);
	}

	protected boolean isParseContent(final URL aURL, final String aContentType) {
		if (StringUtility.isNotEmpty(aContentType)) {
			if (-1 != aContentType.toLowerCase().indexOf("html")) {
				return true;
			}
		}
		return false;
	}

}