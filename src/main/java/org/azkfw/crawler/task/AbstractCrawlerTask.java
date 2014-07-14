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

import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.performance.Performance;
import org.azkfw.crawler.task.support.CrawlerTaskControlSupport;
import org.azkfw.lang.LoggingObject;
import org.azkfw.persistence.parameter.Parameter;
import org.azkfw.persistence.parameter.ParameterSupport;

/**
 * このクラスは、クローラタスク機能の実装を行うための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public abstract class AbstractCrawlerTask extends LoggingObject implements CrawlerTask, CrawlerTaskControlSupport, ParameterSupport {

	/**
	 * 停止要求フラグ
	 */
	private boolean requestStopFlag;

	/** パラメータ */
	private Parameter parameter;

	/**
	 * コンストラクタ
	 */
	public AbstractCrawlerTask() {
		super(CrawlerTask.class);
		parameter = null;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName Name
	 */
	public AbstractCrawlerTask(final String aName) {
		super(aName);
		parameter = null;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public AbstractCrawlerTask(final Class<?> aClass) {
		super(aClass);
		parameter = null;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public final void setup() throws CrawlerSetupException {
		doSetup();
	}

	@Override
	public final void startup() throws CrawlerServiceException {
		doStartup();
	}

	@Override
	public final void shutdown() throws CrawlerServiceException {
		doShutdown();
	}

	@Override
	public final void initialize() throws CrawlerServiceException {
		doInitialize();
		requestStopFlag = false;
	}

	@Override
	public final void release() throws CrawlerServiceException {
		doRelease();
	}

	@Override
	public final CrawlerTaskResult execute() throws CrawlerServiceException {
		CrawlerTaskResult result = null;

		try {
			doBeforeExecute();

			Performance p = new Performance(getName());
			p.start();

			result = doExecute();

			p.stop();

			doAfterExecute();
		} catch (CrawlerServiceException ex) {
			throw ex;
		} finally {

		}

		return result;
	}

	@Override
	public final void stop() {
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
	 * <p>
	 * クローラサーバ起動時に１度のみ実行される。
	 * </p>
	 * 
	 * @throws CrawlerSetupException セットアップ処理において問題が発生した場合
	 */
	protected abstract void doSetup() throws CrawlerSetupException;

	/**
	 * スタートアップ処理を行う。
	 * <p>
	 * クローラスレッド起動時に実行される。
	 * </p>
	 * 
	 * @throws CrawlerServiceException クローラ機能に起因する問題が発生した場合
	 */
	protected abstract void doStartup() throws CrawlerServiceException;

	/**
	 * シャットダウン処理を行う。
	 * <p>
	 * クローラスレッド停止時に実行される。
	 * </p>
	 * 
	 * @throws CrawlerServiceException クローラ機能に起因する問題が発生した場合
	 */
	protected abstract void doShutdown() throws CrawlerServiceException;

	/**
	 * 初期化処理を行う。
	 * <p>
	 * タスク実行前に実行される。
	 * </p>
	 * 
	 * @throws CrawlerServiceException クローラ機能に起因する問題が発生した場合
	 */
	protected abstract void doInitialize() throws CrawlerServiceException;

	/**
	 * 解放処理を行う。
	 * <p>
	 * タスク実行後に実行される。
	 * </p>
	 * 
	 * @throws CrawlerServiceException クローラ機能に起因する問題が発生した場合
	 */
	protected abstract void doRelease() throws CrawlerServiceException;

	/**
	 * タスク実行直前の処理を行う。
	 * <p>
	 * タスク実行直前に処理を行いたい場合、このメソッドをオーバーライドしスーパークラスの同メソッドを呼び出した後で処理を記述すること。
	 * </p>
	 */
	protected void doBeforeExecute() {

	}

	/**
	 * タスク実行直後の処理を行う。
	 * <p>
	 * タスク実行直後に処理を行いたい場合、このメソッドをオーバーライドし処理を記述した後でスーバークラスの同メソッドを呼び出すこと。
	 * </p>
	 */
	protected void doAfterExecute() {

	}

	/**
	 * タスクを実行する。
	 * 
	 * @return 実行結果
	 * @throws CrawlerServiceException クローラ機能に起因する問題が発生した場合
	 */
	protected abstract CrawlerTaskResult doExecute() throws CrawlerServiceException;

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

	/**
	 * ログを出力する。
	 * 
	 * @param aMessage メッセージ
	 */
	protected final void log(final String aMessage) {
		@SuppressWarnings("unused")
		String message = aMessage;
	}

	/**
	 * ログを出力する。
	 * 
	 * @param aMessage メッセージ
	 * @param objs メッセージ
	 */
	protected final void log(final String aMessage, final Object... objs) {
		@SuppressWarnings("unused")
		String message = String.format(aMessage, objs);
	}
}
