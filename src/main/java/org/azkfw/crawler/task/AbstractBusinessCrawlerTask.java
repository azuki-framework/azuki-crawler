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
import java.util.HashMap;
import java.util.Map;

import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.database.DatabaseConnectionSupport;
import org.azkfw.business.logic.Logic;
import org.azkfw.business.logic.LogicManager;
import org.azkfw.plugin.PluginManager;
import org.azkfw.plugin.PluginServiceException;
import org.azkfw.util.StringUtility;

/**
 * このクラスは、ビジネス機能を実装するクローラタスククラスです。
 * <p>
 * このクラスは、ロジック機能を提供します。
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/28
 * @author Kawakicchi
 */
public abstract class AbstractBusinessCrawlerTask extends AbstractDatabaseCrawlerTask {

	/** Logics */
	private Map<String, Map<String, Logic>> logics;

	/**
	 * コンストラクタ
	 */
	public AbstractBusinessCrawlerTask() {
		super();
		logics = new HashMap<String, Map<String, Logic>>();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name 名前
	 */
	public AbstractBusinessCrawlerTask(final String name) {
		super(name);
		logics = new HashMap<String, Map<String, Logic>>();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param clazz クラス
	 */
	public AbstractBusinessCrawlerTask(final Class<?> clazz) {
		super(clazz);
		logics = new HashMap<String, Map<String, Logic>>();
	}

	/**
	 * ロジックを取得する。
	 * 
	 * @param name ロジック名
	 * @return ロジック
	 * @throws BusinessServiceException ビジネスサービス層に起因する問題が発生した場合
	 */
	protected final Logic getLogic(final String name) throws BusinessServiceException {
		return getLogic(StringUtility.EMPTY, name);
	}
	
	/**
	 * ロジックを取得する。
	 * 
	 * @param namespace 名前空間
	 * @param name ロジック名
	 * @return ロジック
	 * @throws BusinessServiceException ビジネスサービス層に起因する問題が発生した場合
	 */
	protected final Logic getLogic(final String namespace, final String name) throws BusinessServiceException {
		Logic logic = null;
		try {
			Map<String, Logic> logics = null;
			if (this.logics.containsKey(namespace)) {
				logics = this.logics.get(namespace);
			} else {
				logics = new HashMap<String, Logic>();
				this.logics.put(namespace, logics);
			}
			
			if (logics.containsKey(name)) {
				logic = logics.get(name);
			} else {
				logic = LogicManager.create(name);
				if (null != logic) {
					if (logic instanceof DatabaseConnectionSupport) {
						DatabaseConnectionSupport support = (DatabaseConnectionSupport) logic;
						support.setConnection(getConnection());
					}
					PluginManager.getInstance().support(logic);
					logic.initialize();
				}
				logics.put(name, logic);
			}
		} catch (SQLException ex) {
			throw new BusinessServiceException(ex);
		} catch (PluginServiceException ex) {
			throw new BusinessServiceException(ex);
		}
		return logic;
	}
	
	@Override
	protected void doBeforeExecute() {
		super.doBeforeExecute();
		// TODO Write doBeforeExecute code.

	}

	@Override
	protected void doAfterExecute() {
		for (Map<String, Logic> logics : this.logics.values()) {
			for (Logic logic : logics.values()) {
				logic.destroy();
			}
		}
		logics.clear();

		super.doAfterExecute();
	}

}
