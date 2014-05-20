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
package org.azkfw.crawler.context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.azkfw.persistence.context.AbstractContext;

/**
 * このクラスは、Crawler用のコンテキスト機能を実装するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/20
 * @author Kawakicchi
 */
public class CrawlerContext extends AbstractContext {

	/**
	 * ベースディレクトリ
	 */
	private String baseDir;

	/**
	 * コンストラクタ
	 */
	public CrawlerContext() {
		baseDir = "./";
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aBaseDir ベースディレクトリ
	 */
	public CrawlerContext(final String aBaseDir) {
		if (null == aBaseDir || 0 == aBaseDir.length()) {
			baseDir = "";
		} else if (!aBaseDir.endsWith("/") && !aBaseDir.endsWith("\\")) {
			baseDir = aBaseDir + "/";
		}
	}

	@Override
	public String getAbstractPath(final String aName) {
		return getFullPath(aName);
	}

	@Override
	@SuppressWarnings("resource")
	public InputStream getResourceAsStream(final String aName) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(getFullPath(aName));
		} catch (FileNotFoundException ex) {
			;
		}
		if (null == stream) {
			stream = this.getClass().getResourceAsStream(aName);
		}
		if (null == stream) {
			stream = Class.class.getResourceAsStream(aName);
		}
		return stream;
	}

	/**
	 * フルパスを取得する。
	 * 
	 * @param aName 名前
	 * @return パス
	 */
	private String getFullPath(final String aName) {
		StringBuilder path = new StringBuilder();
		if (aName.startsWith("/")) {
			// 絶対パス
			path.append(aName);
		} else {
			path.append(baseDir);
			path.append(aName);
		}
		return path.toString();
	}
}
