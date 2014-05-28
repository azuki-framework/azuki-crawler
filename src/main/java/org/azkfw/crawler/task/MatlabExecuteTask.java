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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.azkfw.core.util.PathUtility;
import org.azkfw.core.util.StringUtility;
import org.azkfw.crawler.lang.CrawlerSetupException;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/05/21
 * @author kawakita
 */
public class MatlabExecuteTask extends AbstractPersistenceCrawlerTask {

	/**
	 * 実行スクリプトファイル
	 */
	private String runScriptFileName = "run.m";

	/**
	 * 成功判定ファイル
	 */
	private String successFileName = "success";

	/**
	 * 失敗判定ファイル
	 */
	private String errorFileName = "error";

	private String workDir;
	private String fileName;
	private String logFile;
	private String scriptName;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		workDir = getParameter("workDir", null);
		fileName = getParameter("fileName", null);
		logFile = getParameter("logFile", null);

		logFile = PathUtility.cat(workDir, logFile);

		scriptName = fileName;
		int index = scriptName.lastIndexOf(".");
		if (-1 != index) {
			scriptName = scriptName.substring(0, index);
		}

		errorFileName = scriptName + ".error.log";
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected CrawlerTaskResult doExecute() {

		try {
			String runFilePath = createRunScriptFile(workDir, fileName);

			List<String> list = new ArrayList<String>();
			list.add("matlab");
			list.add("-nosplash");
			list.add("-minimize");
			list.add("-wait");
			list.add("-r");
			list.add("run('" + runFilePath + "');");
			if (StringUtility.isNotEmpty(logFile)) {
				list.add("-logfile");
				list.add(logFile);
			}

			ProcessBuilder pb = new ProcessBuilder(list);
			Process p = pb.start();
			p.waitFor();
		} catch (IOException ex) {

		} catch (InterruptedException ex) {

		}

		return null;
	}

	/**
	 * スクリプトファイル実行用スクリプトファイルを生成する。
	 * <p>
	 * </p>
	 * 
	 * @param aTargetDir 対象ディレクトリ
	 * @param aScriptName 実行スクリプトファイル名
	 * @return 実行用スクリプトファイルパス。
	 * @throws IOException IO操作時に問題が発生した場合。
	 */
	private String createRunScriptFile(final String aTargetDir, final String aScriptName) throws IOException {
		String runFilePath = PathUtility.cat(aTargetDir, runScriptFileName);
		String scriptName = aScriptName;
		int index = aScriptName.lastIndexOf(".");
		if (-1 != index) {
			scriptName = aScriptName.substring(0, index);
		}

		String crlf = "\n";
		try {
			crlf = System.getProperty("line.separator");
		} catch (SecurityException e) {
		}

		StringBuilder s = new StringBuilder();
		s.append("try").append(crlf);
		s.append("  cd('").append(aTargetDir).append("');").append(crlf);
		s.append("  ").append(scriptName).append(crlf);
		s.append("  successFileId = fopen('").append(successFileName).append("','W');").append(crlf);
		s.append("  fclose(successFileId);").append(crlf);
		s.append("catch err").append(crlf);
		s.append("  msg = getReport(err, 'extended');").append(crlf);
		s.append("  errorFileId = fopen('").append(errorFileName).append("','W');").append(crlf);
		s.append("  fprintf(errorFileId, '%s', msg);").append(crlf);
		s.append("  fclose(errorFileId);").append(crlf);
		s.append("end").append(crlf);
		s.append("exit;");

		BufferedWriter writer = new BufferedWriter(new FileWriter(runFilePath));
		writer.write(s.toString());
		writer.close();
		return runFilePath;
	}

}
