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
package org.n52.sps.db.access;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.n52.sps.sensor.model.SensorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateSensorConfigurationDao extends HibernateDaoSupport implements SensorConfigurationDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSensorConfigurationDao.class);
    
    public SensorConfiguration getInstance(String procedure) {
        LOGGER.debug("getInstance({}}", procedure);
        Session session = getCurrentSession();
        String queryString = "from SensorConfiguration where procedure = :procedure";
        Query query = session.createQuery(queryString).setParameter("procedure", procedure);
        return (SensorConfiguration) query.uniqueResult();
    }

    public Iterable<SensorConfiguration> getAllInstances() {
        LOGGER.debug("getAllInstance()");
        return getCurrentSession().createQuery("from SensorConfiguration").list();
    }

    public void saveInstance(SensorConfiguration instance) {
        LOGGER.debug("saveInstance({})", instance);
        getCurrentSession().save(instance);
    }
    

    public void updateInstance(SensorConfiguration instance) {
        LOGGER.debug("updateInstance({})", instance);
        getCurrentSession().update(instance);
    }

    public void deleteInstance(SensorConfiguration instance) {
        LOGGER.debug("deleteInstance({})", instance);
        getCurrentSession().delete(instance);
    }

    public long getCount(String procedure) {
        LOGGER.debug("getCount({})", procedure);
        Session currentSession = getCurrentSession();
        Query query = currentSession.createQuery("select count(*) from SensorConfiguration");
        return ((Long) query.iterate().next()).longValue();
    }

    private Session getCurrentSession() {
        return getHibernateTemplate().getSessionFactory().getCurrentSession();
    }

}
