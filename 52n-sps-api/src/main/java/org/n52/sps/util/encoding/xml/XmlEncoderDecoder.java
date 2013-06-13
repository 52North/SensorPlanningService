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
package org.n52.sps.util.encoding.xml;

import net.opengis.swe.x20.XMLEncodingType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sps.util.encoding.EncodingException;
import org.n52.sps.util.encoding.SweEncoderDecoder;

// TODO implement XmlEncoderDecoder
public class XmlEncoderDecoder implements SweEncoderDecoder<Void, Void> {

    public static final String XML_ENCODING = "http://www.opengis.net/swe/2.0/XMLEncoding";

    public XmlEncoderDecoder(XMLEncodingType xmlEncoding) {
        // TODO Auto-generated constructor stub
        
    }

    public Void encode(Void toEncode) throws EncodingException {
        // TODO Auto-generated method stub
        return null;
        
    }

    public Void decode(Void toDecode) throws EncodingException {
        // TODO Auto-generated method stub
        return null;
        
    }

    public Void decode(XmlObject toDecode) throws EncodingException {
        // TODO Auto-generated method stub
        return null;
        
    }

}
