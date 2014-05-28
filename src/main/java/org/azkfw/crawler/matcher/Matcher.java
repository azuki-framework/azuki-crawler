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
package org.azkfw.crawler.matcher;

/**
 * このインターフェースは、文字列のマッチング機能を表現するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public interface Matcher {

	/**
	 * パターン文字列をコンパイルする。
	 * @param aPattern パターン
	 * @return 結果
	 */
	public boolean compile(final String aPattern);

	/**
	 * 対象文字列がマッチングするか判断する。
	 * @param aValue 対象文字列
	 * @return 判断
	 */
	public boolean match(final String aValue);

	/**
	 * 対象文字列でマッチングした文字列を取得する。
	 * @param aValue 対象文字列
	 * @return　文字列
	 */
	public String getMatchString(final String aValue);

}
