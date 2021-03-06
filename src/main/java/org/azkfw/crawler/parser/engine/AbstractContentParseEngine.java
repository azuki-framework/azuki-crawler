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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.azkfw.crawler.content.Content;

/**
 * このクラスは、コンテンツに対して解析を行うエンジンを定義するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public abstract class AbstractContentParseEngine extends AbstractParseEngine {

	/** Content */
	private Content content;

	/**
	 * コンストラクタ
	 * 
	 * @param aContent コンテンツ
	 */
	public AbstractContentParseEngine(final Content aContent) {
		super();
		content = aContent;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aContent コンテンツ
	 */
	public AbstractContentParseEngine(final String aName, final Content aContent) {
		super(aName);
		content = aContent;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aContent コンテンツ
	 */
	public AbstractContentParseEngine(final Class<?> aClass, final Content aContent) {
		super(aClass);
		content = aContent;
	}

	/**
	 * コンテンツを取得する。
	 * 
	 * @return コンテンツ
	 */
	protected final Content getContent() {
		return content;
	}

	@Override
	protected final ParseEngineResult doParse() {
		return doParseContent(getContent());
	}

	/**
	 * コンテンツ解析処理を行う。
	 * <p>
	 * このメソッドをオーバーライドしコンテンツ解析処理を記述する。
	 * </p>
	 * 
	 * @return 解析結果
	 */
	protected abstract ParseEngineResult doParseContent(final Content aContent);

	/**
	 * ストリームを解放します。
	 * 
	 * @param streams ストリーム
	 */
	protected final void release(final InputStream... streams) {
		for (InputStream stream : streams) {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
		}
	}

	/**
	 * ストリームを解放します。
	 * 
	 * @param streams ストリーム
	 */
	protected final void release(final OutputStream... streams) {
		for (OutputStream stream : streams) {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
		}
	}
}
