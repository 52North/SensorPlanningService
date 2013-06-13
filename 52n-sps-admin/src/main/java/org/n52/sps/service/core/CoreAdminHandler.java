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
