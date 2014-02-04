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
package org.n52.sps.sensor.cite;

import static org.junit.Assert.*;

import net.opengis.ows.x11.ServiceReferenceType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.admin.MissingSensorInformationException;
import org.n52.sps.service.core.SensorInstanceProvider;
import org.n52.testing.utilities.FileContentLoader;
import org.x52North.schemas.sps.v2.SensorInstanceDataDocument;
import org.x52North.schemas.sps.v2.SensorInstanceDataDocument.SensorInstanceData;

public class CiteTaskResultAccessTest {

    private CiteTaskResultAccess citeTaskResultAccess;
    private String procedure;
    private String taskId;

    @Before
    public void setUp() throws Exception {
        XmlObject file = FileContentLoader.loadXmlFileViaClassloader("/files/sensorInstanceData.xml", getClass());
        SensorInstanceDataDocument.Factory.newInstance();
        SensorInstanceDataDocument instanceDataDoc = SensorInstanceDataDocument.Factory.parse(file.newInputStream());
        SensorInstanceData sensorInstanceData = instanceDataDoc.getSensorInstanceData();
        SensorInstanceProviderSeam sensorInstanceProviderSeam = new SensorInstanceProviderSeam();
        ResultAccessDataServiceReference reference = sensorInstanceProviderSeam.createResultAccess(sensorInstanceData);

        procedure = "http://host.tld/procedure1/";
        taskId = "http://host.tld/procedure1/tasks/1";
        SensorTask validTask = new SensorTask(taskId, procedure);
        citeTaskResultAccess = new CiteTaskResultAccess(validTask, reference);
    }

    @Test
    public void testCreateRequestMessageReference() throws Exception {
        String request = citeTaskResultAccess.createRequest();
        assertNotNull(XmlObject.Factory.parse(request));
    }
    
    @Test
    public void testCreateServiceReference() throws Exception {
        ServiceReferenceType reference = citeTaskResultAccess.createServiceReference();
        assertNotNull(reference);
    }
    
    class SensorInstanceProviderSeam extends SensorInstanceProvider {
        ResultAccessDataServiceReference createResultAccess(SensorInstanceData sensorInstanceData) throws MissingSensorInformationException {
            return this.createResultAccessReference(sensorInstanceData);
        }
    }

}
