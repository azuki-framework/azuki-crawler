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
package org.azkfw.crawler.logic;

import java.util.List;
import java.util.Map;

import org.azkfw.business.logic.Logic;

/**
 * このインターフェースは、 Webクロール管理機能を表現するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public interface WebCrawlerManager extends Logic {

	/**
	 * 対象ホストを取得する。
	 * <p>
	 * <ul>
	 * <li>id - ホストID</li>
	 * <li>name - ホスト名</li>
	 * <li>port - ポート番号</li>
	 * </ul>
	 * </p>
	 * 
	 * @return ホスト情報
	 */
	public Map<String, Object> lockHost();

	/**
	 * 対象ホストを解放する。
	 * 
	 * @param aHostId ホストID
	 * @param aResultCode 結果コード
	 */
	public void unlockHost(final String aHostId, final int aResultCode);

	/**
	 * 対象ホストからダウンロードページの一覧を取得する。
	 * 
	 * <p>
	 * <ul>
	 * <li>id - ページID</li>
	 * <li>areas - エイリアス</li>
	 * </ul>
	 * </p>
	 * @param aHostId ホストID
	 * @param aPageSize ページ数
	 * @return　ページ情報
	 */
	public List<Map<String, Object>> getDownloadPages(final String aHostId, final int aPageSize);

}
