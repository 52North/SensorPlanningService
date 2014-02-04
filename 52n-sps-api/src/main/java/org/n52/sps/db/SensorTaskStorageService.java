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
package org.n52.sps.db;

import java.util.ArrayList;
import java.util.List;

import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.sps.db.access.SensorTaskDao;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.util.IdWithPrefixGenerator;
import org.n52.sps.util.UuidWithPrefixGenerator;

public class SensorTaskStorageService implements SensorTaskRepository {
    
    // taskId != native db id
    // TODO inject id generator to make id creation customizable
    private IdWithPrefixGenerator taskIdGenerator = new UuidWithPrefixGenerator();
    
    private SensorTaskDao sensorTaskDao;

    public SensorTask createNewSensorTask(String procedure) {
        String taskId = taskIdGenerator.generatePrefixedId(procedure, "/");
        SensorTask sensorTask = new SensorTask(taskId, procedure);
        sensorTaskDao.saveInstance(sensorTask);
        return sensorTaskDao.getInstance(taskId);
    }

    public boolean containsSensorTask(String procedure, String taskId) {
        return sensorTaskDao.findBy(procedure, taskId) != null;
    }
    
    public void saveOrUpdateSensorTask(SensorTask sensorTask) {
        sensorTaskDao.updateInstance(sensorTask);
    }

    public void removeSensorTask(SensorTask sensorTask) {
        sensorTaskDao.deleteInstance(sensorTask);
    }

    public Iterable<String> getSensorTaskIds(String procedure) {
        List<String> taskIds = new ArrayList<String>();
        for (SensorTask sensorTask : sensorTaskDao.findByProcedure(procedure)) {
            taskIds.add(sensorTask.getTaskId());
        }
        return taskIds;
    }

    public SensorTask getTask(String taskId) {
        return sensorTaskDao.getInstance(taskId);
    }
    
    public Iterable<SensorTask> getSensorTasks(String procedure) {
        return sensorTaskDao.findByTaskId(procedure);
    }

    public SensorTask getSensorTask(String procedure, String taskId) throws InvalidParameterValueException {
        SensorTask sensorTask = sensorTaskDao.findBy(procedure, taskId);
        if (sensorTask == null) {
            throw new InvalidParameterValueException("task");
        }
        return sensorTask;
    }

    public long getTaskAmountOf(String procedure) {
        return sensorTaskDao.getCount(procedure);
    }

    public SensorTaskDao getSensorTaskDao() {
        return sensorTaskDao;
    }

    public void setSensorTaskDao(SensorTaskDao sensorTaskDao) {
        this.sensorTaskDao = sensorTaskDao;
    }
    
}
