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
