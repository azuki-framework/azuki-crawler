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

import java.sql.SQLException;

import org.azkfw.persistence.database.DatabaseConnection;
import org.azkfw.persistence.database.DatabaseConnectionManager;
import org.azkfw.persistence.database.DatabaseSource;

/**
 * このクラスは、データベース機能を実装するクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/28
 * @author Kawakicchi
 */
public abstract class AbstractDatabaseCrawlerTask extends AbstractPersistenceCrawlerTask {

	/**
	 * データベースソース
	 */
	private DatabaseSource source;

	/**
	 * コネクション
	 */
	private DatabaseConnection myConnection;

	/**
	 * コンストラクタ
	 */
	public AbstractDatabaseCrawlerTask() {
		super();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName Name
	 */
	public AbstractDatabaseCrawlerTask(final String aName) {
		super(aName);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass Class
	 */
	public AbstractDatabaseCrawlerTask(final Class<?> aClass) {
		super(aClass);
	}

	@Override
	protected void doBeforeExecute() {
		super.doBeforeExecute();

	}

	@Override
	protected void doAfterExecute() {
		try {
			if (null != myConnection) {
				myConnection.getConnection().commit();
			}
		} catch (SQLException ex) {
			fatal(ex);
		}
		if (null != source && null != myConnection) {
			try {
				source.returnConnection(myConnection);
			} catch (SQLException ex) {
				warn(ex);
			}
			myConnection = null;
			source = null;
		}

		super.doAfterExecute();
	}

	/**
	 * コネクションを取得する。
	 * 
	 * @return コネクション
	 * @throws SQL実行時に問題が発生した場合
	 */
	protected final DatabaseConnection getConnection() throws SQLException {
		if (null == myConnection) {
			source = DatabaseConnectionManager.getSource();
			myConnection = source.getConnection();
			if (null != myConnection) {
				myConnection.getConnection().setAutoCommit(false);
			}
		}
		return myConnection;
	}

	/**
	 * コミット処理を行う。
	 * 
	 * @throws SQLException SQL実行中に問題が発生した場合
	 */
	protected final void commit() throws SQLException {
		DatabaseConnection connection = getConnection();
		if (null != connection) {
			connection.getConnection().commit();
		}
	}

	/**
	 * ロールバック処理を行う。
	 * 
	 * @throws SQLException SQL実行中に問題が発生した場合
	 */
	protected final void rollback() throws SQLException {
		DatabaseConnection connection = getConnection();
		if (null != connection) {
			connection.getConnection().rollback();
		}
	}
}
