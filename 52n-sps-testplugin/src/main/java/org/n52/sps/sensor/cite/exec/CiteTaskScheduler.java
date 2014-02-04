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
package org.n52.sps.sensor.cite.exec;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.tasking.TaskingRequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CiteTaskScheduler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CiteTaskScheduler.class);
    
    private static final int MAXIMUM_RUNS_WITHOUT_BATTERY_RECHARGE = 2;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private CiteTaskSimulation scheduledSimulation;
    
    private int countOfExecutedTasks = 0;

    private CiteTaskSimulation rechargeSimulation;

    public CiteTaskScheduler(SensorTaskService sensorTaskService) {
        createRechargeSimulation(sensorTaskService);
    }

    private void createRechargeSimulation(SensorTaskService sensorTaskService) {
        SensorTask rechargeTask = sensorTaskService.createNewTask();
        rechargeTask.setRequestStatus(TaskingRequestStatus.ACCEPTED); // internal task
        addRechargeStatusMessages(rechargeTask);
        rechargeSimulation = CiteTaskSimulation.createInternalTaskSimulation(rechargeTask);
        rechargeSimulation.setSensorTaskService(sensorTaskService);
    }

    public boolean schedule(CiteTaskSimulation citeTaskSimulation) {
        SensorTask sensorTask = citeTaskSimulation.getSensorTask();
        citeTaskSimulation.setRechargeAfterTaskExecutionsSimulation(this, rechargeSimulation);
        boolean schedulingPossible = isSchedulingPossible();
        if (schedulingPossible) {
            scheduledSimulation = citeTaskSimulation;
            executorService.execute(citeTaskSimulation);
            LOGGER.info("Executing task: {}", sensorTask);
            countOfExecutedTasks++;
        } else {
            if (isRechargingBatteries()) {
                addRechargeStatusMessages(sensorTask);
            } else {
                sensorTask.addStatusMessage("Sensor is currently in execution.");
            }
        }
        return schedulingPossible;
    }

    private void addRechargeStatusMessages(SensorTask sensorTask) {
        sensorTask.addStatusMessage("The Sensor you have called is temporarily not available!");
        sensorTask.addStatusMessage("Sensor is loading batteries at base station. Please try again later.");
    }

    private boolean isSchedulingPossible() {
        return scheduledSimulation == null || !scheduledSimulation.isTaskExecuting();
    }

    public void cancel(String taskId) {
        // TODO cancel running simulation in case of a task with higher priority 
    }
    
    public void resetBatteryStatus() {
        countOfExecutedTasks = 0;
    }
    
    public boolean mustRechargeBatteries() {
        return countOfExecutedTasks == MAXIMUM_RUNS_WITHOUT_BATTERY_RECHARGE;
    }
    
    public boolean isRechargingBatteries() {
        return rechargeSimulation != null && rechargeSimulation.isTaskExecuting();
    }
    
}
