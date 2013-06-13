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
package org.n52.sps.sensor.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.n52.sps.sensor.SensorTaskStatus;
import org.n52.sps.tasking.TaskingRequestStatus;

public class SensorTaskTest  {

    private SensorTask validTask;
    private String taskId;
    private String procedure;

    @Before
    public void setUp() {
        procedure = "http://host.tld/procedure1/";
        taskId = "http://host.tld/procedure1/tasks/1";
        validTask = new SensorTask(taskId, procedure);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSensorTaskCreation() {
        new SensorTask(null, "");
    }
    
    @Test
    public void testSensorTaskCreation() {
        assertPendingStateAftertaskCreation();
        assertEquals(procedure, validTask.getProcedure());
        assertEquals(taskId, validTask.getTaskId());
        assertEquals("Pending", validTask.getRequestStatusAsString());
    }

    private void assertPendingStateAftertaskCreation() {
        boolean pendingTaskStatus = validTask.getRequestStatus().equals(TaskingRequestStatus.PENDING);
        assertTrue("SensorTask request status has to be in PENDING state directly after creation!", pendingTaskStatus);
    }

    @Test
    public void testGetTaskStatusAsString() {
        validTask.setTaskStatus(SensorTaskStatus.INEXECUTION);
        assertEquals(SensorTaskStatus.INEXECUTION.toString(), validTask.getTaskStatusAsString());
    }

}
