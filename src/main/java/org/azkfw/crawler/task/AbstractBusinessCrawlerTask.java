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
import org.azkfw.business.logic.Logic;
import org.azkfw.business.logic.LogicManager;
import org.azkfw.business.property.Property;
import org.azkfw.business.property.PropertyManager;
import org.azkfw.business.property.PropertySupport;
import org.azkfw.context.ContextSupport;
import org.azkfw.persistence.database.DatabaseConnectionSupport;
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

	/**
	 * Logics
	 */
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
	 * @param aName Name
	 */
	public AbstractBusinessCrawlerTask(final String aName) {
		super(aName);
		logics = new HashMap<String, Map<String, Logic>>();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass Class
	 */
	public AbstractBusinessCrawlerTask(final Class<?> aClass) {
		super(aClass);
		logics = new HashMap<String, Map<String, Logic>>();
	}

	@Override
	protected void doBeforeExecute() {
		super.doBeforeExecute();
		// TODO Write doBeforeExecute code.

	}

	@Override
	protected void doAfterExecute() {
		for (String namespace : logics.keySet()) {
			Map<String, Logic> ls = logics.get(namespace);
			for (String name : ls.keySet()) {
				ls.get(name).destroy();
			}
		}
		logics.clear();

		super.doAfterExecute();
	}

	/**
	 * ロジックを取得する。
	 * 
	 * @param aName ロジック名
	 * @return ロジック
	 * @throws BusinessServiceException ビジネスサービス層に起因する問題が発生した場合
	 */
	protected final Logic getLogic(final String aName) throws BusinessServiceException {
		return getLogic(StringUtility.EMPTY, aName);
	}

	/**
	 * ロジックを取得する。
	 * 
	 * @param aNamespace 名前空間
	 * @param aName ロジック名
	 * @return ロジック
	 * @throws BusinessServiceException ビジネスサービス層に起因する問題が発生した場合
	 */
	protected final Logic getLogic(final String aNamespace, final String aName) throws BusinessServiceException {
		Logic logic = null;
		try {

			Map<String, Logic> ls = null;
			if (logics.containsKey(aNamespace)) {
				ls = logics.get(aNamespace);
			} else {
				ls = new HashMap<String, Logic>();
				logics.put(aNamespace, ls);
			}

			if (ls.containsKey(aName)) {
				logic = ls.get(aName);
			} else {
				logic = LogicManager.create(aNamespace, aName);
				if (null != logic) {
					doLogicSupport(logic);
					logic.initialize();
				}
				ls.put(aName, logic);
			}

		} catch (SQLException ex) {
			throw new BusinessServiceException(ex);
		}
		return logic;
	}

	/**
	 * ロジックにサポートを行う。
	 * <p>
	 * ロジックに新機能を追加したい場合、このメソッドをオーバーライドしスーパークラスの同メソッドを呼び出した後に追加機能をサポートすること。
	 * </p>
	 * 
	 * @param aLogic ロジック
	 * @throws SQLException SQL操作時に問題が発生した場合
	 */
	protected void doLogicSupport(final Logic aLogic) throws SQLException {
		if (aLogic instanceof ContextSupport) {
			((ContextSupport) aLogic).setContext(getContext());
		}
		if (aLogic instanceof PropertySupport) {
			Property property = PropertyManager.get(aLogic.getClass());
			if (null == property) {
				property = PropertyManager.load(aLogic.getClass(), getContext());
			}
			((PropertySupport) aLogic).setProperty(property);
		}
		if (aLogic instanceof DatabaseConnectionSupport) {
			((DatabaseConnectionSupport) aLogic).setConnection(getConnection());
		}
	}
}
