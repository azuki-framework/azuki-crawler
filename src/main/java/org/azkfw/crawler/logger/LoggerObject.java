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
package org.azkfw.crawler.logger;

/**
 * このクラスは、ログ機能を実装した基底クラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public abstract class LoggerObject implements LoggerSupport {

	private Logger logger;

	public LoggerObject() {
		logger = null;
	}

	public LoggerObject(final Class<?> aClass) {
		logger = LoggerFactory.generate(aClass);
	}

	public void setLogger(final Logger aLogger) {
		logger = aLogger;
	}

	protected final void debug(final String aMessage) {
		logger.debug(aMessage);
	}

	protected final void debug(final Throwable t) {
		logger.debug(t);
	}

	protected final void debug(final String aMessage, final Throwable t) {
		logger.debug(aMessage, t);
	}

	protected final void info(final String aMessage) {
		logger.info(aMessage);
	}

	protected final void info(final Throwable t) {
		logger.info(t);
	}

	protected final void info(final String aMessage, final Throwable t) {
		logger.info(aMessage, t);
	}

	protected final void warn(final String aMessage) {
		logger.warn(aMessage);
	}

	protected final void warn(final Throwable t) {
		logger.warn(t);
	}

	protected final void warn(final String aMessage, final Throwable t) {
		logger.warn(aMessage, t);
	}

	protected final void error(final String aMessage) {
		logger.error(aMessage);
	}

	protected final void error(final Throwable t) {
		logger.error(t);
	}

	protected final void error(final String aMessage, final Throwable t) {
		logger.error(aMessage, t);
	}

	protected final void fatal(final String aMessage) {
		logger.fatal(aMessage);
	}

	protected final void fatal(final Throwable t) {
		logger.fatal(t);
	}

	protected final void fatal(final String aMessage, final Throwable t) {
		logger.fatal(aMessage, t);
	}
}
