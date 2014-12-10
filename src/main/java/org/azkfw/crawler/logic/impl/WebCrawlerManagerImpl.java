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

import java.net.URL;
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
import org.azkfw.util.StringUtility;
import org.azkfw.util.UUIDUtility;

/**
 * このクラスは、 Webクロール管理機能を実装するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public class WebCrawlerManagerImpl extends AbstractDynamicSQLLogic implements WebCrawlerManager {

	/**
	 * コンストラクタ
	 */
	public WebCrawlerManagerImpl() {
		super(WebCrawlerManagerImpl.class);
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doDestroy() {

	}

	@Override
	public Map<String, Object> lockHost() throws DataAccessServiceException, SQLException {
		Timestamp date = new Timestamp((new Date()).getTime());

		Map<String, Object> host = new HashMap<String, Object>();

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerL01");
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			Map<String, Object> record = records.get(0);

			String hostId = MapUtility.getString(record, ("id"));

			host.put("id", hostId);
			host.put("name", record.get("name"));
			host.put("protocol", record.get("protocol"));
			host.put("port", record.get("port"));

			Parameter params = new Parameter();
			params.put("id", hostId);
			params.put("status", 2);
			params.put("date", date);

			dao = getDao("WebCrawlerManagerU01", params);
			dao.execute();
		}

		commit();

		return host;
	}

	@Override
	public void unlockHost(final String aHostId, final int aStatus) throws DataAccessServiceException, SQLException {
		Timestamp date = new Timestamp((new Date()).getTime());

		Parameter params = new Parameter();
		params.put("id", aHostId);
		params.put("status", aStatus);
		params.put("date", date);
		params.put("accessDate", date);

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

	@Override
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

	@Override
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

	@Override
	public void downloadErrorContent(final String aContentId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentId);
		params.put("status", -1);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU02", params);
		dao.execute();

		commit();
	}

	@Override
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

	@Override
	public Map<String, Object> getParseContent() throws DataAccessServiceException, SQLException {
		Map<String, Object> result = new HashMap<String, Object>();

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerS02");
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			Map<String, Object> record = records.get(0);
			result.put("contentParseId", record.get("contentParseId"));
			result.put("hostId", record.get("hostId"));
			result.put("hostProtocol", "http");
			result.put("hostPort", 80);
			result.put("hostName", record.get("hostName"));
			result.put("contentId", record.get("contentId"));
			result.put("contentAreas", record.get("contentAreas"));
			result.put("contentType", record.get("contentType"));

			Parameter params = new Parameter();
			params.put("id", record.get("contentParseId"));
			params.put("status", 2);

			dao = getDao("WebCrawlerManagerU03", params);
			dao.execute();

			commit();
		}

		return result;
	}

	@Override
	public Map<String, Object> getHost(final String aName, final String aProtocol, final int aPort) throws DataAccessServiceException, SQLException {
		Map<String, Object> host = new HashMap<String, Object>();

		Parameter params = new Parameter();
		params.put("name", aName);
		params.put("protocol", aProtocol);
		params.put("port", aPort);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerS03", params);
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			Map<String, Object> record = records.get(0);

			String hostId = MapUtility.getString(record, ("id"));

			host.put("id", hostId);
			host.put("name", record.get("name"));
			host.put("protocol", record.get("protocol"));
			host.put("port", record.get("port"));
		}

		return host;
	}

	@Override
	public Map<String, Object> registHost(final String aName, final String aProtocol, final int aPort) throws DataAccessServiceException,
			SQLException {
		Timestamp date = new Timestamp((new Date()).getTime());

		Map<String, Object> host = new HashMap<String, Object>();

		String id = UUIDUtility.generateToShortString().toUpperCase();

		Parameter params = new Parameter();
		params.put("id", id);
		params.put("name", aName);
		params.put("protocol", aProtocol);
		params.put("port", aPort);
		params.put("date", date);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerI02", params);
		dao.execute();

		host.put("id", id);

		return host;
	}

	public void registContents(final String aHostId, final List<URL> aUrls, final Date aDate) throws DataAccessServiceException, SQLException {
		Timestamp date = new Timestamp((new Date()).getTime());

		Parameter params = new Parameter();

		DataAccessObject dao = null;
		for (URL url : aUrls) {
			String areas = url.getFile();
			if (StringUtility.isEmpty(areas)) {
				areas = "/";
			}

			params.clear();
			params.put("areas", areas);
			params.put("hostId", aHostId);
			dao = getDao("WebCrawlerManagerS04", params);

			if (0 == dao.count()) {
				params.clear();
				params.put("id", UUIDUtility.generateToShortString());
				params.put("areas", areas);
				params.put("type", "");
				params.put("hostId", aHostId);
				params.put("date", date);

				dao = getDao("WebCrawlerManagerI03", params);
				dao.execute();
			}
		}

		commit();
	}

	@Override
	public void parseContent(final String aContentParseId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentParseId);
		params.put("status", 3);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU03", params);
		dao.execute();

		commit();
	}

	public void parseErrorContent(final String aContentParseId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentParseId);
		params.put("status", -1);

		DataAccessObject dao = null;
		dao = getDao("WebCrawlerManagerU03", params);
		dao.execute();

		commit();
	}
}
