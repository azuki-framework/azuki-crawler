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
package org.azkfw.crawler;

/**
 * この列挙型は、クロールタイプを定義した列挙型です。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/12/26
 * @author Kawakicchi
 */
public enum CrawlType {

	/** None */
	None(0),
	/** 一度のみ */
	Once(1),
	/** 繰り返し */
	Loop(2);

	/** タイプ */
	private int type;

	/**
	 * コンストラクタ
	 * 
	 * @param type タイプ
	 */
	private CrawlType(final int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return Integer.toString(type);
	}
}
