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
package org.azkfw.crawler.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.xml.sax.SAXException;

/**
 * このクラスは、クローラ設定情報を保持するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public class CrawlerConfig {

	/**
	 * クローラ設定ファイルより設定情報を解析する。
	 * 
	 * @param aFile 設定ファイル
	 * @return 設定情報
	 */
	public static CrawlerConfig parse(final String aFile) {
		CrawlerConfig config = null;
		try {
			Digester digester = new Digester();
			digester.addRule("crawler", new ObjectCreateRule(CrawlerConfig.class));

			digester.addRule("crawler/controller", new ObjectCreateRule(CrawlerControllerConfig.class));
			digester.addRule("crawler/controller", new SetPropertiesRule());
			digester.addRule("crawler/controller", new SetNextRule("setController"));

			digester.addRule("crawler/logger", new ObjectCreateRule(CrawlerLoggerConfig.class));
			digester.addRule("crawler/logger", new SetPropertiesRule());
			digester.addRule("crawler/logger", new SetNextRule("setLogger"));

			digester.addRule("crawler/manager", new ObjectCreateRule(CrawlerManagerConfig.class));
			digester.addRule("crawler/manager", new SetPropertiesRule());
			digester.addRule("crawler/manager", new SetNextRule("setManager"));

			digester.addRule("crawler/threads", new ObjectCreateRule(ArrayList.class));
			digester.addRule("crawler/threads", new SetNextRule("setThreads"));

			digester.addRule("crawler/threads/thread", new ObjectCreateRule(CrawlerThreadConfig.class));
			digester.addRule("crawler/threads/thread", new SetPropertiesRule());
			digester.addRule("crawler/threads/thread", new SetNextRule("add"));

			digester.addRule("crawler/threads/thread/task", new ObjectCreateRule(CrawlerTaskConfig.class));
			digester.addRule("crawler/threads/thread/task", new SetPropertiesRule());
			digester.addRule("crawler/threads/thread/task", new SetNextRule("setTask"));

			digester.addRule("crawler/threads/thread/task/parameter", new ObjectCreateRule(CrawlerParameterConfig.class));
			digester.addRule("crawler/threads/thread/task/parameter", new SetPropertiesRule());
			digester.addRule("crawler/threads/thread/task/parameter", new SetNextRule("addParameter"));

			digester.addRule("crawler/threads/thread/schedule", new ObjectCreateRule(CrawlerScheduleConfig.class));
			digester.addRule("crawler/threads/thread/schedule", new SetPropertiesRule());
			digester.addRule("crawler/threads/thread/schedule", new SetNextRule("setSchedule"));

			digester.addRule("crawler/threads/thread/schedule/parameter", new ObjectCreateRule(CrawlerParameterConfig.class));
			digester.addRule("crawler/threads/thread/schedule/parameter", new SetPropertiesRule());
			digester.addRule("crawler/threads/thread/schedule/parameter", new SetNextRule("addParameter"));

			config = (CrawlerConfig) digester.parse(new File(aFile));
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return config;
	}

	private CrawlerControllerConfig controller;
	private CrawlerLoggerConfig logger;
	private CrawlerManagerConfig manager;
	private List<CrawlerThreadConfig> threads;

	public void setController(final CrawlerControllerConfig aController) {
		controller = aController;
	}

	public CrawlerControllerConfig getController() {
		return controller;
	}

	public void setLogger(final CrawlerLoggerConfig aLogger) {
		logger = aLogger;
	}

	public CrawlerLoggerConfig getLogger() {
		return logger;
	}

	public void setManager(final CrawlerManagerConfig aManager) {
		manager = aManager;
	}

	public CrawlerManagerConfig getManager() {
		return manager;
	}

	public void setThreads(final List<CrawlerThreadConfig> aThreads) {
		threads = aThreads;
	}

	public List<CrawlerThreadConfig> getThreads() {
		return threads;
	}

	/**
	 * このクラスは、クローラコントローラ設定情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerControllerConfig {
		private String contextPath;
		private int port;
		private String charset;

		public void setContextpath(final String aPath) {
			contextPath = aPath;
		}

		public String getContextpath() {
			return contextPath;
		}

		public void setPort(final int aPort) {
			port = aPort;
		}

		public int getPort() {
			return port;
		}

		public void setCharset(final String aCharset) {
			charset = aCharset;
		}

		public String getCharset() {
			return charset;
		}
	}

	/**
	 * このクラスは、クローラマネージャー設定情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerManagerConfig {
		private String contextPath;
		private String basedir;
		private int port;
		private String charset;

		public void setContextpath(final String aPath) {
			contextPath = aPath;
		}

		public String getContextpath() {
			return contextPath;
		}

		public void setBasedir(final String aDir) {
			basedir = aDir;
		}

		public String getBasedir() {
			return basedir;
		}

		public void setPort(final int aPort) {
			port = aPort;
		}

		public int getPort() {
			return port;
		}

		public void setCharset(final String aCharset) {
			charset = aCharset;
		}

		public String getCharset() {
			return charset;
		}
	}

	/**
	 * このクラスは、クローラスレッド設定情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerThreadConfig {

		private String title;
		private String description;
		private boolean startup;
		private int thread;

		private CrawlerTaskConfig task;
		private CrawlerScheduleConfig schedule;

		public CrawlerThreadConfig() {
			thread = 1;
		}

		public void setTitle(final String aTitle) {
			title = aTitle;
		}

		public String getTitle() {
			return title;
		}

		public void setDescription(final String aDescription) {
			description = aDescription;
		}

		public String getDescription() {
			return description;
		}

		public void setStartup(final boolean aStartup) {
			startup = aStartup;
		}

		public boolean isStartup() {
			return startup;
		}

		public void setThread(final int aThread) {
			thread = aThread;
		}

		public int getThread() {
			return thread;
		}

		public void setTask(final CrawlerTaskConfig aTask) {
			task = aTask;
		}

		public CrawlerTaskConfig getTask() {
			return task;
		}

		public void setSchedule(final CrawlerScheduleConfig aSchedule) {
			schedule = aSchedule;
		}

		public CrawlerScheduleConfig getSchedule() {
			return schedule;
		}
	}

	/**
	 * このクラスは、クローラタスク情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerTaskConfig {

		private String classname;

		private List<CrawlerParameterConfig> parameters;

		public CrawlerTaskConfig() {
			parameters = new ArrayList<CrawlerParameterConfig>();
		}

		public void setClassname(final String aClassname) {
			classname = aClassname;
		}

		public String getClassname() {
			return classname;
		}

		public void addParameter(final CrawlerParameterConfig aParameter) {
			parameters.add(aParameter);
		}

		public List<CrawlerParameterConfig> getParameters() {
			return parameters;
		}
	}

	/**
	 * このクラスは、クローラスケジュール設定情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerScheduleConfig {

		private String classname;

		private List<CrawlerParameterConfig> parameters;

		public CrawlerScheduleConfig() {
			parameters = new ArrayList<CrawlerParameterConfig>();
		}

		public void setClassname(final String aClassname) {
			classname = aClassname;
		}

		public String getClassname() {
			return classname;
		}

		public void addParameter(final CrawlerParameterConfig aParameter) {
			parameters.add(aParameter);
		}

		public List<CrawlerParameterConfig> getParameters() {
			return parameters;
		}
	}

	/**
	 * このクラスは、クローラパラメータ設定情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerParameterConfig {
		private String key;
		private String value;
		private String file;

		public void setKey(final String aKey) {
			key = aKey;
		}

		public String getKey() {
			return key;
		}

		public void setValue(final String aValue) {
			value = aValue;
		}

		public String getValue() {
			return value;
		}

		public void setFile(final String aFile) {
			file = aFile;
		}

		public String getFile() {
			return file;
		}
	}

	/**
	 * このクラスは、クローラログ設定情報を保持するクラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/14
	 * @author Kawakicchi
	 */
	public static class CrawlerLoggerConfig {
		private String config;

		public void setConfig(final String aConfig) {
			config = aConfig;
		}

		public String getConfig() {
			return config;
		}
	}
}
