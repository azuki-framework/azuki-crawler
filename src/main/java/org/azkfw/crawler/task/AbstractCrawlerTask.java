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

import java.util.HashMap;
import java.util.Map;

import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logger.LoggerObject;
import org.azkfw.crawler.parameter.ParameterSupport;
import org.azkfw.crawler.performance.Performance;

/**
 * このクラスは、クローラタスク機能の実装を行うための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public abstract class AbstractCrawlerTask extends LoggerObject implements CrawlerTask, ParameterSupport {

	/**
	 * パラメータ
	 */
	private Map<String, Object> parameters;

	/**
	 * 停止要求フラグ
	 */
	private boolean requestStopFlag;

	/**
	 * コンストラクタ
	 */
	public AbstractCrawlerTask() {
		super(CrawlerTask.class);
		parameters = new HashMap<String, Object>();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public AbstractCrawlerTask(final Class<?> aClass) {
		super(aClass);
		parameters = new HashMap<String, Object>();
	}

	@Override
	public final void setup() throws CrawlerSetupException {
		doSetup();
	}

	@Override
	public final void initialize() {
		doInitialize();
		requestStopFlag = false;
	}

	@Override
	public final void release() {
		doRelease();
	}

	@Override
	public final CrawlerTaskResult execute() {
		CrawlerTaskResult result = null;

		Performance p = new Performance(getName());
		p.start();
		result = doExecute();
		p.stop();

		return result;
	}

	@Override
	public final void requestStop() {
		requestStopFlag = true;
	}

	/**
	 * 停止要求があるか判断する。
	 * 
	 * @return 判断
	 */
	protected final boolean isRequestStop() {
		return requestStopFlag;
	}

	/**
	 * セットアップ処理を行う。
	 * 
	 * @throws CrawlerSetupException セットアップ処理において問題が発生した場合
	 */
	protected abstract void doSetup() throws CrawlerSetupException;

	/**
	 * 初期化処理を行う。
	 */
	protected abstract void doInitialize();

	/**
	 * 解放処理を行う。
	 */
	protected abstract void doRelease();

	/**
	 * タスクを実行する。
	 * 
	 * @return 実行結果
	 */
	protected abstract CrawlerTaskResult doExecute();

	@Override
	public final void addParameter(final String aKey, final Object aValue) {
		parameters.put(aKey, aValue);
	}

	@Override
	public final void addParameters(final Map<String, Object> aMap) {
		parameters.putAll(aMap);
	}

	protected final Object getParameter(final String aKey) {
		return parameters.get(aKey);
	}

	protected final String getParameter(final String aKey, final String aDefault) {
		String result = aDefault;
		if (parameters.containsKey(aKey)) {
			Object obj = parameters.get(aKey);
			if (null == obj) {
				result = null;
			} else if (obj instanceof String) {
				result = (String) obj;
			} else {
				result = obj.toString();
			}
		}
		return result;
	}

	protected final int getParameter(final String aKey, final int aDefault) {
		int result = aDefault;
		if (parameters.containsKey(aKey)) {
			Object obj = parameters.get(aKey);
			if (null != obj) {
				if (obj instanceof Integer) {
					result = ((Integer) obj).intValue();
				} else {
					try {
						result = Integer.parseInt(obj.toString());
					} catch (NumberFormatException ex) {
						error("Integer parse error.[Key: " + aKey + "; value: " + obj.toString() + "]", ex);
					}
				}
			}
		}
		return result;
	}

	protected final long getParameter(final String aKey, final long aDefault) {
		long result = aDefault;
		if (parameters.containsKey(aKey)) {
			Object obj = parameters.get(aKey);
			if (null != obj) {
				if (obj instanceof Long) {
					result = ((Long) obj).intValue();
				} else {
					try {
						result = Long.parseLong(obj.toString());
					} catch (NumberFormatException ex) {
						error("Long parse error.[Key: " + aKey + "; value: " + obj.toString() + "]", ex);
					}
				}
			}
		}
		return result;
	}
}
