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
package org.n52.sps.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.opengis.swes.x20.ExtensibleRequestType;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.SimpleSensorPluginTestInstance;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.core.SensorInstanceProvider;
import org.n52.sps.store.SensorConfigurationRepository;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.util.nodb.InMemorySensorConfigurationRepository;
import org.n52.sps.util.nodb.InMemorySensorTaskRepository;

public class SpsOperatorTest {

    private static final String INVALID_PROCEDURE = "http://my.namespace.org/not-existing-procedure/1";

    private static final String INVALID_TASK = "http://my.namespace.org/not-existing-task/1";

    private static final int AMOUNT_OF_PROCEDURES = 5;

    private static final int AMOUNT_OF_TASKS = 7;

    private List<String> procedures = new ArrayList<String>();

    private List<String> tasks = new ArrayList<String>();

    private SpsOperator operatorUnderTest;

    @Before
    public void setUp() throws Exception {
        SensorConfigurationRepository configurationRepository = new InMemorySensorConfigurationRepository();
        SensorTaskRepository taskRepository = new InMemorySensorTaskRepository();
        MySensorInstanceProviderSeam instanceProvider = new MySensorInstanceProviderSeam();

        for (int i = 0; i < AMOUNT_OF_PROCEDURES; i++) {
            SensorPlugin sensorInstance = createAndStoreSensorConfig(configurationRepository);
            instanceProvider.addSensorInstanceSeam(sensorInstance);
            String procedure = sensorInstance.getProcedure();
            for (int j = 0; j < AMOUNT_OF_TASKS; j++) {
                SensorTask task = taskRepository.createNewSensorTask(procedure);
                assertEquals("task has different procedure as it was created with! ", procedure, task.getProcedure());
                tasks.add(task.getTaskId());
            }
            procedures.add(procedure);
        }

        instanceProvider.setSensorConfigurationRepositorySeam(configurationRepository);
        instanceProvider.setSensorTaskRepositorySeam(taskRepository);
        operatorUnderTest = new SpsOperatorImpl();
        operatorUnderTest.setSensorInstanceProvider(instanceProvider);
    }

    @Test
    public void testGetSensorTask() {
        try {
            for (String taskId : tasks) {
                if (operatorUnderTest.getSensorTask(taskId) == null) {
                    fail("null was returned instead of throwing IVPE!");
                }
            }
        }
        catch (OwsException e) {
            fail("could not get sensor task");
        }
    }

    @Test
    public void testGetSensorTaskThrowingException() {
        try {
            operatorUnderTest.getSensorTask(INVALID_TASK);
            fail("Invalid task id returnd valid non null task instance!");
        }
        catch (InvalidParameterValueException e) {
            // expected
        }
        catch (OwsException e) {
            fail("Invalid task id returnd valid non null task instance!");
        } 
    }

    @Test
    public void testGetSensorInstanceFor() {
        try {
            for (String procedure : procedures) {
                if (operatorUnderTest.getSensorInstance(procedure) == null) {
                    fail("null was returned instead of throwing IVPE!");
                }
            }
        }
        catch (OwsException e) {
            fail("Procedure was unknown!");
        } 
    }

    @Test
    public void testGetSensorInstanceForThrowingException() {
        try {
            operatorUnderTest.getSensorInstance(INVALID_PROCEDURE);
            fail("Invalid procedure returnd valid non null instance!");
        }
        catch (InvalidParameterValueException e) {
            // expected
        }
        catch (OwsException e) {
            // exception was under test => ok!
        }
    }

    private SensorPlugin createAndStoreSensorConfig(SensorConfigurationRepository configurationRepository) throws InternalServiceException {
        SensorPlugin testInstance1 = SimpleSensorPluginTestInstance.createInstance();
        SensorConfiguration sensorConfiguration = testInstance1.getSensorConfiguration();
        configurationRepository.storeNewSensorConfiguration(sensorConfiguration);
        return testInstance1;
    }

    /**
     * Just to test abstract class
     */
    private class SpsOperatorImpl extends SpsOperator {

        @Override
        public boolean isSupportingExtensions() {
            throw new UnsupportedOperationException("not under test.");
        }

        @Override
        protected void checkSupportingSpsRequestExtensions(XmlObject[] extensions) throws OwsExceptionReport {
            throw new UnsupportedOperationException("not under test.");
        }

        @Override
        protected void checkSupportingSwesRequestExtensions(ExtensibleRequestType extensibleRequest) throws OwsExceptionReport {
            throw new UnsupportedOperationException("not under test.");
        }
    }

    /**
     * Provides seams to configure {@link SensorInstanceProvider} for testing purposes. This includes for
     * example local members which are not intended for being set directly from non-testing classes. An
     * example is adding sensorInstances directly so these can be tested from a unit test.
     */
    private class MySensorInstanceProviderSeam extends SensorInstanceProvider {
        void addSensorInstanceSeam(SensorPlugin sensorInstance) {
            super.addSensor(sensorInstance);
        }

        public void setSensorConfigurationRepositorySeam(SensorConfigurationRepository sensorConfigurationRepository) {
            this.sensorConfigurationRepository = sensorConfigurationRepository;
        }

        public void setSensorTaskRepositorySeam(SensorTaskRepository sensorTaskRepository) {
            this.sensorTaskRepository = sensorTaskRepository;
        }

    }

}
