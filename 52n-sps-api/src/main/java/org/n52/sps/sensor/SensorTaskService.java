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
