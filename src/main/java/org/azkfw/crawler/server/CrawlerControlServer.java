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
package org.azkfw.crawler.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.azkfw.crawler.CrawlerServer;
import org.azkfw.crawler.config.CrawlerConfig.CrawlerControllerConfig;
import org.azkfw.lang.LoggingObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * このクラスは、クローラのコントロールサーバクラスです。
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/14
 * @author Kawakicchi
 */
public class CrawlerControlServer extends LoggingObject implements HttpHandler {

	private CrawlerServer server;
	private CrawlerControllerConfig config;

	private HttpServer httpServer;

	public CrawlerControlServer(final CrawlerServer aServer, final CrawlerControllerConfig aConfig) {
		super(CrawlerControlServer.class);

		server = aServer;
		config = aConfig;
	}

	public boolean start() {
		boolean result = false;
		try {
			httpServer = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
			httpServer.createContext(config.getContextpath(), this);
			httpServer.start();

			result = true;
		} catch (IOException ex) {
			fatal(ex);
		}
		return result;
	}

	public void stop(final int sec) {
		httpServer.stop(sec); // 5秒
	}

	// HttpHandler /////////////////////////////////////////////////////////////////////////////////////

	private static final byte[] NOT_FOUND = "<h1>404 Not Found</h1>No context found for request".getBytes();
	private static final byte[] SERVER_ERROR = "<html><head><title>500 - Error</title></head><body>500 - Error</body></html>".getBytes();

	@Override
	public void handle(final HttpExchange exchange) throws IOException {
		String url = exchange.getRequestURI().toString();
		url = url.substring(config.getContextpath().length());
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		int index = url.indexOf("?");
		String areas;
		if (-1 == index) {
			areas = url;
		} else {
			areas = url.substring(0, index);
		}

		OutputStream out = exchange.getResponseBody();
		try {
			String clientName = exchange.getRemoteAddress().getHostName();
			if (!"127.0.0.1".equals(clientName) && !"localhost".equals(clientName)) {
				exchange.sendResponseHeaders(404, NOT_FOUND.length);
				out.write(NOT_FOUND);
				
			} else if ("/active".equals(areas)) {
				String html = "Crawler server active.";
				byte[] buf = html.getBytes(config.getCharset());

				exchange.sendResponseHeaders(200, buf.length);
				out.write(buf);
			} else if ("/stop".equals(areas)) {
				server.requestStop();

				String html = "Success!!";
				byte[] buf = html.getBytes(config.getCharset());

				exchange.sendResponseHeaders(200, buf.length);
				out.write(buf);
			} else {
				exchange.sendResponseHeaders(404, NOT_FOUND.length);
				out.write(NOT_FOUND);
			}
			
			out.flush();
		} catch (Exception ex) {
			fatal(ex);

			try {
				exchange.sendResponseHeaders(500, SERVER_ERROR.length);
				out.write(SERVER_ERROR);
				out.flush();

			} catch (IOException ex2) {
				fatal(ex2);
			}

		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException ex) {
					warn(ex);
				}
			}
			exchange.close();
		}
	}
}
