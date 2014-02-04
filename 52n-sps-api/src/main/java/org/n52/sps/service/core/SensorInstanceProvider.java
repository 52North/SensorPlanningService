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
package org.n52.sps.service.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.ows.x11.MetadataType;
import net.opengis.ows.x11.ReferenceType;
import net.opengis.sps.x20.SPSMetadataDocument;
import net.opengis.sps.x20.SensorOfferingType;
import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.DataChoiceType.Item;

import org.apache.xmlbeans.XmlException;
import org.n52.sps.sensor.SensorInstanceFactory;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorDescription;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.IllegalPluginTypeRelationException;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.service.PluginTypeConfigurationException;
import org.n52.sps.service.admin.InsertSensorOfferingEvent;
import org.n52.sps.service.admin.InsertSensorOfferingListener;
import org.n52.sps.service.admin.InvalidSensorInformationException;
import org.n52.sps.service.admin.MissingSensorInformationException;
import org.n52.sps.store.SensorConfigurationRepository;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.util.convert.InsertSensorOfferingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering.SensorDescriptionData;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering.SensorTaskingParametersSet;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering.SensorTaskingParametersSet.MultipleParameterSets;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering.SensorTaskingParametersSet.SingleParameterSet;
import org.x52North.schemas.sps.v2.SensorInstanceDataDocument.SensorInstanceData;

