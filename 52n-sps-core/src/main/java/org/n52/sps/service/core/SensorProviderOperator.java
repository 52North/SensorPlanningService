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

import java.util.List;

import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.swes.x20.DescribeSensorDocument;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.DescribeSensorType;
import net.opengis.swes.x20.ExtensibleRequestType;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.oxf.swes.exception.RequestExtensionNotSupportedException;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.service.SpsOperator;
import org.n52.sps.util.xmlbeans.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorProviderOperator extends SpsOperator implements SensorProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorProviderOperator.class);

	public DescribeSensorResponseDocument describeSensor(DescribeSensorDocument describeSensor) throws OwsException, OwsExceptionReport {
	    LOGGER.debug("describeSensor: {}" + describeSensor.xmlText(XmlHelper.DEBUG_OPTIONS));
	    checkSupportingSwesRequestExtensions(describeSensor.getExtensibleRequest());
	    checkForValidParameters(describeSensor.getDescribeSensor());
        String procedure = describeSensor.getDescribeSensor().getProcedure();
        String format = describeSensor.getDescribeSensor().getProcedureDescriptionFormat();
        
        DescribeSensorResponseDocument response = DescribeSensorResponseDocument.Factory.newInstance();
        DescribeSensorResponseType describeSensorResponse = response.addNewDescribeSensorResponse();
        describeSensorResponse.setProcedureDescriptionFormat(format);

        SensorPlugin sensorPlugin = getSensorInstance(procedure);
        SensorConfiguration sensorConfiguration = sensorPlugin.getSensorConfiguration();
        SensorDescriptionDownloadHandler downloadHandler = new SensorDescriptionDownloadHandler(sensorConfiguration);
		downloadHandler.addSensorDescriptions(describeSensorResponse, format);
		
		return response;
    }
    
    private void checkForValidParameters(DescribeSensorType describeSensor) throws OwsException {
        if (describeSensor.isSetValidTime()) {
            String[] messages = {
                     "http://www.opengis.net/spec/SWES/2.0/req/DescribeSensor/exception/validTimeNotSupported",
                     String.format("The service does not act as history provider.")
            };
            throwNewOptionNotSupportedException("validTime", messages);
        }
        String procedure = describeSensor.getProcedure();
        if (isParameterValueMissing(procedure)) {
            throwNewMissingParamterValueException("procedure");
        }
        String format = describeSensor.getProcedureDescriptionFormat();
        if (isParameterValueMissing(format)) {
            throwNewMissingParamterValueException("procedureDescriptionFormat");
        }
        SensorPlugin sensorPlugin = getSensorInstance(procedure);
        SensorConfiguration sensorConfiguration = sensorPlugin.getSensorConfiguration();
        if (!sensorConfiguration.supportsProcedureDescriptionFormat(format)) {
            String[] messages = {
                     "http://www.opengis.net/spec/SWES/2.0/req/DescribeSensor/exception/unknownProcedureDescriptionFormat",
                     String.format("The requested description format '%s' is not supported", format)
            };
            throwNewInvalidParameterValueException("procedureDescriptionFormat", format, messages);
        }
    }

    public void interceptCapabilities(CapabilitiesType capabilities, List<HttpBinding> httpBindings) {
        capabilities.getServiceIdentification().addNewProfile().setStringValue(SENSOR_PROVIDER_CONFORMANCE_CLASS);
        insertOperationMetadata(capabilities, httpBindings);
        insertSpsContents(capabilities);
    }

    private void insertOperationMetadata(CapabilitiesType capabilities, List<HttpBinding> httpBindings) {
        OperationsMetadata operationsMetadata = getOperationsMetadata(capabilities);
        addDescribeSensorOperation(operationsMetadata, httpBindings);
    }

    private void addDescribeSensorOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation describeSensorOperation = operationsMetadata.addNewOperation();
        describeSensorOperation.setName(DESCRIBE_SENSOR);
        addSupportedBindings(describeSensorOperation, httpBindings);
    }

    private void insertSpsContents(CapabilitiesType capabilities) {
        // TODO Auto-generated method stub
        
        // TODO what kind of information can the SensorProviderOperator component provide for the contents section?
    }

    @Override
    public boolean isSupportingExtensions() {
        return false;
    }

    @Override
    protected void checkSupportingSpsRequestExtensions(XmlObject[] extensions) {
        OwsExceptionReport owsExceptionReport = new OwsExceptionReport();
        if (extensions != null && extensions.length > 0) {
            for (XmlObject extension : extensions) {
                RequestExtensionNotSupportedException e = new RequestExtensionNotSupportedException();
                e.addExceptionText("RequestExtension is not supported: " + extension.xmlText());
                owsExceptionReport.addOwsException(e);
            }
        }
    }

    @Override
    protected void checkSupportingSwesRequestExtensions(ExtensibleRequestType extensibleRequest) throws OwsExceptionReport {
        XmlObject[] extensionArray = extensibleRequest.getExtensionArray();
        if (extensionArray != null && extensionArray.length > 0) {
            // operator does not support any extension
            OwsExceptionReport owsExceptionReport = new OwsExceptionReport();
            owsExceptionReport.addOwsException(new RequestExtensionNotSupportedException());
            throw owsExceptionReport;
        }
    }

}
