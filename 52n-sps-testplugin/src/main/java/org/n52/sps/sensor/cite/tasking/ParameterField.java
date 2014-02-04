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
package org.n52.sps.sensor.cite.tasking;

import net.opengis.swe.x20.DataRecordType.Field;

enum ParameterField {
    MEASUREMENT_FREQUENCY("measurementFrequency", 1),
    MEASUREMENT_LOCATION("measurementLocation", 2),
    MEASUREMENT_COUNT("measurementCount", 1),
    MEASUREMENT_PURPOSE("measurementPurpose", 1),
    MEASUREMENT_PRIORITY("measurementPriority", 1),
    SHALL_MEASURE("shallMeasure", 1),
    MAX_MISSION_DURATION("maxMissionDuration", 1);
    
    private String fieldName;
    private int length;
    
    private ParameterField(String fieldName, int length) {
        this.fieldName = fieldName;
        this.length = length;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getLength() {
        return length;
    }
    
    public static ParameterField getField(Field field) {
        for (ParameterField parameterField : values()) {
            if (parameterField.fieldName.equalsIgnoreCase(field.getName())) {
                return parameterField;
            }
        }
        return null;
    }
}