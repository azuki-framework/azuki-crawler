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
package org.azkfw.crawler.schedule;

import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.lang.LoggingObject;
import org.azkfw.parameter.Parameter;
import org.azkfw.parameter.ParameterSupport;

/**
 * このクラスは、クローラのスケジュール機能を実装するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public abstract class AbstractCrawlerSchedule extends LoggingObject implements CrawlerSchedule, ParameterSupport {

	/** パラメータ */
	private Parameter parameter;

	/**
	 * コンストラクタ
	 */
	public AbstractCrawlerSchedule() {
		super(CrawlerSchedule.class);
		parameter = null;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public AbstractCrawlerSchedule(final Class<?> aClass) {
		super(aClass);
		parameter = null;
	}

	@Override
	public final void setup() throws CrawlerSetupException {
		doSetup();
	}

	@Override
	public final void initialize() {
		doInitialize();
	}

	@Override
	public final void release() {
		doRelease();
	}

	/**
	 * セットアップ処理を記述する。
	 * <p>
	 * スケジュール読み込み時に1度のみ実行される。
	 * </p>
	 * 
	 * @throws CrawlerSetupException セットアップ処理において問題が発生した場合
	 */
	protected abstract void doSetup() throws CrawlerSetupException;

	/**
	 * 初期化処理を記述する。
	 * <p>
	 * スレッドの起動毎に呼び出される。
	 * </p>
	 */
	protected abstract void doInitialize();

	/**
	 * 解放処理を記述する。
	 * <p>
	 * スレッドの停止毎に呼び出される。
	 * </p>
	 */
	protected abstract void doRelease();

	@Override
	public final void setParameter(final Parameter aParameter) {
		parameter = aParameter;
	}

	/**
	 * パラメータ情報を取得する。
	 * 
	 * @return パラメータ情報
	 */
	protected final Parameter getParameter() {
		return parameter;
	}
}
