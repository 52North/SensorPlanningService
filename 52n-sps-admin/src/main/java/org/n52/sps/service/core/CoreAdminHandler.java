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
package org.n52.sps.service.core;

import org.n52.sps.service.InternalServiceException;
import org.n52.sps.service.SensorPlanningService;
import org.n52.sps.service.admin.DeleteSensorOfferingEvent;
import org.n52.sps.service.admin.DeleteSensorOfferingListener;
import org.n52.sps.service.admin.InsertSensorOfferingEvent;
import org.n52.sps.service.admin.InsertSensorOfferingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreAdminHandler implements InsertSensorOfferingListener, DeleteSensorOfferingListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreAdminHandler.class);
    
    public SensorPlanningService service;
    
    public CoreAdminHandler(SensorPlanningService service) {
        this.service = service;
    }

    public void handleInsertSensorOffering(InsertSensorOfferingEvent registerSensorEvent) throws InternalServiceException {
        LOGGER.debug("handleInsertSensorOffering");
        // TODO Auto-generated method stub

    }

    public void handleDeleteSensorOffering(DeleteSensorOfferingEvent deleteSensorOfferingEvent) throws InternalServiceException {
        LOGGER.debug("handleDeleteSensorOffering");
        // TODO Auto-generated method stub

    }

    public SensorPlanningService getService() {
        return service;
    }

    public void setService(SensorPlanningService service) {
        this.service = service;
    }
    
}
