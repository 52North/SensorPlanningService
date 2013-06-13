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

package org.n52.sps.service.admin;

import java.util.List;

import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.sps.x20.CapabilitiesType;
import net.opengis.swes.x20.ExtensibleRequestType;

import org.apache.xmlbeans.XmlObject;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.sps.service.InternalServiceException;
import org.n52.sps.service.SpsOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument;

public class AdminOperator extends SpsOperator implements SpsAdmin {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminOperator.class);

    private boolean interceptCapabilities;

    private HttpBinding httpBinding;

    private List<InsertSensorOfferingListener> insertSensorOfferingListeners;

    private List<DeleteSensorOfferingListener> deleteSensorOfferingListeners;

    public List<InsertSensorOfferingListener> getInsertSensorOfferingListeners() {
        return insertSensorOfferingListeners;
    }

    public void setInsertSensorOfferingListeners(List<InsertSensorOfferingListener> insertSensorOfferingListeners) {
        this.insertSensorOfferingListeners = insertSensorOfferingListeners;
    }

    public void setDeleteSensorOfferingListeners(List<DeleteSensorOfferingListener> deleteSensorOfferingListeners) {
        this.deleteSensorOfferingListeners = deleteSensorOfferingListeners;
    }

    public void setExtensionBinding(HttpBinding httpBinding) {
        this.httpBinding = httpBinding;
    }

    public void insertSensorOffering(InsertSensorOfferingDocument insertSensorOfferingDocument, OwsExceptionReport exceptionReport) throws InternalServiceException {
        LOGGER.debug("insertSensorOffering: {}", insertSensorOfferingDocument.xmlText());

        InsertSensorOfferingEvent insertSensorOfferingEvent = new InsertSensorOfferingEvent(insertSensorOfferingDocument);

        // TODO extract information, inform listeners

        fireRegisterSensorOfferingEvent(insertSensorOfferingEvent);
    }

    private void fireRegisterSensorOfferingEvent(InsertSensorOfferingEvent insertSensorOfferingEvent) throws InternalServiceException {
        for (InsertSensorOfferingListener listener : insertSensorOfferingListeners) {
            listener.handleInsertSensorOffering(insertSensorOfferingEvent);
        }
    }

    public void deleteSensorOffering(XmlObject request, OwsExceptionReport exceptionReport) throws InternalServiceException {
        LOGGER.debug("deleteSensorOffering: {}", request.xmlText());
        DeleteSensorOfferingEvent deleteSensorOfferingEvent = new DeleteSensorOfferingEvent();

        // TODO delete sensor

        fireDeleteSensorOfferingEvent(deleteSensorOfferingEvent);
    }

    private void fireDeleteSensorOfferingEvent(DeleteSensorOfferingEvent deleteSensorOfferingEvent) throws InternalServiceException {
        for (DeleteSensorOfferingListener listener : deleteSensorOfferingListeners) {
            listener.handleDeleteSensorOffering(deleteSensorOfferingEvent);
        }
    }

    public void interceptCapabilities(CapabilitiesType capabilities, List<HttpBinding> httpBindings) {
        // ignore incoming bindings, as extension has its own httpBinding
        if (interceptCapabilities) {
            insertOperationMetadata(capabilities);
        }
    }

    private void insertOperationMetadata(CapabilitiesType capabilities) {
        OperationsMetadata operationsMetadata = getOperationsMetadata(capabilities);
        addRegisterSensorOperation(operationsMetadata);
        addDeleteSensorOperation(operationsMetadata);
    }

    private void addRegisterSensorOperation(OperationsMetadata operationsMetadata) {
        Operation registerSensorOperation = operationsMetadata.addNewOperation();
        registerSensorOperation.setName(INSERT_SENSOR_OFFERING);
        addDcpExtensionBinding(registerSensorOperation, httpBinding, INSERT_RESOURCE);
    }

    private void addDeleteSensorOperation(OperationsMetadata operationsMetadata) {
        Operation deleteSensorOperation = operationsMetadata.addNewOperation();
        deleteSensorOperation.setName(DELETE_SENSOR_OFFERING);
        addDcpExtensionBinding(deleteSensorOperation, httpBinding, DELETE_RESOURCE);
    }

    public boolean isInterceptCapabilities() {
        return interceptCapabilities;
    }

    /**
     * @param interceptCapabilities if this administrator shall intercept operations to capabilities metadata.
     */
    public void setInterceptCapabilities(boolean interceptCapabilities) {
        this.interceptCapabilities = interceptCapabilities;
    }

    @Override
    public boolean isSupportingExtensions() {
        return false;
    }

    @Override
    protected void checkSupportingSpsRequestExtensions(XmlObject[] extensions) throws OwsExceptionReport {
        // no OWS implementation
    }

    @Override
    protected void checkSupportingSwesRequestExtensions(ExtensibleRequestType extensibleRequest) throws OwsExceptionReport {
        // no OWS implementation
    }

}
