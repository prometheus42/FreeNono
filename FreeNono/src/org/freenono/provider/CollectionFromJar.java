/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Christian Wichmann
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
package org.freenono.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;
import org.freenono.model.Course;
import org.freenono.serializer.CourseFormatException;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.XMLCourseSerializer;
import org.freenono.serializer.ZipCourseSerializer;


/**
 * Loads a collection of courses and nonograms from a given jar file in the
 * classpath. The jar is looked up by the getResources-mechanism of the class
 * loader. Included courses are declared by a courseList file in the jar.
 * 
 * @author Christian Wichmann
 * 
 */
public class CollectionFromJar implements CollectionProvider {

	private static Logger logger = Logger.getLogger(CollectionFromJar.class);

	private String jarPath = null;
	private String providerName = null;
	private XMLCourseSerializer xmlCourseSerializer = new XMLCourseSerializer();
	private ZipCourseSerializer zipCourseSerializer = new ZipCourseSerializer();
	private List<Course> courseList = null;
	private List<CourseProvider> courseProviderList = null;

	
	public CollectionFromJar(final String jarPath, String name) {

		this.jarPath = jarPath;
		this.providerName = name;

		loadCollection();
	}

	private void loadCollection() {

		if (jarPath == null) {
			
			throw new NullPointerException("Parameter jarPath is null");
		}

		// find jar and load file list from jar
		try {
			
			Enumeration<URL> systemResources = getClass().getClassLoader()
					.getResources("nonograms/courseList");
			
			InputStream content = null;
			
			while (systemResources.hasMoreElements()) {
				
				content = systemResources.nextElement().openConnection().getInputStream();
		    }
			
			// split names from file list by line ending...
			Scanner s = new Scanner(content).useDelimiter("\n");
			
			// ...and load course from all given files
			while (s.hasNext()) {
				
				String courseFile = s.next();
				
				logger.debug("Getting course from jar: "+courseFile);
				
				loadCourse(getClass().getResource("nonograms/"+courseFile));
			}
			
		} catch (IOException e) {
			
			logger.warn("Could not load course resources from classpath.");
		}
		
		generateCourseProviderList();
	}

	private synchronized void loadCourse(URL source)
			throws FileNotFoundException {

		List<Course> lst = Collections
				.synchronizedList(new ArrayList<Course>());

		synchronized (lst) {

			try {

				Course c = null;

				if (source.getFile().endsWith("."
							+ ZipCourseSerializer.DEFAULT_FILE_EXTENSION)) {

					c = zipCourseSerializer.load(source.openStream());
				}

				if (c != null) {

					lst.add(c);
					logger.debug("loaded course \"" + source.getFile()
								+ "\" successfully");

				} else {

					logger.info("unable to load file \"" + source.getFile() + "\"");
				}

			} catch (NullPointerException e) {
				
				logger.warn("loading course \"" + source.getFile()
						+ "\" caused a NullPointerException");
				
			} catch (IOException e) {
				
				logger.warn("loading course \"" + source.getFile()
						+ "\" caused a IOException");
			}
		}

		this.courseList = lst;
	}

	
	@Override
	public synchronized List<String> getCourseList() {

		List<String> courses = new ArrayList<String>();

		for (Course c : courseList) {
			courses.add(c.getName());
		}

		return courses;
	}

	private synchronized void generateCourseProviderList() {

		logger.debug("Getting list of all CourseProvider.");

		courseProviderList = Collections
				.synchronizedList(new ArrayList<CourseProvider>());

		synchronized (courseProviderList) {

			if (courseList != null) {

				CourseProvider cp;

				for (Course c : courseList) {
					cp = new CourseFromJar(c);
					courseProviderList.add(cp);
					logger.debug("Getting CourseProvider for " + cp.toString()
							+ ".");
				}
			}
		}
	}

	@Override
	public synchronized List<CourseProvider> getCourseProvider() {

		return courseProviderList;
	}

	@Override
	public synchronized String getProviderName() {

		if (providerName == null)
			return "Filesystem: " + jarPath;
		else
			return providerName;

	}

	@Override
	public synchronized void setProviderName(String name) {

		this.providerName = name;

	}

	public synchronized void changeRootPath(String rootPath) {

		this.jarPath = rootPath;
		loadCollection();
	}

	public String toString() {

		return this.providerName;
	}

	public synchronized int getNumberOfNonograms() {

		int n = 0;

		for (CourseProvider cp : courseProviderList) {

			n += cp.getNumberOfNonograms();
		}

		return n;
	}

	public String getRootPath() {

		return jarPath;
	}

}
