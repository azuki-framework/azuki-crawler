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
package org.azkfw.crawler.engine;

import java.net.URL;

import org.azkfw.crawler.CrawlInfo;
import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.downloader.engine.DownloadEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;

/**
 * このインターフェースは、クローラエンジン機能を定義したインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/12/11
 * @author kawakicchi
 */
public interface CrawlerEngine {
	
	/**
	 * ホストグループを取得する。
	 * @param aName
	 * @param aProtocol
	 * @param aPort
	 * @return 不明な場合、-1
	 */
	public int getHostGroup(final String aName, final String aProtocol, final int aPort);

	/**
	 * クロール情報を取得する。
	 * 
	 * @param url URL
	 * @return クロール情報
	 */
	public CrawlInfo getCrawlInfo(final URL url);

	/**
	 * ダウンロード対象のコンテンツか判断する。
	 * 
	 * @param url コンテンツURL
	 * @param referUrl 参照元URL
	 * @return 判断結果。ダウンロード対象の場合、<code>true</code>を返す。
	 */
	public boolean isDownloadContent(final URL url, final URL referUrl);

	/**
	 * ダウンロードエンジンを取得する。
	 * 
	 * @param url コンテンツURL
	 * @param referUrl 参照元URL
	 * @return ダウンロードエンジン。
	 */
	public DownloadEngine getDownloadEngine(final URL url, final URL referUrl);

	/**
	 * 解析対象のコンテンツか判断する。
	 * 
	 * @param url コンテンツURL
	 * @param contentType コンテンツタイプ
	 * @return 判断結果。解析対象の場合、<code>true</code>を返す。
	 */
	public boolean isParseContent(final URL url, final String contentType);

	/**
	 * 解析エンジンを取得する。
	 * 
	 * @param aUrl コンテンツURL
	 * @param contentType コンテンツタイプ
	 * @param aContent コンテンツ
	 * @return 解析エンジン
	 */
	public ParseEngine getParseEngine(final URL aUrl, final String contentType, final Content aContent);
}
