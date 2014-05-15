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
package org.azkfw.crawler;

import org.apache.log4j.xml.DOMConfigurator;
import org.azkfw.crawler.config.CrawlerConfig;

/**
 * このクラスは、クローラを動作させるためのメインクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public final class Crawler {

	/**
	 * Crawler version
	 */
	public final static String VERSION = "1.0.0";

	/**
	 * メイン関数
	 * 
	 * @param args 引数
	 */
	public static void main(final String[] args) {
		if (0 == args.length) {
			doHelp();

		} else {
			String cmd = args[0].toLowerCase();
			if ("version".equals(cmd)) {
				doVersion();

			} else if ("help".equals(cmd)) {
				doHelp();

			} else if ("start".equals(cmd)) {
				String configFile = "./config/crawler.xml";
				if (2 <= args.length) {
					configFile = args[1];
				} else {
					System.out.println("Read default crawler config.[./config/crawler.xml]");
				}

				CrawlerConfig config = CrawlerConfig.parse(configFile);
				DOMConfigurator.configure(config.getLogger().getConfig());

				CrawlerServer server = new CrawlerServer(config);
				server.start();

			} else if ("stop".equals(cmd)) {
				String configFile = "./config/crawler.xml";
				if (2 <= args.length) {
					configFile = args[1];
				} else {
					System.out.println("Read default crawler config.[./config/crawler.xml]");
				}

				CrawlerConfig config = CrawlerConfig.parse(configFile);
				DOMConfigurator.configure(config.getLogger().getConfig());

				CrawlerController controller = new CrawlerController(config.getController());
				controller.stop();

			} else if ("restart".equals(cmd)) {
				String configFile = "./config/crawler.xml";
				if (2 <= args.length) {
					configFile = args[1];
				} else {
					System.out.println("Read default crawler config.[./config/crawler.xml]");
				}

				CrawlerConfig config = CrawlerConfig.parse(configFile);
				DOMConfigurator.configure(config.getLogger().getConfig());

				CrawlerController controller = new CrawlerController(config.getController());
				boolean result = controller.stop();

				if (result) {
					CrawlerServer server = new CrawlerServer(config);
					server.start();
				}

			} else {
				doHelp();
			}
		}
	}

	private static void doVersion() {
		StringBuilder s = new StringBuilder();
		s.append(String.format("Crawler version \"%s\"", Crawler.VERSION));
		System.out.println(s.toString());
	}

	private static void doHelp() {
		String crlf = "\n";
		try {
			crlf = System.getProperty("line.separator");
		} catch (SecurityException ex) {
		}
		StringBuilder s = new StringBuilder();
		s.append("使用方法: Crawler command configfile").append(crlf);
		s.append("  start").append(crlf);
		s.append("  \tクローラを起動する").append(crlf);
		s.append("  stop").append(crlf);
		s.append("  \tクローラを停止する").append(crlf);
		s.append("  restart").append(crlf);
		s.append("  \tクローラを再起動する").append(crlf);
		s.append("  help").append(crlf);
		s.append("  \tこのヘルプ・メッセージを出力する").append(crlf);
		s.append("  version").append(crlf);
		s.append("  \t製品バージョンを出力して終了する").append(crlf);
		s.append(crlf);
		System.out.println(s.toString());
	}

	private Crawler() {
	}
}