// TODO find a better name for this class. It initiates available sensor instances,
// but also provides access to tasks and sensor configurations via repository interface.
// Besides that is also is a handler for incoming InsertSensorOfferings
// => Think of how to separate all these concerns
public class SensorInstanceProvider implements InsertSensorOfferingListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorInstanceProvider.class);

    protected SensorConfigurationRepository sensorConfigurationRepository;

    protected SensorTaskRepository sensorTaskRepository;

    private ServiceLoader<SensorInstanceFactory> sensorPluginLoader;

    private List<SensorPlugin> sensorInstances = new ArrayList<SensorPlugin>();

    public void init() throws InternalServiceException {
        sensorPluginLoader = ServiceLoader.load(SensorInstanceFactory.class);
        if (isMissingRepository()) {
            throw new IllegalStateException("SensorInstanceProvider misses a repository.");
        }
        Iterable<SensorConfiguration> configs = sensorConfigurationRepository.getSensorConfigurations();
        for (SensorConfiguration sensorConfiguration : configs) {
            sensorInstances.add(initSensorInstance(sensorConfiguration));
        }
        LOGGER.info("Initialised {} with #{} sensor configuration(s).", getClass().getSimpleName(), sensorInstances.size());
    }

    private boolean isMissingRepository() {
        return sensorConfigurationRepository == null || sensorTaskRepository == null;
    }
    
    protected void addSensor(SensorPlugin sensorInstance) {
        // protected access to provide a seam for testing only
        sensorInstances.add(sensorInstance);
    }

    public Iterable<SensorPlugin> getSensors() {
        return sensorInstances;
    }
    
    public boolean containsTaskWith(String taskId) {
        return getTaskForTaskId(taskId) != null;
    }

    public SensorTask getTaskForTaskId(String taskId) {
        return getSensorTaskRepository().getTask(taskId);
    }
    
    public boolean containsSensorWith(String procedure) {
        return getSensorForProcedure(procedure) != null;
    }

    public SensorPlugin getSensorForProcedure(String procedure) {
        for (SensorPlugin sensorInstance : sensorInstances) {
            if (sensorInstance.getProcedure().equals(procedure)) {
                return sensorInstance;
            }
        }
        return null;
    }

    public SensorPlugin getSensorForTaskId(String taskId) {
        SensorTask sensorTask = sensorTaskRepository.getTask(taskId);
        return getSensorForProcedure(sensorTask.getProcedure());
    }

    public SensorConfigurationRepository getSensorConfigurationRepository() {
        return sensorConfigurationRepository;
    }

    public void setSensorConfigurationRepository(SensorConfigurationRepository sensorConfigurationRepository) throws InternalServiceException {
        this.sensorConfigurationRepository = sensorConfigurationRepository;
    }
    
    public SensorTaskRepository getSensorTaskRepository() {
        return sensorTaskRepository;
    }

    public void setSensorTaskRepository(SensorTaskRepository sensorTaskRepository) {
        this.sensorTaskRepository = sensorTaskRepository;
    }

    public void handleInsertSensorOffering(InsertSensorOfferingEvent insertSensorOfferingEvent) throws InternalServiceException {
        InsertSensorOfferingDocument insertSensorOfferingDocument = insertSensorOfferingEvent.getInsertSensorOffering();
        InsertSensorOffering insertSensorOffering = insertSensorOfferingDocument.getInsertSensorOffering();
        SensorOfferingType sensorOffering = createFromInsertSensorOffering(insertSensorOffering);
        SensorConfiguration sensorConfiguration = createSensorConfiguration(insertSensorOffering, sensorOffering);
        if (isSensorExisting(insertSensorOffering.getSensorInstanceData())) {
            SensorPlugin sensorPlugin = getSensorForProcedure(sensorOffering.getProcedure());
            sensorPlugin.mergeSensorConfigurations(sensorConfiguration);
            sensorConfigurationRepository.saveOrUpdateSensorConfiguration(sensorConfiguration);
        }
        else {
            checkMandatoryContentForNewSensorOffering(insertSensorOffering);
            sensorInstances.add(initSensorInstance(sensorConfiguration));
            sensorConfigurationRepository.storeNewSensorConfiguration(sensorConfiguration);
        }
    }

    private SensorPlugin initSensorInstance(SensorConfiguration sensorConfiguration) throws InternalServiceException {
        String pluginType = sensorConfiguration.getSensorPluginType();
        SensorInstanceFactory factory = getSensorFactory(pluginType);
        SensorTaskService sensorTaskService = new SensorTaskService(sensorTaskRepository);
        return factory.createSensorPlugin(sensorTaskService, sensorConfiguration);
    }

    private void checkMandatoryContentForNewSensorOffering(InsertSensorOffering insertSensorOffering) throws MissingSensorInformationException {
        // XXX validating InsertSensorOffering may make this check unneccessary
        SensorDescriptionData[] sensorDescriptions = insertSensorOffering.getSensorDescriptionDataArray();
        if (sensorDescriptions == null || sensorDescriptions.length == 0) {
            LOGGER.warn("No sensor description found.");
            throw new MissingSensorInformationException("sensorDescription");
        }
        if (insertSensorOffering.getSensorTaskingParametersSet() == null) {
            LOGGER.warn("No sensor tasking parameters found.");
            throw new MissingSensorInformationException("sensorTaskingParameters");
        }
    }

    protected SensorInstanceFactory getSensorFactory(String pluginType) throws PluginTypeConfigurationException {
        Iterator<SensorInstanceFactory> iterator = sensorPluginLoader.iterator();
        while (iterator.hasNext()) {
            SensorInstanceFactory factory = iterator.next();
            if (factory.getPluginType().equals(pluginType)) {
                return factory;
            }
        }
        LOGGER.error("Unknown sensor plugin type or no factory has been configured for '{}'.", pluginType);
        throw new PluginTypeConfigurationException(pluginType);
    }

    private SensorOfferingType createFromInsertSensorOffering(InsertSensorOffering insertSensorOffering) {
        InsertSensorOfferingConverter converter = new InsertSensorOfferingConverter();
        return converter.convertToSensorOfferingType(insertSensorOffering);
    }

    private SensorConfiguration createSensorConfiguration(InsertSensorOffering insertSensorOffering, SensorOfferingType sensorOffering) throws InternalServiceException {
        SensorConfiguration sensorConfiguration = new SensorConfiguration();
        SensorInstanceData sensorInstanceData = insertSensorOffering.getSensorInstanceData();
        sensorConfiguration.setSensorPluginType(sensorInstanceData.getSensorPluginType());
        sensorConfiguration.setProcedure(sensorInstanceData.getProcedure().getStringValue());
        sensorConfiguration.setSensorDescriptions(createSensorDescriptions(insertSensorOffering.getSensorDescriptionDataArray()));
        sensorConfiguration.addSensorOffering(sensorOffering);
        sensorConfiguration.setTaskingParametersTemplate(createTaskingParameterTemplate(insertSensorOffering.getSensorTaskingParametersSet()));
        sensorConfiguration.setResultAccessDataService(createResultAccessReference(sensorInstanceData));
        if (insertSensorOffering.isSetSensorSetup()) {
            sensorConfiguration.setSensorSetup(insertSensorOffering.getSensorSetup());
        }
        return sensorConfiguration;
    }


    /**
     * Checks if the sensor instance data passed can be associated with an existing sensor instance. This is
     * done by checking the procedure and the corresponding plugin type. If the plugin type was omitted, the
     * plugin type of the sensor instance found (if so) will be assumed. An exception will be thrown if there
     * seems to be a request for an existing sensor instance with a procedure not matching its plugin type.
     * 
     * @param sensorInstanceData
     *        the sensor instance data of the sensor to check.
     * @return <code>true</code> if a sensor instance with matching procedure and plugin type does already
     *         exist, and <code>false</code> if procedure is unknown.
     * @throws IllegalPluginTypeRelationException
     *         if a sensor instance with matching procedure but different plugin type does exit already.
     */
    private boolean isSensorExisting(SensorInstanceData sensorInstanceData) throws IllegalPluginTypeRelationException {
        String procedure = sensorInstanceData.getProcedure().getStringValue();
        String sensorPluginType = sensorInstanceData.getSensorPluginType();
        if (sensorConfigurationRepository.containsSensorConfiguration(procedure)) {
            SensorPlugin sensorInstance = getSensorForProcedure(procedure);
            String existingType = sensorInstance.getSensorPluginType();
            if (sensorPluginType == null || existingType.equals(sensorPluginType)) {
                return true;
            }
            else {
                LOGGER.error("Procedure '{}' already exists (for type '{}')", procedure, existingType);
                throw new IllegalPluginTypeRelationException("Procedure already in use.");
            }
        }
        return false;
    }

    private List<SensorDescription> createSensorDescriptions(SensorDescriptionData[] sensorDescriptionDataArray) {
        List<SensorDescription> sensorDescriptions = new ArrayList<SensorDescription>();
        for (SensorDescriptionData sensorDescriptionData : sensorDescriptionDataArray) {
            String procedureDescriptionFormat = sensorDescriptionData.getProcedureDescriptionFormat();
            String downloadLink = sensorDescriptionData.getDownloadLink();
            sensorDescriptions.add(new SensorDescription(procedureDescriptionFormat, downloadLink));
        }
        return sensorDescriptions;
    }
    

    private AbstractDataComponentType createTaskingParameterTemplate(SensorTaskingParametersSet sensorTaskingParametersSet) throws InvalidSensorInformationException {
        AbstractDataComponentType parameterTemplate = null;
        if (sensorTaskingParametersSet.isSetSingleParameterSet()) {
            SingleParameterSet singleParameterSet = sensorTaskingParametersSet.getSingleParameterSet();
            AbstractDataComponentType parameterSet = singleParameterSet.getAbstractDataComponent();
            if (isValidTaskingName(parameterSet.getIdentifier())) {
                parameterTemplate = parameterSet;
            } else {
                throwInvalidSensorInformation(parameterSet.getIdentifier());
            }
        } else {
            MultipleParameterSets parameterSets = sensorTaskingParametersSet.getMultipleParameterSets();
            List<String> validIdentifiers = new ArrayList<String>();
            for (Item parameterSet : parameterSets.getDataChoice().getItemArray()) {
                if (!isValidTaskingName(parameterSet.getName(), validIdentifiers)) {
                    throwInvalidSensorInformation(parameterSet.getName());
                }
            }
            parameterTemplate = parameterSets.getDataChoice();
        }
        return parameterTemplate;
    }

    private boolean isValidTaskingName(String name, List<String> validIdentifiers) {
        boolean alreadyContainsIdentifier = validIdentifiers.contains(name);
        if (alreadyContainsIdentifier) {
            return false;
        } else {
            if (isValidTaskingName(name)) {
                return validIdentifiers.add(name);
            } else {
                return false;
            }
        }
    }

    private boolean isValidTaskingName(String name) {
        return name != null && !name.isEmpty();
    }

    private void throwInvalidSensorInformation(String identifier) throws InvalidSensorInformationException {
        InvalidSensorInformationException e = new InvalidSensorInformationException("taskingParameters");
        String msg = String.format("Invalid identifier for tasking parameter: '%s'", identifier);
        e.addDetailedMessage(msg);
        LOGGER.warn(msg);
        throw e;
    }

    protected ResultAccessDataServiceReference createResultAccessReference(SensorInstanceData sensorInstanceData) throws MissingSensorInformationException {
        ResultAccessDataServiceReference resultAccessDataServiceReference = new ResultAccessDataServiceReference();
        ReferenceType reference = sensorInstanceData.getReference();
        if (reference == null) {
            throw new MissingSensorInformationException("reference");
        }
        resultAccessDataServiceReference.setRole(reference.getRole());
        resultAccessDataServiceReference.setTitle(reference.getTitle());
        resultAccessDataServiceReference.setFormat(reference.getFormat());
        resultAccessDataServiceReference.setReference(reference.getHref());
        resultAccessDataServiceReference.setIdentifier(reference.getIdentifier().getStringValue());
        resultAccessDataServiceReference.setDataAccessTypes(createDataAccessTypes(reference.getMetadataArray()));
        handleAbstractContent(resultAccessDataServiceReference, reference.getAbstractArray());
        return resultAccessDataServiceReference;
    }

    private List<String> createDataAccessTypes(MetadataType[] metadataArray) throws MissingSensorInformationException {
        try {
            List<String> metadatas = new ArrayList<String>();
            for (MetadataType metadataType : metadataArray) {
                SPSMetadataDocument spsMetadata = SPSMetadataDocument.Factory.parse(metadataType.newInputStream());
                metadatas.add(spsMetadata.getSPSMetadata().getDataAccessType());
            }
            return metadatas;
        }
        catch (XmlException e) {
            LOGGER.warn("Invalid metadata element detected.", e);
            throw new MissingSensorInformationException("sps metadata");
        }
        catch (IOException e) {
            LOGGER.warn("Invalid metadata element detected.", e);
            throw new MissingSensorInformationException("sps metadata");
        }
    }

    private void handleAbstractContent(ResultAccessDataServiceReference reference, LanguageStringType[] abstracts) {
        if (abstracts != null && abstracts.length > 0) {
            reference.setReferenceAbstract(abstracts[0].getStringValue());
            if (abstracts.length > 1) {
                LOGGER.warn("Ignore further abstract descriptions, as SPS spec. requires only one!");
            }
        }
    }

}
