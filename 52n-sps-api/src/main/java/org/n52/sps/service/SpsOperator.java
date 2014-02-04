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
package org.n52.sps.service;

import java.util.List;

import net.opengis.ows.x11.DCPDocument.DCP;
import net.opengis.ows.x11.HTTPDocument;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.swes.x20.ExtensibleRequestType;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.MissingParameterValueException;
import org.n52.ows.exception.OptionNotSupportedException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.oxf.swes.exception.RequestExtensionNotSupportedException;
import org.n52.sps.sensor.NonBlockingTaskingDecorator;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.core.SensorInstanceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpsOperator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpsOperator.class);

    private SensorInstanceProvider sensorInstanceProvider;

    protected SensorTask getSensorTask(String sensorTaskId) throws OwsException {
        if (!sensorInstanceProvider.containsTaskWith(sensorTaskId)) {
            handleInvalidParameter("task", sensorTaskId);
        }
        return sensorInstanceProvider.getTaskForTaskId(sensorTaskId);
    }

    /**
     * Gets the sensor instance for a given procedure. If the procedure is not known to the service an 
     * {@link InvalidParameterValueException} is thrown as required by the SPS specification.<br/>
     * <br/>
     * To avoid a freezing framework when a sensor instance gets stuck executing (tasking) requests the
     * returned {@link SensorPlugin} instance is decorated to enable non blocking execution via a 
     * {@link NonBlockingTaskingDecorator}.
     * 
     * @param procedure
     *        the <code>procedure</code> to lookup SensorPlugin instance for.
     * @return the SensorPlugin instance associated with the given <code>procedure</code>
     * @throws InvalidParameterValueException
     *         if no SensorPlugin instance could be found (according to REQ 4: <a
     *         href="http://www.opengis.net/spec/SPS/2.0/req/exceptions/UnknownIdentifier"
     *         >http://www.opengis.net/spec/SPS/2.0/req/exceptions/UnknownIdentifier</a>)
     * @throws MissingParameterValueException
     *         if an empty procedure parameter was given (according to REQ 4: <a
     *         href="http://www.opengis.net/spec/SWES/2.0/req/SOAP/Fault/MissingParameterValueException"
     *         >http://www.opengis.net/spec/SWES/2.0/req/SOAP/Fault/MissingParameterValueException</a>)
     */
    public SensorPlugin getSensorInstance(String procedure) throws OwsException {
        if (!sensorInstanceProvider.containsSensorWith(procedure)) {
            handleInvalidParameter("procedure", procedure);
        }
        SensorPlugin sensorPlugin = sensorInstanceProvider.getSensorForProcedure(procedure);
        return NonBlockingTaskingDecorator.enableNonBlockingTasking(sensorPlugin);
    }

    private void handleInvalidParameter(String locator, String parameter) throws MissingParameterValueException, InvalidParameterValueException {
        if (isParameterValueMissing(parameter)) {
            LOGGER.info("Task parameter is missing.");
            throwNewMissingParamterValueException(locator);
        } else {
            LOGGER.info("Invalid parameter: {}", parameter);
            throwNewInvalidParameterValueException(locator, parameter);
        }
    }

    protected boolean isParameterValueMissing(String parameter) {
        return parameter == null || parameter.isEmpty();
    }

    protected void throwNewMissingParamterValueException(String locator, String... messages) throws MissingParameterValueException {
        MissingParameterValueException e = new MissingParameterValueException(locator);
        e.addExceptionText(String.format("The %s paramter is missing.", locator));
        addDetailedMessagesToOwsException(e, messages);
        throw e;
    }

    protected void throwNewInvalidParameterValueException(String locator, String parameterValue, String... messages) throws InvalidParameterValueException {
        InvalidParameterValueException e = new InvalidParameterValueException(locator);
        e.addExceptionText(String.format("'%s' is unknown.", parameterValue));
        addDetailedMessagesToOwsException(e, messages);
        throw e;
    }
    
    protected void throwNewOptionNotSupportedException(String locator, String... messages) throws OptionNotSupportedException {
        OptionNotSupportedException e = new OptionNotSupportedException(locator);
        addDetailedMessagesToOwsException(e, messages);
        throw e;
    }

    private void addDetailedMessagesToOwsException(OwsException e, String[] messages) {
        for (String message : messages) {
            e.addExceptionText(message);
        }
    }


    public SensorInstanceProvider getSensorInstanceProvider() {
        return this.sensorInstanceProvider;
    }

    public void setSensorInstanceProvider(SensorInstanceProvider sensorInstanceProvider) {
        this.sensorInstanceProvider = sensorInstanceProvider;
    }

    protected OperationsMetadata getOperationsMetadata(CapabilitiesType capabilities) {
        OperationsMetadata operationsMetadata = capabilities.getOperationsMetadata();
        if (operationsMetadata == null) {
            operationsMetadata = capabilities.addNewOperationsMetadata();
        }
        return operationsMetadata;
    }

    /**
     * @param operation
     *        the operation for which to add each binding
     * @param httpBindings
     *        all bindings supported by the service.
     */
    protected void addSupportedBindings(Operation operation, List<HttpBinding> httpBindings) {
        for (HttpBinding httpBinding : httpBindings) {
            addDcpBinding(operation, httpBinding);
        }
    }

    protected void addDcpBinding(Operation operation, HttpBinding httpBinding) {
        HTTPDocument httpInfo = httpBinding.getHttpInfo();
        addDistributedComputingPlatform(operation, httpInfo);
    }

    protected void addDcpExtensionBinding(Operation operation, HttpBinding httpBinding, String resource) {
        HTTPDocument httpInfo = httpBinding.getHttpInfo(resource);
        addDistributedComputingPlatform(operation, httpInfo);
    }

    private void addDistributedComputingPlatform(Operation operation, HTTPDocument httpInfo) {
        DCP distributedComputingPlatform = operation.addNewDCP();
        distributedComputingPlatform.setHTTP(httpInfo.getHTTP());
    }

    /**
     * @return <code>true</code> if the implementing operator supports extension(s), <code>false</code>
     *         otherwise.
     */
    public abstract boolean isSupportingExtensions();

    /**
     * Checks if the passed extension array is supported. For each unsupported extension found, a
     * {@link RequestExtensionNotSupportedException} is added to an {@link OwsExceptionReport} which 
     * is thrown at the end.
     * 
     * @param extensions
     *        the extensions to check.
     * @throws OwsExceptionReport
     *        if there are unsupported extensions.
     */
    protected abstract void checkSupportingSpsRequestExtensions(XmlObject[] extensions) throws OwsExceptionReport;

    /**
     * Checks if the passed extension array is supported. If a not supported extension is found, a
     * {@link RequestExtensionNotSupportedException} is added to the given {@link OwsExceptionReport}
     * 
     * @param extensibleRequest
     *        the extensible request.
     * @throws OwsExceptionReport
     *        if there are unsupported extensions.
     */
    protected abstract void checkSupportingSwesRequestExtensions(ExtensibleRequestType extensibleRequest) throws OwsExceptionReport;

}
