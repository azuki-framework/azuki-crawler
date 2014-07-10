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
package org.azkfw.crawler.schedule;

import org.azkfw.persistence.parameter.Parameter;

/**
 * このクラスは、指定回数実行を行うスケジュールクラスです。
 * 
 * <p>
 * パラメータ一覧
 * <ul>
 * <li>count - 実行する回数(default:1)</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/12
 * @author Kawakicchi
 */
public final class CountSchedule extends AbstractCrawlerSchedule {

	private int maxCount;
	private int nowCount;

	@Override
	protected void doSetup() {
		Parameter p = getParameter();
		maxCount = p.getInteger("count", Integer.valueOf(0));
	}

	@Override
	protected void doInitialize() {
		nowCount = 0;
	}

	@Override
	protected void doRelease() {

	}

	@Override
	public String getOutline() {
		StringBuilder s = new StringBuilder();
		s.append(String.format("%d回実行", maxCount));
		return s.toString();
	}

	@Override
	public boolean check() {
		nowCount++;
		return true;
	}

	@Override
	public boolean isStop() {
		return (nowCount > maxCount);
	}

	@Override
	public boolean isRun() {
		return !(nowCount > maxCount);
	}

	@Override
	public void sleep() throws InterruptedException {

	}

}
