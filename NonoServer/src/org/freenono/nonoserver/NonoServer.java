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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.serializer.data.CourseFormatException;
import org.freenono.serializer.data.CourseSerializer;
import org.freenono.serializer.data.NonogramFormatException;
import org.freenono.serializer.data.XMLCourseSerializer;
import org.freenono.serializer.data.ZipCourseSerializer;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Router;

/**
 * NonoServer is the main class for the FreeNono server. It maps all of the
 * resources found in the file system to URLs for clients to access.
 */
public class NonoServer extends Application {

	private static Logger logger = Logger.getLogger(NonoServer.class);

	static List<Course> courseList = null;

	public final static String DEFAULT_NONOGRAM_PATH = "./nonograms";
	private CourseSerializer xmlCourseSerializer = new XMLCourseSerializer();
	private CourseSerializer zipCourseSerializer = new ZipCourseSerializer();

	// private CourseListResource courseListResource = null;
	// private NonogramListResource nonogramListResource = null;
	// private NonogramResource nonogramResource = null;

	/**
	 * Creates a new NonoServer object.
	 */
	public NonoServer() {

		try {
			loadCourses(new File(DEFAULT_NONOGRAM_PATH));
		} catch (FileNotFoundException e) {
			logger.warn("No nonograms found at default nonogram directory!");
		}

	}

	private void loadCourses(File dir) throws FileNotFoundException {

		if (!dir.isDirectory()) {
			throw new FileNotFoundException("Parameter is no directory");
		}
		if (!dir.exists()) {
			throw new FileNotFoundException("Specified directory not found");
		}

		List<Course> lst = new ArrayList<Course>();

		for (File file : dir.listFiles()) {

			try {

				Course c = null;

				if (!file.getName().startsWith(".")) {

					if (file.isDirectory()) {

						c = xmlCourseSerializer.load(file);

					} else {

						if (file.getName()
								.endsWith(
										"."
												+ ZipCourseSerializer.DEFAULT_FILE_EXTENSION)) {
							c = zipCourseSerializer.load(file);
						}

					}

					if (c != null) {

						lst.add(c);
						logger.debug("loaded course \"" + file
								+ "\" successfully");

					} else {

						logger.info("unable to load file \"" + file + "\"");

					}
				}

			} catch (NullPointerException e) {
				logger.warn("loading course \"" + file
						+ "\" caused a NullPointerException");
			} catch (IOException e) {
				logger.warn("loading course \"" + file
						+ "\" caused a IOException");
			} catch (NonogramFormatException e) {
				logger.warn("loading course \"" + file
						+ "\" caused a NonogramFormatException");
			} catch (CourseFormatException e) {
				logger.warn("loading course \"" + file
						+ "\" caused a CourseFormatException");
			}
		}

		NonoServer.courseList = lst;
	}

	public NonoServer(Context parentContext) {
		super(parentContext);
	}

	/**
	 * The Restlet instance that will call the correct resource depending up on
	 * URL mapped to it.
	 * 
	 * @return -- The resource Restlet mapped to the URL.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Restlet createRoot() {

		Router router = new Router(getContext());
		router.setRoutingMode(Router.BEST);

		Restlet hint = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				response.setEntity("What is...", MediaType.TEXT_PLAIN);
			}
		};
		router.attach("/42", hint);

		// CourseListResource courseListResource = new
		// CourseListResource(courseList);
		// NonogramListResource nonogramListResource = new
		// NonogramListResource(courseList);
		// NonogramResource nonogramResource = new NonogramResource(courseList);

		router.attach("/courseList", CourseListResource.class);
		router.attach("/{course}/nonogramList", NonogramListResource.class);
		router.attach("/{course}", NonogramListResource.class);
		router.attach("/seed", RandomNonogramResource.class);
		router.attach("/seed/{seed}", RandomNonogramResource.class);
		router.attach("/{course}/{nonogram}", NonogramResource.class);

		Restlet helppage = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				StringBuilder stringBuilder = new StringBuilder();

				stringBuilder.append("<html>");
				stringBuilder.append("<head><title>NonoServer</title></head>");
				stringBuilder.append("<body bgcolor=white>");
				stringBuilder.append("<table border=\"0\">");
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append("<h3>NonoServer - A FreeNono server</h3>");
				stringBuilder
						.append("<ol><li>/courseList --> returns a list of all available courses.</li>");
				stringBuilder
						.append("<li>/{course}/ --> returns a list of all nonograms for the given course.</li>");
				stringBuilder
						.append("<li>/{course}/{nonogram} --> returns the given nonogram from a course.</li>");
				stringBuilder
						.append("<li>/{course}/random --> returns a randomly chosen nonogram in each course.</li>");
				stringBuilder
						.append("<li>/seed/{seed} --> returns a randomly generated nonogram by seed.</li>");
				stringBuilder.append("<li>/ --> returns this help page.</li>");
				stringBuilder.append("</ol>");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
				stringBuilder.append("</table>");
				stringBuilder.append("</body>");
				stringBuilder.append("</html>");

				response.setEntity(new StringRepresentation(stringBuilder
						.toString(), MediaType.TEXT_HTML));
			}
		};
		router.attach("/", helppage);

		return router;
	}

}
