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
package org.azkfw.crawler.downloader.engine;

import java.io.File;
import java.net.URL;

/**
 * このクラスは、ダウンロード条件情報を保持するクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/12/16
 * @author Kawakicchi
 */
public class DownloadEngineCondition {

	private URL contentUrl;
	private File destFile;
	private URL refererUrl;

	public void setContentURL(final URL url) {
		contentUrl = url;
	}

	public URL getContentURL() {
		return contentUrl;
	}

	public void setDestFile(final File file) {
		destFile = file;
	}

	public File getDestFile() {
		return destFile;
	}

	public void setRefererURL(final URL url) {
		refererUrl = url;
	}

	public URL getRefererURL() {
		return refererUrl;
	}
}
