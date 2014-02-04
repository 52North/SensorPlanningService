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
package org.n52.sps.sensor.model;

public class SensorDescription {
    
    private Long id; // database id

    private String procedureDescriptionFormat;
    
    private String downloadLink;

    public SensorDescription(String procedureDescriptionFormat, String downloadLink) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
        this.downloadLink = downloadLink;
    }
    
    SensorDescription() {
        // db serialization
    }
    
    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SensorDescription [ ");
        sb.append("procedureDescriptionFormat: ").append(procedureDescriptionFormat);
        sb.append(" downloadLink: ").append(downloadLink).append(" ]");
        return sb.toString();
        
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (downloadLink == null) ? 0 : downloadLink.hashCode());
        result = prime * result + ( (procedureDescriptionFormat == null) ? 0 : procedureDescriptionFormat.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensorDescription other = (SensorDescription) obj;
        if (downloadLink == null) {
            if (other.downloadLink != null)
                return false;
        }
        else if ( !downloadLink.equals(other.downloadLink))
            return false;
        if (procedureDescriptionFormat == null) {
            if (other.procedureDescriptionFormat != null)
                return false;
        }
        else if ( !procedureDescriptionFormat.equals(other.procedureDescriptionFormat))
            return false;
        return true;
    }
    
}
