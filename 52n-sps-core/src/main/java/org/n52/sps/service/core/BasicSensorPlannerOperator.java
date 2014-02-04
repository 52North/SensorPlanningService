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
import net.opengis.sps.x20.CapabilitiesDocument;
import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.sps.x20.DescribeResultAccessDocument;
import net.opengis.sps.x20.DescribeResultAccessResponseDocument;
import net.opengis.sps.x20.DescribeResultAccessResponseType;
import net.opengis.sps.x20.DescribeResultAccessType.Target;
import net.opengis.sps.x20.DescribeTaskingDocument;
import net.opengis.sps.x20.DescribeTaskingResponseDocument;
import net.opengis.sps.x20.DescribeTaskingResponseType;
import net.opengis.sps.x20.DescribeTaskingResponseType.TaskingParameters;
import net.opengis.sps.x20.GetCapabilitiesDocument;
import net.opengis.sps.x20.GetCapabilitiesType;
import net.opengis.sps.x20.GetStatusDocument;
import net.opengis.sps.x20.GetStatusResponseDocument;
import net.opengis.sps.x20.GetStatusResponseType;
import net.opengis.sps.x20.GetTaskDocument;
import net.opengis.sps.x20.GetTaskResponseDocument;
import net.opengis.sps.x20.GetTaskResponseType;
import net.opengis.sps.x20.GetTaskResponseType.Task;
import net.opengis.sps.x20.GetTaskType;
import net.opengis.sps.x20.SubmitDocument;
import net.opengis.sps.x20.SubmitResponseDocument;
import net.opengis.sps.x20.SubmitResponseType;
import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swes.x20.ExtensibleRequestType;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OptionNotSupportedException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.oxf.swes.exception.RequestExtensionNotSupportedException;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.StatusReportGenerator;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.SpsOperator;
import org.n52.sps.tasking.SubmitTaskingRequest;
import org.n52.sps.util.xmlbeans.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicSensorPlannerOperator extends SpsOperator implements BasicSensorPlanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSensorPlannerOperator.class);

    public DescribeResultAccessResponseDocument describeResultAccess(DescribeResultAccessDocument describeResultAccess) throws OwsException, OwsExceptionReport {
        LOGGER.debug("describeResultAccess: {}", describeResultAccess.xmlText(XmlHelper.DEBUG_OPTIONS));
        checkSupportingSwesRequestExtensions(describeResultAccess.getExtensibleRequest());
        DescribeResultAccessResponseDocument responseDocument = DescribeResultAccessResponseDocument.Factory.newInstance();
        DescribeResultAccessResponseType resultAccessResponse = responseDocument.addNewDescribeResultAccessResponse();
        Target target = describeResultAccess.getDescribeResultAccess().getTarget();
        if(target.isSetProcedure()){
            SensorPlugin sensor = getSensorInstance(target.getProcedure());
        	resultAccessResponse.setAvailability(sensor.getResultAccessibility());
        } else {
            SensorTask sensorTask = getSensorTask(target.getTask());
            SensorPlugin sensor = getSensorInstance(sensorTask.getProcedure());
            resultAccessResponse.setAvailability(sensor.getResultAccessibilityFor(sensorTask));
        }
        return responseDocument;
    }

    public DescribeTaskingResponseDocument describeTasking(DescribeTaskingDocument describeTasking) throws OwsException, OwsExceptionReport {
        LOGGER.debug("describeTasking: {}", describeTasking.xmlText(XmlHelper.DEBUG_OPTIONS));
        checkSupportingSwesRequestExtensions(describeTasking.getExtensibleRequest());
        String procedure = describeTasking.getDescribeTasking().getProcedure();
        SensorPlugin sensorPlugin = getSensorInstance(procedure);
        SensorConfiguration sensorConfiguration = sensorPlugin.getSensorConfiguration();
        AbstractDataComponentType taskingParametersTemplate = sensorConfiguration.getTaskingParametersTemplate();
        DescribeTaskingResponseDocument responseDocument = DescribeTaskingResponseDocument.Factory.newInstance();
        DescribeTaskingResponseType describeTaskingResponse = responseDocument.addNewDescribeTaskingResponse();
        TaskingParameters taskingParameters = describeTaskingResponse.addNewTaskingParameters();
        taskingParameters.setAbstractDataComponent(taskingParametersTemplate);
        taskingParameters.setName(taskingParametersTemplate.getIdentifier());
        sensorPlugin.qualifyDataComponent(taskingParameters.getAbstractDataComponent());
        return responseDocument;
    }

    public CapabilitiesDocument getCapabilities(GetCapabilitiesDocument getCapabilities) throws OwsException, OwsExceptionReport {
        LOGGER.debug("getCapabilities: {}", getCapabilities.xmlText(XmlHelper.DEBUG_OPTIONS));
        return handleGetCapabilitiesRequest(getCapabilities.getGetCapabilities2());
    }

    private CapabilitiesDocument handleGetCapabilitiesRequest(GetCapabilitiesType getCapabilities) throws OwsException {
        checkSupportingSpsRequestExtensions(getCapabilities.getExtensionArray());
        return CapabilitiesHelper.createSpsCapabilities(getSensorInstanceProvider());
    }

    public GetStatusResponseDocument getStatus(GetStatusDocument getStatus) throws OwsException, OwsExceptionReport {
        LOGGER.debug("getStatus: {}", getStatus.xmlText(XmlHelper.DEBUG_OPTIONS));
        checkSupportingSwesRequestExtensions(getStatus.getExtensibleRequest());
        String sensorTaskId = getStatus.getGetStatus().getTask();
        SensorTask sensorTask = getSensorTask(sensorTaskId);
        
        if (getStatus.getGetStatus().isSetSince()) {
            // REQ 55: http://www.opengis.net/spec/SPS/2.0/req/GetStatus/service-metadata/since-parameter
            throw new OptionNotSupportedException("since");

            // if state logging shall be supported we have to make sure that each state transistion
            // is been stored in the task repository
        }

        StatusReportGenerator statusReporter = StatusReportGenerator.createFor(sensorTask);
        GetStatusResponseDocument responseDocument = GetStatusResponseDocument.Factory.newInstance();
        GetStatusResponseType statusResponse = responseDocument.addNewGetStatusResponse();
        statusResponse.addNewStatus().setStatusReport(statusReporter.generateWithoutTaskingParameters());
        return responseDocument;
    }

    public GetTaskResponseDocument getTask(GetTaskDocument getTask) throws OwsException, OwsExceptionReport {
        LOGGER.debug("getTask: {}", getTask.xmlText(XmlHelper.DEBUG_OPTIONS));
        checkSupportingSwesRequestExtensions(getTask.getExtensibleRequest());
        OwsExceptionReport owsExceptionReport = new OwsExceptionReport();
        GetTaskResponseDocument responseDocument = GetTaskResponseDocument.Factory.newInstance();
        GetTaskResponseType getTaskResponse = responseDocument.addNewGetTaskResponse();
        GetTaskType getTaskType = getTask.getGetTask();
        for (String taskId : getTaskType.getTaskArray()) {
            Task task = getTaskResponse.addNewTask();
            handleTaskContent(taskId, task, owsExceptionReport);
        }
        if (owsExceptionReport.containsExceptions()) {
            throw owsExceptionReport;
        }
        return responseDocument;
    }

    private void handleTaskContent(String taskId, Task task, OwsExceptionReport owsExceptionReport) {
        try {
            SensorTask sensorTask = getSensorTask(taskId);
            task.setTask(sensorTask.getTask());
        }
        catch (OwsException e) {
            owsExceptionReport.addOwsException(e);
        }
    }

    public SubmitResponseDocument submit(SubmitDocument submit) throws OwsException, OwsExceptionReport {
        LOGGER.debug("submit: {}", submit.xmlText(XmlHelper.DEBUG_OPTIONS));
        checkSupportingSwesRequestExtensions(submit.getExtensibleRequest());
        String procedure = submit.getSubmit().getProcedure();
        SensorPlugin sensor = getSensorInstance(procedure);
        SubmitTaskingRequest taskingRequest = new SubmitTaskingRequest(submit);
        SensorTask submitTask = sensor.submit(taskingRequest, new OwsExceptionReport());
        sensor.getSensorTaskService().updateSensorTask(submitTask); // update last status
        return createSubmitResponse(submitTask);
    }

    private SubmitResponseDocument createSubmitResponse(SensorTask submitTask) throws StatusInformationExpiredException {
        SubmitResponseDocument responseDocument = SubmitResponseDocument.Factory.newInstance();
        SubmitResponseType submitResponse = responseDocument.addNewSubmitResponse();
        StatusReportGenerator statusReporter = StatusReportGenerator.createFor(submitTask);
        submitResponse.addNewResult().setStatusReport(statusReporter.generateWithoutTaskingParameters());
        return responseDocument;
    }

    public void interceptCapabilities(CapabilitiesType capabilities, List<HttpBinding> httpBindings) {
        capabilities.getServiceIdentification().addNewProfile().setStringValue(CORE_CONFORMANCE_CLASS);
        OperationsMetadata operationsMetadata = getOperationsMetadata(capabilities);
        addGetCapabilitiesOperation(operationsMetadata, httpBindings);
        addDescribeTaskingOperation(operationsMetadata, httpBindings);
        addSubmitOperation(operationsMetadata, httpBindings);
        addDescribeResultAccessOperation(operationsMetadata, httpBindings);
        addGetTaskOperation(operationsMetadata, httpBindings);
        addGetStatusOperation(operationsMetadata, httpBindings);
    }

    private void addGetCapabilitiesOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation getCapabilitiesOperation = operationsMetadata.addNewOperation();
        getCapabilitiesOperation.setName(GET_CAPABILITIES);
        addSupportedBindings(getCapabilitiesOperation, httpBindings);
    }

    private void addDescribeTaskingOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation describeTaskingOperation = operationsMetadata.addNewOperation();
        describeTaskingOperation.setName(DESCRIBE_TASKING);
        addSupportedBindings(describeTaskingOperation, httpBindings);
    }

    private void addSubmitOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation submitOperation = operationsMetadata.addNewOperation();
        submitOperation.setName(SUBMIT);
        addSupportedBindings(submitOperation, httpBindings);
    }

    private void addDescribeResultAccessOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation describeResultAccessOperation = operationsMetadata.addNewOperation();
        describeResultAccessOperation.setName(DESCRIBE_RESULT_ACCESS);
        addSupportedBindings(describeResultAccessOperation, httpBindings);
    }

    private void addGetTaskOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation getTaskOperation = operationsMetadata.addNewOperation();
        getTaskOperation.setName(GET_TASK);
        addSupportedBindings(getTaskOperation, httpBindings);
    }

    private void addGetStatusOperation(OperationsMetadata operationsMetadata, List<HttpBinding> httpBindings) {
        Operation getStatusOperation = operationsMetadata.addNewOperation();
        getStatusOperation.setName(GET_STATUS);
        addSupportedBindings(getStatusOperation, httpBindings);
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
        if (extensibleRequest != null) {
            // operator does not support any extension
            OwsExceptionReport owsExceptionReport = new OwsExceptionReport();
            owsExceptionReport.addOwsException(new RequestExtensionNotSupportedException());
            throw owsExceptionReport;
        }
    }

}
