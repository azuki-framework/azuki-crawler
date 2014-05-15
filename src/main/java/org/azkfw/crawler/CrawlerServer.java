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

import java.util.ArrayList;
import java.util.List;

import org.azkfw.crawler.config.CrawlerConfig;
import org.azkfw.crawler.config.CrawlerConfig.CrawlerThreadConfig;
import org.azkfw.crawler.control.CrawlerControlServer;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logger.LoggerObject;
import org.azkfw.crawler.manager.CrawlerManagerServer;
import org.azkfw.crawler.thread.BasicCrawlerThread;
import org.azkfw.crawler.thread.CrawlerThread;
import org.azkfw.crawler.thread.CrawlerThread.Status;

/**
 * このクラスは、クローラのサーバクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public class CrawlerServer extends LoggerObject {

	/**
	 * 設定情報
	 */
	private CrawlerConfig config;

	/**
	 * スレッド一覧
	 */
	private List<CrawlerThread> threads = new ArrayList<CrawlerThread>();

	/**
	 * 停止要求フラグ
	 */
	private boolean stopRequestFlag;

	/**
	 * コントロールサーバ
	 */
	private CrawlerControlServer controlServer;

	/**
	 * マネージャーサーバ
	 */
	private CrawlerManagerServer managerServer;

	/**
	 * コンストラクタ
	 * 
	 * @param aConfig 設定情報
	 */
	CrawlerServer(final CrawlerConfig aConfig) {
		super(CrawlerServer.class);

		config = aConfig;
	}

	/**
	 * スレッド一覧を取得する。
	 * 
	 * @return 一覧
	 */
	public List<CrawlerThread> getThreads() {
		return threads;
	}

	/**
	 * クローラサーバを起動する。
	 * 
	 * @return 結果
	 */
	public boolean start() {
		boolean result = false;

		System.out.println("Crawler server running...");

		info("Crawler starting...");
		try {

			// ControlServer start
			info("Control server starting...");
			controlServer = new CrawlerControlServer(this, config.getController());
			controlServer.start();
			info("Control server started.");

			// ManagerServer start
			info("Manager server starting...");
			managerServer = new CrawlerManagerServer(this, config.getManager());
			managerServer.start();
			info("Manager server started.");

			try {
				// Crawler thread create
				info("Crawler thread creating...");
				createThreads(); // スレッド生成
				info("Crawler thread created.");

				// Crawler thread setup
				info("Crawler thread setuping...");
				setupThreads(); // スレッドセットアップ
				info("Crawler thread setuped.");

				// Crawler thread initialize
				info("Crawler thread initializing...");
				initializeThreads(); // スレッド初期化
				info("Crawler thread initialized.");

				// Crawler thread start
				info("Crawler thread starting...");
				startThreads(); // スレッド開始
				info("Crawler thread started.");

				info("Crawler started.");

				// Main
				stopRequestFlag = false;
				while (!stopRequestFlag) {
					// メインループ
					Thread.sleep(1000);
				}

				info("Crawler stoping...");

				// Crawler thread stop
				info("Crawler thread stopping...");
				while (!isStopThreads()) { // スレッド停止待ち
					debug("Crawler thread stopping(sleep)...");
					Thread.sleep(500);
				}
				info("Crawler thread stoped.");

			} catch (CrawlerSetupException ex) {
				fatal(ex);

			} finally {
				// Crawler thread release
				info("Crawler thread releasing...");
				releaseThreads();
				info("Crawler thread released.");
			}

			// ManagerServer stop
			info("Manager server stoping...");
			managerServer.stop(1);
			info("Manager server stoped.");

			// ControlServer stop
			info("Control server stoping...");
			controlServer.stop(1);
			info("Control server stoped.");

			result = true;
		} catch (InterruptedException ex) {
			fatal(ex);
		}

		info("Crawler stoped.");

		System.out.println("Crawler server stop.");

		return result;
	}

	/**
	 * クローラサーバに停止要求を行う。
	 */
	public void requestStop() {
		stopRequestFlag = true;
		requestStopThreads();
	}

	/**
	 * スレッド設定のスレッドを作成する。
	 */
	private void createThreads() {
		synchronized (threads) {
			threads.clear();
			List<CrawlerThreadConfig> threadConfigs = config.getThreads();
			for (CrawlerThreadConfig threadConfig : threadConfigs) {

				for (int i = 0; i < threadConfig.getThread(); i++) {
					CrawlerThread thread = new BasicCrawlerThread(threadConfig);
					threads.add(thread);
				}

			}
		}
	}

	/**
	 * すべてのスレッドのセットアップ処理を行う。
	 */
	private void setupThreads() throws CrawlerSetupException {
		for (CrawlerThread thread : threads) {
			thread.setup();
		}
	}

	/**
	 * すべてのスレッドの初期化処理を行う。
	 */
	private void initializeThreads() {
		for (CrawlerThread thread : threads) {
			thread.initialize();
		}
	}

	/**
	 * すべてのスレッドの解放処理を行う。
	 */
	private void releaseThreads() {
		for (CrawlerThread thread : threads) {
			thread.release();
		}
	}

	/**
	 * スタートアップ設定が行われているスレッドをすべて起動する。
	 */
	private void startThreads() {
		for (CrawlerThread thread : threads) {
			if (thread.getConfig().isStartup()) {
				thread.start();
			}
		}
	}

	/**
	 * すべてのスレッドに停止要求を行う。
	 */
	private void requestStopThreads() {
		for (CrawlerThread thread : threads) {
			if (thread.getStatus() == Status.running || thread.getStatus() == Status.sleeping) {
				thread.requestStop();
			}
		}
	}

	/**
	 * すべてのスレッドが停止しているか判断する。
	 * 
	 * @return 判断
	 */
	private boolean isStopThreads() {
		synchronized (threads) {
			for (CrawlerThread thread : threads) {
				if (thread.getStatus() == Status.running || thread.getStatus() == Status.sleeping) {
					return false;
				}
			}
		}
		return true;
	}

}