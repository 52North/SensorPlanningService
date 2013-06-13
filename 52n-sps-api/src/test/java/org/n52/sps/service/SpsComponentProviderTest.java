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
package org.n52.sps.service;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.n52.ows.exception.OwsException;
import org.n52.sps.service.core.BasicSensorPlannerMockup;
import org.n52.sps.service.core.SensorProviderMockup;

public class SpsComponentProviderTest {

    private SpsComponentProvider spsComponentProvider;

    @Before
    public void setUp() throws Exception {
        spsComponentProvider = new SpsComponentProvider(new BasicSensorPlannerMockup(), new SensorProviderMockup());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithInvalidComponents() {
        new SpsComponentProvider(null, null);
    }

    @Test
    public void testInvalidServiceRequestParameter() throws OwsException {
        assertOwsExceptionWithServiceLocator("oiweur", "2.0.0");
        assertOwsExceptionWithServiceLocator("sps", "2.0.0");
        assertOwsExceptionWithVersionLocator("SPS", "2.0");
        assertOwsExceptionWithVersionLocator("SPS", "");
        assertOwsExceptionWithVersionLocator("SPS", null);
        spsComponentProvider.validateMandatoryServiceParameter("SPS");
        spsComponentProvider.validateVersionParameter("2.0.0");
    }
    
    private void assertOwsExceptionWithServiceLocator(String service, String version) {
        try {
            spsComponentProvider.validateMandatoryServiceParameter(service);
            spsComponentProvider.validateVersionParameter(version);
        }
        catch (OwsException e) {
            if (!"service".equalsIgnoreCase(e.getLocator())) {
                fail("Wrong locator value.");
            }
        }
    }
    
    private void assertOwsExceptionWithVersionLocator(String service, String version) {
        try {
            spsComponentProvider.validateMandatoryServiceParameter(service);
            spsComponentProvider.validateVersionParameter(version);
        }
        catch (OwsException e) {
            if (!"version".equalsIgnoreCase(e.getLocator())) {
                fail("Wrong locator value.");
            }
        }
    }
}
