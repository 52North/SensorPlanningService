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
