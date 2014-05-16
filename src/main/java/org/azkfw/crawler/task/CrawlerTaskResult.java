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

/**
 * このクラスは、クローラタスクの結果情報を保持するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public class CrawlerTaskResult {

	private boolean resultFlag;
	private boolean stopFlag;

	/**
	 * コンストラクタ
	 */
	public CrawlerTaskResult() {
		resultFlag = true;
		stopFlag = true;
	}

	/**
	 * 結果を設定する。
	 * 
	 * @param aResult 結果
	 */
	public void setResult(final boolean aResult) {
		resultFlag = aResult;
	}

	/**
	 * 結果を判断する。
	 * 
	 * @return 判断
	 */
	public boolean isResult() {
		return resultFlag;
	}

	/**
	 * 停止を設定する。
	 * 
	 * @param aStop 停止
	 */
	public void setStop(final boolean aStop) {
		stopFlag = aStop;
	}

	/**
	 * 停止を判断する。
	 * 
	 * @return 停止
	 */
	public boolean isStop() {
		return stopFlag;
	}

}
