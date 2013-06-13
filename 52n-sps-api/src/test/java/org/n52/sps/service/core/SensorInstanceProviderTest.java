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

package org.n52.sps.service.core;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.Before;
import org.junit.Test;
import org.n52.sps.sensor.SensorInstanceFactory;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.SimpleSensorPluginTestInstance;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.service.PluginTypeConfigurationException;
import org.n52.sps.service.admin.InsertSensorOfferingEvent;
import org.n52.sps.service.core.SensorInstanceProvider;
import org.n52.sps.store.SensorConfigurationRepository;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.util.nodb.InMemorySensorConfigurationRepository;
import org.n52.sps.util.nodb.InMemorySensorTaskRepository;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument;

public class SensorInstanceProviderTest {

    private static final String INSERT_OFFERING_PROCEDURE = "http://www.52north.org/sensor/cite/1";

    private static final String INSERT_SENSOR_OFFERING_FILE = "/files/insert_sensor_offering_test.xml";
    
    private static final String INSERT_SENSOR_OFFERING_PLUGIN_TYPE = "CiteTestSensor";

    private InsertSensorOfferingDocument insertSensorOffering;

    private SensorInstanceProvider sensorInstanceProvider;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream(INSERT_SENSOR_OFFERING_FILE);
        insertSensorOffering = InsertSensorOfferingDocument.Factory.parse(is);
        sensorInstanceProvider = new SensorInstanceProviderSeam();
        setEmptyInMemoryRepositories(sensorInstanceProvider);
        sensorInstanceProvider.init();
    }

    private void setEmptyInMemoryRepositories(SensorInstanceProvider provider) throws InternalServiceException {
        provider.setSensorConfigurationRepository(new InMemorySensorConfigurationRepository());
        provider.setSensorTaskRepository(new InMemorySensorTaskRepository());
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateSensorInstanceProviderWithIncompleteSetup() throws InternalServiceException {
        new SensorInstanceProvider().init(); // have no repositories set.
    }

    @Test
    public void testInit() throws InternalServiceException {
        SensorInstanceProvider provider = new SensorInstanceProvider();
        setEmptyInMemoryRepositories(provider);
        provider.init();
        assertTrue(iterableToCollection(provider.getSensors()).isEmpty());
    }

    @Test
    public void testAddSensor() throws InternalServiceException {
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance();
        assertFalse(getSensorPluginsFrom(sensorInstanceProvider).contains(testPlugin));
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(testPlugin);
        assertTrue(getSensorPluginsFrom(sensorInstanceProvider).contains(testPlugin));
    }

    @Test
    public void testGetSensors() throws InternalServiceException {
        assertTrue(getSensorPluginsFrom(sensorInstanceProvider).isEmpty());
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance();
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(testPlugin);
        SensorPlugin anotherTestPlugin = SimpleSensorPluginTestInstance.createInstance();
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(anotherTestPlugin);
        assertTrue( !getSensorPluginsFrom(sensorInstanceProvider).isEmpty());
        assertTrue(getSensorPluginsFrom(sensorInstanceProvider).size() == 2);
    }

    @Test
    public void testContainsTaskWith() throws InternalServiceException {
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance(sensorInstanceProvider);
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(testPlugin);
        SensorTask testTask = testPlugin.getSensorTaskService().createNewTask();
        assertTrue(sensorInstanceProvider.containsTaskWith(testTask.getTaskId()));
    }

    @Test
    public void testGetTaskForTaskId() throws InternalServiceException {
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance(sensorInstanceProvider);
        assertByTaskCreationViaGlobalTaskRepository(testPlugin);
        assertByTaskCreationViaSensorTaskServiceProxy(testPlugin);
    }

    private void assertByTaskCreationViaGlobalTaskRepository(SensorPlugin testPlugin) {
        SensorTaskRepository taskRepository = sensorInstanceProvider.getSensorTaskRepository();
        SensorTask sensorTask = taskRepository.createNewSensorTask(testPlugin.getProcedure());
        assertTrue(sensorInstanceProvider.containsTaskWith(sensorTask.getTaskId()));
    }

    private void assertByTaskCreationViaSensorTaskServiceProxy(SensorPlugin testPlugin) {
        SensorTaskService taskService = testPlugin.getSensorTaskService();
        SensorTask anotherSensorTask = taskService.createNewTask();
        assertTrue(sensorInstanceProvider.containsTaskWith(anotherSensorTask.getTaskId()));
    }

    @Test
    public void testContainsSensorWith() throws InternalServiceException {
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance(sensorInstanceProvider);
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(testPlugin);
        assertTrue(sensorInstanceProvider.containsSensorWith(testPlugin.getProcedure()));
    }

    @Test
    public void testGetSensorForProcedure() throws InternalServiceException {
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance(sensorInstanceProvider);
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(testPlugin);
        assertEquals(sensorInstanceProvider.getSensorForProcedure(testPlugin.getProcedure()), testPlugin);
    }

    @Test
    public void testGetSensorForTaskId() throws InternalServiceException {
        SensorPlugin testPlugin = SimpleSensorPluginTestInstance.createInstance(sensorInstanceProvider);
        ((SensorInstanceProviderSeam) sensorInstanceProvider).addSensor(testPlugin);
        SensorTaskService taskService = testPlugin.getSensorTaskService();
        SensorTask sensorTask = taskService.createNewTask();
        assertEquals(testPlugin, sensorInstanceProvider.getSensorForTaskId(sensorTask.getTaskId()));
    }

    @Test
    public void testGetSensorConfigurationRepository() throws InternalServiceException {
        SensorInstanceProvider provider = new SensorInstanceProvider();
        assertNull(provider.getSensorConfigurationRepository());
        setEmptyInMemoryRepositories(provider);
        assertNotNull(provider.getSensorConfigurationRepository());
    }

    @Test
    public void testSetSensorConfigurationRepository() throws InternalServiceException {
        SensorInstanceProvider provider = new SensorInstanceProvider();
        SensorConfigurationRepository repository = new InMemorySensorConfigurationRepository();
        provider.setSensorConfigurationRepository(repository);
        assertEquals(repository, provider.getSensorConfigurationRepository());
    }

    @Test
    public void testGetSensorTaskRepository() throws InternalServiceException {
        SensorInstanceProvider provider = new SensorInstanceProvider();
        assertNull(provider.getSensorTaskRepository());
        setEmptyInMemoryRepositories(provider);
        assertNotNull(provider.getSensorTaskRepository());
    }

    @Test
    public void testSetSensorTaskRepository() {
        SensorInstanceProvider provider = new SensorInstanceProvider();
        SensorTaskRepository repository = new InMemorySensorTaskRepository();
        provider.setSensorTaskRepository(repository);
        assertEquals(repository, provider.getSensorTaskRepository());
    }

    /**
     * Does testing with overridden {@link SensorInstanceProviderSeam#getSensorFactory()}. This does
     * <b>not</b> test the {@link ServiceLoader} implementations which dynamically loads {@link SensorPlugin}
     * instances available from classpath.
     * 
     * @throws InternalServiceException
     */
    @Test
    public void testHandleInsertSensorOffering() throws InternalServiceException {
        InsertSensorOfferingEvent event = new InsertSensorOfferingEvent(insertSensorOffering);
        assertCorrectInsertOfferingHandling(event);
        assertCorrectSensorConfigurationHandling();
    }

    private void assertCorrectInsertOfferingHandling(InsertSensorOfferingEvent event) throws InternalServiceException {
        sensorInstanceProvider.handleInsertSensorOffering(event);
        assertTrue(getSensorPluginsFrom(sensorInstanceProvider).size() == 1);
        assertTrue(sensorInstanceProvider.containsSensorWith(INSERT_OFFERING_PROCEDURE));
    }

    private void assertCorrectSensorConfigurationHandling() {
        SensorPlugin sensorPlugin = sensorInstanceProvider.getSensorForProcedure(INSERT_OFFERING_PROCEDURE);
        SensorConfigurationRepository configRepo = sensorInstanceProvider.getSensorConfigurationRepository();
        assertEquals(sensorPlugin.getSensorConfiguration(), configRepo.getSensorConfiguration(sensorPlugin.getProcedure()));
    }

    private Collection<SensorPlugin> getSensorPluginsFrom(SensorInstanceProvider provider) {
        return iterableToCollection(provider.getSensors());
    }
    
    private <T> Collection<T> iterableToCollection(Iterable<T> elements) {
        List<T> sensorPlugins = new ArrayList<T>();
        Iterator<T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            sensorPlugins.add(iterator.next());
        }
        return sensorPlugins;
    }

    private class SensorInstanceProviderSeam extends SensorInstanceProvider {

        @Override
        public void addSensor(SensorPlugin sensorInstance) {
            super.addSensor(sensorInstance);
        }

        @Override
        protected SensorInstanceFactory getSensorFactory(String pluginType) throws PluginTypeConfigurationException {
            return new SensorInstanceFactory() {
                
                public String getPluginType() {
                    return INSERT_SENSOR_OFFERING_PLUGIN_TYPE;
                }
                
                public SensorPlugin createSensorPlugin(SensorTaskService sensorTaskService, SensorConfiguration sensorConfiguration) throws InternalServiceException {
                    return SimpleSensorPluginTestInstance.createInstance(sensorTaskService, sensorConfiguration);
                }
            };
        }
    }

}
