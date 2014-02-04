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
