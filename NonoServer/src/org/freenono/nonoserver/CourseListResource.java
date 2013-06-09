/*****************************************************************************
 * NonoServer - A FreeNono server
 * Copyright (c) 2013 by FreeNono Development Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/
package org.freenono.nonoserver;

import java.util.List;

import org.freenono.model.Course;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;


/**
 * 
 * @author Christian Wichmann
 */
public class CourseListResource extends ServerResource {
	
	//private static Logger logger = Logger.getLogger(CourseListResource.class);

	private List<Course> courseList = NonoServer.courseList;

	@Get
	public void handleGet(Request request, Response response) {
		
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		stringBuilder.append("<FreeNono>");
		for (Course c: courseList) {
			stringBuilder.append("<Course name=\"");
			stringBuilder.append(c.getName());
			stringBuilder.append("\" path =\"");
			stringBuilder.append(c.getName());
			stringBuilder.append("\" />");
		}
		stringBuilder.append("</FreeNono>");

		getResponse().setEntity(new StringRepresentation(stringBuilder
				.toString(), MediaType.TEXT_XML));
		getResponse().setStatus(Status.SUCCESS_OK);
				
	}

	@Post
	public String handlePost(Request request, Response response) {
		return "Not yet implemented!";
	}

	@Put
	public String handlePut(Request request, Response response) {
		return "Not yet implemented!";
	}

	@Delete
	public String handleDelete(Request request, Response response) {
		return "Not yet implemented!";
	}

}
