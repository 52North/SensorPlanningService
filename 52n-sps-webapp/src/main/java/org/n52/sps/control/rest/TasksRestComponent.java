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

