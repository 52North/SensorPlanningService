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

import java.util.List;

import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.sps.x20.DescribeResultAccessDocument;
import net.opengis.sps.x20.DescribeTaskingDocument;
import net.opengis.sps.x20.GetCapabilitiesDocument;
import net.opengis.sps.x20.GetStatusDocument;
import net.opengis.sps.x20.GetTaskDocument;
import net.opengis.sps.x20.SubmitDocument;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicSensorPlannerMockup implements BasicSensorPlanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSensorPlannerMockup.class);

    public void interceptCapabilities(CapabilitiesType capabilities, List<HttpBinding> httpBindings) {
        LOGGER.info("Mockup: skipping interceptCapabilities");
    }

    public XmlObject describeResultAccess(DescribeResultAccessDocument describeResultAccess) throws OwsException, OwsExceptionReport {
        LOGGER.info("Mockup: skipping describeResultAccess and return null");
        return null;
    }

    public XmlObject describeTasking(DescribeTaskingDocument describeTasking) throws OwsException, OwsExceptionReport {
        LOGGER.info("Mockup: skipping describeTasking and return null");
        return null;
    }

    public XmlObject getCapabilities(GetCapabilitiesDocument getCapabilities) throws OwsException, OwsExceptionReport {
        LOGGER.info("Mockup: skipping getCapabilities and return null");
        return null;
    }

    public XmlObject getStatus(GetStatusDocument getStatus) throws OwsException, OwsExceptionReport {
        LOGGER.info("Mockup: skipping getStatus and return null");
        return null;
    }

    public XmlObject getTask(GetTaskDocument getTask) throws OwsException, OwsExceptionReport {
        LOGGER.info("Mockup: skipping getTask and return null");
        return null;
    }

    public XmlObject submit(SubmitDocument submit) throws OwsException, OwsExceptionReport {
        LOGGER.info("Mockup: skipping submit and return null");
        return null;
    }

}
