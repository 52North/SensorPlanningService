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
package org.n52.sps.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.sps.x20.CapabilitiesDocument;
import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.sps.x20.GetCapabilitiesDocument;
import net.opengis.sps.x20.GetCapabilitiesType;

import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.ows.exception.MissingParameterValueException;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.VersionNegotiationFailedException;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.sps.service.admin.SpsAdmin;
import org.n52.sps.service.cancel.TaskCanceller;
import org.n52.sps.service.core.BasicSensorPlanner;
import org.n52.sps.service.core.SensorProvider;
import org.n52.sps.service.description.SensorDescriptionManager;
import org.n52.sps.service.feasibility.FeasibilityController;
import org.n52.sps.service.reservation.ReservationManager;
import org.n52.sps.service.update.TaskUpdater;

public class SpsComponentProvider implements SensorPlanningService {

    private BasicSensorPlanner basicSensorPlanner;

    private SensorProvider sensorProvider;

    private FeasibilityController feasibilityController;

    private ReservationManager reservationManager;

    private SensorDescriptionManager sensorDescriptionManager;

    private TaskCanceller taskCanceller;

    private TaskUpdater taskUpdater;

    private List<HttpBinding> httpBindings;

    private SpsAdmin spsAdmin;

    /**
     * Creates an SPS with both mandatory core components {@link BasicSensorPlanner} and
     * {@link SensorProvider} as required by REQ 1: <a
     * href="http://www.opengis.net/spec/SPS/2.0/req/interfaces"
     * >http://www.opengis.net/spec/SPS/2.0/req/interfaces</a>
     * 
     * @param basicSensorPlanner
     *        the mandatory BasicSensorPlanner component
     * @param sensorProvider
     *        the mandatory SensorProvider component
     */
    public SpsComponentProvider(BasicSensorPlanner basicSensorPlanner, SensorProvider sensorProvider) {
        assertMandatoryOperationComponents(basicSensorPlanner, sensorProvider);
        this.httpBindings = new ArrayList<HttpBinding>();
        this.basicSensorPlanner = basicSensorPlanner;
        this.sensorProvider = sensorProvider;
    }

    private void assertMandatoryOperationComponents(BasicSensorPlanner planner, SensorProvider provider) {
        if (planner == null) {
            throw new IllegalArgumentException("Missing BasicSensorPlanner component.");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Missing SensorProvider component.");
        }
    }

    public BasicSensorPlanner getBasicSensorPlanner() {
        return basicSensorPlanner;
    }

    public SensorProvider getSensorProvider() {
        return sensorProvider;
    }

    public FeasibilityController getFeasibilityController() {
        return feasibilityController;
    }

    public void setFeasibilityController(FeasibilityController feasibilityController) {
        this.feasibilityController = feasibilityController;
    }

    public ReservationManager getReservationManager() {
        return reservationManager;
    }

    public void setReservationManager(ReservationManager reservationManager) {
        this.reservationManager = reservationManager;
    }

    public SensorDescriptionManager getSensorDescriptionManager() {
        return sensorDescriptionManager;
    }

    public void setSensorDescriptionManager(SensorDescriptionManager sensorDescriptionManager) {
        this.sensorDescriptionManager = sensorDescriptionManager;
    }

    public TaskCanceller getTaskCanceller() {
        return taskCanceller;
    }

    public void setTaskCanceller(TaskCanceller taskCanceller) {
        this.taskCanceller = taskCanceller;
    }

    public TaskUpdater getTaskUpdater() {
        return taskUpdater;
    }

    public void setTaskUpdater(TaskUpdater taskUpdater) {
        this.taskUpdater = taskUpdater;
    }

    public SpsAdmin getSpsAdmin() {
        return spsAdmin;
    }

    public void setSpsAdmin(SpsAdmin spsAdmin) {
        this.spsAdmin = spsAdmin;
    }
    
    public void shutdown() {
        // TODO Auto-generated method stub
    }

    public void validateGetCapabilitiesParameters(GetCapabilitiesDocument getCapabilitiesDoc) throws OwsException {
        GetCapabilitiesType getCapabilities = getCapabilitiesDoc.getGetCapabilities2();
        if (getCapabilities.isSetService()) {
            validateMandatoryServiceParameter(getCapabilities.getService());
        }
        
        if (getCapabilities.isSetAcceptVersions()) {
            // TODO improve to get true version negotiation
            AcceptVersionsType acceptVersions = getCapabilities.getAcceptVersions();
            List<String> versions = Arrays.asList(acceptVersions.getVersionArray());
            if (!versions.contains("2.0.0")) {
                throw new VersionNegotiationFailedException();
            }
        }
    }
    
    public void validateMandatoryServiceParameter(String serviceParameter) throws OwsException {
        if (serviceParameter == null || serviceParameter.isEmpty()) {
            throw new MissingParameterValueException("service");
        }
        if (!"SPS".equals(serviceParameter)) {
            InvalidParameterValueException e = new InvalidParameterValueException("service");
            e.addExceptionText(String.format("Service parmater '%s' is invalid.", serviceParameter));
            throw e;
        }
    }
    
    public void validateVersionParameter(String version) throws OwsException {
        if (isVersionParamaterMissing(version)) {
            throw new MissingParameterValueException("version");
        }
        if (!isSupportedVersion(version)) {
            throw new InvalidParameterValueException("version");
        }
    }

    private boolean isVersionParamaterMissing(String version) {
        return version == null || version.isEmpty();
    }

    private boolean isSupportedVersion(String version) {
        return "2.0.0".equalsIgnoreCase(version);
    }

    public void addHttpBinding(HttpBinding httpBinding) {
        this.httpBindings.add(httpBinding);
    }

    public boolean isSupportingFeasibilityControllerProfile() {
        return feasibilityController != null;
    }

    public boolean isSupportingReservationManagerProfile() {
        return reservationManager != null;
    }

    public boolean isSupportingSensorHistoryProviderProfile() {
        return sensorDescriptionManager != null;
    }

    public boolean isSupportingTaskCancellerProfile() {
        return taskCanceller != null;
    }

    public boolean isSupportingTaskUpdaterProfile() {
        return taskUpdater != null;
    }

    public boolean isSpsAdminAvailable() {
        return spsAdmin != null;
    }

    public void interceptCapabilities(CapabilitiesDocument capabilitiesDocument) {
        CapabilitiesType capabilities = capabilitiesDocument.getCapabilities();
        basicSensorPlanner.interceptCapabilities(capabilities, httpBindings);
        sensorProvider.interceptCapabilities(capabilities, httpBindings);
        if (isSpsAdminAvailable() && spsAdmin.isInterceptCapabilities()) {
            spsAdmin.interceptCapabilities(capabilities, httpBindings);
        }
        // TODO intercept profiles
        // TODO intercept operations metadata
        // TODO intercept notifications
    }

}
