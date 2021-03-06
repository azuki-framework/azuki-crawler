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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.azkfw.crawler.content.Content;

/**
 * このクラスは、テキストコンテンツに対して解析を行うエンジンを定義するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public abstract class AbstractTextParseEngine extends AbstractContentParseEngine {

	/** 文字コード */
	private Charset charset;

	/**
	 * コンストラクタ
	 * 
	 * @param aContent コンテンツ
	 */
	public AbstractTextParseEngine(final Content aContent) {
		super(aContent);
		charset = null;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public AbstractTextParseEngine(final Content aContent, final Charset aCharset) {
		super(aContent);
		charset = aCharset;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aContent コンテンツ
	 */
	public AbstractTextParseEngine(final String aName, final Content aContent) {
		super(aName, aContent);
		charset = null;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public AbstractTextParseEngine(final String aName, final Content aContent, final Charset aCharset) {
		super(aName, aContent);
		charset = aCharset;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aContent コンテンツ
	 */
	public AbstractTextParseEngine(final Class<?> aClass, final Content aContent) {
		super(aClass, aContent);
		charset = null;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public AbstractTextParseEngine(final Class<?> aClass, final Content aContent, final Charset aCharset) {
		super(aClass, aContent);
		charset = aCharset;
	}

	/**
	 * 文字コードを設定する。
	 * 
	 * @param aCharset 文字コード
	 */
	public final void setCharset(final Charset aCharset) {
		charset = aCharset;
	}

	/**
	 * 文字コードを取得する。
	 * 
	 * @return 文字コード
	 */
	public final Charset getCharset() {
		return charset;
	}

	@Override
	protected final ParseEngineResult doParseContent(final Content aContent) {
		return doParseTextContent(aContent);
	}

	/**
	 * テキスト解析処理を行う。
	 * <p>
	 * このメソッドをオーバーライドしテキスト解析処理を記述する。
	 * </p>
	 * 
	 * @return 解析結果
	 */
	protected abstract ParseEngineResult doParseTextContent(final Content aContent);

	protected final String getSource(final Content aContent, final Charset aCharset) {
		String source = null;

		ByteArrayOutputStream os = null;
		InputStream is = null;
		try {
			os = new ByteArrayOutputStream();
			is = aContent.getInputStream();

			int readSize;
			byte[] buf = new byte[1024];
			while (-1 != (readSize = is.read(buf, 0, 1024))) {
				if (0 == readSize)
					continue;
				os.write(buf, 0, readSize);
			}

			source = new String(os.toByteArray(), aCharset);

		} catch (IOException ex) {
			fatal(ex);
		} finally {
			release(is);
			release(os);
		}
		return source;
	}

	/**
	 * リーダを解放します。
	 * 
	 * @param readers リーダ
	 */
	protected final void release(final Reader... readers) {
		for (Reader reader : readers) {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
		}
	}
}
