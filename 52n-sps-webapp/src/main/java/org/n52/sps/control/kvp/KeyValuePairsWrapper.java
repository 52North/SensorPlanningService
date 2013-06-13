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
package org.n52.sps.control.kvp;

import java.util.HashMap;
import java.util.Map;

import org.n52.ows.exception.MissingParameterValueException;

/**
 * Provides convenient methods for a consistent work with KVPs. 
 */
public class KeyValuePairsWrapper {
    
    private Map<String,String> parameterNamesUpperCase;

    public KeyValuePairsWrapper(final Map<String, String> parameters) {
        this.parameterNamesUpperCase = convertParametersToUpperCaseKeysMap(parameters);
    }
    
    private Map<String, String> convertParametersToUpperCaseKeysMap(Map<String, String> parameters) {
        Map<String,String> upperKeysMap = new HashMap<String, String>();
        for (String parameter : parameters.keySet()) {
            String parameterValue = parameters.get(parameter);
            upperKeysMap.put(parameter.toUpperCase(), parameterValue);
        }
        return upperKeysMap;
    }
    
    public String getMandatoryParameterValue(String parameterName) throws MissingParameterValueException {
        String value = getTrimmedParameterValue(parameterName);
        if (value == null) {
            throw new MissingParameterValueException(parameterName);
        }
        return value;
    }
    
    public String getTrimmedParameterValue(String parameterName) {
        String upperCaseParameter = parameterName.toUpperCase();
        String value = parameterNamesUpperCase.get(upperCaseParameter);
        return value == null ? null : value.trim();
    }
    
}
