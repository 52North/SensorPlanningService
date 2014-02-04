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
package org.n52.sps.control.kvp;

import net.opengis.sps.x20.CapabilitiesDocument;
import net.opengis.sps.x20.GetCapabilitiesDocument;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.OperationNotSupportedException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.parameter.KeyValuePairParameter;
import org.n52.oxf.xmlbeans.SwesXmlUtil;
import org.n52.sps.control.RequestDelegationHandler;
import org.n52.sps.service.SensorPlanningService;
import org.n52.sps.service.core.BasicSensorPlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyValuePairsDelegate implements RequestDelegationHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyValuePairsDelegate.class);

    private SensorPlanningService service;
    
    private KeyValuePairsWrapper kvpParser;

    public KeyValuePairsDelegate(SensorPlanningService service, KeyValuePairsWrapper kvpParser) {
        this.service = service;
        this.kvpParser = kvpParser;
    }

    public XmlObject delegate() throws OwsException, OwsExceptionReport {
        String request = kvpParser.getMandatoryParameterValue(KeyValuePairParameter.REQUEST.name());
        if (request.isEmpty()) {
            throw new InvalidParameterValueException(KeyValuePairParameter.REQUEST.name());
        }
        
        if (request.equalsIgnoreCase(BasicSensorPlanner.GET_CAPABILITIES)) {
            GetCapabilitiesConverter converter = new GetCapabilitiesConverter(kvpParser);
            GetCapabilitiesDocument getCapabilities = converter.convert();
            service.validateGetCapabilitiesParameters(getCapabilities);
            SwesXmlUtil.validateSwesRequest(getCapabilities);
            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
            XmlObject capabilities = basicSensorPlanner.getCapabilities(getCapabilities);
            service.interceptCapabilities((CapabilitiesDocument) capabilities);
            return capabilities;
        }
        
         // TODO other requests are not supported for now for KVP binding
        
        LOGGER.error("Could not find request delegate for request '{}'.", request);
        throw new OperationNotSupportedException(request);
    }
    
}
