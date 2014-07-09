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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
public abstract class SimpleHtmlParseEngine extends AbstractHtmlParseEngine {

	/** URL */
	private String url;

	/**
	 * コンストラクタ
	 * 
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	public SimpleHtmlParseEngine(final String aUrl, final Content aContent) {
		super(SimpleHtmlParseEngine.class, aContent);
		url = aUrl;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	public SimpleHtmlParseEngine(final String aName, final String aUrl, final Content aContent) {
		super(aName, aContent);
		url = aUrl;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 */
	public SimpleHtmlParseEngine(final Class<?> aClass, final String aUrl, final Content aContent) {
		super(aClass, aContent);
		url = aUrl;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public SimpleHtmlParseEngine(final String aUrl, final Content aContent, final Charset aCharset) {
		super(SimpleHtmlParseEngine.class, aContent, aCharset);
		url = aUrl;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public SimpleHtmlParseEngine(final String aName, final String aUrl, final Content aContent, final Charset aCharset) {
		super(aName, aContent, aCharset);
		url = aUrl;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 * @param aUrl URL
	 * @param aContent コンテンツ
	 * @param aCharset 文字コード
	 */
	public SimpleHtmlParseEngine(final Class<?> aClass, final String aUrl, final Content aContent, final Charset aCharset) {
		super(aClass, aContent, aCharset);
		url = aUrl;
	}

	@Override
	protected final boolean doParseHtmlContent(final Content aContent) {
		boolean result = false;

		Charset charset = getCharset();
		if (null == charset) {
			charset = getCharset(aContent);
		}

		if (null != charset) {
			String source = null;
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream is = null;
				try {
					is = aContent.getInputStream();
					byte[] buf = new byte[1024];
					int readSize;
					while (-1 != (readSize = is.read(buf, 0, 1024))) {
						if (0 == readSize)
							continue;
						baos.write(buf, 0, readSize);
					}

					source = new String(baos.toByteArray(), charset);
				} catch (IOException ex) {
					fatal(ex);
				} finally {
					if (null != is) {
						try {
							is.close();
						} catch (IOException ex) {
							fatal(ex);
						}
					}
				}
			}

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(aContent.getInputStream(), charset));
				ParserDelegator pd = new ParserDelegator();
				HtmlParserCallback cb = new HtmlParserCallback(this, source);
				pd.parse(reader, cb, true);

				result = true;
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
		}

		return result;
	}

	private void findTitle(final String aTitle) {
		doFindTitle(aTitle);
	}

	private void findDescription(final String aDescription) {
		doFindDescription(aDescription);
	}

	private void findKeywords(final List<String> aKeywords) {
		doFindKeywords(aKeywords);
	}

	private void findAnchor(final String aHref, final List<String> aInnerTexts) {
		try {
			String u = URLUtility.get(url, aHref);
			doFindAnchor(new URL(u), aInnerTexts);
		} catch (MalformedURLException ex) {
			fatal("Anchor href : " + aHref, ex);
		}
	}

	private void findImage(final String aSrc) {
		try {
			String u = URLUtility.get(url, aSrc);
			doFindImage(new URL(u));
		} catch (MalformedURLException ex) {
			fatal("Image src : " + ex);
		}
	}

	private void findScript(final String aSrc, final String aInnerText) {
		try {
			URL u = null;
			if (StringUtility.isNotEmpty(aSrc)) {
				String s = URLUtility.get(url, aSrc);
				u = new URL(s);
			}
			doFindScript(u, aInnerText);
		} catch (MalformedURLException ex) {
			fatal("Script src : " + ex);
		}
	}

	private void findLink(final String aHref) {
		try {
			String u = URLUtility.get(url, aHref);
			doFindLink(new URL(u));
		} catch (MalformedURLException ex) {
			fatal("Link src : " + ex);
		}
	}

	private void findText(final String aText) {
		doFindText(aText);
	}

	/**
	 * タイトルが見つかった場合に呼び出される。
	 * 
	 * @param aTitle タイトル
	 */
	protected abstract void doFindTitle(final String aTitle);

	/**
	 * 説明が見つかった場合に呼び出される。
	 * 
	 * @param aDescription 説明
	 */
	protected abstract void doFindDescription(final String aDescription);

	/**
	 * キーワードが見つかった場合に呼び出される。
	 * 
	 * @param aKeywords キーワード一覧
	 */
	protected abstract void doFindKeywords(final List<String> aKeywords);

	/**
	 * アンカーが見つかった場合に呼び出される。
	 * 
	 * @param aUrl URL
	 * @param aInnerTexts タグ内文字列群
	 */
	protected abstract void doFindAnchor(final URL aUrl, final List<String> aInnerTexts);

	/**
	 * イメージが見つかった場合に呼び出される。
	 * 
	 * @param aUrl URL
	 */
	protected abstract void doFindImage(final URL aUrl);

	/**
	 * スクリプトが見つかった場合に呼び出される。
	 * 
	 * @param aUrl Source url
	 * @param aInnerText タグ内文字列
	 */
	protected abstract void doFindScript(final URL aUrl, final String aInnerText);

	/**
	 * リンクが見つかった場合に呼び出される。
	 * 
	 * @param aUrl リンクソースURL
	 */
	protected abstract void doFindLink(final URL aUrl);

	/**
	 * テキストが見つかった場合に呼び出される。
	 * 
	 * @param aText テキスト
	 */
	protected abstract void doFindText(final String aText);

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

			charset = Charset.forName(cb.getCharset());
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

		@SuppressWarnings("unused")
		@Override
		public void handleSimpleTag(final Tag tag, final MutableAttributeSet attr, final int pos) {
			if (tag.equals(HTML.Tag.IMG)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				String alt = (String) attr.getAttribute(HTML.Attribute.ALT);
				if (StringUtility.isNotEmpty(src)) {
					engin.findImage(src);
				}
			} else if (tag.equals(HTML.Tag.LINK)) {
				String href = (String) attr.getAttribute(HTML.Attribute.HREF);
				if (StringUtility.isNotEmpty(href)) {
					engin.findLink(href);
				}
			} else if (tag.equals(HTML.Tag.META)) {
				String name = (String) attr.getAttribute(HTML.Attribute.NAME);
				if (StringUtility.isNotEmpty(name)) {
					name = name.toLowerCase();
					if ("description".equals(name)) {
						String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
						if (StringUtility.isNotEmpty(content)) {
							engin.findDescription(content);
						}
					} else if ("keywords".equals(name)) {
						String content = (String) attr.getAttribute(HTML.Attribute.CONTENT);
						if (StringUtility.isNotEmpty(content)) {
							String[] split = content.split(",");
							List<String> keywords = new ArrayList<String>();
							for (String s : split) {
								keywords.add(s);
							}
							engin.findKeywords(keywords);
						}
					}
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
						engin.findAnchor(anchorHref, anchorInnerTexts);
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
						engin.findScript(scriptSrc, str);
					} else if (nextScript > endScript) {
						String str = source.substring(scriptEndPos, endScript);
						engin.findScript(scriptSrc, str);
					}
				}
			} else if (tag.equals(HTML.Tag.TITLE)) {
				if (titleFlag) {
					titleFlag = false;
					if (StringUtility.isNotEmpty(title)) {
						engin.findTitle(title);
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
					engin.findText(text);
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

			}
			super.handleSimpleTag(tag, attr, pos);
		}
	}
}
