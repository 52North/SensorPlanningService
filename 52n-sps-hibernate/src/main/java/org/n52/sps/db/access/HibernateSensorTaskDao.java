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
import org.n52.sps.sensor.model.SensorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateSensorTaskDao extends HibernateDaoSupport implements SensorTaskDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSensorTaskDao.class);

    public SensorTask getInstance(String taskId) {
        LOGGER.debug("getInstance({})", taskId);
        Query query = getCurrentSession().createQuery("from SensorTask as st where st.taskId = :taskId");
        query.setParameter("taskId", taskId);
        return (SensorTask) query.uniqueResult();
    }

    public Iterable<SensorTask> getAllInstances() {
        LOGGER.debug("getAllInstances()");
        return getCurrentSession().createQuery("from SensorTask").list();
    }

    public void saveInstance(SensorTask instance) {
        LOGGER.debug("saveInstance({})", instance);
        getCurrentSession().save(instance);
    }

    public void updateInstance(SensorTask instance) {
        LOGGER.debug("updateInstance({})", instance);
        getCurrentSession().update(instance);
    }

    public void deleteInstance(SensorTask instance) {
        LOGGER.debug("deleteInstance({})", instance);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    public SensorTask findBy(String procedure, String taskId) {
        LOGGER.debug("findBy({}, {})", procedure, taskId);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    public Iterable<SensorTask> findByTaskId(String taskId) {
        LOGGER.debug("findByTaskId({})", taskId);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    public long getCount(String procedure) {
        LOGGER.debug("getCount({})", procedure);
        return ((Long)getCurrentSession().createQuery("select count(st) from SensorTask st").uniqueResult()).longValue();
    }

    private Session getCurrentSession() {
        return getHibernateTemplate().getSessionFactory().getCurrentSession();
    }
}
