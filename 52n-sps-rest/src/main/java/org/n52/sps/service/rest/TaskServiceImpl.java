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

package org.n52.sps.service.rest;

import java.util.ArrayList;
import java.util.List;

import org.n52.sps.store.SensorTaskRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TaskServiceImpl implements TaskService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    
	private SensorTaskRepository sensorTaskRepository;
	
	
	public SensorTaskRepository getSensorTaskRepository() {
		return sensorTaskRepository;
	}
	public void setSensorTaskRepository(SensorTaskRepository sensorTaskRepository) {
		this.sensorTaskRepository = sensorTaskRepository;
	}
	

	private List<String> iterableToArray(Iterable<String> iterableString){
		ArrayList<String> array = new ArrayList<String>();
   		for (String iter : iterableString ) {
   			array.add(iter);
   		 }
   		return array;
	}
	
	 public List<String> getTaskIds(String procedure){
	    	Iterable<String> iterableSensorTasks = sensorTaskRepository.getSensorTaskIds(procedure);
	   	    return iterableToArray(iterableSensorTasks);
	 }
}
