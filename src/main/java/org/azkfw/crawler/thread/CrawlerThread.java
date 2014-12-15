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
package org.azkfw.crawler.thread;

import java.util.Date;
import java.util.List;

import org.azkfw.crawler.config.CrawlerConfig.CrawlerThreadConfig;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.schedule.CrawlerSchedule;
import org.azkfw.crawler.task.CrawlerTask;

/**
 * このインターフェースは、クローラスレッド機能を表現するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public interface CrawlerThread {

	/**
	 * クローラスレッドIDを取得する。
	 * 
	 * @return スレッドID
	 */
	public String getId();

	/**
	 * タイトルを取得する。
	 * 
	 * @return タイトル
	 */
	public String getTitle();

	/**
	 * 概要を取得する。
	 * 
	 * @return 概要
	 */
	public String getDescription();

	/**
	 * クローラスレッドステータスを取得する。
	 * 
	 * @return ステータス
	 */
	public Status getStatus();

	/**
	 * タスクを取得する。
	 * 
	 * @return タスク
	 */
	public CrawlerTask getTask();

	/**
	 * スケジュールを取得する。
	 * 
	 * @return タスク
	 */
	public CrawlerSchedule getSchedule();

	/**
	 * スレッド開始日付を取得する。
	 * 
	 * @return 日付
	 */
	public Date getStartDate();

	/**
	 * スレッド停止日付を取得する。
	 * 
	 * @return 日付
	 */
	public Date getStopDate();

	/**
	 * スレッド設定を取得する。
	 * 
	 * @return 設定
	 */
	public CrawlerThreadConfig getConfig();

	/**
	 * タスクのログを取得する。
	 * 
	 * @return タスクログ一覧
	 */
	public List<CrawlerTaskLog> getLogs();

	/**
	 * セットアップ処理を行う。
	 * 
	 * @throws CrawlerSetupException セットアップ処理において問題が発生した場合
	 */
	public void setup() throws CrawlerSetupException;

	/**
	 * 初期化処理を行う。
	 */
	public void initialize();

	/**
	 * 解放処理を行う。
	 */
	public void release();

	/**
	 * スレッドを起動する。
	 */
	public void start();

	/**
	 * スレッドに停止要求を行う。
	 */
	public void requestStop();

	/**
	 * このEnumは、クローラスレッドのステータスを表現するEnumです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/05/12
	 * @author Kawakicchi
	 */
	public enum Status {
		/**
		 * ジョブ停止(終了リクエストがあった場合)
		 */
		stoped(1, "停止"),

		/**
		 * ジョブ実行中
		 */
		running(2, "起動中"),

		/**
		 * スリープ中（ループやタイムの場合の次回実行待ち）
		 */
		sleeping(3, "待機中"),

		/**
		 * 停止中（ループやタイムの場合の次回実行待ち）
		 */
		stoping(4, "停止中"),

		/**
		 * 異常エラー(システムエラー／ジョブからの例外エラー）
		 * <p>
		 * この状態の場合、ループ・日付指定などが以降は行われない。 また、再開もさせない。削除のみ出来る様にする。
		 * </p>
		 */
		error(9, "エラー");

		/** 状態 */
		private int status;
		/** 状態名 */
		private String name;

		private Status(final int aStatus, final String aName) {
			status = aStatus;
			name = aName;
		}

		/**
		 * ステータスを取得する。
		 * 
		 * @return ステータス
		 */
		public int getStatus() {
			return status;
		}

		/**
		 * ステータス名を取得する。
		 * 
		 * @return ステータス名
		 */
		public String getName() {
			return name;
		}
	}
}
