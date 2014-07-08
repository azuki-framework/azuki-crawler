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

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.content.FileContent;
import org.azkfw.util.StringUtility;

/**
 * このクラスは、簡易的なHTMLテキスト解析を行う解析エンジンです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public final class SampleHtmlParseEngine extends SimpleHtmlParseEngine {

	/**
	 * メイン関数
	 * 
	 * @param args 引数
	 */
	public static void main(final String[] args) {
		//Content content = new FileContent(new File("C:\\temp\\bbb.html"));
		//ParseEngine engin = new SampleHtmlTextParseEngine("http://yahoo.co.jp", content);
		Content content = new FileContent(new File("C:\\html\\6\\6.html"));
		ParseEngine engin = new SampleHtmlParseEngine("http://tabelog.com/osaka/A2704/A270402/27041365", content);

		engin.initialize();
		engin.parse();
		engin.release();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	public SampleHtmlParseEngine(final String aUrl, final Content aContent) {
		super(SampleHtmlParseEngine.class, aUrl, aContent);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public SampleHtmlParseEngine(final String aUrl, final Content aContent, final Charset aCharset) {
		super(SampleHtmlParseEngine.class, aUrl, aContent, aCharset);
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected void doFindTitle(final String aTitle) {
		System.out.println("Title  : " + aTitle);
	}

	@Override
	protected void doFindDescription(final String aDescription) {
		System.out.println("Descrip: " + aDescription);
	}

	@Override
	protected void doFindKeywords(final List<String> aKeywords) {
		for (String keyword : aKeywords) {
			System.out.println("Keyword: " + keyword);
		}
	}

	@Override
	protected void doFindAnchor(final URL aUrl, final List<String> aInnerTexts) {
		System.out.println("Anchor : " + aUrl.toExternalForm());
		for (String text : aInnerTexts) {
			System.out.println("         " + text);
		}
	}

	@Override
	protected void doFindImage(final URL aUrl) {
		System.out.println("Image  : " + aUrl.toExternalForm());
	}

	@Override
	protected void doFindScript(final URL aUrl, final String aInnerText) {
		if (null != aUrl) {
			System.out.println("Scipt  : " + aUrl.toExternalForm());
		}
		if (StringUtility.isNotEmpty(aInnerText)) {
			System.out.println("Scipt  : " + aInnerText);
		}
	}

	@Override
	protected void doFindLink(final URL aUrl) {
		System.out.println("Link   : " + aUrl.toExternalForm());
	}

	@Override
	protected void doFindText(final String aText) {
		System.out.println("Text   : " + aText);
	}

}
