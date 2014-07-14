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
package org.azkfw.crawler.downloader.engine;

import java.io.File;
import java.net.URL;

import org.azkfw.lang.LoggingObject;

/**
 * このクラスは、ダウンロードエンジンを定義するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/09
 * @author Kawakicchi
 */
public abstract class AbstractDownloadEngine extends LoggingObject implements DownloadEngine {

	/**
	 * コンストラクタ
	 */
	public AbstractDownloadEngine() {
		super(DownloadEngine.class);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 */
	public AbstractDownloadEngine(final String aName) {
		super(aName);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public AbstractDownloadEngine(final Class<?> aClass) {
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
	public final DownloadEngineResult download(final URL aTargetUrl, final File aDestFile) {
		return doDownload(aTargetUrl, aDestFile);
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
	 * ダウンロード処理を行う。
	 * <p>
	 * このメソッドをオーバーライドしダウンロード処理を記述する。
	 * </p>
	 * 
	 * @param aTargetUrl URL
	 * @param aDestFile File
	 * @return ダウンロード結果
	 */
	protected abstract DownloadEngineResult doDownload(final URL aTargetUrl, final File aDestFile);
}
