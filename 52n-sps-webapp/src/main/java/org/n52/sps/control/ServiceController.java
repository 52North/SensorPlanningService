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

package org.n52.sps.control;

import javax.servlet.http.HttpServletResponse;

import org.n52.ows.exception.OwsException;
import org.n52.ows.exception.OwsExceptionReport;
import org.n52.ows.exception.ServiceException;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.sps.service.SensorPlanningService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.servlet.ModelAndView;

public abstract class ServiceController  implements DisposableBean {

    protected SensorPlanningService service;

    protected HttpBinding binding;

    public ServiceController(SensorPlanningService service, HttpBinding httpBinding) {
        this.service = service;
        this.binding = httpBinding;
        if ( !httpBinding.isExtension()) {
            service.addHttpBinding(httpBinding);
        }
    }

    // protected void handleServiceException(HttpServletResponse response, ModelAndView mav, OwsException e) {
    // ServiceException exception = ServiceException.createFromOwsException(e);
    // mav.addObject(exception.getExceptionReport());
    // response.setStatus(e.getHttpStatusCode());
    // }

    /**
     * Handles OwsExceptions which occured during request processing. If no exceptions are available within
     * the {@link OwsExceptionReport} instance the method leaves the passed {@link ModelAndView} untouched.
     * 
     * @param response
     *        the actual HTTP response to set an HTTP status on. XXX this is only required by OWS 2.0, but
     *        let's see if we can provide it also (if CITE TEAM engine allows it).
     * @param mav
     *        model and view to be responded depending on Spring configuration.
     * @param exceptionReport
     *        the container all {@link OwsException}s has been collected to be thrown together as OWS
     *        Exception Report (according to chapter 8 of [OGC 06-121r3]).
     */
    protected void handleServiceExceptionReport(HttpServletResponse response,
                                                ModelAndView mav,
                                                OwsExceptionReport exceptionReport) {
        ServiceException exception = ServiceException.createFromOwsExceptionReport(exceptionReport);
        exception.setVersion(SensorPlanningService.OWS_VERSION);
        if (exception.containsExceptions()) {
            mav.addObject(exception.getExceptionReport());
            // response.setStatus(OwsException.BAD_REQUEST); // only required by OWS 2.0
        }
    }

}
