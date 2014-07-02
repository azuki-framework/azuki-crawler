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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.azkfw.util.URLUtility;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public class HtmlTextParseEngine extends TextParseEngine {

	public static void main(final String[] args) {
		ParseEngine engine = new HtmlTextParseEngine("http://yahoo.co.jp");

		try {
			InputStream stream = new FileInputStream(new File("C:\\temp\\aaa.html"));
			engine.parse(stream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String url;

	public HtmlTextParseEngine(final String aUrl) {
		url = aUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.azkfw.crawler.parser.engine.AbstractParseEngine#doInitialize()
	 */
	@Override
	protected void doInitialize() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.azkfw.crawler.parser.engine.AbstractParseEngine#doRelease()
	 */
	@Override
	protected void doRelease() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.azkfw.crawler.parser.engine.AbstractParseEngine#doParse()
	 */
	@Override
	protected void doParse(final InputStream aStream) throws IOException {
		// TODO Auto-generated method stub

		BufferedReader br = new BufferedReader(new InputStreamReader(aStream, "UTF-8"));
		ParserDelegator pd = new ParserDelegator();
		MyParserCallback cb = new MyParserCallback(url);
		pd.parse(br, cb, true);

		if (null != cb.getCharset() && !"UTF-8".equals(cb.getCharset().toUpperCase())) {

		}

		Map<String, Integer> wordCounts = cb.getWordCounts();
		for (String key : wordCounts.keySet()) {
			Integer count = wordCounts.get(key);
			System.out.println(String.format("%5d %s", count, key));
		}

		Map<String, Integer> urlCounts = cb.getUrlCounts();
		for (String key : urlCounts.keySet()) {
			Integer count = urlCounts.get(key);
			System.out.println(String.format("%5d %s", count, key));
		}
	}

	public class MyParserCallback extends ParserCallback {

		private String originalURL;

		private String charset;

		private Map<String, Integer> wordCounts;
		private Map<String, Integer> urlCounts;

		public MyParserCallback(final String aUrl) {
			super();
			originalURL = aUrl;

			wordCounts = new HashMap<String, Integer>();
			urlCounts = new HashMap<String, Integer>();
		}

		public String getCharset() {
			return charset;
		}

		private void addWord(final String aWord) {
			// System.out.println(aWord);

			Tokenizer tokenizer = Tokenizer.builder().build();
			List<Token> tokens = tokenizer.tokenize(aWord);

			for (Token token : tokens) {
				//				System.out.println("==================================================");
				//				System.out.println("allFeatures : " + token.getAllFeatures());
				//				System.out.println("partOfSpeech : " + token.getPartOfSpeech());
				//				System.out.println("position : " + token.getPosition());
				//				System.out.println("reading : " + token.getReading());
				//				System.out.println("surfaceFrom : " + token.getSurfaceForm());
				//				System.out.println("allFeaturesArray : " + Arrays.asList(token.getAllFeaturesArray()));
				//				System.out.println("辞書にある言葉? : " + token.isKnown());
				//				System.out.println("未知語? : " + token.isUnknown());
				//				System.out.println("ユーザ定義? : " + token.isUser());
				String word = token.getAllFeaturesArray()[6];

				if (wordCounts.containsKey(word)) {
					Integer i = wordCounts.get(word);
					wordCounts.put(word, i + 1);
				} else {
					wordCounts.put(word, Integer.valueOf(1));
				}
			}
		}

		public Map<String, Integer> getWordCounts() {
			return wordCounts;
		}

		public Map<String, Integer> getUrlCounts() {
			return urlCounts;
		}

		private void addLink(final String aUrl) {
			try {
				String url = URLUtility.get(originalURL, aUrl);

				if (urlCounts.containsKey(url)) {
					Integer i = urlCounts.get(url);
					urlCounts.put(url, i + 1);
				} else {
					urlCounts.put(url, Integer.valueOf(1));
				}

			} catch (MalformedURLException ex) {
				System.out.println("ERROR : " + aUrl);
				ex.printStackTrace();
			}
		}

		private void addImage(final String aUrl) {

		}

		public void handleSimpleTag(Tag tag, MutableAttributeSet attr, int pos) {
			if (tag.equals(HTML.Tag.IMG)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				addImage(src);

			} else if (tag.equals(HTML.Tag.META)) {
				String httpEquiv = (String) attr.getAttribute(HTML.Attribute.HTTPEQUIV);
				if (null != httpEquiv && "content-type".equals(httpEquiv.toLowerCase())) {
					String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
					if (null != content) {
						int index = content.toLowerCase().indexOf("charset");
						if (-1 != index) {
							index = content.indexOf("=", index + 7);
							if (-1 != index) {
								charset = content.substring(index + 1).trim();
							}
						}
					}
				}

			} else if (tag.equals(HTML.Tag.AREA)) {
				String src = (String) attr.getAttribute(HTML.Attribute.HREF);
				//an.addSrc(src);
			} else if (tag.equals(HTML.Tag.BASE)) {
				String src = (String) attr.getAttribute(HTML.Attribute.HREF);
				//an.addSrc(src);
			} else if (tag.equals(HTML.Tag.LINK)) {
				String src = (String) attr.getAttribute(HTML.Attribute.HREF);
				//an.addSrc(src);
			} else if (tag.equals("BGSOUND")) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				//an.addSrc(src);
			} else if (tag.equals("EMBED")) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				//an.addSrc(src);
			}
			super.handleSimpleTag(tag, attr, pos);
		}

		public void handleStartTag(Tag tag, MutableAttributeSet attr, int pos) {
			if (tag.equals(HTML.Tag.A)) {
				String src = (String) attr.getAttribute(HTML.Attribute.HREF);
				addLink(src);
			} else if (tag.equals(HTML.Tag.SCRIPT)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				//an.addSrc(src);				
			} else if (tag.equals(HTML.Tag.FRAME)) {
			} else if (tag.equals(HTML.Tag.OBJECT)) {
				String src = (String) attr.getAttribute(HTML.Attribute.DATA);
				//an.addSrc(src);				
			} else if (tag.equals(HTML.Tag.APPLET)) {
				String src = (String) attr.getAttribute(HTML.Attribute.CODE);
				//an.addSrc(src);				
			}
			super.handleStartTag(tag, attr, pos);
		}

		public void handleText(char[] data, int pos) {
			addWord(String.copyValueOf(data));
		}

	}
}
