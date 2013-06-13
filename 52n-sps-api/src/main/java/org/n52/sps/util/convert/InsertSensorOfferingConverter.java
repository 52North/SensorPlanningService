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

package org.n52.sps.util.convert;

import java.util.UUID;

import net.opengis.gml.x32.PointType;
import net.opengis.gml.x32.PolygonType;
import net.opengis.sps.x20.SensorOfferingType;
import net.opengis.swes.x20.FeatureRelationshipType;

import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering;
import org.x52North.schemas.sps.v2.InsertSensorOfferingDocument.InsertSensorOffering.SensorDescriptionData;
import org.x52North.schemas.sps.v2.SensorInstanceDataDocument.SensorInstanceData;
import org.x52North.schemas.sps.v2.SensorOfferingDataDocument.SensorOfferingData;
import org.x52North.schemas.sps.v2.SensorOfferingDataDocument.SensorOfferingData.ObservableArea;
import org.x52North.schemas.sps.v2.SensorOfferingDataDocument.SensorOfferingData.RelatedFeature;

public class InsertSensorOfferingConverter {

    /**
     * Converts custom {@link InsertSensorOffering} type into well-known {@link SensorOfferingType}.
     * 
     * @param insertSensorOffering
     *        custom description document for inserting new sensor offerings.
     * @return a sensor offering as specified.
     */
    public SensorOfferingType convertToSensorOfferingType(final InsertSensorOffering insertSensorOffering) {
        SensorOfferingType sensorOfferingType = SensorOfferingType.Factory.newInstance();
        handleSensorOfferingContent(insertSensorOffering, sensorOfferingType);
        return sensorOfferingType;
    }

    private void handleSensorOfferingContent(InsertSensorOffering insertSensorOffering, SensorOfferingType sensorOfferingType) {
        addSensorOfferingData(insertSensorOffering.getSensorOfferingData(), sensorOfferingType);
        addSensorInstanceData(insertSensorOffering.getSensorInstanceData(), sensorOfferingType);
        addProcedureDescriptionFormats(insertSensorOffering.getSensorDescriptionDataArray(), sensorOfferingType);
    }

    private void addSensorOfferingData(SensorOfferingData sensorOfferingData, SensorOfferingType sensorOfferingType) {
        sensorOfferingType.setIdentifier(sensorOfferingData.getOfferingIdentifier());
        sensorOfferingType.setDescription(sensorOfferingData.getOfferingDescription());
        sensorOfferingType.setNameArray(sensorOfferingData.getOfferingNameArray());
        sensorOfferingType.setObservablePropertyArray(sensorOfferingData.getObservablePropertyArray());
        addRelatedFeatures(sensorOfferingData.getRelatedFeatureArray(), sensorOfferingType);
        addObservableArea(sensorOfferingData.getObservableArea(), sensorOfferingType);
    }

    private void addSensorInstanceData(SensorInstanceData sensorInstanceData, SensorOfferingType sensorOfferingType) {
        sensorOfferingType.setProcedure(sensorInstanceData.getProcedure().getStringValue());
    }

    private void addProcedureDescriptionFormats(SensorDescriptionData[] sensorDescriptionDataArray, SensorOfferingType sensorOfferingType) {
        String[] procedureDescriptionFormats = new String[sensorDescriptionDataArray.length];
        for (int i = 0; i < sensorDescriptionDataArray.length; i++) {
            procedureDescriptionFormats[i] = sensorDescriptionDataArray[i].getProcedureDescriptionFormat();
        }
        sensorOfferingType.setProcedureDescriptionFormatArray(procedureDescriptionFormats);
    }

    private void addRelatedFeatures(RelatedFeature[] relatedFeatureArray, SensorOfferingType sensorOfferingType) {
        for (RelatedFeature relatedFeature : relatedFeatureArray) {
            FeatureRelationshipType featureRelationship = relatedFeature.getFeatureRelationship();
            sensorOfferingType.addNewRelatedFeature().setFeatureRelationship(featureRelationship);
        }
    }

    private void addObservableArea(ObservableArea observableArea, SensorOfferingType sensorOffering) {
        if (observableArea.isSetByPoint()) {
            PointType point = observableArea.getByPoint().getPoint();
            point.setId(createRandomGmlId());
            sensorOffering.addNewObservableArea().addNewByPoint().setPoint(point);
        }
        else {
            PolygonType polygon = observableArea.getByPolygon().getPolygon();
            polygon.setId(createRandomGmlId());
            sensorOffering.addNewObservableArea().addNewByPolygon().setPolygon(polygon);
        }
    }
    
    String createRandomGmlId() {
        return "uuid_" + UUID.randomUUID().toString();
    }

}
