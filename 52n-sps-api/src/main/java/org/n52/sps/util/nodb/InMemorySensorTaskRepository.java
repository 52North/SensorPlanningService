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
