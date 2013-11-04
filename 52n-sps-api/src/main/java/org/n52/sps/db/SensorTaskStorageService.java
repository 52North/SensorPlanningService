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
