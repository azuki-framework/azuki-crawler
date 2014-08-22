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
package org.azkfw.crawler.store;

import java.util.HashMap;
import java.util.Map;

import org.azkfw.store.AbstractStore;

/**
 * このクラスは、クローラセッション用のストアクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/06/02
 * @author Kawakicchi
 */
public class CrawlerSessionStore extends AbstractStore<String, Object> {

	/**
	 * セッション情報
	 */
	private Map<String, Object> session;

	/**
	 * コンストラクタ
	 */
	public CrawlerSessionStore() {
		session = new HashMap<String, Object>();
	}

	@Override
	public void put(final String aKey, final Object aValue) {
		session.put(aKey, aValue);
	}

	@Override
	public void putAll(final Map<String, Object> aMap) {
		session.putAll(aMap);
	}

	@Override
	public Object get(final String aKey) {
		Object result = null;
		result = session.get(aKey);
		return result;
	}

	@Override
	public Object get(final String aKey, final Object aDefault) {
		Object result = aDefault;
		if (session.containsKey(aKey)) {
			result = session.get(aKey);
		}
		return result;
	}

	@Override
	public boolean has(final String aKey) {
		return session.containsKey(aKey);
	}

	@Override
	public void remove(final String aKey) {
		session.remove(aKey);
	}

}
