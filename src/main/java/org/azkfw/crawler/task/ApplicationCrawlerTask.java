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
import java.util.ArrayList;
import java.util.List;

import org.azkfw.crawler.lang.CrawlerSetupException;

/**
 * このクラスは、外部アプリケーションの実行を行うクローラタスククラスです。
 * 
 * <p>
 * このクローラタスクのパラメータを下記に記す。
 * <ul>
 * <li>application - 実行アプリケーションパス</li>
 * <li>currentDir - カレントディレクトリ。未設定の場合、アプリケーションのディレクトリをカレントとする。</li>
 * <li>parameter[0-9]{1,} - 実行アプリケーションへの引数。(parameter1から連番で指定)</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/16
 * @author Kawakicchi
 */
public class ApplicationCrawlerTask extends AbstractPersistenceCrawlerTask {

	private String application;
	private String currentDir;
	private List<String> parameters;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		application = getParameter("application", null);
		currentDir = getParameter("currentDir", null);
		parameters = new ArrayList<String>();
		int index = 1;
		while (true) {
			String value = getParameter("parameter" + index, null);
			if (null == value)
				break;
			parameters.add(value);
			index++;
		}
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
		result.setResult(false);
		result.setStop(true);

		StringBuilder cmd = new StringBuilder();

		List<String> list = new ArrayList<String>();
		list.add(application);
		cmd.append(application);
		for (String parameter : parameters) {
			if (0 == parameter.length()) {
				parameter = "\"\"";
			}

			list.add(parameter);
			cmd.append(" ").append(parameter);
		}

		info(cmd.toString());

		ProcessBuilder pb = new ProcessBuilder(list);

		if (null != currentDir && 0 < currentDir.length()) {
			File dir = new File(currentDir);
			pb.directory(dir);
		} else {
			File apli = new File(application);
			apli.getParent();
			info(apli.getParent());
			pb.directory(apli.getParentFile());
		}

		try {
			Process process = pb.start();

			process.waitFor();

			if (0 == process.exitValue()) {
				result.setResult(true);
				result.setStop(false);
			}

		} catch (InterruptedException ex) {
			fatal(ex);
		} catch (IOException ex) {
			fatal(ex);
		}

		return result;
	}

}
