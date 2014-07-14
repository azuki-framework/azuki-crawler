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
package org.azkfw.crawler.logic.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azkfw.business.dao.DataAccessObject;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.dsql.Parameter;
import org.azkfw.business.logic.AbstractDynamicSQLLogic;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.util.ListUtility;
import org.azkfw.util.MapUtility;
import org.azkfw.util.UUIDUtility;

/**
 * このクラスは、 Webクロール管理機能を実装するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public class WebCrawlerManagerImpl extends AbstractDynamicSQLLogic implements WebCrawlerManager {

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doDestroy() {

	}

	@Override
	public Map<String, Object> lockHost() throws DataAccessServiceException, SQLException {
		Map<String, Object> host = new HashMap<String, Object>();

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerL01");
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			Map<String, Object> record = records.get(0);

			String hostId = MapUtility.getString(record, ("id"));

			host.put("id", hostId);
			host.put("protocol", "http");
			host.put("name", record.get("name"));
			host.put("port", 80);

			Parameter params = new Parameter();
			params.put("id", hostId);
			params.put("status", 2);

			dao = getDao("WebCrawlerManagerU01", params);
			dao.execute();
		}

		commit();

		return host;
	}

	@Override
	public void unlockHost(final String aHostId, final int aResultCode) throws DataAccessServiceException, SQLException {
		// result code
		Timestamp date = new Timestamp((new Date()).getTime());

		Parameter params = new Parameter();
		params.put("id", aHostId);
		params.put("status", 1);
		params.put("date", date);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU01", params);
		dao.execute();
	}

	@Override
	public List<Map<String, Object>> getDownloadPages(final String aHostId, final int aPageSize) throws DataAccessServiceException, SQLException {
		List<Map<String, Object>> pages = new ArrayList<Map<String, Object>>();

		Parameter params = new Parameter();
		params.put("hostId", aHostId);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerS01", params);
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			for (Map<String, Object> record : records) {
				info(record.get("id").toString());

				Map<String, Object> page = new HashMap<String, Object>();
				page.put("id", record.get("id"));
				page.put("areas", record.get("areas"));

				params = new Parameter();
				params.put("id", record.get("id"));
				params.put("status", 2);

				dao = getDao("WebCrawlerManagerU02", params);
				dao.execute();

				pages.add(page);
			}
		}

		return pages;
	}

	public void downloadContent(final String aContentId, final int aStatusCode, final long aLength, final String aType)
			throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentId);
		params.put("status", 3);
		params.put("type", aType);
		params.put("length", Long.valueOf(aLength));
		params.put("code", Integer.valueOf(aStatusCode));

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU02", params);
		dao.execute();

		commit();
	}

	public void downloadContent(final String aContentId, final int aStatusCode) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentId);
		params.put("status", 3);
		params.put("code", Integer.valueOf(aStatusCode));

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU02", params);
		dao.execute();

		commit();
	}

	public void downloadErrorContent(final String aContentId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentId);
		params.put("status", -1);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU02", params);
		dao.execute();

		commit();
	}

	public void requestContentParse(final String aContentId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("parseId", UUIDUtility.generateToShortString());
		params.put("contentId", aContentId);
		params.put("status", 1);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerI01", params);
		dao.execute();

		commit();
	}

	public Map<String, Object> getParseContent() throws DataAccessServiceException, SQLException {
		Map<String, Object> result = new HashMap<String, Object>();

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerS02");
		List<Map<String, Object>> records = dao.query();
		
		if (ListUtility.isNotEmpty(records)) {
			Map<String, Object> record = records.get(0);
			result.put("hostId", record.get("hostId"));
			result.put("hostProtocol", "http");
			result.put("hostPort", 80);
			result.put("hostName", record.get("hostName"));
			result.put("contentId", record.get("contentId"));
			result.put("contentAreas", record.get("contentAreas"));
		}
		return result;
	}

	public void parseContent(final String aContentId) throws DataAccessServiceException, SQLException {

	}

	public void parseErrorContent(final String aContentId) throws DataAccessServiceException, SQLException {

	}
}
