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
package org.azkfw.crawler.performance;

import org.azkfw.log.LoggingObject;

/**
 * このクラスは、パフォーマンス計測を行うクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/15
 * @author Kawakicchi
 */
public class Performance extends LoggingObject {

	private String name;
	private long start;
	private long stop;

	public Performance(final String aName) {
		super(Performance.class);
		name = aName;
	}

	public void start() {
		start = System.nanoTime();
	}

	public void stop() {
		stop = System.nanoTime();

		StringBuilder s = new StringBuilder();
		s.append(name);
		s.append(String.format(" [%.9f sec]", (double) (stop - start) * Math.pow(10.f, -9.f)));
		info(s.toString());
	}
}
