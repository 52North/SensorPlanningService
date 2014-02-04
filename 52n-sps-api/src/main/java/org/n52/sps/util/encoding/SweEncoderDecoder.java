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
package org.n52.sps.util.encoding;

import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.BinaryEncodingDocument;
import net.opengis.swe.x20.TextEncodingDocument;
import net.opengis.swe.x20.XMLEncodingDocument;

import org.apache.xmlbeans.XmlObject;
import org.n52.sps.util.encoding.binary.BinaryEncoderDecoder;
import org.n52.sps.util.encoding.text.TextEncoderDecoder;
import org.n52.sps.util.encoding.xml.XmlEncoderDecoder;
import org.n52.sps.util.xmlbeans.XmlHelper;

/**
 * A generic interface to be implemented by SweCommon encoder/decoder classes. A nested {@link Factory}
 * provides creation of encoder/decoder implementations. However, these may not fit to the developer's 
 * current context, so these shall only be considered as best guess default encoder/decoder.
 * 
 * @param <T> the 'internal' type (which shall be encoded to <E>)
 * @param <E> the 'external' type (which shall be decoded to <T>)
 */
public interface SweEncoderDecoder<T, E> {

    public static class Factory {

        public static TextEncoderDecoder createTextEncoderDecoder(AbstractEncodingType encoding) {
//            XmlHelper.qualifyWith(TextEncodingDocument.type, encoding); TODO check this qualifying principle and add tests for it
            TextEncodingDocument textEncoding = XmlHelper.substituteWithTextEncoding(encoding);
            return new TextEncoderDecoder(textEncoding.getTextEncoding());
        }

        public static XmlEncoderDecoder createXmlEncoderDecoder(AbstractEncodingType encoding) {
            XMLEncodingDocument xmlEncoding = XmlHelper.substituteWithXmlEncoding(encoding);
            return new XmlEncoderDecoder(xmlEncoding.getXMLEncoding());
        }

        public static BinaryEncoderDecoder createBinaryEncoderDecoder(AbstractEncodingType encoding) {
            BinaryEncodingDocument binaryEncoding = XmlHelper.substituteWithBinaryEncoding(encoding);
            return new BinaryEncoderDecoder(binaryEncoding.getBinaryEncoding());
        }

    }

    /**
     * @param toEncode
     *        the data representation to be encoded.
     * @return the encoded data.
     * @throws EncodingException
     *         if encoding fails.
     */
    public E encode(T toEncode) throws EncodingException;

    /**
     * @param toDecode
     *        a generic type representation to be decoded.
     * @return the decoded data.
     * @throws EncodingException
     *         if decoding fails.
     */
    public T decode(E toDecode) throws EncodingException;

    /**
     * @param toDecode
     *        an xml data representation to be decoded.
     * @return the decoded data.
     * @throws EncodingException
     *         if decoding fails.
     */
    public T decode(XmlObject toDecode) throws EncodingException;

}
