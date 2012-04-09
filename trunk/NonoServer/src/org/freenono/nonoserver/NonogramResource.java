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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.Course;
import org.freenono.model.Nonogram;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.XMLNonogramSerializer;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class NonogramResource extends ServerResource {

	private static Logger logger = Logger.getLogger(NonogramResource.class);

	private List<Course> courseList = NonoServer.courseList;

	@Get
	public void handleGet() {

		String result = null;
		String courseName = Reference.decode((String) getRequest()
				.getAttributes().get("course"));
		String nonogramName = Reference.decode((String) getRequest()
				.getAttributes().get("nonogram"));

		Course pickedCourse = null;
		Nonogram pickedNonogram = null;

		// find course the user is looking for
		for (Course c : courseList) {
			if (c.getName().equals(courseName))
				pickedCourse = c;
		}

		// if entered course exists...
		if (pickedCourse != null) {
			// ...find nonogram the user is looking for
			for (Nonogram n : pickedCourse.getNonograms()) {
				if (n.getName().equals(nonogramName))
					pickedNonogram = n;
			}

			// if entered nanogram in course exists...
			if (pickedNonogram != null) {
				// ...serialize picked nonogram
				XMLNonogramSerializer ns = new XMLNonogramSerializer();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					ns.save(baos, pickedNonogram);
				} catch (NullPointerException e) {
					logger.error("Null pointer encountered during nonogram serializing.");
				} catch (IOException e) {
					logger.error("Could not write serialized nonogram to output stream.");
				}

				result = baos.toString();

				getResponse().setEntity(result, MediaType.TEXT_XML);
				getResponse().setStatus(Status.SUCCESS_OK);
			} else {
				getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			}
		}
	}

	@Post
	public String handlePost() {
		return "Not yet implemented!";
	}

	@Put
	public void handlePut(Representation nonogramStream) {

		Course pickedCourse = null;
		Nonogram result[] = null;

		String courseName = Reference.decode((String) getRequest()
				.getAttributes().get("course"));
		String nonogramName = Reference.decode((String) getRequest()
				.getAttributes().get("nonogram"));

		// load xml stream from network
		XMLNonogramSerializer ns = new XMLNonogramSerializer();
		try {
			result = ns.load(nonogramStream.getStream());
		} catch (NullPointerException e) {
			logger.error("Null pointer encountered during nonogram serializing.");
		} catch (NonogramFormatException e) {
			logger.error("Invalid nonogram file format.");
		} catch (IOException e) {
			logger.error("An error occurred during reading of nonogram stream.");
		}

		if (result != null) {

			logger.debug("Adding new nonogram to server repo.");

			// find course the user is looking for
			for (Course c : courseList) {
				if (c.getName().equals(courseName))
					pickedCourse = c;
			}

			// if course does not yet exists...
			if (pickedCourse == null) {

				// ...create course and add nonogram
				List<Nonogram> newList = new ArrayList<Nonogram>();
				newList.add(result[0]);
				courseList.add(new Course(courseName, newList));
			} else {

				// ...or simply add nonogram to existing course
				pickedCourse.addNonogram(result[0]);
			}

			// save new nonogram to file
			try {
				File nonogramFile = new File(new File(new File(
						NonoServer.DEFAULT_NONOGRAM_PATH), courseName),
						nonogramName + ".nonogram");
				ns.save(nonogramFile, result);
			} catch (NullPointerException e) {
				logger.error("Null pointer encountered during nonogram serializing.");
			} catch (IOException e) {
				logger.error("An error occurred during writing of nonogram stream.");
			}
		}
	}

	@Delete
	public void handleDelete() {

		String courseName = Reference.decode((String) getRequest()
				.getAttributes().get("course"));
		String nonogramName = Reference.decode((String) getRequest()
				.getAttributes().get("nonogram"));

		Course pickedCourse = null;
		Nonogram pickedNonogram = null;

		// find course the user is looking for
		for (Course c : courseList) {
			if (c.getName().equals(courseName))
				pickedCourse = c;
		}

		// if entered course exists...
		if (pickedCourse != null) {
			// ...find nonogram the user is looking for
			for (Nonogram n : pickedCourse.getNonograms()) {
				if (n.getName().equals(nonogramName))
					pickedNonogram = n;
			}
		}

		// delete nonogram from course
		pickedCourse.removeNonogram(pickedNonogram);

		// delete nonogram from file system
		File nonogramFile = new File(new File(new File(
				NonoServer.DEFAULT_NONOGRAM_PATH), courseName), nonogramName
				+ ".nonogram");
		nonogramFile.delete();

	}
}
