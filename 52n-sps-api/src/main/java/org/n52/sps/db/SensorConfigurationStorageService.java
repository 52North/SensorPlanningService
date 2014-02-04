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
package org.n52.sps.db;

import org.n52.sps.db.access.SensorConfigurationDao;
import org.n52.sps.db.access.SensorTaskDao;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.store.SensorConfigurationRepository;

public class SensorConfigurationStorageService implements SensorConfigurationRepository {
    
    private SensorConfigurationDao sensorConfigurationDao;
    
    private SensorTaskDao sensorTaskDao;

    public void storeNewSensorConfiguration(SensorConfiguration sensorConfiguration) {
        if (isExisting(sensorConfiguration.getProcedure())) {
            throw new DatabaseServiceException("Sensor configuration already exists!");
        };
        sensorConfigurationDao.saveInstance(sensorConfiguration);
    }

    private boolean isExisting(String procedure) {
        return getSensorConfiguration(procedure) != null;
    }

    public void saveOrUpdateSensorConfiguration(SensorConfiguration sensorConfiguration) {
        sensorConfigurationDao.updateInstance(sensorConfiguration);
    }

    public void removeSensorConfiguration(SensorConfiguration sensorConfiguration) {
        sensorConfigurationDao.deleteInstance(sensorConfiguration);
    }

    public SensorConfiguration getSensorConfiguration(String procedure) {
        return sensorConfigurationDao.getInstance(procedure);
    }

    public String getProcedureForTaskId(String taskId) {
        SensorTask sensorTask = sensorTaskDao.getInstance(taskId);
        return sensorTask.getProcedure();
    }

    public boolean containsSensorConfiguration(String procedure) {
        return isExisting(procedure);
    }

    public boolean containsSensorConfiguration(SensorPlugin sensorInstance) {
        return containsSensorConfiguration(sensorInstance.getProcedure());
    }

    public Iterable<SensorConfiguration> getSensorConfigurations() {
        return sensorConfigurationDao.getAllInstances();
    }

    public SensorConfigurationDao getSensorConfigurationDao() {
        return sensorConfigurationDao;
    }

    public void setSensorConfigurationDao(SensorConfigurationDao sensorConfigurationDao) {
        this.sensorConfigurationDao = sensorConfigurationDao;
    }

    public SensorTaskDao getSensorTaskDao() {
        return sensorTaskDao;
    }

    public void setSensorTaskDao(SensorTaskDao sensorTaskDao) {
        this.sensorTaskDao = sensorTaskDao;
    }

}
