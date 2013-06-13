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
