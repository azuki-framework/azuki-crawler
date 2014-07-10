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

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.persistence.parameter.Parameter;

/**
 * このクラスは、指定のホストへPingを行うクローラタスククラスです。
 * 
 * <p>
 * このクローラタスクのパラメータを下記に記す。
 * <ul>
 * <li>host - ホスト名(default:127.0.0.1)</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public class PingCrawlerTask extends AbstractCrawlerTask {

	private InetAddress address;

	/**
	 * コンストラクタ
	 */
	public PingCrawlerTask() {
		super(PingCrawlerTask.class);
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		Parameter p = getParameter();
		try {
			address = Inet4Address.getByName(p.getString("host", "127.0.0.1"));
			info(String.format("Host : %s", address.getHostName()));
		} catch (UnknownHostException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		}
	}

	@Override
	protected void doStartup() {
	}

	@Override
	protected void doShutdown() {
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected CrawlerTaskResult doExecute() throws CrawlerServiceException {
		boolean ret = false;

		try {
			info(String.format("Ping %s", address.getHostName()));
			ret = ping(address);
			info(ret ? "SUCCESS" : "FAILED");
		} catch (IOException ex) {

		} catch (InterruptedException ex) {

		}

		CrawlerTaskResult result = new CrawlerTaskResult();
		result.setResult(true);
		result.setStop(false);
		return result;
	}

	/**
	 * Ping を実行し、ホストとの疎通を確認します。
	 * 
	 * @param target 疎通確認をしたいホスト
	 * @return 疎通が確認できれば true, 確認できないなら false
	 */
	private boolean ping(final InetAddress target) throws IOException, InterruptedException {
		// Windows の場合
		String[] command = { "ping", "-n", "1", "-w", Long.toString(3000), target.getHostAddress() };
		// Linux の場合
		// String[] command = {"ping", "-c", "1", "-t", Long.toString(3000), target.getHostAddress()};
		// mac の場合
		// String[] command = {"/sbin/ping", "-c", "1", "-t", Long.toString(3000), target.getHostAddress()};

		return new ProcessBuilder(command).start().waitFor() == 0;
	}

}
