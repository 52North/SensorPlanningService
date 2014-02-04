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
package org.n52.sps.control.admin;

import static org.n52.sps.service.admin.SpsAdmin.DELETE_RESOURCE;
import static org.n52.sps.service.admin.SpsAdmin.INSERT_RESOURCE;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.NoApplicableCodeException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.oxf.swes.exception.InvalidRequestException;
import org.n52.sps.control.ServiceController;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.service.SensorPlanningService;
import org.n52.sps.service.admin.SpsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument;

@Controller
public class AdminControl extends ServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminControl.class);
    
    private XmlObject successResponse;

    @Autowired
    public AdminControl(SensorPlanningService service, HttpBinding httpBinding) {
        super(service, httpBinding);
        if (service.isSpsAdminAvailable()) {
            service.getSpsAdmin().setExtensionBinding(httpBinding);
        }
    }

    @RequestMapping(value = INSERT_RESOURCE, method = RequestMethod.POST, headers = "content-type=application/xml; text/xml")
    public ModelAndView registerSensorRequest(InputStream payload, HttpServletResponse response) throws InternalServiceException {
        ModelAndView mav = new ModelAndView("xmlview", "response", null);
        OwsExceptionReport exceptionReport = new OwsExceptionReport(); // re-use reporting concept
        try {
            if (!service.isSpsAdminAvailable()) {
                LOGGER.error("register (SensorOffering)");
                throw new NoApplicableCodeException(OwsException.BAD_REQUEST);
            } else {
                XmlObject request = parseIncomingXmlObject(payload);
                if (request.schemaType() != InsertSensorOfferingDocument.type) {
                    throw new InvalidRequestException("Sent XML is not of type InsertSensorOffering.");
                }
                SpsAdmin spsAdminOperator = service.getSpsAdmin();
                spsAdminOperator.insertSensorOffering((InsertSensorOfferingDocument) request, exceptionReport);
                mav.addObject(getSuccessResponse());
                response.setStatus(HttpStatus.OK.value());
            }
        }
        catch (XmlException e) {
            LOGGER.error("Could not parse request.", e);
            exceptionReport.addOwsException(new InvalidRequestException(e.getMessage()));
        }
        catch (IOException e) {
            LOGGER.error("Could not read request.", e);
            int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
           exceptionReport.addOwsException(new NoApplicableCodeException(code));
        }
        catch (OwsException e) {
            LOGGER.error("Could not handle POST request.", e);
            exceptionReport.addOwsException(e);
        } catch (Throwable e) {
            LOGGER.error("Unexpected Error occured.", e);
            int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            exceptionReport.addOwsException(new NoApplicableCodeException(code));
        }
        handleServiceExceptionReport(response, mav, exceptionReport);
        response.setContentType("application/xml");
        return mav;
    }

    @RequestMapping(value = DELETE_RESOURCE, method = RequestMethod.POST, headers = "content-type=application/xml; text/xml")
    public ModelAndView deleteSensorRequest(InputStream payload, HttpServletResponse response) throws InternalServiceException {
        ModelAndView mav = new ModelAndView("xmlview", "response", null);
        OwsExceptionReport exceptionReport = new OwsExceptionReport(); // re-use reporting concept
        try {
            if (!service.isSpsAdminAvailable()) {
                LOGGER.error("delete (SensorOffering)");
                throw new NoApplicableCodeException(OwsException.BAD_REQUEST);   
            } else {
                XmlObject request = parseIncomingXmlObject(payload);
//                if (request.schemaType() ==) {
//                    
//                }
                SpsAdmin spsAdminOperator = service.getSpsAdmin();
                spsAdminOperator.deleteSensorOffering(request, exceptionReport);
                mav.addObject(getSuccessResponse());
                response.setStatus(HttpStatus.OK.value());
            }
        }
        catch (XmlException e) {
            LOGGER.error("Could not parse request.", e);
            exceptionReport.addOwsException(new InvalidRequestException(e.getMessage()));
        }
        catch (IOException e) {
            LOGGER.error("Could not read request.", e);
            int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            exceptionReport.addOwsException(new NoApplicableCodeException(code));
        }
        catch (OwsException e) {
            LOGGER.info("Could not handle POST request.", e);
            exceptionReport.addOwsException(e);
        }
        handleServiceExceptionReport(response, mav, exceptionReport);
        response.setContentType("application/xml");
        return mav;
    }

    public void destroy() throws Exception {
        // TODO Auto-generated method stub
        
    }

    private XmlObject parseIncomingXmlObject(InputStream payload) throws XmlException, IOException {
        // fixes https://issues.apache.org/jira/browse/XMLBEANS-226
        // @see http://commons.apache.org/io/api-release/org/apache/commons/io/input/AutoCloseInputStream.html
        XmlObject request = XmlObject.Factory.parse(new AutoCloseInputStream(payload));
        return request;
    }

    private XmlObject getSuccessResponse() throws XmlException {
        if (successResponse == null) {
            successResponse = XmlObject.Factory.parse("<?xml version='1.0'?><success />");
        }
        return successResponse;
    }

}
