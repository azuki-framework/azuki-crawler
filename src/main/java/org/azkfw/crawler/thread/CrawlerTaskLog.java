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

/**
 * このクラスは、クローラタスクの実行ログ情報を保持するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/06/02
 * @author Kawakicchi
 */
public class CrawlerTaskLog {

	/** 開始日時 */
	private Date startDate;
	/** 停止日時 */
	private Date stopDate;

	/**
	 * コンストラクタ
	 */
	public CrawlerTaskLog() {

	}

	/**
	 * 開始日時を設定する。
	 * 
	 * @param aDate 日時
	 */
	public void setStartDate(final Date aDate) {
		startDate = aDate;
	}

	/**
	 * 開始日時を取得する。
	 * 
	 * @return 開始日時
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * 停止日時を設定する。
	 * 
	 * @param aDate 日時
	 */
	public void setStopDate(final Date aDate) {
		stopDate = aDate;
	}

	/**
	 * 停止日時を取得する。
	 * 
	 * @return 日時
	 */
	public Date getStopDate() {
		return stopDate;
	}
}
