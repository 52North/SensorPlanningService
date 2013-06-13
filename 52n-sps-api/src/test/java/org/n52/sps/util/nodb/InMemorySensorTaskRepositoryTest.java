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
import java.util.List;

import org.junit.Test;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.OwsExceptionCode;
import org.n52.sps.sensor.SensorTaskStatus;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.util.nodb.InMemorySensorTaskRepository;

import junit.framework.TestCase;

public class InMemorySensorTaskRepositoryTest extends TestCase {
    
    private static final String PROCEDURE_1 = "http://my.namespace.org/procedure/1";
    private static final String PROCEDURE_2 = "http://my.namespace.org/procedure/2";
    
    private static final String INVALID_PROCEDURE = "http://my.namespace.org/not-existing-procedure/1";
    private SensorTaskRepository sensorTaskRepository;
    
    public void setUp() {
        sensorTaskRepository = new InMemorySensorTaskRepository();
    }

    @Test
    public void testCreateNewTask() {
        SensorTask newTask = sensorTaskRepository.createNewSensorTask(PROCEDURE_1);
        assertEquals("Different procedure id of new task.", PROCEDURE_1, newTask.getProcedure());
        assertAmountOfCreatedTasksAvailable(sensorTaskRepository, PROCEDURE_1, 1);
    }

    private void assertAmountOfCreatedTasksAvailable(SensorTaskRepository sensorTaskRepository, String usedProcedure, int expectedSize) {
        List<String> procedures = new ArrayList<String>();
        for (String procedure : sensorTaskRepository.getSensorTaskIds(usedProcedure)) {
            procedures.add(procedure);
        }
        assertEquals("Repository contained unexpected amount of tasks!", expectedSize, procedures.size());
    }
    
    @Test
    public void testSaveOrUpdateTask() {
        SensorTask newTask = sensorTaskRepository.createNewSensorTask(PROCEDURE_1);
        newTask.setTaskStatus(SensorTaskStatus.CANCELLED);
        sensorTaskRepository.saveOrUpdateSensorTask(newTask);
        assertEquals("Status has not been updated.", SensorTaskStatus.CANCELLED, newTask.getTaskStatus());
    }
    
    @Test
    public void testRemoveTask() {
        SensorTask newTask = sensorTaskRepository.createNewSensorTask(PROCEDURE_1);
        sensorTaskRepository.removeSensorTask(newTask);
        assertFalse("Task was not deleted!", sensorTaskRepository.containsSensorTask(PROCEDURE_1, newTask.getTaskId()));
    }
    
    public void testContains() {
        SensorTask newTask = sensorTaskRepository.createNewSensorTask(PROCEDURE_1);
        assertTrue("Repository does not contain created task.", sensorTaskRepository.containsSensorTask(PROCEDURE_1, newTask.getTaskId()));
        assertFalse("Repository contains not created tasks!", sensorTaskRepository.containsSensorTask(INVALID_PROCEDURE, "foobar"));
    }
    
    @Test
    public void testGetTask() {
        SensorTaskRepository sensorTaskRepository = new InMemorySensorTaskRepository();
        SensorTask newTask = sensorTaskRepository.createNewSensorTask(PROCEDURE_1);
        assertEquals("tasks are not equal!", newTask, sensorTaskRepository.getTask(newTask.getTaskId()));
    }

    @Test
    public void testGetSensorTaskIds() {
        List<SensorTask> createdSensorTasks = new ArrayList<SensorTask>();
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_2));
        assertAmountOfCreatedTasksAvailable(sensorTaskRepository, PROCEDURE_1, 2);
        assertAmountOfCreatedTasksAvailable(sensorTaskRepository, PROCEDURE_2, 1);
        try {
            assertCorrectSensorTaskIds(sensorTaskRepository, createdSensorTasks);
        } catch(InvalidParameterValueException e) {
            fail("Task repository reported an unknown task id.");
        }
    }
    
    private void assertCorrectSensorTaskIds(SensorTaskRepository sensorTaskRepository, List<SensorTask> createdSensorTasks) throws InvalidParameterValueException {
        for (SensorTask sensorTask : createdSensorTasks) {
            String taskId = sensorTask.getTaskId();
            String procedure = sensorTask.getProcedure();
            SensorTask storedSensorTask = sensorTaskRepository.getSensorTask(procedure, taskId);
            assertEquals("Task Ids do not match.", taskId, storedSensorTask.getTaskId());
        }
    }
    
    @Test
    public void testGetSensorTasks() {
        List<SensorTask> createdSensorTasks = new ArrayList<SensorTask>();
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        assertAmountOfCreatedTasksAvailable(sensorTaskRepository, PROCEDURE_1, 2);
        assertCorrectSensorTaskList(sensorTaskRepository, sensorTaskRepository.getSensorTasks(PROCEDURE_1));
    }
    
    private void assertCorrectSensorTaskList(SensorTaskRepository sensorTaskRepository, Iterable<SensorTask> returnedSensorTasks) {
        for (SensorTask sensorTask : returnedSensorTasks) {
            String procedure = sensorTask.getProcedure();
            String taskId = sensorTask.getTaskId();
            assertTrue("Repository returned wrong sensor tasks!", sensorTaskRepository.containsSensorTask(procedure, taskId));
        }
    }

    @Test
    public void testGetSensorTask() {
        SensorTask createdTask = sensorTaskRepository.createNewSensorTask(PROCEDURE_2);
        assertCorrectGetSensorTaskResponse(sensorTaskRepository, createdTask);
        assertInValidGetSensorTaskThrowsException(sensorTaskRepository);
    }

    private void assertCorrectGetSensorTaskResponse(SensorTaskRepository sensorTaskRepository, SensorTask createdTask) {
        try {
            String procedure = createdTask.getProcedure();
            String taskId = createdTask.getTaskId();
            assertEquals("Returned task does not equal to the created one!", createdTask, sensorTaskRepository.getSensorTask(procedure, taskId));
        }
        catch (InvalidParameterValueException e) {
            fail("Task repository reported an unknown task id.");
        }
    }
    
    private void assertInValidGetSensorTaskThrowsException(SensorTaskRepository sensorTaskRepository) {
        try {
            SensorTask notStoredSensorTask = new SensorTask("http://not.known.taskId", "http://invalid/procedure");
            String InvalidProcedure = notStoredSensorTask.getProcedure();
            String InvalidTaskId = notStoredSensorTask.getTaskId();
            sensorTaskRepository.getSensorTask(InvalidProcedure, InvalidTaskId);
            fail("Request invalid procedure did not throw InvalidParameterValueException!");
        } catch (InvalidParameterValueException e) {
            assertEquals("Invalid exception code.", e.getExceptionCode(), OwsExceptionCode.INVALID_PARAMETER_VALUE.getExceptionCode());
        }
    }
    
    @Test
    public void testGetAmountOf() {
        List<SensorTask> createdSensorTasks = new ArrayList<SensorTask>();
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        createdSensorTasks.add(sensorTaskRepository.createNewSensorTask(PROCEDURE_1));
        assertEquals("Invalid amount of available sensorTasks returned!", createdSensorTasks.size(), sensorTaskRepository.getTaskAmountOf(PROCEDURE_1));
    }


}
