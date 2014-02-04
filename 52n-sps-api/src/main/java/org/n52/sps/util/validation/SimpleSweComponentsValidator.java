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
package org.n52.sps.util.validation;

import java.math.BigInteger;
import java.util.List;

import net.opengis.swe.x20.AllowedValuesType;
import net.opengis.swe.x20.CountType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.QuantityType;
import net.opengis.swe.x20.VectorType;
import net.opengis.swe.x20.VectorType.Coordinate;

public class SimpleSweComponentsValidator {

    public QuantityType validateQuantity(Field quantityField, double value) throws InvalidComponentException {
        return validateQuantity(getQuantity(quantityField), value);
    }

    public QuantityType validateQuantity(QuantityType quantityTemplate, double value) throws InvalidComponentException {
        if (quantityTemplate.isSetConstraint()) {
            AllowedValuesType allowedValues = quantityTemplate.getConstraint().getAllowedValues();
            List<Double> intervalArray = allowedValues.getIntervalArray(0);
            validateAgainstInterval(value, intervalArray);
            
            // TODO validate value
            // TODO validate significantFigure
        }
        QuantityType quantityToSet = (QuantityType) quantityTemplate.copy();
        quantityToSet.setValue(value);
        return quantityToSet;
    }

    public CountType validateCount(Field countField, BigInteger value) throws InvalidComponentException {
        return validateCount(getCount(countField), value);
    }

    public CountType validateCount(CountType countTemplate, BigInteger value) throws InvalidComponentException {
        if (countTemplate.isSetConstraint()) {
            AllowedValuesType allowedValues = countTemplate.getConstraint().getAllowedValues();
            List<BigInteger> intervalArray = allowedValues.getIntervalArray(0);
            validateAgainstInterval(value, intervalArray);

            // TODO validate value
            // TODO validate significantFigure
        }        
        CountType countToSet = (CountType) countTemplate.copy();
        countToSet.setValue(value);
        return countToSet;
    }
    
    public VectorType validateQuantityVector(Field vectorField, VectorType value) throws InvalidComponentException {
        return validateQuantityVector(getVector(vectorField), value);
    }

    public VectorType validateQuantityVector(VectorType vectorTemplate, VectorType value) throws InvalidComponentException {
        Coordinate[] coords = vectorTemplate.getCoordinateArray();
        Coordinate[] coordsToCheck = value.getCoordinateArray();
        if (coords.length != coordsToCheck.length) {
            throw new InvalidComponentException("Different vector dimensions");
        }
        for (int i = 0 ; i < coords.length ; i++) {
            validateQuantity(coords[i].getQuantity(), coordsToCheck[i].getQuantity().getValue());
        }
        VectorType vectorToSet = (VectorType) vectorTemplate.copy();
        vectorToSet.setCoordinateArray(coordsToCheck);
        return vectorToSet;
    }

    public void validateAgainstInterval(double value, List<Double> interval) throws InvalidComponentException {
        Double intervalBegin = interval.get(0);
        Double intervalEnd = interval.get(1);
        if (Double.compare(value, intervalBegin) <= 0 && Double.compare(value, intervalEnd) >= 0) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append(value)
                    .append(" is not in range [")
                    .append(intervalBegin)
                    .append(",")
                    .append(intervalEnd)
                    .append("].");
            throw new InvalidComponentException(errorMsg.toString());
        }
    }

    private void validateAgainstInterval(BigInteger value, List<BigInteger> interval) throws InvalidComponentException {
        BigInteger intervalBegin = interval.get(0);
        BigInteger intervalEnd = interval.get(1);
        if (intervalBegin.subtract(value).intValue() <= 0 && value.subtract(intervalEnd).intValue() >= 0) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append(value)
                    .append(" is not in range [")
                    .append(intervalBegin)
                    .append(",")
                    .append(intervalEnd)
                    .append("].");
            throw new InvalidComponentException(errorMsg.toString());
        }
    }

    public QuantityType getQuantity(Field field) {
        return (QuantityType)field.getAbstractDataComponent();
    }
    
    public CountType getCount(Field field) {
        return (CountType)field.getAbstractDataComponent();
    }

    public VectorType getVector(Field field) {
        return (VectorType)field.getAbstractDataComponent();
    }


}
