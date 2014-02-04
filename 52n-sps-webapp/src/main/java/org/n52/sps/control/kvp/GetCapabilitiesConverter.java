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
