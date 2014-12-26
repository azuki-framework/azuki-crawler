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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azkfw.business.dao.DataAccessObject;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.dsql.Parameter;
import org.azkfw.business.logic.AbstractDynamicSQLLogic;
import org.azkfw.crawler.logic.WebCrawlerManager;
import org.azkfw.util.DateUtility;
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

	private static String DSQL_I01 = "WebCrawlerManagerI01";
	private static String DSQL_I02 = "WebCrawlerManagerI02";
	private static String DSQL_I03 = "WebCrawlerManagerI03";
	private static String DSQL_I04 = "WebCrawlerManagerI04";
	private static String DSQL_I05 = "WebCrawlerManagerI05";
	private static String DSQL_L01 = "WebCrawlerManagerL01";
	private static String DSQL_U01 = "WebCrawlerManagerU01";
	private static String DSQL_U02 = "WebCrawlerManagerU02";
	private static String DSQL_U03 = "WebCrawlerManagerU03";
	private static String DSQL_U04 = "WebCrawlerManagerU04";
	private static String DSQL_S01 = "WebCrawlerManagerS01";
	private static String DSQL_S02 = "WebCrawlerManagerS02";
	private static String DSQL_S03 = "WebCrawlerManagerS03";
	private static String DSQL_S04 = "WebCrawlerManagerS04";
	private static String DSQL_S05 = "WebCrawlerManagerS05";

	private static String DSQL_S10 = "WebCrawlerManagerS10";
	private static String DSQL_S11 = "WebCrawlerManagerS11";

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
		dao = getDao(DSQL_L01);
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

			dao = getDao(DSQL_U01, params);
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
		dao = getDao(DSQL_U01, params);
		dao.execute();
	}

	@Override
	public List<Map<String, Object>> getDownloadPages(final String aHostId, final int aPageSize) throws DataAccessServiceException, SQLException {
		List<Map<String, Object>> pages = new ArrayList<Map<String, Object>>();

		Timestamp date = new Timestamp((new Date()).getTime());

		DataAccessObject dao = null;
		Parameter params = new Parameter();

		params.clear();
		params.put("hostId", aHostId);
		params.put("max", aPageSize);
		dao = getDao(DSQL_S01, params);
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			for (Map<String, Object> record : records) {
				Map<String, Object> page = new HashMap<String, Object>();
				page.put("id", record.get("id"));
				page.put("historyId", record.get("historyId"));
				page.put("areas", record.get("areas"));
				page.put("refererHostName", record.get("refererHostName"));
				page.put("refererHostProtocol", record.get("refererHostProtocol"));
				page.put("refererHostPort", record.get("refererHostPort"));
				page.put("refererContentAreas", record.get("refererContentAreas"));

				params.clear();
				params.put("id", record.get("id"));
				params.put("status", 2);
				params.put("date", date);
				dao = getDao(DSQL_U02, params);
				dao.execute();

				pages.add(page);
			}
		}

		return pages;
	}

	@Override
	public void successDownloadContent(final String aContentId, final String aHistroyId, final String aPath, final int aStatusCode,
			final long aLength, final String aType) throws DataAccessServiceException, SQLException {

		Timestamp date = new Timestamp((new Date()).getTime());

		DataAccessObject dao = null;
		Parameter params = new Parameter();

		params.clear();
		params.put("id", aContentId);
		params.put("status", 3);
		params.put("date", date);
		dao = getDao(DSQL_U02, params);
		dao.execute();

		params.clear();
		params.put("contentId", aContentId);
		params.put("historyId", aHistroyId);
		params.put("code", Integer.valueOf(aStatusCode));
		params.put("path", aPath);
		params.put("type", aType);
		params.put("length", Long.valueOf(aLength));
		params.put("date", date);
		dao = getDao(DSQL_U04, params);
		dao.execute();

		commit();
	}

	@Override
	public void successDownloadContent(final String aContentId, final String aHistroyId, final String aPath, final int aStatusCode)
			throws DataAccessServiceException, SQLException {

		Timestamp date = new Timestamp((new Date()).getTime());

		DataAccessObject dao = null;
		Parameter params = new Parameter();

		params.clear();
		params.put("id", aContentId);
		params.put("status", 3);
		params.put("date", date);
		dao = getDao(DSQL_U02, params);
		dao.execute();

		params.clear();
		params.put("contentId", aContentId);
		params.put("historyId", aHistroyId);
		params.put("code", Integer.valueOf(aStatusCode));
		params.put("path", aPath);
		params.put("date", date);
		dao = getDao(DSQL_U04, params);
		dao.execute();

		commit();
	}

	@Override
	public void errorDownloadContent(final String aContentId) throws DataAccessServiceException, SQLException {

		Timestamp date = new Timestamp((new Date()).getTime());

		Parameter params = new Parameter();
		params.put("id", aContentId);
		params.put("status", -1);
		params.put("date", date);

		DataAccessObject dao = null;
		dao = getDao(DSQL_U02, params);
		dao.execute();

		commit();
	}

	@Override
	public void requestContentParse(final String aContentId, final String aHistoryId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("parseId", UUIDUtility.generateToShortString());
		params.put("contentId", aContentId);
		params.put("historyId", aHistoryId);
		params.put("status", 1);

		DataAccessObject dao = null;
		dao = getDao(DSQL_I01, params);
		dao.execute();

		commit();
	}

	@Override
	public Map<String, Object> getParseContent() throws DataAccessServiceException, SQLException {
		Map<String, Object> result = new HashMap<String, Object>();

		DataAccessObject dao = null;

		dao = getDao(DSQL_S02);
		List<Map<String, Object>> records = dao.query();

		if (ListUtility.isNotEmpty(records)) {
			Map<String, Object> record = records.get(0);
			result.put("parseId", record.get("parseId"));
			result.put("contentId", record.get("contentId"));
			result.put("historyId", record.get("historyId"));
			result.put("hostId", record.get("hostId"));
			result.put("hostProtocol", "http");
			result.put("hostPort", 80);
			result.put("hostName", record.get("hostName"));
			result.put("contentAreas", record.get("contentAreas"));
			result.put("contentPath", record.get("contentPath"));
			result.put("contentType", record.get("contentType"));

			Parameter params = new Parameter();
			params.put("id", record.get("contentParseId"));
			params.put("status", 2);

			dao = getDao(DSQL_U03, params);
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
		dao = getDao(DSQL_S03, params);
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
		dao = getDao(DSQL_I02, params);
		dao.execute();

		host.put("id", id);

		return host;
	}

	@Override
	public void registContents(final String aHostId, final List<URL> aUrls, final String aRefererContentId, final Date aDate)
			throws DataAccessServiceException, SQLException {
		DataAccessObject dao = null;
		Parameter params = new Parameter();

		Timestamp date = new Timestamp((new Date()).getTime());
		for (URL url : aUrls) {
			String areas = url.getFile();
			if (StringUtility.isEmpty(areas)) {
				areas = "/";
			}

			params.clear();
			params.put("areas", areas);
			params.put("hostId", aHostId);
			dao = getDao(DSQL_S04, params);

			Map<String, Object> data = dao.get();

			String contentId = null;
			if (!data.isEmpty()) {
				contentId = (String) data.get("contentId");
			} else {
				contentId = UUIDUtility.generateToShortString();

				params.clear();
				params.put("id", contentId);
				params.put("areas", areas);
				params.put("hostId", aHostId);
				params.put("engineNo", 0); // TODO: エンジン番号
				params.put("refererContentId", aRefererContentId);
				params.put("date", date);
				dao = getDao(DSQL_I03, params);
				dao.execute();
			}

			String historyId = UUIDUtility.generateToShortString();

			params.clear();
			params.put("id", contentId);
			params.put("historyId", historyId);
			params.put("type", "");
			params.put("date", date);
			dao = getDao(DSQL_I04, params);
			dao.execute();
		}

		commit();
	}

	@Override
	public void parseContent(final String aContentParseId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentParseId);
		params.put("status", 3);

		DataAccessObject dao = null;
		dao = getDao(DSQL_U03, params);
		dao.execute();

		commit();
	}

	@Override
	public void parseErrorContent(final String aContentParseId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("id", aContentParseId);
		params.put("status", -1);

		DataAccessObject dao = null;
		dao = getDao(DSQL_U03, params);
		dao.execute();

		commit();
	}

	@Override
	public void addTag(final String contentId, final long tagId) throws DataAccessServiceException, SQLException {
		Parameter params = new Parameter();
		params.put("contentId", contentId);
		params.put("tagId", tagId);

		DataAccessObject dao = null;
		dao = getDao(DSQL_S05, params);
		if (0 == dao.count()) {

			dao = getDao(DSQL_I05, params);
			dao.execute();

			commit();
		}
	}

	@Override
	public Map<String, Object> getReport(final Date date) throws DataAccessServiceException, SQLException {
		Map<String, Object> result = new HashMap<String, Object>();

		Calendar cln = Calendar.getInstance();
		cln.setTime(date);

		Date toDay = DateUtility.createDate(cln.get(Calendar.YEAR), cln.get(Calendar.MONTH) + 1, cln.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		Date fromDay = DateUtility.getDayOfAddDay(toDay, -1);

		Parameter params = new Parameter();
		params.put("fromDate", new java.sql.Date(fromDay.getTime()));
		params.put("toDate", new java.sql.Date(toDay.getTime()));

		DataAccessObject dao = null;

		dao = getDao(DSQL_S10, params);
		long cntRegistContent = dao.count();

		dao = getDao(DSQL_S11, params);
		long cntDownloadContent = dao.count();

		result.put("registContent", cntRegistContent);
		result.put("downloadContent", cntDownloadContent);

		return result;
	}

}
