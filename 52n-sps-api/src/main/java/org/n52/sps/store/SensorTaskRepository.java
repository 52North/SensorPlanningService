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

package org.n52.sps.store;

import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.tasking.TaskingRequestStatus;

/**
 * A task repository used by SPS components to have access to all {@link SensorTask}s available during
 * runtime. This includes all tasks once created by aall {@link SensorPlugin} instances which have valid
 * status within their task lifecycle (i.e. until they get deleted). <br/>
 * <br/>
 * The {@link SensorTaskRepository} is the main access point on tasks for central SPS components.
 * {@link SensorPlugin} instances only can get access on those tasks created by themselfes via their
 * {@link SensorTaskService} bridge.
 * 
 * @see SensorPlugin
 * @see SensorTask
 * @see SensorTaskService
 */
public interface SensorTaskRepository {

    /**
     * Creates a new {@link SensorTask}. It is added to the {@link SensorTaskRepository} so it can be accessed
     * for further requests independent on if the task was {@link TaskingRequestStatus#ACCEPTED}, or
     * {@link TaskingRequestStatus#REJECTED} or stays in {@link TaskingRequestStatus#PENDING} state.
     * 
     * @param procedure
     *        the procedure the new task belongs to.
     * @return a new SensorTask with request status {@link TaskingRequestStatus#PENDING}
     */
    public SensorTask createNewSensorTask(String procedure);

    public void saveOrUpdateSensorTask(SensorTask sensorTask);

    public void removeSensorTask(SensorTask sensorTask);
    
    /**
     * Checks if a task with id <code>taskId</code> exists for the given procedure.
     * 
     * @param procedure the procedure to check
     * @param taskId the task id
     * @return <code>true</code> if a task with given task id is available for the given <code>procedure</code>.
     */
    public boolean containsSensorTask(String procedure, String taskId);

    /**
     * @param procedure the procedure all sensor tasks ids shall be retrieved from repository.
     * @return access to all sensor task ids found.
     */
    public Iterable<String> getSensorTaskIds(String procedure);

    /**
     * @param procedure the procedure all sensor tasks shall be retrieved from repository.
     * @return access to all sensor tasks found.
     */
    public Iterable<SensorTask> getSensorTasks(String procedure);

    /**
     * @param procedure
     *        the procedure the task belongs to.
     * @param taskId
     *        the task id to lookup SensorTasks for.
     * @return the sensor task associated with given <code>taskId</code>.
     * @throws InvalidParameterValueException
     *         if no SensorTask could be found (according to REQ 4: <a
     *         href="http://www.opengis.net/spec/SPS/2.0/req/exceptions/UnknownIdentifier"
     *         >http://www.opengis.net/spec/SPS/2.0/req/exceptions/UnknownIdentifier</a>)
     */
    public SensorTask getSensorTask(String procedure, String taskId) throws InvalidParameterValueException;
    
    /**
     * Retrieves the amount of stored tasks for a given procedure.
     * 
     * @param procedure the procedure the tasks shall belong to.
     * @return the amount of stored tasks in the repository
     */
    public long getTaskAmountOf(String procedure);

    public SensorTask getTask(String taskId);

}
