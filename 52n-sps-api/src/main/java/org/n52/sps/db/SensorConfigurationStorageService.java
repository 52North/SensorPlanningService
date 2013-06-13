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
