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
package org.n52.sps.tasking;

import java.util.Calendar;

import net.opengis.sps.x20.ParameterDataType;
import net.opengis.sps.x20.TaskingRequestDocument;
import net.opengis.sps.x20.TaskingRequestType;
import net.opengis.swe.x20.AbstractEncodingType;

import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.swes.request.ExtensibleRequest;
import org.n52.sps.util.encoding.SweEncoderDecoder;
import org.n52.sps.util.encoding.binary.BinaryEncoderDecoder;
import org.n52.sps.util.encoding.text.TextEncoderDecoder;
import org.n52.sps.util.encoding.xml.XmlEncoderDecoder;
import org.n52.sps.util.xmlbeans.XmlHelper;

public abstract class TaskingRequest implements ExtensibleRequest {

    private TaskingRequestDocument taskingRequestDocument;
    private XmlObject[] extensions;
    private String procedure;
    private ParameterDataType parameterData;
    private AbstractEncodingType encoding;
    private Calendar latestResponseTime;

    public TaskingRequest(TaskingRequestDocument taskingRequestDoc) {
        taskingRequestDocument = taskingRequestDoc;
        TaskingRequestType taskingRequest = taskingRequestDocument.getTaskingRequest();
        extensions = taskingRequest.getExtensionArray();
        procedure = taskingRequest.getProcedure();
        parameterData = taskingRequest.getTaskingParameters().getParameterData();
        encoding = parameterData.getEncoding().getAbstractEncoding();
        latestResponseTime = taskingRequest.getLatestResponseTime();
    }
    
    public TextEncoderDecoder getTextEncoderDecoder() {
        return SweEncoderDecoder.Factory.createTextEncoderDecoder(encoding);
    }
    
    public XmlEncoderDecoder getXmlEncoderDecoder() {
        return SweEncoderDecoder.Factory.createXmlEncoderDecoder(encoding);
    }
    
    public BinaryEncoderDecoder getBinaryEncoderDecoder() {
        return SweEncoderDecoder.Factory.createBinaryEncoderDecoder(encoding);
    }
    
    public String getParameterEncoding() {
        if (XmlHelper.isSubstitutedTextEncoding(encoding)) {
            return TextEncoderDecoder.TEXT_ENCODING;
        } else if (XmlHelper.isSubstitutedXmlEncoding(encoding)) {
            return XmlEncoderDecoder.XML_ENCODING;
        } else if (XmlHelper.isSubstitutedBinaryEncoding(encoding)) {
            return BinaryEncoderDecoder.BINARY_ENCODING;
        } else {
            return "http://www.opengis.net/swe/2.0/UnknownEncoding";
        }
    }

    public boolean isSupportingExtensions() {
        return extensions != null && extensions.length > 0;
    }
    
    public XmlObject[] getExtensions() {
        return isSupportingExtensions() ? extensions : new XmlObject[0];
    }
    
    public String getProcedure() {
        return procedure;
    }
    
    public ParameterDataType getParameterData() {
        return parameterData;
    }
    
    public Calendar getLatestResponseTime() {
        return latestResponseTime;
    }

    @Override
    public String toString() {
        return taskingRequestDocument.xmlText();
    }

}
