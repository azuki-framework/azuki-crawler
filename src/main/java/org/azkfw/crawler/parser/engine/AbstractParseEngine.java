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
package org.azkfw.crawler.parser.engine;

import org.azkfw.log.LoggingObject;

/**
 * このクラスは、解析エンジンを定義するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public abstract class AbstractParseEngine extends LoggingObject implements ParseEngine {

	/**
	 * コンストラクタ
	 */
	public AbstractParseEngine() {
		super(ParseEngine.class);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 */
	public AbstractParseEngine(final String aName) {
		super(aName);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public AbstractParseEngine(final Class<?> aClass) {
		super(aClass);
	}

	@Override
	public final void initialize() {
		doInitialize();
	}

	@Override
	public final void release() {
		doRelease();
	}

	@Override
	public final ParseEngineResult parse() {
		return doParse();
	}

	/**
	 * 初期化処理を記述する。
	 * <p>
	 * このメソッドをオーバーライドし初期化処理を記述する。
	 * </p>
	 */
	protected abstract void doInitialize();

	/**
	 * 解放処理を行う。
	 * <p>
	 * このメソッドをオーバーライドし解放処理を記述する。
	 * </p>
	 */
	protected abstract void doRelease();

	/**
	 * 解析処理を行う。
	 * <p>
	 * このメソッドをオーバーライドし解析処理を記述する。
	 * </p>
	 * 
	 * @return 解析結果
	 */
	protected abstract ParseEngineResult doParse();
}
