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
package org.azkfw.crawler.parser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.content.FileContent;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngineResult;
import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine;

/**
 * このクラスは、簡易的なHTMLテキスト解析を行う解析エンジンです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public final class SampleHtmlParser {

	/**
	 * メイン関数
	 * 
	 * @param args 引数
	 */
	public static void main(final String[] args) {
		Content content = null;
		URL url = null;
		try {
			//content = new FileContent(new File("C:\\html\\6\\6.html"));
			//url = new URL("http://tabelog.com/osaka/A2704/A270402/27041365");
			content = new FileContent(new File("C:\\temp\\aaa.html"));
			url = new URL("http://yahoo.co.jp");
		} catch (MalformedURLException ex) {

		}

		ParseEngine engine = new SimpleHtmlParseEngine(url, content);

		engine.initialize();
		ParseEngineResult result = engine.parse();
		engine.release();

		System.out.println("Result : " + result.isResult());
		if (result.isResult()) {

			if (engine instanceof SimpleHtmlParseEngine) {
				SimpleHtmlParseEngine e = (SimpleHtmlParseEngine) engine;
				List<String> urls = e.getUrlList();
				for (String u : urls) {
					System.out.println("URL : " + u);
				}
			}

		}
	}

}
