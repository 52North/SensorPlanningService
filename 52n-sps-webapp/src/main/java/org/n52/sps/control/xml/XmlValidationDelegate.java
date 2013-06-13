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
