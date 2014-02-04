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
package org.n52.sps.util.nodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.util.IdWithPrefixGenerator;
import org.n52.sps.util.UuidWithPrefixGenerator;

public class InMemorySensorTaskRepository implements SensorTaskRepository {
    
    private Map<SensorTaskKey, SensorTask> sensorTasks = new HashMap<SensorTaskKey, SensorTask>();

    private IdWithPrefixGenerator taskIdGenerator = new UuidWithPrefixGenerator();

    public SensorTask createNewSensorTask(String procedure) {
        // TODO make separator configurable? => use IdWithPrefixGenerator.DEFAULT_ID_SEPARATOR 
        String taskId = taskIdGenerator.generatePrefixedId(procedure, "/"); 
        SensorTaskKey sensorTaskKey = new SensorTaskKey(procedure, taskId);
        SensorTask sensorTask = new SensorTask(taskId, procedure);
        sensorTasks.put(sensorTaskKey, sensorTask);
        return sensorTask;
    }

    public void saveOrUpdateSensorTask(SensorTask sensorTask) {
        SensorTaskKey key = new SensorTaskKey(sensorTask.getProcedure(), sensorTask.getTaskId());
        sensorTasks.put(key, sensorTask);
    }

    public void removeSensorTask(SensorTask sensorTask) {
        sensorTasks.remove(new SensorTaskKey(sensorTask.getProcedure(), sensorTask.getTaskId()));
    }

    public boolean containsSensorTask(String procedure, String taskId) {
        SensorTaskKey sensorTaskKey = new SensorTaskKey(procedure, taskId);
        return sensorTasks.containsKey(sensorTaskKey);
    }

    public SensorTask getTask(String taskId) {
        for (SensorTask sensorTask : sensorTasks.values()) {
            if (sensorTask.getTaskId().equals(taskId)) {
                return sensorTask;
            }
        }
        return null;
    }
    
    public Iterable<String> getSensorTaskIds(String procedure) {
        List<String> taskIds = new ArrayList<String>();
        for (SensorTask sensorTask : sensorTasks.values()) {
            if (sensorTask.getProcedure().equals(procedure)) {
                taskIds.add(sensorTask.getTaskId());
            }
        }
        return taskIds;
    }

    public Iterable<SensorTask> getSensorTasks(String procedure) {
        return getAllSensorTasksFor(procedure);
    }

    private List<SensorTask> getAllSensorTasksFor(String procedure) {
        List<SensorTask> tasks = new ArrayList<SensorTask>();
        for (SensorTask sensorTask : sensorTasks.values()) {
            if (sensorTask.getProcedure().equals(procedure)) {
                tasks.add(sensorTask);
            }
        }
        return tasks;
    }

    public SensorTask getSensorTask(String procedure, String taskId) throws InvalidParameterValueException {
        SensorTaskKey sensorTaskKey = new SensorTaskKey(procedure, taskId);
        if (!sensorTasks.containsKey(sensorTaskKey)) {
            throw new InvalidParameterValueException("task");
        } else {
            return sensorTasks.get(sensorTaskKey);
        }
        
    }

    public IdWithPrefixGenerator getIdGenerator() {
        return taskIdGenerator;
    }

    public long getTaskAmountOf(String procedure) {
        return getAllSensorTasksFor(procedure).size();
    }
    
    static class SensorTaskKey {
        private String procedure;
        private String taskId;

        SensorTaskKey(String procedure, String taskId) {
            this.procedure = procedure;
            this.taskId = taskId;
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
            SensorTaskKey other = (SensorTaskKey) obj;
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
    }

}
