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
package org.azkfw.crawler.access;

import java.net.InetSocketAddress;

import org.azkfw.crawler.logger.LoggerObject;

/**
 * このクラスは、マネージャーへのアクセス制限を行うクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/13
 * @author Kawakicchi
 */
public class AccessControl extends LoggerObject {

	public AccessControl() {
		super(AccessControl.class);
	}

	public boolean authentication(final InetSocketAddress aAddress, final String aAreas) {
		boolean auth = true;

		String address = aAddress.getHostString();

		String message = null;
		if (auth) {
			message = String.format("A %s %s", address, aAreas);
		} else {
			message = String.format("D %s %s", address, aAreas);
		}
		info(message);
		return auth;
	}

}
