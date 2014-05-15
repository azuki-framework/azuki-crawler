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

import org.azkfw.crawler.lang.CrawlerSetupException;

/**
 * このインターフェースは、クローラタスク機能を表現するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public interface CrawlerTask {

	/**
	 * タスク名を取得する。
	 * 
	 * @return タスク名
	 */
	public String getName();

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
	 * タスクを実行する。
	 * 
	 * @return 結果
	 */
	public CrawlerTaskResult execute();

	/**
	 * タスクに停止要求する。
	 */
	public void requestStop();
}
