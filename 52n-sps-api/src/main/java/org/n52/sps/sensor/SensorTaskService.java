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

import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.store.SensorTaskRepository;

/**
 * Proxy service a {@link SensorPlugin} instance can use to operate on a {@link SensorTaskRepository}. <br>
 * <br>
 * There is <b>one</b> central {@link SensorTaskRepository} (e.g. in memory or database) so that each
 * {@link SensorPlugin} has its own {@link SensorTaskService} available to
 * <ol>
 * <li>operate only on data belonging to the {@link SensorPlugin} instance
 * <li>establish the instance context when retrieving information form the {@link SensorTaskRepository}.
 * </ol>
 * 
 * @see SensorTaskRepository
 */
public class SensorTaskService {

    private SensorPlugin procedure;

    private SensorTaskRepository sensorTaskRepository;

    public SensorTaskService(SensorTaskRepository sensorTaskRepository) {
        this.sensorTaskRepository = sensorTaskRepository;
    }

    public void setProcedure(SensorPlugin procedure) {
        this.procedure = procedure;
    }

    public String getProcedure() {
        return procedure.getProcedure();
    }

    public SensorTask createNewTask() {
        return sensorTaskRepository.createNewSensorTask(getProcedure());
    }
    
    public void updateSensorTask(SensorTask sensorTask) {
        sensorTaskRepository.saveOrUpdateSensorTask(sensorTask);
    }

    public boolean contains(String taskId) {
        return sensorTaskRepository.containsSensorTask(getProcedure(), taskId);
    }

    public Iterable<SensorTask> getSensorTasks() {
        return sensorTaskRepository.getSensorTasks(getProcedure());
    }

    public SensorTask getSensorTask(String taskId) throws InvalidParameterValueException {
        return sensorTaskRepository.getSensorTask(getProcedure(), taskId);
    }

    public long getAmountOfAvailableTasks() {
        return sensorTaskRepository.getTaskAmountOf(getProcedure());
    }

}
