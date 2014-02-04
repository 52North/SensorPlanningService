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
package org.n52.sps.control.xml;

import javax.xml.namespace.QName;

import net.opengis.sps.x20.GetCapabilitiesDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.oxf.swes.exception.InvalidRequestException;
import org.n52.oxf.xmlbeans.SwesXmlUtil;
import org.n52.sps.control.RequestDelegationHandler;
import org.n52.sps.service.SensorPlanningService;

public class XmlValidationDelegate implements RequestDelegationHandler {
    
    private XmlDelegate requestHandler;
    private SensorPlanningService service;
    
    public XmlValidationDelegate(XmlDelegate toDecorate) {
        this.requestHandler = toDecorate;
        this.service = toDecorate.getService();
    }

    public XmlObject delegate() throws OwsException, OwsExceptionReport {
        assureCorrectServiceAndVersionRequestParameters(requestHandler.getRequest());
        validateSwesRequest(requestHandler.getRequest());
        return requestHandler.delegate();
    }

    private void validateSwesRequest(XmlObject swesRequest) throws OwsExceptionReport {
        try {
            SwesXmlUtil.validateSwesRequest(swesRequest);
        } catch (InvalidRequestException e) {
            throw new OwsExceptionReport(e);
        }
    }

    protected void assureCorrectServiceAndVersionRequestParameters(XmlObject request) throws OwsException {
        if (!isGetCapabilitiesRequest(request)) {
            validateRequiredServiceParameter(request);
            validateRequiredVersionParameter(request);
        } else {
            service.validateGetCapabilitiesParameters((GetCapabilitiesDocument) request);
        }
    }
    
    protected boolean isGetCapabilitiesRequest(XmlObject request) {
        XmlCursor xmlCursor = request.newCursor();
        xmlCursor.toFirstChild();
        String operationName = xmlCursor.getName().getLocalPart();
        return operationName.equalsIgnoreCase("GetCapabilities");
    }

    protected void validateRequiredServiceParameter(XmlObject requestDocument) throws OwsException {
        XmlCursor xmlCursor = requestDocument.newCursor();
        xmlCursor.toFirstChild();
        String serviceParameter = "service";
        if (moveToAttribute(xmlCursor, serviceParameter)) {
            validateServiceParameter(xmlCursor.getTextValue());
        }
    }
    
    protected void validateRequiredVersionParameter(XmlObject requestDocument) throws OwsException {
        XmlCursor xmlCursor = requestDocument.newCursor();
        xmlCursor.toFirstChild();
        if (moveToAttribute(xmlCursor, "version")) {
            service.validateVersionParameter(xmlCursor.getTextValue());
        }
    }

    private boolean moveToAttribute(final XmlCursor xmlCursor, final String attribute) {
        while (moveToNextAttribute(xmlCursor)) {
            if (isOnAttribute(attribute, xmlCursor)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean moveToNextAttribute(final XmlCursor xmlCursor) {
        return xmlCursor.toFirstAttribute() || xmlCursor.toNextAttribute();
    }

    private boolean isOnAttribute(final String attribute, final XmlCursor xmlCursor) {
        return xmlCursor.isAttr() && xmlCursor.getName().equals(new QName(attribute));
    }

    protected void validateServiceParameter(String serviceParameter) throws OwsException {
        service.validateMandatoryServiceParameter(serviceParameter);
    }


}
