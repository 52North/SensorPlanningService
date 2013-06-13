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

import net.opengis.sps.x20.CapabilitiesDocument;
import net.opengis.sps.x20.GetCapabilitiesDocument;

import org.n52.ows.exception.OwsException;
import org.n52.ows.service.binding.HttpBinding;
import org.n52.sps.service.admin.SpsAdmin;
import org.n52.sps.service.cancel.TaskCanceller;
import org.n52.sps.service.core.BasicSensorPlanner;
import org.n52.sps.service.core.SensorProvider;
import org.n52.sps.service.description.SensorDescriptionManager;
import org.n52.sps.service.feasibility.FeasibilityController;
import org.n52.sps.service.reservation.ReservationManager;
import org.n52.sps.service.update.TaskUpdater;


/**
 * Provides access to SPS relevant interface components. {@link BasicSensorPlanner} and {@link SensorProvider}
 * are mandatory interfaces to fulfill SPS Core profile. The other components may return <code>null</code>, if
 * the SPS implementation does not provides such component (namely {@link FeasibilityController}, 
 * {@link ReservationManager}, {@link SensorDescriptionManager}, {@link TaskCanceller}, and 
 * {@link TaskUpdater})
 */
public interface SensorPlanningService {
    
    public static final String SPS_VERSION = "2.0.0";
    
    public static final String OWS_VERSION = "1.1.0";

    // mandatory
    public BasicSensorPlanner getBasicSensorPlanner();

    // mandatory
    public SensorProvider getSensorProvider();

    // optional
    public FeasibilityController getFeasibilityController();

    // optional
    public ReservationManager getReservationManager();

    // optional
    public SensorDescriptionManager getSensorDescriptionManager();

    // optional
    public TaskCanceller getTaskCanceller();

    // optional
    public TaskUpdater getTaskUpdater();
    
    // optional/custom
    public SpsAdmin getSpsAdmin();
    
    /**
     * Method intiating a shutdown of all SPS components.
     */
    public void shutdown();
    
    public void validateGetCapabilitiesParameters(GetCapabilitiesDocument getCapabilities) throws OwsException;
    
    public void validateMandatoryServiceParameter(String serviceParameter) throws OwsException;
    
    public void validateVersionParameter(String version) throws OwsException;
    
    /**
     * @param httpBinding supported DCP binding.
     */
    public void addHttpBinding(HttpBinding httpBinding);
    
    /**
     * @return <code>true</code> if http://www.opengis.net/spec/SPS/2.0/conf/FeasibilityController profile is supported, <code>false</code> otherwise.
     */
    public boolean isSupportingFeasibilityControllerProfile();

    /**
     * @return <code>true</code> if http://www.opengis.net/spec/SPS/2.0/conf/ReservationManager profile is supported, <code>false</code> otherwise.
     */
    public boolean isSupportingReservationManagerProfile();

    /**
     * @return <code>true</code> if http://www.opengis.net/spec/SPS/2.0/conf/SensorHistoryProvider profile is supported, <code>false</code> otherwise.
     */
    public boolean isSupportingSensorHistoryProviderProfile();

    /**
     * @return <code>true</code> if http://www.opengis.net/spec/SPS/2.0/conf/TaskCanceller profile is supported, <code>false</code> otherwise.
     */
    public boolean isSupportingTaskCancellerProfile();
    
    /**
     * @return <code>true</code> if http://www.opengis.net/spec/SPS/2.0/conf/TaskUpdater profile is supported, <code>false</code> otherwise.
     */
    public boolean isSupportingTaskUpdaterProfile();
    
    
    /**
     * @return <code>true</code> if an SPS administratin interface is available.
     */
    public boolean isSpsAdminAvailable();
    
    /**
     * Intercepts the passed {@link CapabilitiesDocument} with service related setup (e.g. OperationsMetadata, supported Profiles, etc.) info.
     * 
     * @param capabilities the capabilities to intercept.
     */
    public void interceptCapabilities(CapabilitiesDocument capabilities);


}
