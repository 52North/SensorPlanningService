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
package org.n52.sps.service.description;

import net.opengis.swes.x20.UpdateSensorDescriptionDocument;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.service.CapabilitiesInterceptor;

public interface SensorDescriptionManager extends CapabilitiesInterceptor {

    final static String SENSOR_HISTORY_PROVIDER_CONFORMANCE_CLASS = "http://www.opengis.net/spec/SWES/2.0/conf/SensorHistoryProvider";

    final static String UPDATE_SENSOR_DESCRIPTION = "UpdateSensorDescription";

    // TODO determine implementation cycle for SensorDescriptionManager

    /**
     * @param updateSensorDescription
     * @return
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public XmlObject updateSensorDescription(UpdateSensorDescriptionDocument updateSensorDescription) throws OwsException, OwsExceptionReport;
}
