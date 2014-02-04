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
