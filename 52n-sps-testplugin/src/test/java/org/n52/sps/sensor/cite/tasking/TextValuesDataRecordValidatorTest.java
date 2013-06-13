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
