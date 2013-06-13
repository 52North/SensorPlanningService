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

import net.opengis.sps.x20.ParameterDataDocument;
import net.opengis.sps.x20.ParameterDataType;
import net.opengis.sps.x20.ParameterDataType.Encoding;
import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.TextEncodingDocument;
import net.opengis.swe.x20.TextEncodingType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sps.util.encoding.EncodingException;
import org.n52.sps.util.encoding.SweEncoderDecoder;
import org.n52.sps.util.xmlbeans.XmlHelper;

/**
 * Decodes and encodes text strings according to SweCommon 2.0 specification (08-094r1).
 * 
 * {@link TextEncoderDecoder} can be used in static context to de-/encode on-the-fly, but also can be
 * instantiated with a {@link ParameterDataType} as template so the instance can be reused for  de- and 
 * encoding text values.
 */
// ceck for conformance class: http://www.opengis.net/spec/SWE/2.0/req/text-encoding-rules
public class TextEncoderDecoder implements SweEncoderDecoder<String[][], String> {

    public final static String TEXT_ENCODING = "http://www.opengis.net/swe/2.0/TextEncoding";

    private TextEncodingType encoding;

    public static ParameterDataDocument encode(TextEncodingType textEncoding, String[][] toEncode) throws EncodingException {
        ParameterDataDocument parameterDataDoc = ParameterDataDocument.Factory.newInstance();
        ParameterDataType parameterData = parameterDataDoc.addNewParameterData();
        XmlObject values = parameterData.addNewValues();
        Encoding newEncoding = parameterData.addNewEncoding();
        newEncoding.setAbstractEncoding(textEncoding);
        XmlHelper.setTextContent(values, encodeValues(textEncoding, toEncode));
        return parameterDataDoc;
    }

    private static String encodeValues(TextEncodingType textEncoding, String[][] toEncode) throws EncodingException {
        TextEncoderDecoder decoderEncoder = new TextEncoderDecoder(textEncoding);
        return decoderEncoder.encode(toEncode);
    }

    public static String[][] decode(ParameterDataType parameterData) throws EncodingException {
        XmlObject values = parameterData.getValues();
        String toDecode = XmlHelper.getTextContentFromAnyNode(values);
        TextEncoderDecoder decoderEncoder = createDecoderEncoder(parameterData);
        return decoderEncoder.decode(toDecode);
    }

    private static TextEncoderDecoder createDecoderEncoder(ParameterDataType parameterData) {
        AbstractEncodingType abstractEncoding = parameterData.getEncoding().getAbstractEncoding();
        TextEncodingDocument textEncoding = XmlHelper.substituteWithTextEncoding(abstractEncoding);
        return new TextEncoderDecoder(textEncoding.getTextEncoding());
    }

    public TextEncoderDecoder(TextEncodingType encoding) {
        this.encoding = encoding;
    }

    public String encode(String[][] toEncode) throws EncodingException {
        try {
            String tokenSeparator = encoding.getTokenSeparator();
            String blockSeparator = encoding.getBlockSeparator();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < toEncode.length; i++) {
                String[] tokens = toEncode[i];
                for (String token : tokens) {
                    sb.append(token).append(tokenSeparator);
                }
                int tokensEndIdx = sb.lastIndexOf(tokenSeparator);
                sb.replace(tokensEndIdx, sb.length(), blockSeparator);
            }
            int blocksEndIdx = sb.lastIndexOf(blockSeparator);
            sb.delete(blocksEndIdx, sb.length());
            return sb.toString();
        }
        catch (Exception e) {
            throw new EncodingException("Could not decode String.", e);
        }
    }

    public String[][] decode(String toDecode) throws EncodingException {
        try {
            boolean collapse = shallCollapse();
            String[] blocks = getBlocks(toDecode);
            String[][] blocksAndTokens = new String[blocks.length][];
            for (int i = 0; i < blocks.length; i++) {
                String[] tokens = getTokens(blocks[i]);
                blocksAndTokens[i] = collapse ? collapseTokens(tokens) : tokens;
            }
            return blocksAndTokens;
        }
        catch (Exception e) {
            throw new EncodingException("Could not encode String.", e);
        }
    }

    public String[][] decode(XmlObject toDecode) throws EncodingException {
        return decode(XmlHelper.getTextContentFromAnyNode(toDecode));
    }

    /**
     * @return <code>true</code> if white spaces shall be collapsed explicitly (or if not set),
     *         <code>false</code> otherwise.
     */
    private boolean shallCollapse() {
        return !encoding.isSetCollapseWhiteSpaces() ? true : encoding.getCollapseWhiteSpaces();
    }

    private String[] collapseTokens(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        return tokens;
    }

    private String[] getBlocks(String encodedString) {
        return encodedString.split(encoding.getBlockSeparator());
    }

    private String[] getTokens(String encodedString) {
        return encodedString.split(encoding.getTokenSeparator());
    }

}
