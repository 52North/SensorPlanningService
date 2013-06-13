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

package org.n52.sps.service.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.ows.x11.ServiceIdentificationDocument;
import net.opengis.ows.x11.ServiceProviderDocument;
import net.opengis.sps.x20.CapabilitiesDocument;
import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.sps.x20.CapabilitiesType.Contents;
import net.opengis.sps.x20.SPSContentsDocument;
import net.opengis.sps.x20.SPSContentsType;
import net.opengis.sps.x20.SensorOfferingType;
import net.opengis.swes.x20.AbstractContentsType.Offering;
import net.opengis.swes.x20.AbstractContentsType.RelatedFeature;
import net.opengis.swes.x20.FeatureRelationshipType;

import org.apache.xmlbeans.XmlException;
import org.n52.ows.exception.NoApplicableCodeException;
import org.n52.ows.exception.OwsException;
import org.n52.oxf.xmlbeans.tools.XMLBeansTools;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.service.SensorPlanningService;
import org.n52.sps.util.xmlbeans.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilitiesHelper {

    private static final String SERVICE_CONTENTS = "/capabilities/service-contents.xml";
    private static final String SERVICE_PROVIDER = "/capabilities/service-provider.xml";
    private static final String SERVICE_IDENTIFICATION = "/capabilities/service-identification.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(CapabilitiesHelper.class);

    // XXX change to non-static?
    public static CapabilitiesDocument createSpsCapabilities(SensorInstanceProvider sensorInstanceProvider) throws OwsException {
        try {
            CapabilitiesDocument capabilitiesDocument = CapabilitiesDocument.Factory.newInstance();
            CapabilitiesType capabilities = capabilitiesDocument.addNewCapabilities();
            capabilities.setVersion(SensorPlanningService.SPS_VERSION);
            insertServiceIdentification(capabilities);
            insertServiceProvider(capabilities);
            insertContents(capabilities, sensorInstanceProvider);
            return capabilitiesDocument;
        } catch (XmlException e) {
            LOGGER.error("Could not parse configuration file.", e);
            throw new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
        }
    }

    private static File getAbsoluteFilePath(URL path) {
        // TODO % encoding can be decoded in a better way.
        return new File(path.getFile().replace("%20", " "));
    }
    
    private static void insertServiceIdentification(CapabilitiesType capabilities) throws XmlException, NoApplicableCodeException {
        try {
            URL path = CapabilitiesHelper.class.getResource(SERVICE_IDENTIFICATION);
            if (path != null) {
                InputStream is = new FileInputStream(getAbsoluteFilePath(path));
                ServiceIdentificationDocument doc = ServiceIdentificationDocument.Factory.parse(is);
                capabilities.setServiceIdentification(doc.getServiceIdentification());
            } else {
                LOGGER.error("Could find file '{}'.", SERVICE_IDENTIFICATION);
            }
        } catch (IOException e) {
            LOGGER.error("Could not read file '{}'.", SERVICE_IDENTIFICATION, e);
            throw new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
        }
    }

    private static void insertServiceProvider(CapabilitiesType capabilities) throws XmlException, NoApplicableCodeException {
        try {
            URL path = CapabilitiesHelper.class.getResource(SERVICE_PROVIDER);
            if (path != null) {
                InputStream is = new FileInputStream(getAbsoluteFilePath(path));
                ServiceProviderDocument doc = ServiceProviderDocument.Factory.parse(is);
                capabilities.setServiceProvider(doc.getServiceProvider());
            } else {
                LOGGER.error("Could find file '{}'.", SERVICE_PROVIDER);
            }
        } catch (IOException e) {
            LOGGER.error("Could not read file '{}'.", SERVICE_PROVIDER, e);
            throw new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
        }
    }

    private static void insertContents(CapabilitiesType capabilities, SensorInstanceProvider sensorInstanceProvider) throws XmlException, NoApplicableCodeException {
        try {
            URL path = CapabilitiesHelper.class.getResource(SERVICE_CONTENTS);
            if (path != null) {
                InputStream is = new FileInputStream(getAbsoluteFilePath(path));
                SPSContentsDocument contentsDocument = SPSContentsDocument.Factory.parse(is);
                SPSContentsType contents = contentsDocument.getSPSContents();
                if (sensorInstanceProvider != null) {
                    for (SensorPlugin sensor : sensorInstanceProvider.getSensors()) {
                        List<SensorOfferingType> sensorOfferings = sensor.getSensorConfiguration().getSensorOfferings();
                        for (SensorOfferingType sensorOfferingType : sensorOfferings) {
                            SensorOfferingType sensorOffering = (SensorOfferingType) sensorOfferingType.copy();
                            removeAllInheritedInformationFromSensorOffering(contents, sensorOffering);
                            Offering offering = contents.addNewOffering();
                            offering.setAbstractOffering(sensorOffering);
                            QName qname = new QName("http://www.opengis.net/sps/2.0", "SensorOffering");
                            XMLBeansTools.qualifySubstitutionGroup(offering.getAbstractOffering(), qname);
                        }
                    }
                    Contents capabilitiesContents = addSpsContentsToCapabilities(capabilities, contents);
                    XmlHelper.qualifyWith(SPSContentsDocument.type, capabilitiesContents.getSPSContents());
                }
            } else {
                LOGGER.error("Could find file '{}'.", SERVICE_CONTENTS);
            }
        } catch (IOException e) {
            LOGGER.error("Could not read file '{}'.", SERVICE_CONTENTS, e);
            throw new NoApplicableCodeException(OwsException.INTERNAL_SERVER_ERROR);
        }
    }

    private static Contents addSpsContentsToCapabilities(CapabilitiesType capabilities, SPSContentsType contents) {
        Contents capabilitiesContents = capabilities.addNewContents();
        capabilitiesContents.setSPSContents(contents);
        return capabilitiesContents;
    }

    private static void removeAllInheritedInformationFromSensorOffering(SPSContentsType spsContents, SensorOfferingType sensorOffering) {
        removeInheritedObservedProperties(spsContents, sensorOffering);
        removeInheritedProcedureDescriptionFormats(spsContents, sensorOffering);
        removeInheritedRelatedFeatures(spsContents, sensorOffering);
    }

    private static void removeInheritedObservedProperties(SPSContentsType spsContents, SensorOfferingType sensorOffering) {
        if (isInheritObservedPropertiesFromSpsContents(spsContents)) {
            String[] observablePropertyArray = sensorOffering.getObservablePropertyArray();
            for (int i = 0 ; i < observablePropertyArray.length ; i++) {
                String observedProperty = observablePropertyArray[i];
                if (isObservedPropertyInherited(observedProperty, spsContents)) {
                    sensorOffering.removeObservableProperty(i);
                }
            }
        }
    }

    private static boolean isInheritObservedPropertiesFromSpsContents(SPSContentsType spsContents) {
        return spsContents.getObservablePropertyArray().length != 0;
    }
    
    private static boolean isObservedPropertyInherited(String observedProperty, SPSContentsType spsContents) {
        for (String inheritedObservedProperty : spsContents.getObservablePropertyArray()) {
            if (inheritedObservedProperty.equalsIgnoreCase(observedProperty)) {
                return true;
            }
        }
        return false;
    }
    
    private static void removeInheritedProcedureDescriptionFormats(SPSContentsType spsContents, SensorOfferingType sensorOffering) {
        if (isInheritProcedureDescriptionFormats(spsContents)) {
            String[] procedureDescriptionFormats = sensorOffering.getProcedureDescriptionFormatArray();
            for (int i = 0 ; i < procedureDescriptionFormats.length ; i++) {
                String procedureDescriptionFormat = procedureDescriptionFormats[i];
                if (isProcedureDescriptionFormatInherited(procedureDescriptionFormat, spsContents)) {
                    sensorOffering.removeProcedureDescriptionFormat(i);
                }
            }
        }
    }

    private static boolean isInheritProcedureDescriptionFormats(SPSContentsType spsContents) {
        return spsContents.getProcedureDescriptionFormatArray().length != 0;
    }

    private static boolean isProcedureDescriptionFormatInherited(String procedureDescriptionFormat, SPSContentsType spsContents) {
        for (String inheritedProcedureDescriptionFormat : spsContents.getProcedureDescriptionFormatArray()) {
            if (inheritedProcedureDescriptionFormat.equalsIgnoreCase(procedureDescriptionFormat)) {
                return true;
            }
        }
        return false;
    }

    private static void removeInheritedRelatedFeatures(SPSContentsType spsContents, SensorOfferingType sensorOffering) {
        if (isInheritRelatedFeatures(spsContents)) {
            for (int i = 0 ; i < sensorOffering.getRelatedFeatureArray().length ; i++) {
                FeatureRelationshipType relationship = sensorOffering.getRelatedFeatureArray()[i].getFeatureRelationship();
                if (isRelationShipInherited(relationship, spsContents)) {
                    sensorOffering.removeProcedureDescriptionFormat(i);
                }
            }
        }
    }

    private static boolean isInheritRelatedFeatures(SPSContentsType spsContents) {
        return spsContents.getRelatedFeatureArray().length != 0;
    }

    private static boolean isRelationShipInherited(FeatureRelationshipType relationship, SPSContentsType spsContents) {
        for (RelatedFeature inheritedRelatedFeature : spsContents.getRelatedFeatureArray()) {
            String hrefFromInheritedFeature = inheritedRelatedFeature.getFeatureRelationship().getTarget().getHref();
            String hrefFromOfferingFeature = relationship.getTarget().getHref();
            if (hrefFromInheritedFeature != null && hrefFromOfferingFeature != null && hrefFromInheritedFeature.equalsIgnoreCase(hrefFromOfferingFeature)) {
                return true;
            }
        }
        return false;
    }

}
