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

import net.opengis.sps.x20.DescribeResultAccessResponseType.Availability;
import net.opengis.swe.x20.AbstractDataComponentType;

import org.junit.Before;
import org.junit.Test;
import org.n52.ows.exception.NoApplicableCodeException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.tasking.SubmitTaskingRequest;

public class NonBlockingTaskingDecoratorTest {
    
    private int BLOCK_TIME_OUT = 200; // 0.2 sec.
    
    private long EXPIRING_TIMEOUT_DURATION = BLOCK_TIME_OUT + 300; // + 0.5 sec

    private SensorPlugin instance;

    @Before
    public void setUp() throws Exception {
        NonBlockingTaskingDecorator.setTimeOut(BLOCK_TIME_OUT);
        instance = new SensorPluginSeam();
    }

    @Test(expected = NoApplicableCodeException.class)
    public void testSubmit() throws OwsException {
        SensorPlugin nonBlockInstance = NonBlockingTaskingDecorator.enableNonBlockingTasking(instance);
        nonBlockInstance.submit(null, null);
    }
    
    private class SensorPluginSeam extends SensorPlugin {

        public Availability getResultAccessibility() {
            return null;
        }

        public boolean isDataAvailable() {
            return false;
        }
        
        @Override
        public SensorTask submit(SubmitTaskingRequest submit, OwsExceptionReport owsExceptionReport) throws OwsException {
            try {
                Thread.sleep(EXPIRING_TIMEOUT_DURATION);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void qualifyDataComponent(AbstractDataComponentType componentToQualify) {
            // no intend to test
            
        }

        @Override
        public Availability getResultAccessibilityFor(SensorTask sensorTask) {
            return null;
            
        }
        
    }

}
