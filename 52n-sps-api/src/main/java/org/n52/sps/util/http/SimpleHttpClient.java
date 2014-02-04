/**
 * ﻿Copyright (C) 2012-${latestYearOfContribution} 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.sps.util.http;

import java.io.IOException;
import java.net.ProxySelector;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);

    private static final int DEFAULT_TIMEOUT = 30000; // 30s
    private DefaultHttpClient httpclient = new DefaultHttpClient();
    private int timeout;

    public SimpleHttpClient() {
        this(DEFAULT_TIMEOUT);
    }
    
    public SimpleHttpClient(int timeout) {
        this.timeout = timeout;
        initProxyAwareClient();
    }

    private void initProxyAwareClient() {
        ClientConnectionManager connectionManager = httpclient.getConnectionManager();
        SchemeRegistry schemeRegistry = connectionManager.getSchemeRegistry();
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schemeRegistry, defaultProxySelector);
        httpclient.setRoutePlanner(routePlanner);
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
    }
    
    public HttpEntity executeMethod(HttpRequestBase method) throws ClientProtocolException, IOException {
        return httpclient.execute(method).getEntity();
    }
    
    public HttpEntity executeGetMethod(String uri) throws ClientProtocolException, IOException {
        LOGGER.debug("executing GET method '{}'", uri);
        return executeMethod(new HttpGet(uri));
    }
}
