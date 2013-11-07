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

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.n52.sps.service.rest.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/tasks")
public class TasksRestComponent {

    private TaskService taskService;
    
    /*
     *  TODO currently every user is able to get information about a procedure's
     *  tasks which is NOT always a good thing. Knowing the procedure might be 
     *  security by obscurity for now, but should be reconsidered later to fix
     *  this possible security issue.
     */
    @RequestMapping(method = GET, produces = {"application/json"})
    public ModelAndView getTasks(@RequestParam("procedureId") String procedureId) {
        List<String> tasks = taskService.getTaskIds(procedureId);
        return new ModelAndView().addObject(tasks);
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
    
    
}
