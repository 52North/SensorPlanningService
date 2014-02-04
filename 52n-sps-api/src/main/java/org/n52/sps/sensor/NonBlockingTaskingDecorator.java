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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.opengis.sps.x20.DescribeResultAccessResponseType.Availability;
import net.opengis.swe.x20.AbstractDataComponentType;

import org.n52.ows.exception.NoApplicableCodeException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.tasking.SubmitTaskingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SensorPlugin} which executes all tasking requests asynchronously. Wrap a {@link SensorPlugin}
 * instance with a {@link NonBlockingTaskingDecorator} to avoid framework freezing if the wrapped
 * instance gets stuck when executing tasking requests.
 */
public class NonBlockingTaskingDecorator extends SensorPlugin {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingTaskingDecorator.class);

    private static final Executor taskingExecutor = Executors.newSingleThreadExecutor();
    
    private static int NON_BLOCK_TIMEOUT = 10000; // 10 sec
    
    private SensorPlugin sensorPlugin;
    
    static void setTimeOut(int timeout) {
        NON_BLOCK_TIMEOUT = timeout;
    }
    
    public static SensorPlugin enableNonBlockingTasking(SensorPlugin sensorPlugin) {
        return new NonBlockingTaskingDecorator(sensorPlugin);
    }

    private NonBlockingTaskingDecorator(SensorPlugin sensorPlugin) {
        this.sensorPlugin = sensorPlugin; 
    }
    
    public SensorTaskService getSensorTaskService() {
        return sensorPlugin.getSensorTaskService();
    }

    public void setSensorTaskService(SensorTaskService sensorTaskService) {
        sensorPlugin.setSensorTaskService(sensorTaskService);
    }

    public String getSensorPluginType() {
        return sensorPlugin.getSensorPluginType();
    }

    public SensorConfiguration getSensorConfiguration() {
        return sensorPlugin.getSensorConfiguration();
    }

    public ResultAccessDataServiceReference getResultAccessDataServiceReference() {
        return sensorPlugin.getResultAccessDataServiceReference();
    }

    public SensorConfiguration mergeSensorConfigurations(SensorConfiguration sensorConfiguration) {
        return sensorPlugin.mergeSensorConfigurations(sensorConfiguration);
    }

    public String toString() {
        return sensorPlugin.toString();
    }

    public Availability getResultAccessibility() {
        return sensorPlugin.getResultAccessibility();
    }

    public boolean isDataAvailable() {
        return sensorPlugin.isDataAvailable();
    }

    @Override
    public SensorTask submit(final SubmitTaskingRequest submit, final OwsExceptionReport owsExceptionReport) throws OwsException {
        try {
            FutureTask<SensorTask> createSensorTask = new FutureTask<SensorTask>(new Callable<SensorTask>() {
                public SensorTask call() throws Exception {
                    return sensorPlugin.submit(submit, owsExceptionReport);
                }
            });
            taskingExecutor.execute(createSensorTask);
            return createSensorTask.get(NON_BLOCK_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (ExecutionException e) {
            LOGGER.warn("No SensorTask could be created for submit request {}", submit, e);
            NoApplicableCodeException ex = new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
            ex.addExceptionText("Sensor plugin did not succeed creating a sensor task for submit request.");
            ex.addExceptionText(e.getMessage());
            throw ex;
        }
        catch (InterruptedException e) {
            LOGGER.error("No SensorTask could be created for submit request {}", submit, e);
            NoApplicableCodeException ex = new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
            ex.addExceptionText("Sensor plugin was interrupted while creating a sensor task for submit request.");
            ex.addExceptionText(e.getMessage());
            throw ex;
        }
        catch (TimeoutException e) {
            LOGGER.error("Unacceptable delay creating sensor task (> {} ms)", NON_BLOCK_TIMEOUT, e);
            NoApplicableCodeException ex = new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
            ex.addExceptionText("Sensor plugin got stuck in creating sensor task.");
            throw ex;
        }
    }

    @Override
    public void qualifyDataComponent(AbstractDataComponentType componentToQualify) {
        sensorPlugin.qualifyDataComponent(componentToQualify);
    }

    @Override
    public Availability getResultAccessibilityFor(SensorTask sensorTask) {
        return sensorPlugin.getResultAccessibilityFor(sensorTask);
    }
    
    

}
