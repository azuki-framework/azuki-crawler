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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.azkfw.crawler.content.Content;
import org.azkfw.util.StringUtility;
import org.azkfw.util.URLUtility;

/**
 * このクラスは、簡易的なHTMLテキスト解析を行う解析エンジンです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/08
 * @author Kawakicchi
 */
public class SimpleHtmlParseEngine extends AbstractHtmlParseEngine {

	/** URL - 解析対象URL */
	private URL url;
	/** */
	private String baseUrl;
	/** */
	private List<String> anchorList;
	private List<String> imageList;
	private List<String> scriptList;
	private List<String> linkList;
	/** */
	private Counter urlCounter;
	private Counter anchorCounter;
	private Counter imageCounter;

	/**
	 * コンストラクタ
	 * 
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	public SimpleHtmlParseEngine(final URL aUrl, final Content aContent) {
		super(SimpleHtmlParseEngine.class, aContent);
		init(aUrl);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public SimpleHtmlParseEngine(final URL aUrl, final Content aContent, final Charset aCharset) {
		super(SimpleHtmlParseEngine.class, aContent, aCharset);
		init(aUrl);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	protected SimpleHtmlParseEngine(final String aName, final URL aUrl, final Content aContent) {
		super(aName, aContent);
		init(aUrl);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	protected SimpleHtmlParseEngine(final Class<?> aClass, final URL aUrl, final Content aContent) {
		super(aClass, aContent);
		init(aUrl);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	protected SimpleHtmlParseEngine(final String aName, final URL aUrl, final Content aContent, final Charset aCharset) {
		super(aName, aContent, aCharset);
		init(aUrl);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	protected SimpleHtmlParseEngine(final Class<?> aClass, final URL aUrl, final Content aContent, final Charset aCharset) {
		super(aClass, aContent, aCharset);
		init(aUrl);
	}

	private void init(final URL aUrl) {
		url = aUrl;

		baseUrl = null;
		anchorList = new ArrayList<String>();
		imageList = new ArrayList<String>();
		scriptList = new ArrayList<String>();
		linkList = new ArrayList<String>();

		urlCounter = new Counter();
		anchorCounter = new Counter();
		imageCounter = new Counter();
	}

	public final Counter getUrls() {
		return urlCounter;
	}

	public final Counter getAnchors() {
		return anchorCounter;
	}

	public final Counter getImages() {
		return imageCounter;
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected final ParseEngineResult doParseHtmlContent(final Content aContent) {
		ParseEngineResult result = new ParseEngineResult();

		Charset charset = getCharset();
		if (null == charset) {
			// charsetが不明な場合HTMLから取得
			charset = getCharset(aContent);
		}

		if (null != charset) {
			String html = getSource(aContent, charset);

			doBefore(html);

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(aContent.getInputStream(), charset));
				ParserDelegator pd = new ParserDelegator();
				HtmlParserCallback cb = new HtmlParserCallback(this, html);
				pd.parse(reader, cb, true);
			} catch (IOException ex) {
				fatal(ex);
			} finally {
				release(reader);
			}

			doAfter(html);

			replaseURL();

			result.setResult(true);
		}

		return result;
	}

	protected void doBefore(final String html) {

	}

	protected void doAfter(final String html) {

	}

	/**
	 * タイトルを設定する。
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aTitle
	 */
	protected final void setTitle(final String aTitle) {
	}

