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
