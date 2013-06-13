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

package org.n52.sps.control.kvp;

import static org.n52.ows.service.parameter.KeyValuePairParameter.ACCEPTFORMATS;
import static org.n52.ows.service.parameter.KeyValuePairParameter.ACCEPTVERSIONS;
import static org.n52.ows.service.parameter.KeyValuePairParameter.SECTIONS;
import static org.n52.ows.service.parameter.KeyValuePairParameter.SERVICE;
import net.opengis.ows.x11.AcceptFormatsType;
import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.ows.x11.SectionsType;
import net.opengis.sps.x20.GetCapabilitiesDocument;
import net.opengis.sps.x20.GetCapabilitiesType;

import org.n52.ows.exception.OwsException;
import org.n52.oxf.swes.exception.InvalidRequestException;

public class GetCapabilitiesConverter implements XmlObjectConverter<GetCapabilitiesDocument> {
    
    private KeyValuePairsWrapper kvpParser;

    public GetCapabilitiesConverter(KeyValuePairsWrapper kvpParser) {
        this.kvpParser = kvpParser;
    }

    public GetCapabilitiesDocument convert() throws OwsException {
        GetCapabilitiesDocument getCapabilities = GetCapabilitiesDocument.Factory.newInstance();
        GetCapabilitiesType getCapabilitiesType = getCapabilities.addNewGetCapabilities2();
        convertKeyValuePairsToXmlContent(getCapabilitiesType);
        return getCapabilities;
    }

    private void convertKeyValuePairsToXmlContent(GetCapabilitiesType getCapabilitiesType) throws InvalidRequestException {
        handleUpdateSequence(getCapabilitiesType);
        handleService(getCapabilitiesType);
        handleAcceptVersions(getCapabilitiesType);
        handleSections(getCapabilitiesType);
        handleAcceptFormats(getCapabilitiesType);
        handleExtensions(getCapabilitiesType);
        validate();
    }

    /* 
     * server does not support update sequence .. the latest version will be returned
     */
    private void handleUpdateSequence(GetCapabilitiesType getCapabilitiesType) {
        //String updateSequence = kvpParser.getTrimmedParameterValue(UPDATESEQUENCE.name());
        //getCapabilitiesType.setUpdateSequence(updateSequence);
    }
    
    private void handleService(GetCapabilitiesType getCapabilitiesType) {
        String service = kvpParser.getTrimmedParameterValue(SERVICE.name());
        if (service == null) {
            service = "SPS"; // set default
        }
        getCapabilitiesType.setService(service);
    }
    
    private void handleAcceptVersions(GetCapabilitiesType getCapabilitiesType) {
        String acceptVersions = kvpParser.getTrimmedParameterValue(ACCEPTVERSIONS.name());
        if (acceptVersions != null) {
            AcceptVersionsType acceptVersionsType = getCapabilitiesType.addNewAcceptVersions();
            for (String version : acceptVersions.split(",")) {
                acceptVersionsType.addVersion(version);
            }
        }
    }
    
    private void handleSections(GetCapabilitiesType getCapabilitiesType) {
        String sections = kvpParser.getTrimmedParameterValue(SECTIONS.name());
        if (sections != null) {
            SectionsType sectionsType = getCapabilitiesType.addNewSections();
            for (String section : sections.split(",")) {
                sectionsType.addSection(section);
            }
        }
    }

    private void handleAcceptFormats(GetCapabilitiesType getCapabilitiesType) {
        String acceptFormats = kvpParser.getTrimmedParameterValue(ACCEPTFORMATS.name());
        if (acceptFormats != null) {
            AcceptFormatsType acceptFormatsType = getCapabilitiesType.addNewAcceptFormats();
            for (String acceptFormat : acceptFormats.split(",")) {
                acceptFormatsType.addOutputFormat(acceptFormat);
            }
        }
    }

    /* 
     * server does not support extensions .. 
     */
    private void handleExtensions(GetCapabilitiesType getCapabilitiesType) {
        // do nothing
    }
    
    public void validate() throws InvalidRequestException {
        
    }

}
