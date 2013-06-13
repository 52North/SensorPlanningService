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

import static org.junit.Assert.*;

import java.util.Calendar;

import net.opengis.sps.x20.StatusReportType;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.core.StatusInformationExpiredException;
import org.n52.sps.tasking.TaskingRequestStatus;

public class StatusReportGeneratorTest {

    private String procedure;
    private String taskId;
    private SensorTask validTask;

    @Before
    public void setUp() throws Exception {
        procedure = "http://host.tld/procedure1/";
        taskId = "http://host.tld/procedure1/tasks/1";
        validTask = new SensorTask(taskId, procedure);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalCreation() {
        StatusReportGenerator.createFor(null);
    }

    @Test
    public void testValidCreation() {
        assertNotNull(getStatusReporter());
    }
    
    @Test(expected = StatusInformationExpiredException.class)
    public void testGenerationOfAlreadyExpiredSensorTask() throws Exception {
        validTask.setTaskStatus(SensorTaskStatus.EXPIRED);
        getStatusReporter().generateWithoutTaskingParameters();
    }
    
    @Test
    public void testGenerate() throws Exception {
        assertNotNull(getStatusReporter().generateWithoutTaskingParameters());
    }

    private StatusReportGenerator getStatusReporter() {
        return StatusReportGenerator.createFor(validTask);
    }
    
    @Test
    public void testGenerateForRejectedTask() throws Exception {
        StatusReportGenerator statusReporter = getStatusReporter();
        validTask.setRequestStatus(TaskingRequestStatus.REJECTED);
        StatusReportType reportUnderTest = statusReporter.generateWithoutTaskingParameters();
        assertSetParametersOfNonAcceptedTask(reportUnderTest);
        assertFalse(reportUnderTest.isSetEvent());
    }
    
    @Test
    public void testGenerateForPendingTask() throws Exception {
        StatusReportGenerator statusReporter = getStatusReporter();
        StatusReportType reportUnderTest = statusReporter.generateWithoutTaskingParameters();
        assertSetParametersOfNonAcceptedTask(reportUnderTest);
        assertFalse(reportUnderTest.isSetEvent());
    }

    private void assertSetParametersOfNonAcceptedTask(StatusReportType reportUnderTest) {
        assertNotNull(reportUnderTest);
        assertFalse(reportUnderTest.isSetEvent());
        assertFalse(reportUnderTest.isSetTaskStatus());
        assertFalse(reportUnderTest.isSetEstimatedToC());
        assertFalse(reportUnderTest.isSetPercentCompletion());
        assertCorrectCommonParameters(reportUnderTest);
    }

    private void assertCorrectCommonParameters(StatusReportType reportUnderTest) {
        assertEquals(validTask.getTaskId(), reportUnderTest.getTask());
        assertEquals(validTask.getProcedure(), reportUnderTest.getProcedure());
        assertEquals(validTask.getStatusMessages().size(), reportUnderTest.getStatusMessageArray().length);
        assertEqualCalendars(validTask.getUpdateTime(), reportUnderTest.getUpdateTime());
    }
    
    @Test
    public void testGenerateForAcceptedTask() throws Exception {
        StatusReportGenerator statusReporter = getStatusReporter();
        validTask.setRequestStatus(TaskingRequestStatus.ACCEPTED);
        StatusReportType reportUnderTest = statusReporter.generateWithoutTaskingParameters();
        assertSetParametersOfAcceptedTask(reportUnderTest);
    }
    
    private void assertSetParametersOfAcceptedTask(StatusReportType reportUnderTest) {
        assertNotNull(reportUnderTest);
        if (reportUnderTest.isSetEstimatedToC()) {
            assertEqualCalendars(validTask.getEstimatedToC(), reportUnderTest.getEstimatedToC());
        }
        assertTrue(reportUnderTest.isSetTaskStatus());
        assertTrue(reportUnderTest.isSetPercentCompletion());
        assertEquals(validTask.getEvent(), reportUnderTest.getEvent());
        assertCorrectCommonParameters(reportUnderTest);
    }

    private void assertEqualCalendars(Calendar first, Calendar second) {
        assertTrue(new DateTime(first).isEqual(new DateTime(second)));
    }
    
}
