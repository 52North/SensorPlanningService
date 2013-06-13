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

import java.util.List;

import net.opengis.sps.x20.CapabilitiesType;

import org.n52.ows.service.binding.HttpBinding;

public interface CapabilitiesInterceptor {

    /**
     * Enriches passed capabilities with appropriate information. This includes for example <b>Profile</b>
     * description(s) of the intercepting SPS component. Another example is adding an <b>OperationMetadata</b>
     * section describing operation information.
     * 
     * @param capabilities
     *        the capabilities to intercept.
     * @param httpBindings
     *        the supported operation bindings
     */
    public void interceptCapabilities(CapabilitiesType capabilities, List<HttpBinding> httpBindings);
}
