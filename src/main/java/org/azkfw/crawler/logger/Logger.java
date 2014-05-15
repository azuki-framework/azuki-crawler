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
 * このインターフェースは、ログ機能を表現するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public interface Logger {

	public void debug(final String aMessage);

	public void debug(final Throwable t);

	public void debug(final String aMessage, final Throwable t);

	public void info(final String aMessage);

	public void info(final Throwable t);

	public void info(final String aMessage, final Throwable t);

	public void warn(final String aMessage);

	public void warn(final Throwable t);

	public void warn(final String aMessage, final Throwable t);

	public void error(final String aMessage);

	public void error(final Throwable t);

	public void error(final String aMessage, final Throwable t);

	public void fatal(final String aMessage);

	public void fatal(final Throwable t);

	public void fatal(final String aMessage, final Throwable t);
}
