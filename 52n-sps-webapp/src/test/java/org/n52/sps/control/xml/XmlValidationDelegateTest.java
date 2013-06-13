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
package org.n52.sps.control.xml;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.MissingParameterValueException;
import org.n52.ows.exception.OwsException;
import org.n52.sps.service.SensorPlanningService;
import org.n52.sps.service.SpsComponentProvider;
import org.n52.sps.service.core.BasicSensorPlanner;
import org.n52.sps.service.core.BasicSensorPlannerMockup;
import org.n52.sps.service.core.SensorProvider;
import org.n52.sps.service.core.SensorProviderMockup;
import org.n52.testing.utilities.FileContentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlValidationDelegateTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlValidationDelegateTest.class);

    private final static String REQUEST_VALID = "/files/request_valid.xml";

    private final static String REQUEST_GET_CAPABILITIES = "/files/GetCapabilities.xml";

    private final static String REQUEST_GET_CAPABILITIES_MISSING_SERVICE = "/files/GetCapabilities_missingServiceValue.xml";
    
    private final static String REQUEST_MISSING_SERVICE = "/files/request_missingServiceValue.xml";

    private final static String REQUEST_INVALID_SERVICE = "/files/request_invalidServiceValue.xml";
    
    private final static String REQUEST_MISSING_VERSION = "/files/request_missingVersionValue.xml";

    private final static String REQUEST_INVALID_VERSION = "/files/request_invalidVersionValue.xml";

    private XmlValidationDelegate validatingDelegate;

    @Before
    public void setUp() throws Exception {
        SensorPlanningService service = createSpsMockup();
        XmlDelegate delagate = new XmlDelegate(service, null);
        validatingDelegate = new XmlValidationDelegate(delagate);
    }

    private SensorPlanningService createSpsMockup() {
        BasicSensorPlanner planner = new BasicSensorPlannerMockup();
        SensorProvider provider = new SensorProviderMockup();
        return new SpsComponentProvider(planner, provider);
    }
    
    @Test
    public void testValidRequest() {
        try {
            assertValidServiceInRequest();
            assertValidVersionInRequest();
        } catch (Exception e) {
            LOGGER.error("A valid request resulted in an exception: {}", e);
            fail("A valid request resulted in an exception.");
        }
    }
    
    private void assertValidServiceInRequest() throws Exception {
        XmlObject submitDocument = getRequest(REQUEST_VALID);
        validatingDelegate.validateRequiredServiceParameter(submitDocument);
    }

    private void assertValidVersionInRequest() throws Exception {
        XmlObject submitDocument = getRequest(REQUEST_VALID);
        validatingDelegate.validateRequiredVersionParameter(submitDocument);
    }
    
    @Test(expected = MissingParameterValueException.class)
    public void testMissingServiceInRequest() throws Exception {
        XmlObject submitDocument = getRequest(REQUEST_MISSING_SERVICE);
        validatingDelegate.validateRequiredServiceParameter(submitDocument);
    }
    
    @Test(expected = InvalidParameterValueException.class)
    public void testInvalidServiceInRequest() throws Exception {
        XmlObject submitDocument = getRequest(REQUEST_INVALID_SERVICE);
        validatingDelegate.validateRequiredServiceParameter(submitDocument);
    }

    @Test(expected = MissingParameterValueException.class) // in xml: version=""
    public void testMissingVersionParameterInRequest() throws Exception {
        XmlObject submitDocument = getRequest(REQUEST_MISSING_VERSION);
        validatingDelegate.validateRequiredVersionParameter(submitDocument);
    }

    @Test(expected = InvalidParameterValueException.class) // in xml: version="INVALID"
    public void testInvalidVersionParameterInRequest() throws Exception {
        XmlObject submitDocument = getRequest(REQUEST_INVALID_VERSION);
        validatingDelegate.validateRequiredVersionParameter(submitDocument);
    }
    
    @Test
    public void testGetServiceParameter() throws OwsException {
        assertMissingParameterValueException(null);
        assertInvalidParamterValueException("SPS_");
        assertMissingParameterValueException("");
        assertInvalidParamterValueException("sps");
    }
    
    private void assertMissingParameterValueException(String parameter) throws OwsException {
        try {
            validatingDelegate.validateServiceParameter(parameter);
            fail("Expected MissingParameterValueException.");
        } catch (MissingParameterValueException e) {
            // expected
        }
    }
    
    private void assertInvalidParamterValueException(String parameter) throws OwsException {
        try {
            validatingDelegate.validateServiceParameter(parameter);
            fail("Expected InvalidParameterValueException.");
        } catch (InvalidParameterValueException e) {
            // expected
        }
    }

    @Test
    public void testIsGetCapabilitiesRequest() throws XmlException, IOException {
        XmlObject getCapabilities = getRequest(REQUEST_GET_CAPABILITIES);
        assertTrue(validatingDelegate.isGetCapabilitiesRequest(getCapabilities));
    }
    
    @Test(expected = MissingParameterValueException.class)
    public void testMissingServiceInGetCapabilitiesRequest() throws XmlException, IOException, OwsException {
        XmlObject getCapabilities = getRequest(REQUEST_GET_CAPABILITIES_MISSING_SERVICE);
        validatingDelegate.assureCorrectServiceAndVersionRequestParameters(getCapabilities);
    }

    private XmlObject getRequest(String file) throws XmlException, IOException {
        return FileContentLoader.loadXmlFileViaClassloader(file, getClass());
    }
    
}
