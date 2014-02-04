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
package org.n52.sps.sensor.cite.tasking;

import static org.junit.Assert.fail;

import java.io.IOException;

import net.opengis.sps.x20.SubmitDocument;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.DataRecordType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.sps.tasking.SubmitTaskingRequest;
import org.n52.testing.utilities.FileContentLoader;

public class TextValuesDataRecordValidatorTest {

    private final static String TASKING_PARAMETERS_TEMPLATE = "/files/taskingParameters.xml";
    
    private final static String VALID_SUBMIT_REQUEST = "/files/validSubmitRequest.xml";
    
    private TextValuesDataRecordValidator validator;

    private SubmitTaskingRequest validTaskingRequest;

    private DataRecordType dataRecordTemplate;

    @Before
    public void setUp() throws Exception {
        dataRecordTemplate = getTaskingParamters();
        validTaskingRequest = getValidTaskingRequest();
    }

    private DataRecordType getTaskingParamters() throws XmlException, IOException {
        XmlObject template = FileContentLoader.loadXmlFileViaClassloader(TASKING_PARAMETERS_TEMPLATE, getClass());
        return ((DataRecordDocument) template).getDataRecord();
    }

    private SubmitTaskingRequest getValidTaskingRequest() throws XmlException, IOException {
        XmlObject template = FileContentLoader.loadXmlFileViaClassloader(VALID_SUBMIT_REQUEST, getClass());
        return new SubmitTaskingRequest((SubmitDocument) template);
    }

    @Test
    public void testGetValidDataRecords() {
        try {
            // XXX test not complete ... FieldArray cannot be loaded during test => solve
            validator = new TextValuesDataRecordValidator(dataRecordTemplate, validTaskingRequest);
            validator.getValidDataRecords();
        }
        catch (InvalidParameterValueException e) {
            fail("Could not create valid data records from tasking request: " + validTaskingRequest);
            e.printStackTrace();
        }
    }
}
