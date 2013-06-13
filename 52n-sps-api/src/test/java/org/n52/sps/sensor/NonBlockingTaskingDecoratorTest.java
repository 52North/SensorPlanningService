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
