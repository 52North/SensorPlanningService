/**
 * Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
