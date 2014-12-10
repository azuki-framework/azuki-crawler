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

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.azkfw.business.dao.DataAccessServiceException;
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
	 * ホスト情報は下記の項目を保持する。
	 * <ul>
	 * <li>id - ホストID</li>
	 * <li>name - ホスト名</li>
	 * <li>protocol - プロトコル名</li>
	 * <li>port - ポート番号</li>
	 * </ul>
	 * </p>
	 * 
	 * @return ホスト情報、対象ホストが見つからない場合、空のマップを返す。
	 */
	public Map<String, Object> lockHost() throws DataAccessServiceException, SQLException;

	/**
	 * 対象ホストを解放する。
	 * 
	 * @param aHostId ホストID
	 * @param aStatus ステータス
	 */
	public void unlockHost(final String aHostId, final int aStatus) throws DataAccessServiceException, SQLException;

	/**
	 * 対象ホストからダウンロードページの一覧を取得する。
	 * 
	 * <p>
	 * ページ情報一覧を下記の項目のマップのリストを返す。
	 * <ul>
	 * <li>id - ページID</li>
	 * <li>areas - エイリアス</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aHostId ホストID
	 * @param aPageSize ページ数
	 * @return　ページ情報一覧、対象のページがない場合、空のリストを返す。
	 */
	public List<Map<String, Object>> getDownloadPages(final String aHostId, final int aPageSize) throws DataAccessServiceException, SQLException;

	/**
	 * コンテンツのダウンロードに成功
	 * 
	 * @param aContentId
	 * @param aStatusCode
	 * @param aLength
	 * @param aType
	 * @throws DataAccessServiceException
	 * @throws SQLException
	 */
	public void downloadContent(final String aContentId, final int aStatusCode, final long aLength, final String aType)
			throws DataAccessServiceException, SQLException;

	/**
	 * コンテンツのダウンロードに失敗
	 * 
	 * @param aContentId
	 * @param aStatusCode
	 * @throws DataAccessServiceException
	 * @throws SQLException
	 */
	public void downloadContent(final String aContentId, final int aStatusCode) throws DataAccessServiceException, SQLException;

	/**
	 * コンテンツのダウンロードに失敗(例外)
	 * 
	 * @param aContentId
	 * @throws DataAccessServiceException
	 * @throws SQLException
	 */
	public void downloadErrorContent(final String aContentId) throws DataAccessServiceException, SQLException;

	/**
	 * コンテンツの解析を依頼する。
	 * 
	 * @param aContentId コンテンツID
	 * @throws DataAccessServiceException
	 * @throws SQLException
	 */
	public void requestContentParse(final String aContentId) throws DataAccessServiceException, SQLException;

	/**
	 * 解析するコンテンツ情報を取得する。
	 * <p>
	 * <ul>
	 * <li>hostId</li>
	 * <li>contentId</li>
	 * <li>contentType</li>
	 * </ul>
	 * </p>
	 * 
	 * @return
	 * @throws DataAccessServiceException
	 * @throws SQLException
	 */
	public Map<String, Object> getParseContent() throws DataAccessServiceException, SQLException;

	public Map<String, Object> getHost(final String aName, final String aProtocol, final int aPort) throws DataAccessServiceException, SQLException;

	public Map<String, Object> registHost(final String aName, final String aProtocol, final int aPort) throws DataAccessServiceException,
			SQLException;

	public void registContents(final String aHostId, final List<URL> aUrls, final Date aDate) throws DataAccessServiceException, SQLException;

	public void parseContent(final String aContentParseId) throws DataAccessServiceException, SQLException;

	public void parseErrorContent(final String aContentParseId) throws DataAccessServiceException, SQLException;
}
