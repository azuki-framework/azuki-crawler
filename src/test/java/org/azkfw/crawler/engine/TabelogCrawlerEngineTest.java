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
package org.azkfw.crawler.engine;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.azkfw.crawler.engine.tabelog.TabelogCrawlerEngine;
import org.junit.Test;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/12/11
 * @author kawakicchi
 */
public class TabelogCrawlerEngineTest extends TestCase {

	@Test
	public void test() throws MalformedURLException {
		TabelogCrawlerEngine engine = new TabelogCrawlerEngine();

		assertEquals(true, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/")));
		assertEquals(true, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/A0101/")));
		assertEquals(true, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/A0101/A010201/")));

		assertEquals(true, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/A0101/A010201/R5103/rstLst/")));
		assertEquals(true, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/A0101/A010201/R5103/rstLst/2/")));

		assertEquals(true, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/A0101/A010201/1010964/")));

		assertEquals(false, engine.isDownloadContent(new URL("http://tabelog.com/hokkaido/XXX/")));
	}
}
