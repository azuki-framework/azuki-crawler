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
package org.azkfw.crawler.task;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.azkfw.biz.crypto.Crypto;
import org.azkfw.biz.crypto.CryptoFactory;
import org.azkfw.business.BusinessServiceException;
import org.azkfw.business.dao.DataAccessServiceException;
import org.azkfw.business.property.Property;
import org.azkfw.business.property.PropertyFile;
import org.azkfw.crawler.CrawlerServiceException;
import org.azkfw.crawler.lang.CrawlerSetupException;
import org.azkfw.crawler.logic.WebCrawlerMaintenanceManager;
import org.azkfw.util.DateUtility;
import org.azkfw.util.MapUtility;

/**
 * このクラスは、スタントアロンでWebクロールを行うクローラタスククラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/07/10
 * @author Kawakicchi
 */
@PropertyFile("conf/StandAloneWebCrawleReport.properties")
public final class StandAloneWebCrawleReportTask extends StandAloneWebCrawleTask {

	private String userName;
	private String password;

	private String tmplMailSubject;
	private String tmplMailToName;
	private String tmplMailToAddress;
	private String tmplMailFromName;
	private String tmplMailFromAddress;

	/**
	 * コンストラクタ
	 */
	public StandAloneWebCrawleReportTask() {
		super(StandAloneWebCrawleReportTask.class);
	}

	@Override
	protected void doSetup() throws CrawlerSetupException {
		Property p = getProperty();

		try {
			Crypto c = CryptoFactory.createAES();
			userName = c.decrypt(p.getString("mail.userName"));
			password = c.decrypt(p.getString("mail.password"));

			tmplMailSubject = p.getString("mail.subject");
			tmplMailToName = p.getString("mail.to.name");
			tmplMailToAddress = p.getString("mail.to.address");
			tmplMailFromName = p.getString("mail.from.name");
			tmplMailFromAddress = p.getString("mail.from.address");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	protected void doStartup() {
	}

	@Override
	protected void doShutdown() {
	}

	@Override
	protected void doInitialize() {

	}

	@Override
	protected void doRelease() {

	}

	@Override
	protected CrawlerTaskResult doExecute() throws CrawlerServiceException {
		CrawlerTaskResult result = new CrawlerTaskResult();

		try {
			WebCrawlerMaintenanceManager manager = (WebCrawlerMaintenanceManager) getLogic("WebCrawlerMaintenanceManager");

			Date date = new Date();
			Calendar cln = Calendar.getInstance();
			cln.setTime(DateUtility.getDayOfAddDay(date, -1));
			String dateString = String.format("%04d/%02d/%02d", cln.get(Calendar.YEAR), cln.get(Calendar.MONTH) + 1, cln.get(Calendar.DAY_OF_MONTH));

			String mailFromAddress = tmplMailFromAddress;
			String mailFromName = tmplMailFromName.replaceAll("\\$\\{date\\}", dateString);
			String mailSubject = tmplMailSubject.replaceAll("\\$\\{date\\}", dateString);

			String ln = "\r\n";
			Map<String, Object> report = manager.getReport(date);
			long regist = MapUtility.getLong(report, "registContent", -1L);
			long download = MapUtility.getLong(report, "downloadContent", -1L);

			StringBuffer s = new StringBuffer();
			s.append(dateString).append(ln);
			s.append(String.format("登録コンテンツ数：%d", regist)).append(ln);
			s.append(String.format("ダンロードコンテンツ数：%d", download)).append(ln);

			Properties property = new Properties();
			//GmailのSMTPを使う場合
			property.put("mail.smtp.host", "smtp.gmail.com");
			property.put("mail.smtp.auth", "true");
			property.put("mail.smtp.starttls.enable", "true");
			property.put("mail.smtp.host", "smtp.gmail.com");
			property.put("mail.smtp.port", "587");
			property.put("mail.smtp.debug", "true");
			Session session = Session.getInstance(property, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			try {
				MimeMessage mimeMessage = new MimeMessage(session);
				InternetAddress toAddress = new InternetAddress(tmplMailToAddress, tmplMailToName);
				mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
				InternetAddress fromAddress = new InternetAddress(mailFromAddress, mailFromName);

				mimeMessage.setFrom(fromAddress);
				mimeMessage.setSubject(mailSubject, "ISO-2022-JP");
				mimeMessage.setText(s.toString(), "ISO-2022-JP");

				Transport.send(mimeMessage);
			} catch (UnsupportedEncodingException ex) {
				fatal(ex);
			} catch (MessagingException ex) {
				fatal(ex);
			}

			result.setResult(true);
			result.setStop(false);
		} catch (SQLException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		} catch (DataAccessServiceException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		} catch (BusinessServiceException ex) {
			fatal(ex);

			result.setResult(false);
			result.setStop(true);
		}
		return result;
	}

}
