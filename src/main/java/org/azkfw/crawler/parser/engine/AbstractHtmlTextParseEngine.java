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

import java.nio.charset.Charset;

import org.azkfw.crawler.content.Content;

/**
 * このクラスは、HTMLテキスト解析を行うためのエンジンを定義するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public abstract class AbstractHtmlTextParseEngine extends AbstractTextParseEngine {

	/**
	 * コンストラクタ
	 * 
	 * @param aContent コンテンツ
	 */
	public AbstractHtmlTextParseEngine(final Content aContent) {
		super(aContent);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public AbstractHtmlTextParseEngine(final Content aContent, final Charset aCharset) {
		super(aContent, aCharset);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aContent コンテンツ
	 */
	public AbstractHtmlTextParseEngine(final String aName, final Content aContent) {
		super(aName, aContent);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public AbstractHtmlTextParseEngine(final String aName, final Content aContent, final Charset aCharset) {
		super(aName, aContent, aCharset);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aContent コンテンツ
	 */
	public AbstractHtmlTextParseEngine(final Class<?> aClass, final Content aContent) {
		super(aClass, aContent);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public AbstractHtmlTextParseEngine(final Class<?> aClass, final Content aContent, final Charset aCharset) {
		super(aClass, aContent, aCharset);
	}

	@Override
	protected final boolean doParseTextContent(final Content aContent) {
		return doParseHtmlTextContent(aContent);
	}

	/**
	 * HTML解析処理を行う。
	 * <p>
	 * このメソッドをオーバーライドしHTML解析処理を記述する。
	 * </p>
	 * 
	 * @return 解析結果
	 */
	protected abstract boolean doParseHtmlTextContent(final Content aContent);
}