	/**
	 * 説明文を設定する。
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aDescription
	 */
	protected final void setDescription(final String aDescription) {
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aKeywords
	 */
	protected final void setKeywords(final List<String> aKeywords) {
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aKeywords
	 */
	protected final void addAnchor(final String aHref, final List<String> aInnerTexts) {
		if (StringUtility.isNotEmpty(aHref)) {
			anchorList.add(aHref);
		}
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aSrc
	 * @param aAlt
	 */
	protected final void addImage(final String aSrc, final String aAlt) {
		if (StringUtility.isNotEmpty(aSrc)) {
			imageList.add(aSrc);
		}
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aSrc
	 * @param aInnerText
	 */
	protected final void addScript(final String aSrc, final String aInnerText) {
		if (StringUtility.isNotEmpty(aSrc)) {
			scriptList.add(aSrc);
		}
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aHref
	 */
	protected final void addLink(final String aHref) {
		if (StringUtility.isNotEmpty(aHref)) {
			linkList.add(aHref);
		}
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aHref
	 */
	protected final void setBase(final String aHref) {
		baseUrl = aHref;
	}

	/**
	 * <p>
	 * ParserCallbackから呼び出される
	 * </p>
	 * 
	 * @param aText
	 */
	protected final void addText(final String aText) {
	}

	/**
	 * コンテンツを一時読み込みし文字コードを取得する。
	 * 
	 * @param aContent コンテンツ
	 * @return 文字コード。取得できない場合、<code>null</code>を返す。
	 */
	private Charset getCharset(final Content aContent) {
		Charset charset = null;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(aContent.getInputStream(), Charset.forName("ASCII")));
			ParserDelegator pd = new ParserDelegator();
			PreHtmlParserCallback cb = new PreHtmlParserCallback();
			pd.parse(reader, cb, true);

			if (StringUtility.isNotEmpty(cb.getCharset())) {
				charset = Charset.forName(cb.getCharset());
			}
		} catch (IOException ex) {
			fatal(ex);
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return charset;
	}

	private void replaseURL() {
		String base = url.toExternalForm();
		if (StringUtility.isNotEmpty(baseUrl)) {
			base = baseUrl;
		}

		for (String url : anchorList) {
			try {
				String buf = decorate(URLUtility.get(base, url));
				urlCounter.countup(buf);
				anchorCounter.countup(buf);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
		for (String url : imageList) {
			try {
				String buf = decorate(URLUtility.get(base, url));
				urlCounter.countup(buf);
				imageCounter.countup(buf);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
		for (String url : scriptList) {
			try {
				String buf = decorate(URLUtility.get(base, url));
				urlCounter.countup(buf);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
		for (String url : linkList) {
			try {
				String buf = decorate(URLUtility.get(base, url));
				urlCounter.countup(buf);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private String decorate(final String aUrl) throws MalformedURLException {
		URL url = new URL(aUrl);
		String str = url.toExternalForm();
		String ref = url.getRef();
		if (null != ref) {
			str = str.substring(0, str.length() - (ref.length() + 1));
		}
		if (StringUtility.isEmpty(url.getFile())) {
			str += "/";
		}
		return str;
	}

	public static class Counter {

		private Map<String, Integer> counter;

		public Counter() {
			counter = new HashMap<String, Integer>();
		}

		public Set<String> keyset() {
			return counter.keySet();
		}

		public int countup(final String key) {
			int count = 0;
			if (counter.containsKey(key)) {
				count = counter.get(key);
			}
			count++;
			counter.put(key, count);
			return count;
		}

		public int getCount(final String key) {
			int count = 0;
			if (counter.containsKey(key)) {
				count = counter.get(key);
			}
			return count;
		}
	}

	/**
	 * このクラスは、解析をするためのコールバッククラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/07/07
	 * @author Kawakicchi
	 */
	private final class HtmlParserCallback extends ParserCallback {

		private SimpleHtmlParseEngine engin;
		private String source;

		private boolean titleFlag;
		private String title;

		private boolean scriptFlag;
		private String scriptSrc;
		@SuppressWarnings("unused")
		private int scriptStartPos;
		private int scriptEndPos;

		private boolean anchorFlag;
		private String anchorHref;
		private List<String> anchorInnerTexts;

		/**
		 * コンストラクタ
		 * 
		 * @param aEngin エンジン
		 * @param aSource HTML文字列
		 */
		public HtmlParserCallback(final SimpleHtmlParseEngine aEngin, final String aSource) {
			engin = aEngin;
			source = aSource;

			titleFlag = false;
			title = null;

			scriptFlag = false;
			scriptSrc = null;
			scriptStartPos = -1;
			scriptEndPos = -1;

			anchorFlag = false;
			anchorHref = null;
			anchorInnerTexts = new ArrayList<String>();
		}

		@Override
		public void handleSimpleTag(final Tag tag, final MutableAttributeSet attr, final int pos) {
			if (tag.equals(HTML.Tag.IMG)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				String alt = (String) attr.getAttribute(HTML.Attribute.ALT);
				if (StringUtility.isNotEmpty(src)) {
					engin.addImage(src, alt);
				}
			} else if (tag.equals(HTML.Tag.LINK)) {
				String href = (String) attr.getAttribute(HTML.Attribute.HREF);
				if (StringUtility.isNotEmpty(href)) {
					engin.addLink(href);
				}
			} else if (tag.equals(HTML.Tag.META)) {
				String name = (String) attr.getAttribute(HTML.Attribute.NAME);
				if (StringUtility.isNotEmpty(name)) {
					name = name.toLowerCase();
					if ("description".equals(name)) {
						String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
						if (StringUtility.isNotEmpty(content)) {
							engin.setDescription(content);
						}
					} else if ("keywords".equals(name)) {
						String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
						if (StringUtility.isNotEmpty(content)) {
							String[] split = content.split(",");
							List<String> keywords = new ArrayList<String>();
							for (String s : split) {
								keywords.add(s);
							}
							engin.setKeywords(keywords);
						}
					}
				}
			} else if (tag.equals(HTML.Tag.BASE)) {
				String href = (String) attr.getAttribute(HTML.Attribute.HREF);
				if (StringUtility.isNotEmpty(href)) {
					engin.setBase(href);
				}
			}
			super.handleSimpleTag(tag, attr, pos);
		}

		@SuppressWarnings("unused")
		@Override
		public void handleStartTag(final Tag tag, final MutableAttributeSet attr, final int pos) {
			if (tag.equals(HTML.Tag.A)) {
				if (!anchorFlag) {
					anchorFlag = true;
					String href = (String) attr.getAttribute(HTML.Attribute.HREF);
					String title = (String) attr.getAttribute(HTML.Attribute.TITLE);
					anchorHref = href;
					anchorInnerTexts.clear();
				}
			} else if (tag.equals(HTML.Tag.SCRIPT)) {
				if (!scriptFlag) {
					scriptFlag = true;
					String src = (String) attr.getAttribute(HTML.Attribute.SRC);
					scriptSrc = src;
					scriptStartPos = pos;
				}
			} else if (tag.equals(HTML.Tag.TITLE)) {
				if (!titleFlag) {
					titleFlag = true;
					title = null;
				}
			}
			super.handleStartTag(tag, attr, pos);
		}

		@Override
		public void handleEndTag(final Tag tag, final int pos) {
			if (tag.equals(HTML.Tag.A)) {
				if (anchorFlag) {
					anchorFlag = false;
					if (StringUtility.isNotEmpty(anchorHref)) {
						engin.addAnchor(anchorHref, anchorInnerTexts);
					}
				}
			} else if (tag.equals(HTML.Tag.SCRIPT)) {
				if (scriptFlag) {
					scriptFlag = false;
					scriptEndPos = pos;

					int endScript = source.indexOf("</script", scriptEndPos);
					int nextScript = source.indexOf("<script", scriptEndPos);
					if (-1 == endScript) {

					} else if (-1 == nextScript) {
						String str = source.substring(scriptEndPos, endScript);
						engin.addScript(scriptSrc, str);
					} else if (nextScript > endScript) {
						String str = source.substring(scriptEndPos, endScript);
						engin.addScript(scriptSrc, str);
					}
				}
			} else if (tag.equals(HTML.Tag.TITLE)) {
				if (titleFlag) {
					titleFlag = false;
					if (StringUtility.isNotEmpty(title)) {
						engin.setTitle(title);
					}
				}
			}
			super.handleEndTag(tag, pos);
		}

		@Override
		public void handleText(final char[] data, final int pos) {
			if (anchorFlag) {
				String text = new String(data);
				if (StringUtility.isNotEmpty(text)) {
					anchorInnerTexts.add(text);
				}
			} else if (titleFlag) {
				String text = new String(data);
				if (StringUtility.isNotEmpty(text)) {
					title = text;
				}
			} else {
				String text = new String(data);
				if (StringUtility.isNotEmpty(text)) {
					engin.addText(text);
				}
			}
			super.handleText(data, pos);
		}
	}

	/**
	 * このクラスは、プレ解析をするためのコールバッククラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/07/07
	 * @author Kawakicchi
	 */
	private final class PreHtmlParserCallback extends ParserCallback {

		/** 文字コード */
		private String charset;

		/**
		 * コンストラクタ
		 */
		public PreHtmlParserCallback() {

		}

		/**
		 * 文字コードを取得する。
		 * 
		 * @return 文字コード
		 */
		public String getCharset() {
			return charset;
		}

		@Override
		public void handleSimpleTag(Tag tag, MutableAttributeSet attr, int pos) {
			if (tag.equals(HTML.Tag.META)) {
				String httpEquiv = (String) attr.getAttribute(HTML.Attribute.HTTPEQUIV);
				if (StringUtility.isNotEmpty(httpEquiv) && "content-type".equals(httpEquiv.toLowerCase())) {
					String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
					if (StringUtility.isNotEmpty(content)) {
						int index = content.toLowerCase().indexOf("charset");
						if (-1 != index) {
							index = content.indexOf("=", index + 7);
							if (-1 != index) {
								charset = content.substring(index + 1).trim();
							}
						}
					}
				}
				String str = (String) attr.getAttribute("charset");
				if (StringUtility.isNotEmpty(str)) {
					charset = str;
				}
			}
			super.handleSimpleTag(tag, attr, pos);
		}
	}
}
