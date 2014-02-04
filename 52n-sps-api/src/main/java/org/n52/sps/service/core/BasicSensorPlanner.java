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

import net.opengis.sps.x20.DescribeResultAccessDocument;
import net.opengis.sps.x20.DescribeTaskingDocument;
import net.opengis.sps.x20.GetCapabilitiesDocument;
import net.opengis.sps.x20.GetStatusDocument;
import net.opengis.sps.x20.GetTaskDocument;
import net.opengis.sps.x20.SubmitDocument;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.sps.service.CapabilitiesInterceptor;

/**
 * Represents the core functionality of an SPS. <br>
 * Describes functions required by Conformatance Class: http://www.opengis.net/spec/SPS/2.0/conf/Core
 */
public interface BasicSensorPlanner extends CapabilitiesInterceptor {

    final static String CORE_CONFORMANCE_CLASS = "http://www.opengis.net/spec/SPS/2.0/conf/Core";

    final static String DESCRIBE_RESULT_ACCESS = "DescribeResultAccess";

    final static String DESCRIBE_TASKING = "DescribeTasking";

    final static String GET_CAPABILITIES = "GetCapabilities";

    final static String GET_STATUS = "GetStatus";

    final static String GET_TASK = "GetTask";

    final static String SUBMIT = "Submit";

    /**
     * allows a client to retrieve information, which enables access to the data produced by the asset. The
     * server response may contain references to any kind of data accessing OGC Web services such as SOS, WMS,
     * WCS or WFS.
     * 
     * @param describeResultAccess
     *        the request
     * @return the result access information
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public XmlObject describeResultAccess(DescribeResultAccessDocument describeResultAccess) throws OwsException, OwsExceptionReport;

    /**
     * Allows a client to request the information that is needed in order to prepare a tasking request
     * targeted at the assets that are supported by the SPS and that are selected by the client. The server
     * will return information about all parameters that have to be set by the client in order to create a
     * task.
     * 
     * @param describeTasking
     *        the request
     * @return the tasking information
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public XmlObject describeTasking(DescribeTaskingDocument describeTasking) throws OwsException, OwsExceptionReport;

    /**
     * Allows a client to request and receive service metadata documents that describe the capabilities of the
     * specific server implementation. This operation also supports negotiation of the specification version
     * being used for client-server interactions.
     * 
     * @param getCapabilities
     *        the request
     * @return the capabilities information
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public XmlObject getCapabilities(GetCapabilitiesDocument getCapabilities) throws OwsException, OwsExceptionReport;

    /**
     * Allows a client to receive information about the current status of the requested task.
     * 
     * @param getStatus
     *        the request
     * @return the status information
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public XmlObject getStatus(GetStatusDocument getStatus) throws OwsException, OwsExceptionReport;

    /**
     * Returns complete information about the requested task.
     * 
     * @param getTask
     *        the request
     * @return the task information
     * @throws OwsException
     * @throws OwsExceptionReport
     */
    public XmlObject getTask(GetTaskDocument getTask) throws OwsException, OwsExceptionReport;

    /**
     * Submits a task. Depending on the facaded asset, it may perform a simple modification of the asset or
     * start a complex mission.
     * 
     * @param submit
     *        the request
     * @return the submit information
     * @throws OwsException
     * @throws OwsExceptionReport 
     */
    public XmlObject submit(SubmitDocument submit) throws OwsException, OwsExceptionReport;

}
