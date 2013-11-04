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
