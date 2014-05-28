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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public class HtmlTextParseEngine extends TextParseEngine {

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
	}

	public class MyParserCallback extends ParserCallback {

		private String originalURL;
		private String areasURL;
		
		private String charset;

		public MyParserCallback(final String aUrl) {
			super();

			originalURL = aUrl;
			areasURL = areas(originalURL);
		}
		
		public String getCharset() {
			return charset;
		}

		public void handleSimpleTag(Tag tag, MutableAttributeSet attr, int pos) {
			if (tag.equals(HTML.Tag.IMG)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				String url = full(areasURL, src);
				if (null == url) {
					url = String.format("#(%s)", src);
				} else {
					url = String.format("%s - %s - %s", url, areasURL, src);
				}
				System.out.println("IMAGE : " + url);
			} else if (tag.equals(HTML.Tag.META)) {
				String httpEquiv = (String) attr.getAttribute(HTML.Attribute.HTTPEQUIV);
				if (null != httpEquiv && "content-type".equals(httpEquiv.toLowerCase())) {
					String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
					if (null != content) {
						int index = content.toLowerCase().indexOf("charset");
						if (-1 != index) {
							index = content.indexOf("=", index+7);
							if (-1 != index) {
								charset = content.substring(index+1).trim();
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
				if (null != src && 0 < src.length()) {
					String url = full(areasURL, src);
					if (null == url) {
						url = String.format("#(%s)", src);
					} else {
						url = String.format("%s - %s - %s", url, areasURL, src);
					}
					System.out.println("LINK  : " + url);
				}
			} else if (tag.equals(HTML.Tag.SCRIPT)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				//an.addSrc(src);				
			} else if (tag.equals(HTML.Tag.FRAME)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				String url = full(areasURL, src);
				if (null == url) {
					url = String.format("#(%s)", src);
				} else {
					url = String.format("%s - %s - %s", url, areasURL, src);
				}
				System.out.println("SCRPT : " + url);
			} else if (tag.equals(HTML.Tag.OBJECT)) {
				String src = (String) attr.getAttribute(HTML.Attribute.DATA);
				String url = full(areasURL, src);
				if (null == url) {
					url = String.format("#(%s)", src);
				} else {
					url = String.format("%s - %s - %s", url, areasURL, src);
				}
				System.out.println("OBJCT : " + url);
			} else if (tag.equals(HTML.Tag.APPLET)) {
				String src = (String) attr.getAttribute(HTML.Attribute.CODE);
				//an.addSrc(src);				
			}
			super.handleStartTag(tag, attr, pos);
		}
		
		public void handleText(char[] data, int pos) {
			System.out.println("WORD  : " + String.copyValueOf(data));
		}

		private String areas(final String aUrl) {
			String url = null;
			// javascript:void(0);
			int index = aUrl.indexOf("://");
			if (-1 != index) {
				index += 3;
				index = aUrl.indexOf("/", index);
				if (-1 == index) {
					// http://localhost -> http://localhost/
					url = aUrl + "/";
				} else {
					int end = aUrl.lastIndexOf("/");
					if (index == end) {
						// http://localhost/ -> http://localhost/
						url = aUrl;
					} else {
						if (end + 1 == aUrl.length()) {
							// http://localhost/aaa/ -> http://localhost/aaa/
							url = aUrl;
						} else {
							// http://localhost/aaa/bb -> http://localhost/aaa/
							url = aUrl.substring(0, end + 1);
						}
					}
				}
			}
			return url;
		}

		private String full(final String aAreas, final String aUrl) {
			String url = null;
			if (null == aAreas || null == aUrl) {
			} else if (aUrl.startsWith("http://") || aUrl.startsWith("https://")) {
				url = aUrl;
			} else if (-1 != aUrl.indexOf("../")) {
			} else if (aUrl.startsWith("/")) {
			} else if (aUrl.startsWith("#")) {
				// ファイル名の後ろを消して+@
				// index.html#aaa -> index.html#bbb
			} else {
				url = aAreas + aUrl;
			}
			return url;
		}

	}
}
