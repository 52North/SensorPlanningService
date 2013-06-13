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
