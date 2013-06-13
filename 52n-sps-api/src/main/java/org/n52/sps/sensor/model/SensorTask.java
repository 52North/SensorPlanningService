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

package org.n52.sps.sensor.model;

import static org.n52.sps.tasking.TaskStatusTransitionEvent.RESET_TRANSITION_EVENT;
import static org.n52.sps.tasking.TaskStatusTransitionEvent.TASKING_REQUEST_EXPIRED;
import static org.n52.sps.tasking.TaskStatusTransitionEvent.TASK_COMPLETED;
import static org.n52.sps.tasking.TaskStatusTransitionEvent.TASK_SUBMITTED;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.sps.x20.ParameterDataType;
import net.opengis.sps.x20.StatusReportPropertyType;
import net.opengis.sps.x20.StatusReportType;
import net.opengis.sps.x20.TaskType;

import org.apache.xmlbeans.XmlException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.n52.sps.sensor.SensorTaskStatus;
import org.n52.sps.sensor.StatusReportGenerator;
import org.n52.sps.service.core.StatusInformationExpiredException;
import org.n52.sps.tasking.TaskStatusTransitionEvent;
import org.n52.sps.tasking.TaskingRequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorTask {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorTask.class);
    
    private static final DateTimeFormatter ISO8601_FORMATTER = ISODateTimeFormat.dateTime();

    private Long id; // database id
    
    private String taskId;
    
    private String procedure;

    private Calendar estimatedToC;
    
    private SensorTaskStatus taskStatus;

    private double percentCompletion = 0d;

    private TaskingRequestStatus requestStatus = TaskingRequestStatus.PENDING;

    private Calendar updateTime = new GregorianCalendar(); // default;

    private List<String> statusMessages = new ArrayList<String>();

    private ParameterDataType parameterData;

    private String event;

    SensorTask() {
        // database serialization
    }
    
    public SensorTask(String taskId, String procedure) {
        checkNonEmptyParameter(taskId, procedure);
        this.taskId = taskId;
        this.procedure = procedure;
    }

    private void checkNonEmptyParameter(String taskId, String procedure) {
        if (taskId == null || taskId.isEmpty()) {
            LOGGER.error("Illegal parameter => taskId: {}", taskId);
            throw new IllegalArgumentException("taskId must not be null!");
        }
        if (procedure == null || procedure.isEmpty()) {
            LOGGER.error("Illegal parameter => procedure: {}", procedure);
            throw new IllegalArgumentException("procedure must not be null!");
        }
    }
    
    Long getId() {
        return id;
    }

    void setId(Long id) {
        // keep package private for database
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }
    
    void setTaskId(String taskId) {
        // keep package private for database
        this.taskId = taskId;
    }
    
    public Calendar getCalendarInstance() {
        return updateTime;
    }

    public void setUpdateTime(Calendar calendar) {
        this.updateTime = calendar;
    }
    
    public Calendar getUpdateTime() {
        return updateTime;
    }

    public String getFormattedUpdateTime(DateFormat formatter) {
        return formatter.format(updateTime.getTime());
    }
    
    public String getUpdateTimeFormattedAsIso8601() {
        return ISO8601_FORMATTER.print(updateTime.getTimeInMillis());
    }

    /**
     * @param requestStatus
     *        according to REQ 14: <a
     *        href="http://www.opengis.net/spec/SPS/2.0/req/TaskingResponse/content">http
     *        ://www.opengis.net/spec/SPS/2.0/req/TaskingResponse/content</a>
     */
    public void setRequestStatus(TaskingRequestStatus requestStatus) {
        this.updateTime.setTimeInMillis(System.currentTimeMillis());
        this.requestStatus = requestStatus;
        if (!isAccepted()) {
            updateStatusTransitionEvent(RESET_TRANSITION_EVENT);
        } else {
            updateStatusTransitionEvent(TASK_SUBMITTED);
        } 
    }

    public void setRequestStatusAsString(String requestStatus) {
        if (requestStatus != null) {
            this.requestStatus = TaskingRequestStatus.getTaskingRequestStatus(requestStatus);
        }
    }

    private void updateStatusTransitionEvent(TaskStatusTransitionEvent transition) {
        event = transition.getEvent();
    }

    public TaskingRequestStatus getRequestStatus() {
        return requestStatus;
    }
    
    public String getRequestStatusAsString() {
        return requestStatus.toString();
    }

    /**
     * @return the estimated time when task will finish.
     */
    public Calendar getEstimatedToC() {
        return estimatedToC;
    }

    public void setEstimatedToC(Calendar estimatedToC) {
        this.estimatedToC = estimatedToC;
    }

    public double getPercentCompletion() {
        return percentCompletion;
    }

    public void setPercentCompletion(double percentCompletion) {
        this.percentCompletion = percentCompletion;
    }

    public String getProcedure() {
        return procedure;
    }
    
    void setProcedure(String procedure) {
        // keep package private for database
        this.procedure = procedure;
    }

    public void clearStatusMessages() {
        statusMessages.clear();
    }

    public void addStatusMessage(String message) {
        statusMessages.add(message);
    }
    
    public void setStatusMessages(List<String> statusMessages) {
        this.statusMessages = statusMessages;
    }

    public List<String> getStatusMessages() {
        return statusMessages;
    }

    /**
     * @param taskStatus
     *        according to REQ 15: <a
     *        href="http://www.opengis.net/spec/SPS/2.0/req/TaskingResponse/statusCodes"
     *        >http://www.opengis.net/spec/SPS/2.0/req/TaskingResponse/statusCodes</a>
     */
    public void setTaskStatus(SensorTaskStatus taskStatus) {
        this.taskStatus = taskStatus;
        if (isExecuting()) {
            updateStatusTransitionEvent(TASK_SUBMITTED);
        } else if (isFinished()) {
            updateStatusTransitionEvent(TASK_COMPLETED);
            setPercentCompletion(100.0);
        } else if (isExpired()) {
            updateStatusTransitionEvent(TASKING_REQUEST_EXPIRED);
        } else {
            updateStatusTransitionEvent(RESET_TRANSITION_EVENT);
        }
    }
    
    public void setTaskStatusAsString(String taskStatus) {
        if (taskStatus != null) {
            this.taskStatus = SensorTaskStatus.getSensorTaskStatus(taskStatus);
        }
    }

    public String getFormattedEstimatedToC(DateFormat formatter) {
        return formatter.format(estimatedToC.getTime());
    }
    
    public String getEstimatedToCFormattedAsIso8601() {
        return ISO8601_FORMATTER.print(estimatedToC.getTimeInMillis());
    }
    
    public SensorTaskStatus getTaskStatus()  {
        return taskStatus;
    }

    public String getTaskStatusAsString() {
        return taskStatus != null ? taskStatus.toString() : null;
    }
    
    public ParameterDataType getParameterData() {
        return parameterData;
    }

    public String getParameterDataAsString() {
        return parameterData != null ? parameterData.xmlText() : null;
    }

    public void setParameterData(ParameterDataType parameterData) {
        this.parameterData = parameterData;
    }
    
    public void setParameterDataAsString(String parameterData) throws XmlException {
        if (parameterData != null) {
            this.parameterData = ParameterDataType.Factory.parse(parameterData);
        }
    }
    
    public String getEvent() {
        return event;
    }
    
    void setEvent(String event) {
        this.event = event;
    }
    
    public TaskType getTask() throws StatusInformationExpiredException {
        StatusReportGenerator statusReporter = StatusReportGenerator.createFor(this);
        TaskType taskType = TaskType.Factory.newInstance();
        taskType.setIdentifier(getTaskId());
        StatusReportPropertyType status = taskType.addNewStatus();
        status.setStatusReport(statusReporter.generateWithoutTaskingParameters());
        return taskType;
    }

    /**
     * @deprecated use {@link StatusReportGenerator} instead.
     * @return
     * @throws StatusInformationExpiredException
     */
    @Deprecated
    public StatusReportType generateStatusReport() throws StatusInformationExpiredException {
        if (taskStatus == SensorTaskStatus.EXPIRED) {
            throw new StatusInformationExpiredException();
        }
        StatusReportType statusReport = StatusReportType.Factory.newInstance();
        statusReport.setTask(getTaskId());
        statusReport.setProcedure(getProcedure());
        statusReport.setRequestStatus(getRequestStatusAsString());
        statusReport.setUpdateTime(updateTime);
        if (estimatedToC != null) {
            statusReport.setEstimatedToC(estimatedToC);
        }

        // TODO implement event

        if (percentCompletion >= 0d) {
            statusReport.setPercentCompletion(percentCompletion);
        }
        if ( !statusMessages.isEmpty()) {
            for (String message : statusMessages) {
                LanguageStringType statusMessage = statusReport.addNewStatusMessage();
                statusMessage.setStringValue(message);
            }
        }
        if (taskStatus != null) {
            statusReport.setTaskStatus(getTaskStatusAsString());
        }

        // TODO implement alternative

        if (parameterData != null) {
            statusReport.addNewTaskingParameters().setParameterData(parameterData);
        }
        return statusReport;
    }
    
    public boolean isExpired() {
        return taskStatus == SensorTaskStatus.EXPIRED;
    }
    
    public boolean isFinished() {
        return taskStatus == SensorTaskStatus.COMPLETED;
    }
    
    public boolean isAccepted() {
        return requestStatus == TaskingRequestStatus.ACCEPTED;
    }

    public boolean isExecuting() {
        return taskStatus == SensorTaskStatus.INEXECUTION;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (procedure == null) ? 0 : procedure.hashCode());
        result = prime * result + ( (taskId == null) ? 0 : taskId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensorTask other = (SensorTask) obj;
        if (procedure == null) {
            if (other.procedure != null)
                return false;
        }
        else if ( !procedure.equals(other.procedure))
            return false;
        if (taskId == null) {
            if (other.taskId != null)
                return false;
        }
        else if ( !taskId.equals(other.taskId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TaskId: ").append(getTaskId()).append(", ");
        sb.append("Procedure: ").append(getProcedure()).append(", ");
        sb.append("RequestStatus: ").append(requestStatus).append(", ");
        sb.append("UpdateTime: ").append(getUpdateTimeFormattedAsIso8601());
        return sb.toString();
    }

}
