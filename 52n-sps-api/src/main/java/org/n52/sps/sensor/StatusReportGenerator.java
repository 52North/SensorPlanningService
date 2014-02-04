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
package org.n52.sps.sensor;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.sps.x20.StatusReportType;

import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.core.StatusInformationExpiredException;
import org.n52.sps.tasking.TaskingRequestStatus;

public class StatusReportGenerator {
    
    private SensorTask sensorTask;

    public static StatusReportGenerator createFor(SensorTask sensorTask) {
        return new StatusReportGenerator(sensorTask);
    }

    private StatusReportGenerator(SensorTask sensorTask) {
        if (sensorTask == null) {
            throw new IllegalArgumentException("Cannot operate on a 'null' task.");
        }
        this.sensorTask = sensorTask;
    }
    
    public StatusReportType generateWithTaskingParameters() throws StatusInformationExpiredException {
        StatusReportType statusReport = generateWithoutTaskingParameters();
        statusReport.addNewTaskingParameters().setParameterData(sensorTask.getParameterData());
        return statusReport;
    }

    public StatusReportType generateWithoutTaskingParameters() throws StatusInformationExpiredException {
        if (isStatusInformationExpired()) {
            throw new StatusInformationExpiredException();
        }

        StatusReportType statusReport = StatusReportType.Factory.newInstance();
        statusReport.setRequestStatus(sensorTask.getRequestStatusAsString());
        setCommonStatusReportParameters(statusReport);
        
        if (!isRejected() && !isInPendingState()) {
            setStatusReportParametersForAcceptedTask(statusReport);
        } 

        return statusReport; 
    }

    private boolean isStatusInformationExpired() {
        return sensorTask.getTaskStatus() == SensorTaskStatus.EXPIRED;
    }

    private void setCommonStatusReportParameters(StatusReportType statusReport) {
        statusReport.setTask(sensorTask.getTaskId());
        statusReport.setProcedure(sensorTask.getProcedure());
        statusReport.setRequestStatus(sensorTask.getRequestStatusAsString());
        statusReport.setUpdateTime(sensorTask.getUpdateTime());
        
//        DatatypeFactory.newInstance();
        
        if (canSetStatusMessages()) {
            for (String message : sensorTask.getStatusMessages()) {
                LanguageStringType statusMessage = statusReport.addNewStatusMessage();
                statusMessage.setStringValue(message);
            }
        }
        
        // TODO implement alternative (important: consider table 31, OGC 09-000 p.69)
    }

    private boolean canSetStatusMessages() {
        return !sensorTask.getStatusMessages().isEmpty();
    }

    private void setStatusReportParametersForAcceptedTask(StatusReportType statusReport) {
        statusReport.setPercentCompletion(sensorTask.getPercentCompletion());
        statusReport.setTaskStatus(sensorTask.getTaskStatusAsString());
        statusReport.setEvent(sensorTask.getEvent());
        if (hasSetEstimatedTimeToComplete()) {
            statusReport.setEstimatedToC(sensorTask.getEstimatedToC());
        }
    }

    private boolean hasSetEstimatedTimeToComplete() {
        return sensorTask.getEstimatedToC() != null;
    }

    private boolean isInPendingState() {
        return sensorTask.getRequestStatus() == TaskingRequestStatus.PENDING;
    }
    
    private boolean isRejected() {
        return sensorTask.getRequestStatus() == TaskingRequestStatus.REJECTED;
    }

}
