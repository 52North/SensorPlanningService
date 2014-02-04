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
