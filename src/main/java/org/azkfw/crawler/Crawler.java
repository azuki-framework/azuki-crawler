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
import org.azkfw.business.cui.CommandLinePurser;
import org.azkfw.context.Context;
import org.azkfw.crawler.config.CrawlerConfig;
import org.azkfw.crawler.context.CrawlerContext;

/**
 * このクラスは、クローラを動作させるためのメインクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public final class Crawler {

	/** Crawler version */
	public final static String VERSION = "1.0.0";

	// Command name
	private final static String CMD_VERSION = "version";
	private final static String CMD_HELP = "help";
	private final static String CMD_START = "start";
	private final static String CMD_STOP = "stop";
	private final static String CMD_RESTART = "restart";

	// Command option key 
	private final static String OPT_BASE_DIRECTORY = "baseDir";
	private final static String OPT_CONFIG_FILE = "configFile";
	private final static String OPT_PLUGIN_FILE = "pluginFile";

	// Default command option value
	private final static String DEFAULT_BASE_DIRECTORY = "./";
	private final static String DEFAULT_CONFIG_FILE = "conf/azuki-crawler.xml";
	private final static String DEFAULT_PLUGIN_FILE = "";

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
			if (CMD_VERSION.equals(cmd)) {
				doVersion();

			} else if (CMD_HELP.equals(cmd)) {
				doHelp();

			} else if (CMD_START.equals(cmd)) {
				doStart(args);

			} else if (CMD_STOP.equals(cmd)) {
				doStop(args);

			} else if (CMD_RESTART.equals(cmd)) {
				doRestart(args);

			} else {
				doHelp();
			}
		}
	}

	/**
	 * コンストラクタ
	 * <p>
	 * インスタンス生成を禁止する
	 * </p>
	 */
	private Crawler() {
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

	private static void doStart(final String[] args) {
		CommandLinePurser cl = new CommandLinePurser();
		cl.setOption(OPT_BASE_DIRECTORY, DEFAULT_BASE_DIRECTORY);
		cl.setOption(OPT_CONFIG_FILE, DEFAULT_CONFIG_FILE);
		cl.setOption(OPT_PLUGIN_FILE, DEFAULT_PLUGIN_FILE);
		cl.purse(args);

		String baseDir = cl.getOption(OPT_BASE_DIRECTORY);
		String configFile = cl.getOption(OPT_CONFIG_FILE);
		String pluginFile = cl.getOption(OPT_PLUGIN_FILE);

		Context context = new CrawlerContext(baseDir);

		CrawlerConfig config = CrawlerConfig.parse(context.getAbstractPath(configFile));
		DOMConfigurator.configure(context.getAbstractPath(config.getLogger().getConfig()));

		CrawlerServer server = null;
		server = new CrawlerServer(context, config, pluginFile);
		server.start();
	}

	private static void doStop(final String[] args) {
		CommandLinePurser cl = new CommandLinePurser();
		cl.setOption(OPT_BASE_DIRECTORY, DEFAULT_BASE_DIRECTORY);
		cl.setOption(OPT_CONFIG_FILE, DEFAULT_CONFIG_FILE);
		cl.setOption(OPT_PLUGIN_FILE, DEFAULT_PLUGIN_FILE);
		cl.purse(args);

		String baseDir = cl.getOption(OPT_BASE_DIRECTORY);
		String configFile = cl.getOption(OPT_CONFIG_FILE);

		Context context = new CrawlerContext(baseDir);

		CrawlerConfig config = CrawlerConfig.parse(context.getAbstractPath(configFile));
		DOMConfigurator.configure(context.getAbstractPath(config.getLogger().getConfig()));

		CrawlerController controller = new CrawlerController(context, config.getController());
		controller.stop();
	}

	private static void doRestart(final String[] args) {
		CommandLinePurser cl = new CommandLinePurser();
		cl.setOption(OPT_BASE_DIRECTORY, DEFAULT_BASE_DIRECTORY);
		cl.setOption(OPT_CONFIG_FILE, DEFAULT_CONFIG_FILE);
		cl.setOption(OPT_PLUGIN_FILE, DEFAULT_PLUGIN_FILE);
		cl.purse(args);

		String baseDir = cl.getOption(OPT_BASE_DIRECTORY);
		String configFile = cl.getOption(OPT_CONFIG_FILE);
		String pluginFile = cl.getOption(OPT_PLUGIN_FILE);

		Context context = new CrawlerContext(baseDir);

		CrawlerConfig config = CrawlerConfig.parse(context.getAbstractPath(configFile));
		DOMConfigurator.configure(context.getAbstractPath(config.getLogger().getConfig()));

		CrawlerController controller = new CrawlerController(context, config.getController());
		boolean result = controller.stop();

		if (result) {
			CrawlerServer server = null;
			server = new CrawlerServer(context, config, pluginFile);

			server.start();
		}
	}

}
