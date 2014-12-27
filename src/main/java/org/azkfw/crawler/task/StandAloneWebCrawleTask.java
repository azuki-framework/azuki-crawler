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
package org.azkfw.crawler.task;

import java.io.File;
import java.io.IOException;

import org.azkfw.crawler.parser.engine.SimpleHtmlParseEngine.Counter;
import org.azkfw.io.CsvBufferedWriter;

/**
 * このクラスは、スタンドアロンで動作するWebクローラを実装するためのクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/11
 * @author Kawakicchi
 */
public abstract class StandAloneWebCrawleTask extends AbstractBusinessCrawlerTask {

	/**
	 * コンストラクタ
	 */
	public StandAloneWebCrawleTask() {
		super(StandAloneWebCrawleTask.class);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aClass クラス
	 */
	public StandAloneWebCrawleTask(final Class<?> aClass) {
		super(aClass);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param aName 名前
	 */
	public StandAloneWebCrawleTask(final String aName) {
		super(aName);
	}

	protected final void writeAnchors(final File file, final Counter counter) {
		CsvBufferedWriter writer = null;
		try {
			writer = new CsvBufferedWriter(file, "UTF-8");
			writer.setSeparateCharacter('\t');

			for (String url : counter.keyset()) {
				int count = counter.getCount(url);
				writer.writeCsvLine(Integer.toString(count), url);
			}

		} catch (IOException ex) {
			error("link.txt writer error.[" + file.getAbsolutePath() + "]", ex);
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
		}
	}

}