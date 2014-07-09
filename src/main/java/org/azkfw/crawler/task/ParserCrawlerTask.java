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

import org.azkfw.crawler.content.Content;
import org.azkfw.crawler.content.FileContent;
import org.azkfw.crawler.parser.engine.ParseEngine;
import org.azkfw.crawler.parser.engine.SampleHtmlParseEngine;
import org.azkfw.persistence.parameter.Parameter;

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
public final class ParserCrawlerTask extends AbstractPersistenceCrawlerTask {

	private File file;

	public ParserCrawlerTask() {
		super(ParserCrawlerTask.class);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void doSetup() {
		Parameter p = getParameter();

		file = new File(p.getString("file"));

		info(String.format("file : %s", file.getAbsolutePath()));
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

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

	private boolean parse(final File aFile) {
		boolean result = false;

		Content content = new FileContent(aFile);
		//ParseEngine engine = new HtmlTextParseEngine("http://yahoo.co.jp", content, Charset.forName("UTF-8"));
		ParseEngine engine = new SampleHtmlParseEngine("http://yahoo.co.jp", content);

		engine.initialize();
		engine.parse();
		engine.release();

		result = true;

		return result;
	}
}
