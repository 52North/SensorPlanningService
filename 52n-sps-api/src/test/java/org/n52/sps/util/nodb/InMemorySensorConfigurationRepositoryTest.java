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
