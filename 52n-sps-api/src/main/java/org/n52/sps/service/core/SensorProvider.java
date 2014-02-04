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
package org.n52.sps.service.core;

import net.opengis.swes.x20.DescribeSensorDocument;
import net.opengis.swes.x20.DescribeSensorResponseDocument;

import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.service.CapabilitiesInterceptor;

/**
 * Describes functions required by Conformatance Class: http://www.opengis.net/spec/SPS/2.0/conf/Core
 */
public interface SensorProvider extends CapabilitiesInterceptor {

    final static String SENSOR_PROVIDER_CONFORMANCE_CLASS = "http://www.opengis.net/spec/SPS/2.0/conf/SensorProvider";

    final static String DESCRIBE_SENSOR = "DescribeSensor";

    /**
     * This operation allows a client to request a detailed description of a sensor. The request can be
     * targeted at a description that was valid at a certain point in or during a certain period of time in
     * the past [OGC 09-001 clause 11].
     * 
     * @param describeSensor
     *        the request
     * @return the sensor information
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public DescribeSensorResponseDocument describeSensor(DescribeSensorDocument describeSensor) throws OwsException, OwsExceptionReport;
}
