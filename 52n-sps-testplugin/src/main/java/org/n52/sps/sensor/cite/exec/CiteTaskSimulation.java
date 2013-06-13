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

package org.n52.sps.sensor.cite.exec;

import static org.n52.sps.tasking.TaskingRequestStatus.ACCEPTED;
import static org.n52.sps.tasking.TaskingRequestStatus.REJECTED;

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.opengis.swe.x20.DataRecordType;

import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.SensorTaskStatus;
import org.n52.sps.sensor.model.SensorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CiteTaskSimulation implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CiteTaskSimulation.class);
    
    private static final int ONE_TENTH_OF_EXECUTION_IN_MILLISECONDS = 60 * 1000 / 10; // 6sec.

    private DataRecordType[] inputParameters; // TODO do s/th with it?!
    private SensorTask sensorTask;
    private CiteTaskScheduler scheduler;
    private CiteTaskSimulation internalRechargeSimulation;
    private SensorTaskService sensorTaskService;

    public static CiteTaskSimulation createTaskSimulation(SensorTask sensorTask, DataRecordType[] inputParameters) {
        return new CiteTaskSimulation(sensorTask, inputParameters);
    }

    public static CiteTaskSimulation createInternalTaskSimulation(SensorTask sensorTask) {
        return new CiteTaskSimulation(sensorTask, null);
    }

    private CiteTaskSimulation(SensorTask sensorTask, DataRecordType[] inputParameters) {
        this.sensorTask = sensorTask;
        this.inputParameters = inputParameters;
    }
    
    public void setSensorTaskService(SensorTaskService sensorTaskService) {
        this.sensorTaskService = sensorTaskService;
    }

    public void run() {
        simulateProcessingWhileUpdatingTaskPercentCompletion();
        if (scheduler.mustRechargeBatteries()) {
            // schedule recharge task immediately
            if (scheduler.schedule(internalRechargeSimulation)) {
                LOGGER.info("Running battery recharge task.");
                sensorTask.setRequestStatus(ACCEPTED);
                scheduler.resetBatteryStatus();
            } else {
                sensorTask.setRequestStatus(REJECTED);
            }
        }
    }

    private void simulateProcessingWhileUpdatingTaskPercentCompletion() {
        sensorTask.setTaskStatus(SensorTaskStatus.INEXECUTION);
        Calendar estimatedTimeToC = new GregorianCalendar();
        estimatedTimeToC.add(Calendar.MILLISECOND, ONE_TENTH_OF_EXECUTION_IN_MILLISECONDS);
        sensorTask.setEstimatedToC(estimatedTimeToC);
        try {
            for (int i = 1; i < 11; i++) {
                Thread.sleep(ONE_TENTH_OF_EXECUTION_IN_MILLISECONDS);
                sensorTask.setPercentCompletion(i * 10d); // 10%
                sensorTaskService.updateSensorTask(sensorTask);
                LOGGER.debug("SensorTask {} in execution: {}% done", sensorTask.getTaskId(), sensorTask.getPercentCompletion());
            }
            sensorTask.setTaskStatus(SensorTaskStatus.COMPLETED);
            sensorTaskService.updateSensorTask(sensorTask);
        }
        catch (InterruptedException e) {
            LOGGER.info("Task execution was interrupted.", e);
            sensorTask.addStatusMessage("Task execution was interrupted.");
            sensorTask.setTaskStatus(SensorTaskStatus.FAILED);
            sensorTaskService.updateSensorTask(sensorTask);
        }
    }

    public SensorTask getSensorTask() {
        return sensorTask;
    }

    public boolean isTaskExecuting() {
        return sensorTask.isExecuting();
    }

    public void setRechargeAfterTaskExecutionsSimulation(CiteTaskScheduler scheduler, CiteTaskSimulation rechargeSimulation) {
        this.scheduler = scheduler;
        this.internalRechargeSimulation = rechargeSimulation;
    }

}
