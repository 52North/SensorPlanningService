/**
 * Copyright (C) 2013
 * by Instituto Tecnológico de Galicia
 *
 * Contact: Carlos Giraldo
 * 52 Instituto Tecnológico de Galicia
 * PO.CO.MA.CO. Sector i, Portal 5 15190, A Coruña
 * A Coruña, Spain
 * cgiraldo@5itg.es
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
package org.n52.sps.control.rest;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;


/**
 * Spring HelloWorld Web Application configuration.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class SpsJerseyApplication extends ResourceConfig {

    /**
     * Register JAX-RS application components.
     */
    public SpsJerseyApplication () {
    	//register(RequestContextFilter.class);
        register(TasksRestComponent.class);
    }
}
