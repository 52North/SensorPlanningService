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
package org.n52.sps.util.encoding.text;

import junit.framework.TestCase;
import net.opengis.sps.x20.ParameterDataDocument;
import net.opengis.sps.x20.ParameterDataType;
import net.opengis.sps.x20.TaskingRequestType.TaskingParameters;
import net.opengis.swe.x20.TextEncodingType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sps.util.encoding.EncodingException;
import org.n52.sps.util.xmlbeans.XmlHelper;

public class SweTextEncoderDecoderTest extends TestCase {
    
    private final String[] tokens1 = { "2010-08-20T12:37:00+02:00","2010-08-20T14:30:00+02:00","Y","pointToLookAt","51.902112","8.192728","0","Y","3.5" };
    private final String[] tokens2 = { "2010-08-20T15:37:00+02:00","2010-08-20T20:30:00+02:00","X","pointToLookAt","51.902112","8.192728","0","Y","3.0" }; 
    private final String[][] toEncode = { tokens1, tokens2 };
    private final String toDecode = "2010-08-20T12:37:00+02:00,2010-08-20T14:30:00+02:00,Y,pointToLookAt,51.902112,8.192728,0,Y,3.5@@2010-08-20T15:37:00+02:00,2010-08-20T20:30:00+02:00,X,pointToLookAt,51.902112,8.192728,0,Y,3.0";
    
    private String tokenSeparator = ",";
    private String blockSeparator ="@@";
    private String decimalSeparator = ".";
    private boolean collapse = true;
    private TextEncoderDecoder encoderDecoder;
    private ParameterDataType parameterData;
    private TextEncodingType textEncoding;
    
    @Override
    protected void setUp() throws Exception {
        TaskingParameters taskingParameters = TaskingParameters.Factory.newInstance();
        parameterData = taskingParameters.addNewParameterData();
        textEncoding = TextEncodingType.Factory.newInstance();
        textEncoding.setCollapseWhiteSpaces(collapse);
        textEncoding.setBlockSeparator(blockSeparator);
        textEncoding.setTokenSeparator(tokenSeparator);
        textEncoding.setDecimalSeparator(decimalSeparator);
        parameterData.addNewEncoding().setAbstractEncoding(textEncoding);
        XmlObject values = parameterData.addNewValues();
        XmlHelper.setTextContent(values, toDecode);
        
        encoderDecoder = new TextEncoderDecoder(textEncoding);
    }

    public void testStaticDecode() {
        try {
            String[][] decodedValues = TextEncoderDecoder.decode(parameterData);
            for (int i = 0; i < decodedValues.length; i++) {
                for (int j = 0; j < toEncode.length; j++) {
                    assertEquals(toEncode[i][j], decodedValues[i][j]);
                }
            }
        } catch(EncodingException e) {
            fail();
        }
    }

    public void testStaticEncode() {
        try {
            ParameterDataDocument parameters = TextEncoderDecoder.encode(textEncoding, toEncode);
            String encoded = XmlHelper.getTextContentFromAnyNode(parameters.getParameterData().getValues());
            assertEquals(toDecode, encoded);
        }
        catch (EncodingException e) {
            fail();
        }
    }
    
    public void testDecode() {
        try {
            String[][] decoded = encoderDecoder.decode(toDecode);
            for (int i = 0; i < decoded.length; i++) {
                for (int j = 0; j < toEncode.length; j++) {
                    assertEquals(toEncode[i][j], decoded[i][j]);
                }
            }
        }
        catch (EncodingException e) {
            fail();
        }
    }

    public void testEncode() {
        try {
            String encoded = encoderDecoder.encode(toEncode);
            assertEquals(toDecode, encoded);
        }
        catch (EncodingException e) {
            fail();
        }
    }
}
