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
package org.n52.sps.service;

import java.util.ArrayList;
import java.util.List;

public abstract class InternalServiceException extends Exception {
    
    private static final long serialVersionUID = -1598594769157092219L;
    
    public static final int BAD_REQUEST = 400;
    
    private List<String> detailedMessages = new ArrayList<String>();
    private String exceptionCode;
    private String locator;

    public InternalServiceException(String exceptionCode, String locator) {
        this.exceptionCode = exceptionCode;
        this.locator = locator;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public void addDetailedMessage(String detailedMessage) {
        detailedMessages.add(detailedMessage);
    }
    
    public Iterable<String> getDetailedMessages() {
        return detailedMessages;
    }

    public abstract int getHttpStatusCode();

    
}
