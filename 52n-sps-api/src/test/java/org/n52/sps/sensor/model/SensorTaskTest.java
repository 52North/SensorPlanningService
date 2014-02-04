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
