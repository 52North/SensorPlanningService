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
