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
package org.n52.sps.sensor;

import net.opengis.sps.x20.DescribeResultAccessResponseType.Availability;
import net.opengis.swe.x20.AbstractDataComponentType;

import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.oxf.xmlbeans.tools.XMLBeansTools;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.sensor.result.ResultAccessAvailabilityDescriptor;
import org.n52.sps.sensor.result.ResultAccessServiceReference;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.store.SensorTaskRepository;
import org.n52.sps.tasking.SubmitTaskingRequest;

/**
 * The {@link SensorPlugin} interface serves as central handle/access point to sensor instances within the SPS
 * framework components.<br>
 * <br>
 * Subclasses implements actual sensor instance logic, i.e. scheduling tasks, executing tasks while updating
 * their status, etc. However, to fit perfectly into the framework, implementers or plugin developers
 * respectively are given several framework handles:
 * <ul>
 * <li>TODO document SensorPluginFactory for creating plugins of arbitrary plugin types
 * <li>TODO document sensorTaskService for updating persisted tasks
 * <li>TODO describe tasks life-cycle and how to deal with ResultAccess references/descriptors
 * <li>TODO check what else has to be documented
 * </ul>
 */
public abstract class SensorPlugin implements ResultAccessAvailabilityDescriptor, ResultAccessServiceReference {

    protected SensorConfiguration configuration;

    protected SensorTaskService sensorTaskService;
    
    protected SensorPlugin() {
        // allow default constructor for decoration
    }

    public SensorPlugin(SensorTaskService sensorTaskService, SensorConfiguration configuration) throws InternalServiceException {
        this.configuration = configuration;
        this.sensorTaskService = sensorTaskService;
        sensorTaskService.setProcedure(this);
    }

    public final String getProcedure() {
        return configuration.getProcedure();
    }

    public SensorTaskService getSensorTaskService() {
        return sensorTaskService;
    }

    public void setSensorTaskService(SensorTaskService sensorTaskService) {
        this.sensorTaskService = sensorTaskService;
    }

    /**
     * Delegate submit tasking request to sensor instance for execution. The execution can fail for many
     * reasons so implementors may consider exception handling by adding each occuring exception to the passed
     * {@link OwsExceptionReport} which will be thrown by the SPS framework afterwards.<br>
     * <br>
     * A {@link SensorTask} instance shall be created by the plugin's {@link SensorTaskService} which takes
     * care of storing the task instance into the SPS's {@link SensorTaskRepository}. Once a sensor task is
     * created, the task service can be used to continously update the task within the underlying
     * {@link SensorTaskRepository}.
     * 
     * @param submit
     *        the tasking request
     * @param owsExceptionReport
     *        a container collecting {@link OwsException}s to be thrown together as OWS Exception Report
     *        (according to chapter 8 of [OGC 06-121r3]).
     * @return a SensorTask instance as a result of the Submit task
     * @throws OwsException
     *         if an OwsException is thrown without execption reporting intent.
     */
    public abstract SensorTask submit(SubmitTaskingRequest submit, OwsExceptionReport owsExceptionReport) throws OwsException;

    /*
     * Let the SPS manage this kinds of functionality! Extend SensorPlugin interface only with those 
     * kind of methods a SensorPlugin implementation can provide only!
     * 
     * TODO add abstract getUpdateDescription mehthod
     * TODO add abstract cancel mehod
     * TODO add abstract feasibility method
     * TODO add abstract reservation methods
     * TODO add absrtact update methods
     */

    /**
     * Qualifies sensor's tasking characteristics accordingly. <br>
     * <br>
     * The SPS has to ask a {@link SensorPlugin} instance for a full qualified data component describing its
     * tasking parameters (e.g. AbstractDataComponentType must be qualified to a concrete type implementation
     * like DataRecordType). Implementors can make use of
     * {@link XMLBeansTools#qualifySubstitutionGroup(org.apache.xmlbeans.XmlObject, javax.xml.namespace.QName)}
     * <br>
     * <b>Example:</b>
     * 
     * <pre>
     * {@code
     * public void getQualifiedDataComponent(AbstractDataComponentType componentToQualify) {
     *  QName qname = new QName("http://www.opengis.net/swe/2.0", "DataRecord");
     *  XMLBeansTools.qualifySubstitutionGroup(componentToQualify, qname);
     * }
     * </pre>
     * 
     * @param the
     *        abstract data component to qualify
     */
    public abstract void qualifyDataComponent(AbstractDataComponentType componentToQualify);
    
    public abstract Availability getResultAccessibilityFor(SensorTask sensorTask);

    public String getSensorPluginType() {
        return configuration.getSensorPluginType();
    }

    public SensorConfiguration getSensorConfiguration() {
        return configuration;
    }

    public ResultAccessDataServiceReference getResultAccessDataServiceReference() {
        return configuration.getResultAccessDataService();
    }

    public SensorConfiguration mergeSensorConfigurations(SensorConfiguration sensorConfiguration) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getProcedure() == null) ? 0 : getProcedure().hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensorPlugin other = (SensorPlugin) obj;
        if (getProcedure() == null) {
            if (other.getProcedure() != null)
                return false;
        }
        else if ( !getProcedure().equals(other.getProcedure()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SensorPluginType: ").append(configuration.getSensorPluginType()).append(", ");
        sb.append("Procedure: ").append(configuration.getProcedure());
        return sb.toString();
    }

}
