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
package org.n52.sps.sensor.cite;

import static org.n52.sps.tasking.TaskingRequestStatus.ACCEPTED;
import static org.n52.sps.tasking.TaskingRequestStatus.REJECTED;

import java.io.IOException;

import javax.xml.namespace.QName;

import net.opengis.ows.x11.AbstractReferenceBaseType;
import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.ows.x11.MetadataType;
import net.opengis.ows.x11.ReferenceDocument;
import net.opengis.ows.x11.ReferenceGroupType;
import net.opengis.ows.x11.ReferenceType;
import net.opengis.sps.x20.DataAvailableType;
import net.opengis.sps.x20.DataAvailableType.DataReference;
import net.opengis.sps.x20.DescribeResultAccessResponseType.Availability;
import net.opengis.sps.x20.SPSMetadataType;
import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.DataRecordType;

import org.apache.xmlbeans.XmlException;
import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.oxf.xmlbeans.tools.XMLBeansTools;
import org.n52.sps.sensor.SensorPlugin;
import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.cite.exec.CiteTaskScheduler;
import org.n52.sps.sensor.cite.exec.CiteTaskSimulation;
import org.n52.sps.sensor.cite.tasking.TextValuesDataRecordValidator;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.sensor.result.DataNotAvailable;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.tasking.SubmitTaskingRequest;
import org.n52.sps.tasking.TaskingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CiteTestSensor extends SensorPlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(CiteTestSensor.class);
	
	private CiteTaskScheduler scheduler;
    
    public CiteTestSensor(SensorTaskService sensorTaskService, SensorConfiguration configuration) throws InternalServiceException {
        super(sensorTaskService, configuration);
        scheduler = new CiteTaskScheduler(sensorTaskService);
    }

    @Override
    public SensorTask submit(SubmitTaskingRequest submit, OwsExceptionReport owsExceptionReport) throws OwsException {
        LOGGER.debug("submit method called of procedure '{}'", getProcedure());
        SensorTask submitTask = sensorTaskService.createNewTask();
        submitTask.setParameterData(submit.getParameterData());
        scheduleAndAcceptTask(submit, submitTask);
    	return submitTask;
    }

    private void scheduleAndAcceptTask(SubmitTaskingRequest submit, SensorTask submitTask) throws InvalidParameterValueException {
        try {
            DataRecordType[] validInputs = getValidInputs(submit);
            CiteTaskSimulation simulation = CiteTaskSimulation.createTaskSimulation(submitTask, validInputs);
            simulation.setSensorTaskService(sensorTaskService);
            if (scheduler.schedule(simulation)) {
                submitTask.setRequestStatus(ACCEPTED);
            } else {
                submitTask.setRequestStatus(REJECTED);
            }
        }
        catch (InvalidParameterValueException e) {
            String format = "The given tasking parameters were not valid; %s";
            submitTask.addStatusMessage(String.format(format, e.getMessage()));
            submitTask.setRequestStatus(REJECTED);
            throw e;
        }
    }
    
    private DataRecordType[] getValidInputs(TaskingRequest taskingrequest) throws InvalidParameterValueException {
        DataRecordType dataRecord = getTaskingParametersAsDataRecord();
        TextValuesDataRecordValidator validator = new TextValuesDataRecordValidator(dataRecord, taskingrequest);
        DataRecordType[] validInputs = validator.getValidDataRecords();
        return validInputs;
    }

    private DataRecordType getTaskingParametersAsDataRecord() {
        try {
            AbstractDataComponentType template = configuration.getTaskingParametersTemplate();
            return DataRecordType.Factory.parse(template.newInputStream());
        } catch (XmlException e) {
            throw new IllegalStateException("Sensor's tasking parameters are no valid DataRecordType");
        }
        catch (IOException e) {
            throw new IllegalStateException("Sensor's tasking parameters are no valid DataRecordType");
        }
    }

	@Override
    public void qualifyDataComponent(AbstractDataComponentType componentToQualify) {
        QName qname = new QName("http://www.opengis.net/swe/2.0", "DataRecord");
        XMLBeansTools.qualifySubstitutionGroup(componentToQualify, qname);
    }
	
    @Override
    public Availability getResultAccessibilityFor(SensorTask sensorTask) {
        CiteTaskResultAccess resultAccess = createResultAccessGenerator(sensorTask);
        return resultAccess.getResultAccessibility();
    }

    private CiteTaskResultAccess createResultAccessGenerator(SensorTask sensorTask) {
        return new CiteTaskResultAccess(sensorTask, getResultAccessDataServiceReference());
    }

    public boolean isDataAvailable() {
        for (SensorTask sensorTask : sensorTaskService.getSensorTasks()) {
            if (createResultAccessGenerator(sensorTask).isDataAvailable()) {
                return true; // at lease one task must provide data
            }
        }
        return false;
    }

    public Availability getResultAccessibility() {
        long amountOfAvailableTasks = sensorTaskService.getAmountOfAvailableTasks();
        if (amountOfAvailableTasks == 0) {
            LanguageStringType languageMessage = LanguageStringType.Factory.newInstance();
            languageMessage.setStringValue("No tasks available which could provide result access information.");
            return new DataNotAvailable(languageMessage).getResultAccessibility();
        }
        
        Availability availability = Availability.Factory.newInstance();
        DataAvailableType configuredServiceReference = availability.addNewAvailable().addNewDataAvailable();
        DataReference serviceReference = configuredServiceReference.addNewDataReference();
        createReferenceGroup(serviceReference.addNewReferenceGroup());

        // general configured result access
        return availability;
	}

    private void createReferenceGroup(ReferenceGroupType referenceGroup) {
        referenceGroup.setAbstractReferenceBaseArray(createReferences());
        for (AbstractReferenceBaseType referenceBase : referenceGroup.getAbstractReferenceBaseArray()) {
            // XXX check if there is a better way to qualify abstract element
            QName qname = new QName("http://www.opengis.net/ows/1.1", "Reference");
            XMLBeansTools.qualifySubstitutionGroup(referenceBase, qname);
        }
    }

    private ReferenceType[] createReferences() {
        ResultAccessDataServiceReference resultAccessInformation = getResultAccessDataServiceReference();
        ReferenceDocument referenceDoc = ReferenceDocument.Factory.newInstance();
        ReferenceType reference = referenceDoc.addNewReference();
        reference.setHref(resultAccessInformation.getReference());
        reference.setFormat(resultAccessInformation.getFormat());
        reference.setTitle(resultAccessInformation.getTitle());
        /*
         *  We override the role here as for a DescribeResultAccess with procedure
         *  a service reference shall inform about the data service used in gerenal.
         *  There is no concrete data request to retrieve data as it would be when
         *  a DescribeResultAccess with a task id would be send. 
         *  => see table 41 in SPS spec. 2.0
         */
        reference.setRole("http://www.opengis.net/spec/SPS/2.0/referenceType/ServiceURL");
        
        createAbstract(reference.addNewAbstract(), resultAccessInformation.getReferenceAbstract());
        createCodeType(reference.addNewIdentifier(), resultAccessInformation.getIdentifier());
        for (String dataAccessType : resultAccessInformation.getDataAccessTypes()) {
            createSpsMetadata(reference.addNewMetadata(), dataAccessType);
        }
        return new ReferenceType[] { reference };
    }

    private void createAbstract(LanguageStringType languageType, String referenceAbstract) {
        languageType.setStringValue(referenceAbstract);
    }

    private void createCodeType(CodeType identifierCodeType, String identifier) {
        identifierCodeType.setStringValue(identifier);
    }

    void createSpsMetadata(MetadataType metadata, String dataAccessType) {
        SPSMetadataType spsMetadata = SPSMetadataType.Factory.newInstance();
        spsMetadata.setDataAccessType(dataAccessType);
        metadata.addNewAbstractMetaData().set(spsMetadata);
        // XXX check if there is a better way to qualify abstract element
        QName qname = new QName("http://www.opengis.net/sps/2.0", "SPSMetadata");
        XMLBeansTools.qualifySubstitutionGroup(metadata.getAbstractMetaData(), qname);
    }
}
