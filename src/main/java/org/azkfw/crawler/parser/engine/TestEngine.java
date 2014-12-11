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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.azkfw.util.StringUtility;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/12/10
 * @author kawakicchi
 */
public class TestEngine {

	public static void main(final String[] args) {
		TestEngine test = new TestEngine();
		test.test();
	}

	public void test() {
		String file = "C:\\crawler\\data\\8FB37FD148584C38B211CCED0C3F81EA\\82A1A8F52A834CE994F1238DCC819C60\\content.dat";
		Charset charset = Charset.forName("UTF-8");

		String html = getSource(file, charset);

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			ParserDelegator pd = new ParserDelegator();
			TabelogHtmlParserCallback cb = new TabelogHtmlParserCallback(this, html);
			pd.parse(reader, cb, true);

		} catch (IOException ex) {
			ex.printStackTrace();
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

	private String getSource(final String aFile, final Charset aCharset) {
		String source = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = new FileInputStream(aFile);
			byte[] buf = new byte[1024];
			int readSize;
			while (-1 != (readSize = is.read(buf, 0, 1024))) {
				if (0 == readSize)
					continue;
				baos.write(buf, 0, readSize);
			}

			source = new String(baos.toByteArray(), aCharset);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return source;
	}

	/**
	 * このクラスは、解析をするためのコールバッククラスです。
	 * 
	 * @since 1.0.0
	 * @version 1.0.0 2014/12/10
	 * @author Kawakicchi
	 */
	private static final class TabelogHtmlParserCallback extends ParserCallback {

		private TestEngine engin;
		private String source;

		private boolean nameFlag;
		private boolean ratingFlag;
		private boolean priceFlag;
		private boolean telFlag;
		private boolean categoryFlag;

		/**
		 * コンストラクタ
		 * 
		 * @param aEngin エンジン
		 * @param aSource HTML文字列
		 */
		public TabelogHtmlParserCallback(final TestEngine aEngin, final String aSource) {
			engin = aEngin;
			source = aSource;
		}

		private static final Pattern PTN_GET_LATLON = Pattern.compile("center=([0-9\\.]+),([0-9\\.]+)");

		@Override
		public void handleSimpleTag(final Tag tag, final MutableAttributeSet attr, final int pos) {
			if (tag.equals(HTML.Tag.IMG)) {
				String src = (String) attr.getAttribute(HTML.Attribute.SRC);
				if (StringUtility.isNotEmpty(src)) {
					if (-1 != src.indexOf("http://maps.google.com/maps/api/staticmap")) {
						Matcher m = PTN_GET_LATLON.matcher(src);
						if (m.find()) {
							String lat = m.group(1);
							String lon = m.group(2);
							System.out.println(String.format("Location : %s, %s", lat, lon));
						}
					}
				}
			}
			super.handleSimpleTag(tag, attr, pos);
		}

		@Override
		public void handleStartTag(final Tag tag, final MutableAttributeSet attr, final int pos) {
			if (tag.equals(HTML.Tag.A)) {
				if (!priceFlag) {
					String property = (String) attr.getAttribute("property");
					if ("v:pricerange".equals(property)) {
						priceFlag = true;
					}
				}
			} else if (tag.equals(HTML.Tag.STRONG)) {
				if (!telFlag) {
					String property = (String) attr.getAttribute("property");
					if ("v:tel".equals(property)) {
						telFlag = true;
					}
				}
			} else if (tag.equals(HTML.Tag.SPAN)) {
				if (!nameFlag) {
					String clazz = (String) attr.getAttribute(HTML.Attribute.CLASS);
					if ("display-name".equals(clazz)) {
						nameFlag = true;
					}
				}
				if (!ratingFlag) {
					String property = (String) attr.getAttribute("property");
					if ("v:average".equals(property)) {
						ratingFlag = true;
					}
				}
				if (!categoryFlag) {
					String property = (String) attr.getAttribute("property");
					if ("v:category".equals(property)) {
						categoryFlag = true;
					}
				}
			}
			super.handleStartTag(tag, attr, pos);
		}

		@Override
		public void handleEndTag(final Tag tag, final int pos) {
			if (tag.equals(HTML.Tag.A)) {
				if (priceFlag) {
					priceFlag = false;
				}
			} else if (tag.equals(HTML.Tag.STRONG)) {
				if (telFlag) {
					telFlag = false;
				}
			} else if (tag.equals(HTML.Tag.SPAN)) {
				if (nameFlag) {
					nameFlag = false;
				}
				if (ratingFlag) {
					ratingFlag = false;
				}
				if (categoryFlag) {
					categoryFlag = false;
				}
			}
			super.handleEndTag(tag, pos);
		}

		@Override
		public void handleText(final char[] data, final int pos) {
			if (nameFlag) {
				System.out.println("Name : " + new String(data));
			} else if (priceFlag) {
				System.out.println("Price : " + new String(data));
			} else if (ratingFlag) {
				System.out.println("Rating : " + new String(data));
			} else if (telFlag) {
				System.out.println("Tel : " + new String(data));
			} else if (categoryFlag) {
				System.out.println("Category : " + new String(data));
			}
			super.handleText(data, pos);
		}
	}

}
