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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.NoApplicableCodeException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.oxf.swes.exception.InvalidRequestException;
import org.n52.sps.control.RequestDelegationHandler;
import org.n52.sps.control.ServiceController;
import org.n52.sps.control.kvp.KeyValuePairsDelegate;
import org.n52.sps.control.kvp.KeyValuePairsWrapper;
import org.n52.sps.service.SensorPlanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value= "/*")
public class XmlController extends ServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlController.class);
    
    @Autowired
    public XmlController(SensorPlanningService service, HttpBinding httpBinding) {
        super(service, httpBinding);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleGETRequest(@RequestParam Map<String, String> parameters, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("xmlview", "response", null);
        OwsExceptionReport exceptionReport = new OwsExceptionReport();
        try {
            KeyValuePairsWrapper kvpParser = new KeyValuePairsWrapper(parameters);
            RequestDelegationHandler handler = new KeyValuePairsDelegate(service, kvpParser);
            mav.addObject(handler.delegate());
            response.setStatus(HttpStatus.OK.value());
        } catch(OwsException e) {
            // REQ 2: http://www.opengis.net/spec/SPS/2.0/req/exceptions
            LOGGER.info("Could not handle KVP request.", e);
            exceptionReport.addOwsException(e);
        } catch(OwsExceptionReport e) {
            LOGGER.info("Could not handle KVP request.", e);
            exceptionReport = e;
        } catch(Throwable e) {
            // TODO extract to Spring ExceptionHandler
            LOGGER.error("Internal exception occured!", e);
            exceptionReport.addOwsException(new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR));
        }
        handleServiceExceptionReport(response, mav, exceptionReport);
        LOGGER.debug(mav.toString());
        return mav;
    }
    
    @RequestMapping(method=RequestMethod.POST /*, headers = "content-type=application/xml; text/xml"*/)
    public ModelAndView handlePOSTRequest(InputStream payload, HttpServletResponse response, HttpServletRequest request) throws OwsException {
        ModelAndView mav = new ModelAndView("xmlview", "response", null);
        OwsExceptionReport exceptionReport = new OwsExceptionReport();
        try {
            XmlObject xmlPayload = parseIncomingXmlObject(payload);
            RequestDelegationHandler requestHandler = createRequestHandler(xmlPayload);
            mav.addObject(requestHandler.delegate());
            response.setStatus(HttpStatus.OK.value());
        } catch (XmlException e) {
            LOGGER.error("Could not parse request.", e);
            exceptionReport.addOwsException(new InvalidRequestException(e.getMessage()));
        } catch (IOException e) {
            LOGGER.error("Could not read request.", e);
            int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            exceptionReport.addOwsException(new NoApplicableCodeException(code));
        } catch(OwsException e) {
            // REQ 2: http://www.opengis.net/spec/SPS/2.0/req/exceptions
            LOGGER.info("Could not handle POST request.", e);
            exceptionReport.addOwsException(e);
        } catch(OwsExceptionReport e) {
            LOGGER.info("Could not handle POST request.", e);
            exceptionReport = e;
        } catch(Throwable e) {
            // TODO extract to Spring ExceptionHandler
            LOGGER.error("Unknown exception occured!", e);
            exceptionReport.addOwsException(new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR));
        }
        handleServiceExceptionReport(response, mav, exceptionReport);
        LOGGER.debug(mav.toString());
        return mav;
    }

    private RequestDelegationHandler createRequestHandler(XmlObject xmlPayload) throws OwsException {
        XmlDelegate plainDelegate = new XmlDelegate(service, xmlPayload);
        return new XmlValidationDelegate(plainDelegate);
    }

    private XmlObject parseIncomingXmlObject(InputStream payload) throws XmlException, IOException {
        // @see https://issues.apache.org/jira/browse/XMLBEANS-226
        // @see http://commons.apache.org/io/api-release/org/apache/commons/io/input/AutoCloseInputStream.html
        return XmlObject.Factory.parse(new AutoCloseInputStream(payload));
    }

    public void destroy() throws Exception {
        // TODO Auto-generated method stub
        
    }

}
