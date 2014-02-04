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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.SimpleSensorPluginTestInstance;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.store.SensorConfigurationRepository;
import org.n52.sps.util.nodb.InMemorySensorConfigurationRepository;

public class InMemorySensorConfigurationRepositoryTest {
    
    private SensorConfigurationRepository repository;

    @Before
    public void setUp() {
        repository = new InMemorySensorConfigurationRepository();
    }

    @Test
    public void testCreateNewInstance() throws InternalServiceException {
        SensorPlugin testInstance = SimpleSensorPluginTestInstance.createInstance();
        SensorConfiguration sensorConfiguration = testInstance.getSensorConfiguration();
        repository.storeNewSensorConfiguration(sensorConfiguration);
        assertTrue("Repository does not contain instance!", repository.containsSensorConfiguration(sensorConfiguration.getProcedure()));
    }
    
    @Test
    public void testGetSensorPlugin() throws InternalServiceException {
        SensorPlugin testInstance = SimpleSensorPluginTestInstance.createInstance();
        repository.storeNewSensorConfiguration(testInstance.getSensorConfiguration());
        SensorConfiguration sensorConfiguration = repository.getSensorConfiguration(testInstance.getProcedure());
        assertEquals("Got back wrong sensor instance!", testInstance.getSensorConfiguration(), sensorConfiguration);
    }
    
//    public void testSaveOrUpdate() throws InternalServiceException {
//        SensorPlugin testInstance = SimpleSensorPluginTestInstance.createInstance();
//        repository.createNewInstance(testInstance);
//        SensorPlugin sensorPlugin = repository.getSensorPlugin(testInstance.getProcedure());
//        sensorPlugin.getResultAccessDataServiceReference()
//        
//    }
    
    @Test
    public void testRemoveSensorPlugin() throws InternalServiceException {
        SensorPlugin testInstance = SimpleSensorPluginTestInstance.createInstance();
        repository.storeNewSensorConfiguration(testInstance.getSensorConfiguration());
        assertTrue("Repository does not contain instance!", repository.containsSensorConfiguration(testInstance.getProcedure()));
        repository.removeSensorConfiguration(testInstance.getSensorConfiguration());
        assertFalse("Repository still contains instance after removing it!", repository.containsSensorConfiguration(testInstance.getProcedure()));
    }

}
