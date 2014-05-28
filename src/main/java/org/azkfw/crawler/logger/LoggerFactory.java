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
 * このクラスは、ログ機能を生成するファクトリークラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public class LoggerFactory {

	public static Logger generate(final Class<?> aClass) {
		return new Log4jLogger(aClass);
	}

	private static class Log4jLogger implements Logger {

		private org.apache.log4j.Logger logger;

		public Log4jLogger(final Class<?> aClass) {
			logger = (org.apache.log4j.Logger) org.apache.log4j.Logger.getInstance(aClass);
		}

		public final void debug(final String aMessage) {
			logger.debug(aMessage);
		}

		public final void debug(final Throwable t) {
			logger.debug(null, t);
		}

		public final void debug(final String aMessage, final Throwable t) {
			logger.debug(aMessage, t);
		}

		public final void info(final String aMessage) {
			logger.info(aMessage);
		}

		public final void info(final Throwable t) {
			logger.info(null, t);
		}

		public final void info(final String aMessage, final Throwable t) {
			logger.info(aMessage, t);
		}

		public final void warn(final String aMessage) {
			logger.warn(aMessage);
		}

		public final void warn(final Throwable t) {
			logger.warn(null, t);
		}

		public final void warn(final String aMessage, final Throwable t) {
			logger.warn(aMessage, t);
		}

		public final void error(final String aMessage) {
			logger.error(aMessage);
		}

		public final void error(final Throwable t) {
			logger.error(null, t);
		}

		public final void error(final String aMessage, final Throwable t) {
			logger.error(aMessage, t);
		}

		public final void fatal(final String aMessage) {
			logger.fatal(aMessage);
		}

		public final void fatal(final Throwable t) {
			logger.fatal(null, t);
		}

		public final void fatal(final String aMessage, final Throwable t) {
			logger.fatal(aMessage, t);
		}

	}
}
