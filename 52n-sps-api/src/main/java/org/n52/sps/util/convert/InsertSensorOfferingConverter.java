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
