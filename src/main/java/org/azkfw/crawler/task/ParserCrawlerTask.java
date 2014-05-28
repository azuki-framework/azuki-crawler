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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.azkfw.crawler.parser.engine.HtmlTextParseEngine;
import org.azkfw.crawler.parser.engine.ParseEngine;

/**
 * このクラスは、解析を行うクローラタスククラスです。
 * 
 * <p>
 * このクローラタスクのパラメータを下記に記す。
 * <ul>
 * <li>file - 解析対象ファイル</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/14
 * @author Kawakicchi
 */
public class ParserCrawlerTask extends AbstractPersistenceCrawlerTask {

	private String file;

	public ParserCrawlerTask() {
		super(ParserCrawlerTask.class);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void doSetup() {
		file = getParameter("file", null);
		info(String.format("file : %s", file));
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {
		// TODO Auto-generated method stub

	}

	@Override
	protected CrawlerTaskResult doExecute() {

		CrawlerTaskResult result = new CrawlerTaskResult();
		if (parse(file)) {
			result.setResult(true);
			result.setStop(false);

		} else {
			result.setResult(false);
			result.setStop(true);

		}

		return result;
	}

	private boolean parse(final String aName) {
		boolean result = false;

		ParseEngine engine = new HtmlTextParseEngine("http://yahoo.co.jp");

		InputStream stream = null;

		try {
			stream = new FileInputStream(aName);

			engine.initialize();
			engine.parse(stream);
			engine.release();

			result = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return result;
	}
}
