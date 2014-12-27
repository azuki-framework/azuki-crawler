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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.azkfw.business.dao.DataAccessObject;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.dsql.Parameter;
import org.azkfw.business.logic.AbstractDynamicSQLLogic;
import org.azkfw.crawler.logic.WebCrawlerMaintenanceManager;
import org.azkfw.util.DateUtility;

/**
 * このクラスは、 Webクロール管理機能を実装するインターフェースです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
public class WebCrawlerMaintenanceManagerImpl extends AbstractDynamicSQLLogic implements WebCrawlerMaintenanceManager {

	private static String DSQL_S10 = "WebCrawlerMaintenanceManagerS01";
	private static String DSQL_S11 = "WebCrawlerMaintenanceManagerS02";

	/**
	 * コンストラクタ
	 */
	public WebCrawlerMaintenanceManagerImpl() {
		super(WebCrawlerMaintenanceManagerImpl.class);
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doDestroy() {

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
