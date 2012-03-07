/*****************************************************************************
 * NonoServer - A FreeNono server
 * Copyright (c) 2012 Christian Wichmann
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.freenono.model.Course;
import org.freenono.model.Nonogram;
import org.freenono.serializer.XMLNonogramSerializer;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class NonogramResource extends ServerResource {

	private static Logger logger = Logger.getLogger(XMLNonogramSerializer.class);

	private List<Course> courseList = NonoServer.courseList;

	@Get
	public void handleGet() {

		String result = null;
		String courseName = (String) getRequest().getAttributes().get("course");
		String nonogramName = (String) getRequest().getAttributes().get(
				"nonogram");

		Course pickedCourse = null;
		Nonogram pickedNonogram = null;

		// find course the user is searching for
		for (Course c : courseList) {
			if (c.getName().equals(courseName))
				pickedCourse = c;
		}

		// if entered course exists...
		if (pickedCourse != null) {
			// ...find nonogram the user is searching for
			for (Nonogram n : pickedCourse.getNonograms()) {
				if (n.getName().equals(nonogramName))
					pickedNonogram = n;
			}

			// if entered nanogram in course exists...
			if (pickedNonogram != null) {
				// ...serialize picked nonogram

				result = "<nonogram name=\"" + pickedNonogram.getName()
						+ "\" />";

				getResponse().setEntity(result, MediaType.TEXT_XML);
				getResponse().setStatus(Status.SUCCESS_OK);
			}

		}

		// if (pickedCourse == null || pickedNonogram == null) {
		// result = "<html><body>nonogram \"" + nonogramName
		// + "\" in course \"" + courseName
		// + "\" not found! </body></html>";
		// getResponse().setEntity(result, MediaType.TEXT_HTML);
		// getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		// }
		
		try {
			getResponse().setEntity(readFile("./nonograms/Testing/Kicker.nonogram"), MediaType.TEXT_HTML);
		} catch (IOException e) {
			logger.error("Could not read nonogram file.");
		}
		getResponse().setStatus(Status.SUCCESS_OK);

	}

	private String readFile(String pathname) throws IOException {

		File file = new File(pathname);
		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}
	
	@Post
	public String handlePost() {
		return "Not yet implemented!";
	}

	@Put
	public String handlePut() {
		return "Not yet implemented!";
	}

	@Delete
	public String handleDelete() {
		return "Not yet implemented!";
	}

}
