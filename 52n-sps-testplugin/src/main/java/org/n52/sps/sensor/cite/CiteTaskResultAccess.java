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

package org.n52.sps.sensor.cite;

import javax.xml.namespace.QName;

import net.opengis.ows.x11.AbstractReferenceBaseType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.ows.x11.MetadataType;
import net.opengis.ows.x11.ReferenceGroupType;
import net.opengis.ows.x11.ServiceReferenceType;
import net.opengis.sps.x20.DataAvailableType.DataReference;
import net.opengis.sps.x20.DescribeResultAccessResponseType.Availability;
import net.opengis.sps.x20.SPSMetadataType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.xmlbeans.tools.XMLBeansTools;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.sensor.result.DataAvailable;
import org.n52.sps.sensor.result.DataNotAvailable;
import org.n52.sps.sensor.result.ResultAccessAvailabilityDescriptor;

public class CiteTaskResultAccess implements ResultAccessAvailabilityDescriptor {

    private ResultAccessDataServiceReference resultAccessDataServiceReference;

    private SensorTask sensorTask;

    public CiteTaskResultAccess(SensorTask sensorTask, ResultAccessDataServiceReference dataReference) {
        this.resultAccessDataServiceReference = dataReference;
        this.sensorTask = sensorTask;
    }

    public Availability getResultAccessibility() {
        if ( !sensorTask.isAccepted()) {
            return createDataNotAvailable("Task request was not accepted, yet.");
        }
        switch (sensorTask.getTaskStatus()) {
        case RESERVED:
            return createDataNotAvailable("Task reserved and not started, yet.");
        case FAILED:
            return createDataNotAvailable("Task execution failed.");
        case CANCELLED:
            return createDataNotAvailable("Task has been cancelled.");
        case INEXECUTION:
            // TODO shall the task deliver data during execution?
            return createDataNotAvailable("Task is in execution.");
        case COMPLETED:
            return createDataAvailibility();
        case EXPIRED:
            return createDataNotAvailable("Task status has been expired.");
        }

        throw new RuntimeException("Invalid task status detected!");
    }

    public boolean isDataAvailable() {
        if ( !sensorTask.isAccepted()) {
            return false;
        }
        switch (sensorTask.getTaskStatus()) {
        // case INEXECUTION:
        // TODO shall the task deliver data during execution?
        // return true;
        case COMPLETED:
            return true;
        default:
            return false;
        }
    }

    private Availability createDataNotAvailable(String message) {
        LanguageStringType languageMsg = LanguageStringType.Factory.newInstance();
        languageMsg.setStringValue(message);
        return new DataNotAvailable(languageMsg).getResultAccessibility();
    }

    private Availability createDataAvailibility() {
        DataReference dataReference = DataReference.Factory.newInstance();
        ReferenceGroupType referenceGroup = dataReference.addNewReferenceGroup();
        referenceGroup.addNewIdentifier().setStringValue(sensorTask.getTaskId());

        referenceGroup.addNewAbstractReferenceBase().set(createServiceReference());
        for (AbstractReferenceBaseType referenceBase : referenceGroup.getAbstractReferenceBaseArray()) {
            // XXX check if there is a better way to qualify abstract element
            QName qname = new QName("http://www.opengis.net/ows/1.1", "ServiceReference");
            XMLBeansTools.qualifySubstitutionGroup(referenceBase, qname);
        }
        return new DataAvailable(new DataReference[] {dataReference}).getResultAccessibility();
    }

    /**
     * The {@link CiteTaskResultAccess} applies the service reference template provided when sensor instance
     * gets installed. SPS 2.0.0 specification allows five options to create a result access. This method
     * creates a reference regarding to Option 2 (see SPS 2.0.0 spec., table 41) with role
     * <pre>
     * {@code http://www.opengis.net/spec/SPS/2.0/referenceType/FullServiceAccess}
     * </pre>
     * 
     * @return a service reference which describes result access.
     */
    ServiceReferenceType createServiceReference() {
        ServiceReferenceType serviceReference = ServiceReferenceType.Factory.newInstance();
        serviceReference.addNewIdentifier().setStringValue(sensorTask.getTaskId());
        serviceReference.setHref(resultAccessDataServiceReference.getReference());
        serviceReference.setRole(resultAccessDataServiceReference.getRole());
        serviceReference.setTitle(String.format("Observation data for task %s", sensorTask.getTaskId()));
        serviceReference.setFormat(resultAccessDataServiceReference.getFormat());
        serviceReference.setRequestMessage(encodeXmlRequest(createRequest()));
//        XmlCursor cursor = serviceReference.newCursor();
//        cursor.toFirstContentToken(); 
//        cursor.setBookmark(CDataBookmark.CDATA_BOOKMARK); 

        SPSMetadataType accessMetadata = SPSMetadataType.Factory.newInstance();
        accessMetadata.setDataAccessType("http://www.opengis.net/sos/2.0/GetObservation");
        serviceReference.addNewMetadata().addNewAbstractMetaData().set(accessMetadata);

        for (MetadataType metadata : serviceReference.getMetadataArray()) {
            // XXX check if there is a better way to qualify abstract element
            QName qname = new QName("http://www.opengis.net/sps/2.0", "SPSMetadata");
            XMLBeansTools.qualifySubstitutionGroup(metadata.getAbstractMetaData(), qname);
        }

        return serviceReference;
    }

    private XmlObject encodeXmlRequest(String createRequest) {
        try {
            return XmlObject.Factory.parse(createRequest());
        }
        catch (XmlException e) {
            return XmlObject.Factory.newInstance();
        }
    }

    String createRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<GetObservationById ").append("version='2.0.0' ").append("service='SOS' ");
        sb.append("xmlns='http://www.opengis.net/sos/2.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' ");
        sb.append("xsi:schemaLocation='http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd'>");
        sb.append("<observation>");
        sb.append(sensorTask.getTaskId());
        sb.append("</observation>");
        sb.append("</GetObservationById>");
//        sb.append(resultAccessDataServiceReference.getReference());
//        sb.append("?request=GetObservationById");
//        sb.append("&service=SOS");
//        sb.append("&version=2.0.0");
//        sb.append("&observation=").append(sensorTask.getTaskId());
        // String encodedRequest = URLEncoder.encode(sb.toString(), "utf-8");
        
        return sb.toString();

    }

    public ResultAccessDataServiceReference getResultAccessDataServiceReference() {
        return resultAccessDataServiceReference;
    }

}
