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

package org.n52.sps.sensor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import net.opengis.sps.x20.SensorOfferingType;
import net.opengis.swe.x20.AbstractDataComponentType;

public class SensorConfiguration {

    private Long id; // database id

    private List<SensorOfferingType> sensorOfferings = new ArrayList<SensorOfferingType>();

    private List<SensorDescription> sensorDescriptions = new ArrayList<SensorDescription>();

    private String procedure;

    private String sensorPluginType;
    
    private XmlObject sensorSetup;

    private AbstractDataComponentType taskingParametersTemplate;

    private ResultAccessDataServiceReference resultAccessDataServiceTemplate;

    public SensorConfiguration() {
        // db serialization
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }
    
    public List<String> getSensorOfferingsAsString() {
        List<String> offerings = new ArrayList<String>();
        for (SensorOfferingType offeringType : sensorOfferings) {
            offerings.add(offeringType.xmlText());
        }
        return offerings;
    }

    public List<SensorOfferingType> getSensorOfferings() {
        return sensorOfferings;
    }
    
    public void setSensorOfferingsAsString(List<String> sensorOfferings) throws XmlException {
        this.sensorOfferings.clear();
        for (String sensorOffering : sensorOfferings) {
            this.sensorOfferings.add(SensorOfferingType.Factory.parse(sensorOffering));
        }
    }

    public void setSensorOfferings(List<SensorOfferingType> sensorOfferings) {
        this.sensorOfferings = sensorOfferings;
    }

    public boolean addSensorOffering(SensorOfferingType sensorOfferingType) {
        return sensorOfferings.add(sensorOfferingType);
    }

    public boolean removeSensorOffering(String offeringIdentifier) {
        for (SensorOfferingType sensorOffering : sensorOfferings) {
            if (sensorOffering.getIdentifier().equals(offeringIdentifier)) {
                return sensorOfferings.remove(sensorOffering);
            }
        }
        return false;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getSensorPluginType() {
        return sensorPluginType;
    }

    public void setSensorPluginType(String sensorPluginType) {
        this.sensorPluginType = sensorPluginType;
    }

    public XmlObject getSensorSetup() {
        return sensorSetup;
    }
    
    public void setSensorSetup(XmlObject sensorSetup) {
        this.sensorSetup = sensorSetup;
    }
    
    public String getSensorSetupAsString() {
        return sensorSetup != null ? sensorSetup.xmlText(): null;
    }
    
    public void setSensorSetupAsString(String sensorSetup) throws XmlException {
        if (sensorSetup != null) {
            this.sensorSetup = XmlObject.Factory.parse(sensorSetup);
        }
    }
    
    public List<SensorDescription> getSensorDescriptions() {
        return sensorDescriptions;
    }

    public void setSensorDescriptions(List<SensorDescription> sensorDescriptions) {
        this.sensorDescriptions = sensorDescriptions;
    }

    public List<String> getSensorDescriptionUrisFor(String procedureDescriptionFormat) {
        List<String> descriptionUris = new ArrayList<String>();
        for (SensorDescription sensorDescription : sensorDescriptions) {
            if (sensorDescription.getProcedureDescriptionFormat().equals(procedureDescriptionFormat)) {
                descriptionUris.add(sensorDescription.getDownloadLink());
            }
        }
        return descriptionUris;
    }

    public boolean addSensorDescription(SensorDescription sensorDescription) {
        return sensorDescriptions.add(sensorDescription);
    }

    public boolean removeSensorDescription(SensorDescription sensorDescription) {
        return sensorDescriptions.remove(sensorDescription);
    }

    /**
     * @param procedureDescriptionFormat
     *        the format of SensorDescription
     * @return <code>true</code> if the given format is available, <code>false</code> otherwise.
     */
    public boolean supportsProcedureDescriptionFormat(String procedureDescriptionFormat) {
        for (SensorDescription sensorDescription : sensorDescriptions) {
            if (sensorDescription.getProcedureDescriptionFormat().equals(procedureDescriptionFormat)) {
                return true;
            }
        }
        return false;
    }

    public void setTaskingParametersTemplate(AbstractDataComponentType taskingParameters) {
        this.taskingParametersTemplate = taskingParameters;
    }

    /**
     * @return deep copy of configured tasking parameters to be used as template. The caller has to qualify
     *         the returned {@link AbstractDataComponentType} to a corresponding Substitution type.
     */
    public AbstractDataComponentType getTaskingParametersTemplate() {
        return (AbstractDataComponentType) taskingParametersTemplate.copy();
    }

    public String getTaskingParameters() {
        return taskingParametersTemplate.xmlText();
    }

    public void setTaskingParameters(String taskingParameters) throws XmlException {
        this.taskingParametersTemplate = AbstractDataComponentType.Factory.parse(taskingParameters);
    }

    public ResultAccessDataServiceReference getResultAccessDataService() {
        return resultAccessDataServiceTemplate;
    }

    public void setResultAccessDataService(ResultAccessDataServiceReference resultAccessDataService) {
        this.resultAccessDataServiceTemplate = resultAccessDataService;
    }

}
