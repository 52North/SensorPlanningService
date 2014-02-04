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
package org.n52.sps.control.soap;
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
//package org.n52.sps.binding.soap;
//
//import net.opengis.sps.x20.CancelDocument;
//import net.opengis.sps.x20.ConfirmDocument;
//import net.opengis.sps.x20.DescribeResultAccessDocument;
//import net.opengis.sps.x20.DescribeTaskingDocument;
//import net.opengis.sps.x20.GetCapabilitiesDocument;
//import net.opengis.sps.x20.GetFeasibilityDocument;
//import net.opengis.sps.x20.GetStatusDocument;
//import net.opengis.sps.x20.GetTaskDocument;
//import net.opengis.sps.x20.ReserveDocument;
//import net.opengis.sps.x20.SubmitDocument;
//import net.opengis.sps.x20.UpdateDocument;
//import net.opengis.swes.x20.DescribeSensorDocument;
//import net.opengis.swes.x20.UpdateSensorDescriptionDocument;
//
//import org.apache.xmlbeans.XmlObject;
//import org.n52.ows.exception.OperationNotSupportedException;
//import org.n52.ows.exception.OwsException;
//import org.n52.sps.binding.SensorPlanningService;
//import org.n52.sps.common.Namespace;
//import org.n52.sps.handler.BasicSensorPlanner;
//import org.n52.sps.handler.FeasibilityController;
//import org.n52.sps.handler.ReservationManager;
//import org.n52.sps.handler.SensorDescriptionManager;
//import org.n52.sps.handler.SensorProvider;
//import org.n52.sps.handler.TaskCanceller;
//import org.n52.sps.handler.TaskUpdater;
//import org.n52.sps.service.ServiceController;
//import org.n52.sps.service.ServiceException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
//import org.springframework.ws.server.endpoint.annotation.RequestPayload;
//import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//
//@Endpoint
//public class SoapController extends ServiceController {

//    SensorPlanningService service;
//    
//    @Autowired
//    public SoapController(SensorPlanningService service) {
//        this.service = service;
//    }
//    
//    /*
//     * ######################
//     * MANDATORY OPERATIONS
//     * ######################
//     */
//    
//    @ResponsePayload
//    @PayloadRoot(localPart = "describeSensor", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject describeSensor(@RequestPayload DescribeSensorDocument describeSensor) {
//        try {
//            SensorProvider sensorProvider = service.getSensorProvider();
//            return sensorProvider.describeSensor(describeSensor);
//        } catch(OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//    
//    @ResponsePayload
//    @PayloadRoot(localPart = "describeResultAccess", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject describeResultAccess(@RequestPayload DescribeResultAccessDocument describeResultAccess) {
//        try {
//            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
//            return basicSensorPlanner.describeResultAccess(describeResultAccess);
//        } catch(OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "describeTasking", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject describeTasking(@RequestPayload DescribeTaskingDocument describeTasking) {
//        try {
//            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
//            return basicSensorPlanner.describeTasking(describeTasking);
//        } catch(OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "getCapabilities", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject getCapabilities(@RequestPayload GetCapabilitiesDocument getCapabilities) {
//        try {
//            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
//            return basicSensorPlanner.getCapabilities(getCapabilities);
//        } catch(OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "getStatus", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject getStatus(@RequestPayload GetStatusDocument getStatus) {
//        try {
//            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
//            return basicSensorPlanner.getStatus(getStatus);
//        } catch(OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "getTask", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject getTask(@RequestPayload GetTaskDocument getTask) {
//        try {
//            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
//            return basicSensorPlanner.getTask(getTask);
//        } catch (OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "submit", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject submit(@RequestPayload SubmitDocument submit) {
//        try {
//            BasicSensorPlanner basicSensorPlanner = service.getBasicSensorPlanner();
//            return basicSensorPlanner.submit(submit);
//        } catch(OwsException e) {
//            ServiceException exception = new ServiceException(e);
//            return exception.createExceptionType();
//        }
//    }
//    
//    /*
//     * ######################
//     * OPTIONAL OPERATIONS
//     * ######################
//     */
//    
//    @ResponsePayload
//    @PayloadRoot(localPart = "update", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject update(UpdateDocument update) {
//        TaskUpdater taskUpdater = service.getTaskUpdater();
//        if (taskUpdater == null) {
//            ServiceException exception = new ServiceException(new OperationNotSupportedException("update"));
//            return exception.createExceptionType();
//        }
//        // TODO Auto-generated method stub
//        return null;
//        
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "cancel", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject cancel(CancelDocument cancel) {
//        TaskCanceller taskCanceller = service.getTaskCanceller();
//        if (taskCanceller == null) {
//            ServiceException exception = new ServiceException(new OperationNotSupportedException("cancel"));
//            return exception.createExceptionType();
//        }
//        // TODO Auto-generated method stub
//        return null;
//        
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "updateSensorDescription", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject updateSensorDescription(UpdateSensorDescriptionDocument updateSensorDescription) {
//        SensorDescriptionManager sensorDescriptionManager = service.getSensorDescriptionManager();
//        if (sensorDescriptionManager == null) {
//            ServiceException exception = new ServiceException(new OperationNotSupportedException("updateSensorDescription"));
//            return exception.createExceptionType();
//        }
//        // TODO Auto-generated method stub
//        return null;
//        
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "confirm", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject confirm(ConfirmDocument confirm) {
//        ReservationManager reservationManager = service.getReservationManager();
//        if (reservationManager == null) {
//            ServiceException exception = new ServiceException(new OperationNotSupportedException("confirm"));
//            return exception.createExceptionType();
//        }
//        // TODO Auto-generated method stub
//        return null;
//        
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "reserve", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject reserve(ReserveDocument reserve) {
//        ReservationManager reservationManager = service.getReservationManager();
//        if (reservationManager == null) {
//            ServiceException exception = new ServiceException(new OperationNotSupportedException("reserve"));
//            return exception.createExceptionType();
//        }
//        // TODO Auto-generated method stub
//        return null;
//        
//    }
//
//    @ResponsePayload
//    @PayloadRoot(localPart = "getFeasibility", namespace = Namespace.NET_OGC_SCHEMA_SPS)
//    public XmlObject getFeasibility(GetFeasibilityDocument getFeasibility) {
//        FeasibilityController feasibilityController = service.getFeasibilityController();
//        if (feasibilityController == null) {
//            ServiceException exception = new ServiceException(new OperationNotSupportedException("getFeasibility"));
//            return exception.createExceptionType();
//        }
//        // TODO Auto-generated method stub
//        return null;
//        
//    }
    
//}
