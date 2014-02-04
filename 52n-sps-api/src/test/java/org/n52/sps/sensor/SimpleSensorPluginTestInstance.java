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
package org.n52.sps.sensor;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.sps.x20.DescribeResultAccessResponseType.Availability;
import net.opengis.swe.x20.AbstractDataComponentType;

import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorDescription;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.sensor.result.DataNotAvailable;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.service.core.SensorInstanceProvider;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.tasking.SubmitTaskingRequest;
import org.n52.sps.util.nodb.InMemorySensorTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves as test mockup and does not provide test code for testing implementing methods. To be used to test
 * {@link SensorPlugin} methods in general.
 */
public class SimpleSensorPluginTestInstance extends SensorPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSensorPluginTestInstance.class);
    
    private static final String PROCEDURE_DESCRIPTION_FORMAT = "http://www.opengis.net/sensorML/2.0";
    
    private static final String PROCEDURE = "http://my.namespace.org/procedure/";
    
    private static int procedureNumber = 0;
    
    private static final String PLUGIN_TYPE = "test_procedure";

    /**
     * Creates a plugin instance for testing. It creates its {@link InMemorySensorTaskRepository} instance.
     * 
     * @return a very simple instance of {@link SensorPlugin} for testing purposes.
     * @throws InternalServiceException if instance creation fails
     */
    public static SensorPlugin createInstance() throws InternalServiceException {
        SensorTaskRepository taskRepository = new InMemorySensorTaskRepository();
        SensorTaskService taskService = new SensorTaskService(taskRepository);
        return new SimpleSensorPluginTestInstance(taskService, createSensorConfiguration());
    }
    
    /**
     * Creates a plugin instance for testing which is coupled to a {@link SensorInstanceProvider}'s configuration, 
     * i.e. reuses the {@link SensorTaskRepository} instance from the instance provider to interact with tasks.
     * 
     * @param sensorInstanceProvider a sensorProvider which provides access to a {@link SensorTaskRepository}
     * @return an instance of {@link SensorPlugin} for testing purposes.
     * @throws InternalServiceException if instance creation fails
     */
    public static SensorPlugin createInstance(SensorInstanceProvider sensorInstanceProvider) throws InternalServiceException {
        SensorTaskRepository taskRepository = sensorInstanceProvider.getSensorTaskRepository();
        SensorTaskService taskService = new SensorTaskService(taskRepository);
        return new SimpleSensorPluginTestInstance(taskService, createSensorConfiguration());
    }
    
    private static SensorConfiguration createSensorConfiguration() {
        SensorConfiguration configuration = new SensorConfiguration();
        configuration.setProcedure(PROCEDURE + ++procedureNumber);
        configuration.setSensorPluginType(PLUGIN_TYPE);
        SensorDescription sensorDescription = new SensorDescription(PROCEDURE_DESCRIPTION_FORMAT, "http://my.link.com/");
        configuration.addSensorDescription(sensorDescription);
        return configuration;
    }

    public static SensorPlugin createInstance(SensorTaskService sensorTaskService, SensorConfiguration sensorConfiguration) throws InternalServiceException {
        return new SimpleSensorPluginTestInstance(sensorTaskService, sensorConfiguration);
    }

    private SimpleSensorPluginTestInstance(SensorTaskService sensorTaskService, SensorConfiguration configuration) throws InternalServiceException {
        super(sensorTaskService, configuration);
    }

    public Availability getResultAccessibility() {
        LanguageStringType testMessage = LanguageStringType.Factory.newInstance();
        testMessage.setStringValue("no data available for testing purposes.");
        return new DataNotAvailable(testMessage).getResultAccessibility();
    }

    public boolean isDataAvailable() {
        return false;
    }

    @Override
    public SensorTask submit(SubmitTaskingRequest submit, OwsExceptionReport owsExceptionReport) throws OwsException {
        LOGGER.warn("method does not contain test code.");
        return null;
    }

    @Override
    public void qualifyDataComponent(AbstractDataComponentType componentToQualify) {
        LOGGER.warn("method does not contain test code.");
    }

    @Override
    public Availability getResultAccessibilityFor(SensorTask sensorTask) {
        // TODO Auto-generated method stub
        return null;
        
    }
    
    public void setSensorConfiguration(SensorConfiguration sensorConfiguration) {
        this.configuration = sensorConfiguration;
    }

}
