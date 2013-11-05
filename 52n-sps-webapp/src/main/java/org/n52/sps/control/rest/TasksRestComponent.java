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


import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
// Rest Services
import org.n52.sps.service.rest.TaskService;

@Component
@Path("/tasks")
@Consumes("application/json")
@Produces("application/json")
public class TasksRestComponent{

	@Autowired    
	protected TaskService taskService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TasksRestComponent.class);
	
	@GET
	@Path("/procedure/{procedureId}")
	public Response getTasks(@PathParam("procedureId") String procedureId, @Context UriInfo uriInfo) {
		List<String> tasks = taskService.getTaskIds(procedureId);
		return Response.ok().entity(tasks).build();
	}
}

