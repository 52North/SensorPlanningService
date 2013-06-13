package org.n52.sps.util.convert;

import static org.junit.Assert.assertTrue;
import static org.n52.oxf.xml.XMLTools.isNCName;

import org.junit.Test;


public class InsertSensorOfferingConverterTest {

    @Test public void 
    shouldCreateValidNcNameGmlId()
    {
        InsertSensorOfferingConverter converter = new InsertSensorOfferingConverter();
        String randomGmlId = converter.createRandomGmlId();
        assertTrue(randomGmlId + " is not a valid NcName!", isNCName(randomGmlId));
    }
}
