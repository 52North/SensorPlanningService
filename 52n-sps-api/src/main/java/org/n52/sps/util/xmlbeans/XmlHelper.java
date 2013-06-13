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

package org.n52.sps.util.xmlbeans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import net.opengis.ows.x11.AbstractReferenceBaseType;
import net.opengis.ows.x11.ReferenceDocument;
import net.opengis.ows.x11.ReferenceType;
import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.BinaryEncodingDocument;
import net.opengis.swe.x20.BinaryEncodingType;
import net.opengis.swe.x20.TextEncodingDocument;
import net.opengis.swe.x20.TextEncodingType;
import net.opengis.swe.x20.XMLEncodingDocument;
import net.opengis.swe.x20.XMLEncodingType;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class XmlHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(XmlHelper.class);

    public final static XmlOptions DEBUG_OPTIONS;

    static {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("sps", "http://www.opengis.net/sps/2.0");
        namespaces.put("swe", "http://www.opengis.net/swe/2.0");
        namespaces.put("sensorML", "http://www.opengis.net/sensorML/1.0.1");
        namespaces.put("ows", "http://www.opengis.net/ows/1.1");
        namespaces.put("n52sps", "http://www.52north.org/schemas/sps/v2");

        DEBUG_OPTIONS = new XmlOptions()
                            .setSaveOuter()
                            .setSavePrettyPrint()
                            .setSavePrettyPrintOffset(2)
                            .setSaveSuggestedPrefixes(Collections.unmodifiableMap(namespaces));
    }

    public final static SchemaType TEXT_ENCODING_TYPE = TextEncodingDocument.type;

    public final static SchemaType XML_ENCODING_TYPE = XMLEncodingDocument.type;

    public final static SchemaType BINARY_ENCODING_TYPE = BinaryEncodingDocument.type;

    public final static QName SENSOR_OFFERING = new QName("SensorOfferingType");

    public final static QName REFERENCE = new QName("ReferenceType");

    public static boolean isSubstitutedTextEncoding(AbstractEncodingType encoding) {
        return TEXT_ENCODING_TYPE.equals(encoding.newCursor().getName());
    }

    public static boolean isSubstitutedXmlEncoding(AbstractEncodingType encoding) {
        return XML_ENCODING_TYPE.equals(encoding.newCursor().getName());
    }

    public static boolean isSubstitutedBinaryEncoding(AbstractEncodingType encoding) {
        return BINARY_ENCODING_TYPE.equals(encoding.newCursor().getName());
    }

    /**
     * Substitutes the passed <code>encoding</code> type to <b>TextEncodingType</b>.
     * 
     * @param the
     *        encoding to substitute.
     * @return returns a TextEncoding document containing the substituted encoding type or an empty document.
     */
    public static TextEncodingDocument substituteWithTextEncoding(AbstractEncodingType encoding) {
        TextEncodingDocument encodedDocument = TextEncodingDocument.Factory.newInstance();
        try {
            XmlObject substitute = encoding.substitute(TEXT_ENCODING_TYPE.getDocumentElementName(), TEXT_ENCODING_TYPE);
            encodedDocument.setTextEncoding((TextEncodingType) substitute);
        }
        catch (ClassCastException e) {
            LOGGER.error("Could not substitute to '{}'.", encodedDocument.schemaType());
            return encodedDocument;
        }
        return encodedDocument;
    }

    /**
     * Substitutes the passed <code>encoding</code> type to <b>XmlEncodingType</b>.
     * 
     * @param the
     *        encoding to substitute.
     * @return returns a XmlEncoding document containing the substituted encoding type or an empty document.
     */
    public static XMLEncodingDocument substituteWithXmlEncoding(AbstractEncodingType encoding) {
        XMLEncodingDocument encodedDocument = XMLEncodingDocument.Factory.newInstance();
        try {
            XmlObject substitute = encoding.substitute(XML_ENCODING_TYPE.getDocumentElementName(), XML_ENCODING_TYPE);
            encodedDocument.setXMLEncoding((XMLEncodingType) substitute);
        }
        catch (ClassCastException e) {
            LOGGER.error("Could not substitute to '{}'.", encodedDocument.schemaType());
            return encodedDocument;
        }
        return encodedDocument;
    }

    /**
     * Substitutes the passed <code>encoding</code> type to <b>BinaryEncodingType</b>.
     * 
     * @param the
     *        encoding to substitute.
     * @return returns a BinaryEncoding document containing the substituted encoding type or an empty
     *         document.
     */
    public static BinaryEncodingDocument substituteWithBinaryEncoding(AbstractEncodingType encoding) {
        BinaryEncodingDocument encodedDocument = BinaryEncodingDocument.Factory.newInstance();
        try {
            XmlObject substitute = encoding.substitute(BINARY_ENCODING_TYPE.getDocumentElementName(), BINARY_ENCODING_TYPE);
            encodedDocument.setBinaryEncoding((BinaryEncodingType) substitute);
        }
        catch (ClassCastException e) {
            LOGGER.error("Could not substitute to '{}'.", encodedDocument.schemaType());
            return encodedDocument;
        }
        return encodedDocument;
    }

    public static ReferenceDocument substituteWithReference(AbstractReferenceBaseType abstractReference) {
        ReferenceDocument referenceDocument = ReferenceDocument.Factory.newInstance();
        try {
            XmlObject substitute = abstractReference.substitute(REFERENCE, ReferenceType.type);
            referenceDocument.setAbstractReferenceBase((ReferenceType) substitute);
        }
        catch (ClassCastException e) {
            LOGGER.error("Could not substitute to '{}'.", referenceDocument.schemaType());
            return referenceDocument;
        }
        return referenceDocument;
    }

    public static XmlObject qualifyWith(SchemaType qualifiedType, XmlObject toQualify) {
        return toQualify.substitute(qualifiedType.getDocumentElementName(), qualifiedType);        
    }
    
    public static String getTextContentFromAnyNode(XmlObject xmlObject) {
        Node node = xmlObject.getDomNode();
        return node.getFirstChild().getNodeValue();
    }

    public static XmlObject setTextContent(XmlObject xmlObject, String content) {
        XmlCursor cursor = xmlObject.newCursor();
        cursor.toFirstChild();
        cursor.setTextValue(content);
        return xmlObject;
    }

}
