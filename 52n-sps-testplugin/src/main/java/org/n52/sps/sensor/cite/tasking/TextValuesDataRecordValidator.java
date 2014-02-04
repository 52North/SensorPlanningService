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

import java.math.BigInteger;
import java.util.Arrays;

import net.opengis.swe.x20.CountType;
import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.QuantityType;
import net.opengis.swe.x20.VectorType;
import net.opengis.swe.x20.VectorType.Coordinate;

import org.n52.ows.exception.InvalidParameterValueException;
import org.n52.sps.tasking.TaskingRequest;
import org.n52.sps.util.encoding.EncodingException;
import org.n52.sps.util.encoding.text.TextEncoderDecoder;
import org.n52.sps.util.validation.InvalidComponentException;
import org.n52.sps.util.validation.SimpleSweComponentsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextValuesDataRecordValidator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TextValuesDataRecordValidator.class);
    
    private DataRecordType template;
    private String[][] decodedInputs;
    private SimpleSweComponentsValidator validator;

    public TextValuesDataRecordValidator(DataRecordType template, TaskingRequest taskingrequest) throws InvalidParameterValueException {
        this.template = template;
        this.decodedInputs = decodeTextInputs(taskingrequest);
        this.validator = new SimpleSweComponentsValidator();
    }
    
    /**
     * @param taskingRequest
     *        the tasking request to decode.
     * @return 2-dimensional Array containing decoded values and blocks.
     * @throws InvalidParameterValueException
     *         if the encoding used by the client is not supported by the service or the provided values are
     *         not encoded correctly. REQ 5: <a
     *         href="http://www.opengis.net/spec/SPS/2.0/req/exceptions/InvalidTaskingParameters"
     *         >http://www.opengis.net/spec/SPS/2.0/req/exceptions/InvalidTaskingParameters</a>
     */
    private String[][] decodeTextInputs(TaskingRequest taskingRequest) throws InvalidParameterValueException {
        try {
            TextEncoderDecoder encoder = taskingRequest.getTextEncoderDecoder();
            return encoder.decode(taskingRequest.getParameterData().getValues());
        }
        catch (EncodingException e) {
            throw new InvalidParameterValueException("taskingParameters");
        }
    }
    
    public DataRecordType[] getValidDataRecords() throws InvalidParameterValueException {
        DataRecordType[] validDatas = new DataRecordType[decodedInputs.length];
        for (int i = 0; i < decodedInputs.length; i++) {
            DataRecordType validData = DataRecordType.Factory.newInstance();
            validDatas[i] = parseValueBlockToValidDataRecordType(decodedInputs[i], validData);
            LOGGER.debug("valid data: {}", validDatas[i]);
        }
        return validDatas;
    }

    private DataRecordType parseValueBlockToValidDataRecordType(String[] block, DataRecordType validData) throws InvalidParameterValueException {
        LOGGER.debug("template: {}", template);
        int valueIdx = 0;
        for (Field field : template.getFieldArray()) {
            ParameterField parameterField = ParameterField.getField(field);
            if (isOptional(field)) {
                String value = block[valueIdx];
                if (isSetOptionalValue(value)) {
                    String[] parameters = parseParameterValues(parameterField, block, ++valueIdx);
                    validateAndSet(field, parameters, validData);
                    valueIdx += parameterField.getLength();
                } else {
                    // skip 'N' value
                    ++valueIdx;
                } 
            } else {
                String[] parameters = parseParameterValues(parameterField, block, valueIdx);
                validateAndSet(field, parameters, validData); 
                valueIdx += parameterField.getLength();
            }  
        }
        return validData;
    }

    private boolean isOptional(Field field) {
        return field.getAbstractDataComponent().getOptional();
    }

    private boolean isSetOptionalValue(String value) {
        return "Y".equals(value);
    }
    
    private String[] parseParameterValues(ParameterField parameterField, String[] block, int valueIdx) {
        if (parameterField != null) {
            int offset = parameterField.getLength();
            return Arrays.copyOfRange(block, valueIdx, valueIdx + offset);
        }
        return new String[0];
    }

    private void validateAndSet(Field field, String[] parameters, DataRecordType validData) throws InvalidParameterValueException {
        try {
            String name = field.getName();
            if (name.equalsIgnoreCase(ParameterField.MEASUREMENT_FREQUENCY.getFieldName())) {
                double value = Double.parseDouble(parameters[0]);
                QuantityType quantityToSet = validator.validateQuantity(field, value);
                validData.addNewField().setAbstractDataComponent(quantityToSet);
            } else if (name.equalsIgnoreCase(ParameterField.MEASUREMENT_COUNT.getFieldName())) {
                BigInteger value = BigInteger.valueOf(Long.parseLong(parameters[0]));
                CountType countToSet = validator.validateCount(field, value);
                validData.addNewField().setAbstractDataComponent(countToSet);
            } else if (name.equalsIgnoreCase(ParameterField.MEASUREMENT_LOCATION.getFieldName())) {
                VectorType value = create2DQuantityVector(parameters);
                VectorType coords = validator.validateQuantityVector(field, value);
                validData.addNewField().setAbstractDataComponent(coords);
            } else if (name.equalsIgnoreCase(ParameterField.MAX_MISSION_DURATION.getFieldName())) {
                // TODO add validation methods
            } else if (name.equalsIgnoreCase(ParameterField.MEASUREMENT_PURPOSE.getFieldName())) {
                // TODO add validation methods
            } else if (name.equalsIgnoreCase(ParameterField.SHALL_MEASURE.getFieldName())) {
                // TODO add validation methods 
            } else if (name.equalsIgnoreCase(ParameterField.MEASUREMENT_PRIORITY.getFieldName())) {
                // TODO add validation methods
            }
        } catch (InvalidComponentException e) {
            LOGGER.warn("Invalid component type.", e);
            throw new InvalidParameterValueException("taskingParameters");
        }
    }

    private VectorType create2DQuantityVector(String[] parameters) {
        VectorType vector = VectorType.Factory.newInstance();
        for (String parameter : parameters) {
            Coordinate coordinate = vector.addNewCoordinate();
            coordinate.addNewQuantity().setValue(Double.parseDouble(parameter));
        }
        return vector;
    }

}
