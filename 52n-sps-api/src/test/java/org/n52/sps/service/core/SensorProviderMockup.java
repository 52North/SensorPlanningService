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
import net.opengis.swes.x20.DescribeSensorDocument;
import net.opengis.swes.x20.DescribeSensorResponseDocument;

import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorProviderMockup implements SensorProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorProviderMockup.class);

    public void interceptCapabilities(CapabilitiesType capabilities, List<HttpBinding> httpBindings) {
        LOGGER.info("Mockup: skipping interceptCapabilities");
    }

    public DescribeSensorResponseDocument describeSensor(DescribeSensorDocument describeSensor) throws OwsException,
            OwsExceptionReport {
        LOGGER.info("Mockup: skipping describeSensor and return null");
        return null;
    }

}
