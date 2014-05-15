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
package org.azkfw.crawler.thread;

import java.util.Date;
import java.util.UUID;

import org.azkfw.crawler.config.CrawlerConfig.CrawlerThreadConfig;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logger.LoggerObject;
import org.azkfw.crawler.parameter.ParameterSupport;
import org.azkfw.crawler.schedule.CrawlerSchedule;
import org.azkfw.crawler.task.CrawlerTask;
import org.azkfw.crawler.task.CrawlerTaskResult;

/**
 * このクラスは、クローラスレッド機能を実装するための基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/15
 * @author Kawakicchi
 */
public abstract class AbstractCrawlerThread extends LoggerObject implements CrawlerThread, Runnable {

	private CrawlerThreadConfig config;

	private String id;

	private Status status;

	private Date threadStartDate;
	private Date threadStopDate;

	private boolean stopRequest;

	private CrawlerTask task;
	private CrawlerSchedule schedule;

	public AbstractCrawlerThread(final CrawlerThreadConfig aConfig) {
		super(CrawlerThread.class);
		config = aConfig;
		status = Status.stoped;

		id = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return config.getTitle();
	}

	@Override
	public String getDescription() {
		return config.getDescription();
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public CrawlerTask getTask() {
		return task;
	}

	@Override
	public CrawlerSchedule getSchedule() {
		return schedule;
	}

	@Override
	public Date getStartDate() {
		return threadStartDate;
	}

	@Override
	public Date getStopDate() {
		return threadStopDate;
	}

	@Override
	public CrawlerThreadConfig getConfig() {
		return config;
	}

	@Override
	public void setup() throws CrawlerSetupException {
		setupCrawlerSchedule();
		setupCrawlerTask();

		doSetup();
	}

	@Override
	public void initialize() {
		doInitialize();
	}

	@Override
	public void release() {
		doRelease();
	}

	@Override
	public void start() {
		if (status == Status.stoped) {
			Thread t = new Thread(this);
			t.start();
		}
	}

	@Override
	public void requestStop() {
		if (status == Status.running || status == Status.sleeping) {
			info("Request stop.");
			status = Status.stoping;
			stopRequest = true;

			task.requestStop();
		}
	}

	/**
	 * セットアップ処理を記述する。
	 * 
	 * @throws CrawlerSetupException セットアップ処理において問題が発生した場合
	 */
	protected abstract void doSetup() throws CrawlerSetupException;

	/**
	 * 初期化処理を記述する。
	 */
	protected abstract void doInitialize();

	/**
	 * 解放処理を記述する。
	 */
	protected abstract void doRelease();

	@Override
	public void run() {
		info("Thread start.");

		threadStartDate = new Date();
		threadStopDate = null;

		try {
			schedule.initialize();

			stopRequest = false;
			while (true) {
				status = Status.sleeping;

				while (!schedule.check()) {
					if (stopRequest) {
						break;
					}
					schedule.sleep();
				}

				if (schedule.isStop() || stopRequest) {
					break;
				} else if (schedule.isRun()) {

					info("Run task start.");
					status = Status.running;
					CrawlerTaskResult result = null;
					try {
						task.initialize();
						result = task.execute();
					} catch (Exception ex) {
						throw ex;
					} finally {
						if (null != task) {
							task.release();
						}
					}
					info("Run task stop.");

					if (null == result) {
						break;
					} else {
						if (result.isResult()) {
						} else {
							break;
						}
					}
				}
			}

			status = Status.stoped;

		} catch (Exception ex) {
			status = Status.error;
			fatal("Thread runing exception.", ex);

		} finally {
			schedule.release();
		}

		threadStopDate = new Date();

		info("Thread stop.");
	}

	private void setupCrawlerTask() throws CrawlerSetupException {
		try {
			CrawlerTask bufTask = null;

			String classname = config.getTask().getClassname();

			Class<?> clazz = Class.forName(classname);
			Object object = clazz.newInstance();
			if (object instanceof CrawlerTask) {
				bufTask = (CrawlerTask) object;
				if (bufTask instanceof ParameterSupport) {
					ParameterSupport support = (ParameterSupport) bufTask;
					support.addParameters(config.getTask().getParameters());
				}
				bufTask.setup();

				task = bufTask;
			}

		} catch (InstantiationException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		} catch (IllegalAccessException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		} catch (ClassNotFoundException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		}
	}

	private void setupCrawlerSchedule() throws CrawlerSetupException {
		try {
			CrawlerSchedule bufSchedule = null;

			String classname = config.getSchedule().getClassname();

			Class<?> clazz = Class.forName(classname);
			Object object = clazz.newInstance();
			if (object instanceof CrawlerSchedule) {
				bufSchedule = (CrawlerSchedule) object;
				if (bufSchedule instanceof ParameterSupport) {
					ParameterSupport support = (ParameterSupport) bufSchedule;
					support.addParameters(config.getSchedule().getParameters());
				}
				bufSchedule.setup();

				schedule = bufSchedule;
			}
		} catch (InstantiationException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		} catch (IllegalAccessException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		} catch (ClassNotFoundException ex) {
			fatal(ex);
			throw new CrawlerSetupException(ex);
		}
	}
}
