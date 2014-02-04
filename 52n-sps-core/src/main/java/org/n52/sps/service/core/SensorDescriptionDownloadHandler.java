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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.DescribeSensorResponseType.Description;
import net.opengis.swes.x20.SensorDescriptionType;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.util.http.SimpleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorDescriptionDownloadHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDescriptionDownloadHandler.class);

    private SensorConfiguration sensorConfiguration;

    private String procedure;

    public SensorDescriptionDownloadHandler(SensorConfiguration sensorConfiguration) {
        this.sensorConfiguration = sensorConfiguration;
        this.procedure = sensorConfiguration.getProcedure();
    }

    /**
     * Adds all sensor descriptions for a supported format. <br>
     * If a sensor descriptions could not be resolved, an empty {@link XmlObject} is being set as
     * <code>swes:data</code>
     * 
     * @param describeSensorResponse
     *        the response where the sensorDescriptions shall be added to.
     * @param format
     *        the requested format the sensor descriptions shall have.
     */
    public void addSensorDescriptions(DescribeSensorResponseType describeSensorResponse, String format) {
        List<String> sensorDescriptionUris = sensorConfiguration.getSensorDescriptionUrisFor(format);
        for (String uri : sensorDescriptionUris) {
            Description description = describeSensorResponse.addNewDescription();
            SensorDescriptionType sensorDescription = description.addNewSensorDescription();
            sensorDescription.addNewData().set(downloadSensorDescription(uri));
        }
    }

    private XmlObject downloadSensorDescription(String uri) {
        try {
            SimpleHttpClient sensorDescriptionDownload = new SimpleHttpClient();
            HttpEntity entity = sensorDescriptionDownload.executeGetMethod(uri);
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                try {
                    return XmlObject.Factory.parse(inputStream);
                }
                catch (XmlException e) {
                    LOGGER.error("Could not parse SensorDescription download for procedure '{}'.", procedure, e);
                }
                finally {
                    inputStream.close();
                }
            }
        }
        catch (ClientProtocolException e) {
            LOGGER.error("Could not download sensor description for procedure '{}'.", procedure, e);
        }
        catch (IOException e) {
            LOGGER.error("Could not read sensor description for procedure '{}'.", procedure, e);
        }
        return XmlObject.Factory.newInstance();
    }
}
