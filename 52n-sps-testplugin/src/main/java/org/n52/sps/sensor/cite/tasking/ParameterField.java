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