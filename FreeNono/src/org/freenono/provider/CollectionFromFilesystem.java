/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;
import org.freenono.model.Course;
import org.freenono.serializer.CourseFormatException;
import org.freenono.serializer.CourseSerializer;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.XMLCourseSerializer;
import org.freenono.serializer.ZipCourseSerializer;

// TODO: make this class iterable to iterate over courses in collection.
public class CollectionFromFilesystem implements CollectionProvider {

	private static Logger logger = Logger
			.getLogger(CollectionFromFilesystem.class);

	private String rootPath = null;
	private String providerName = null;
	private CourseSerializer xmlCourseSerializer = new XMLCourseSerializer();
	private CourseSerializer zipCourseSerializer = new ZipCourseSerializer();
	private List<Course> courseList = null;
	private List<CourseProvider> courseProviderList = null;

	
	public CollectionFromFilesystem(final String rootPath, String name) {

		this.rootPath = rootPath;
		this.providerName = name;

		loadCollection();
	}


	private synchronized void loadCollection() {
		
		if (rootPath == null) {
			throw new NullPointerException("Parameter rootPath is null");
		}
		
		// load files in separate thread
		Thread loadThread = new Thread() {
			public void run() {
				try {
					loadCourses(new File(rootPath));
				} catch (FileNotFoundException e) {
					
					logger.warn("No nonograms found at directory: " + rootPath);
				}
				generateCourseProviderList();

			}
		};
		loadThread.setDaemon(true);
		loadThread.start();
	}

	
	private synchronized void loadCourses(File dir) throws FileNotFoundException {

		if (!dir.isDirectory()) {
			throw new FileNotFoundException("Parameter is no directory");
		}
		if (!dir.exists()) {
			throw new FileNotFoundException("Specified directory not found");
		}

		List<Course> lst = new ArrayList<Course>();
		//List<Course> lst = Collections.synchronizedList(ArrayList<Course>);

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

		courseProviderList = new ArrayList<CourseProvider>();

		if (courseList != null) {

			CourseProvider cp;

			for (Course c : courseList) {
				cp = new CourseFromFilesystem(c);
				courseProviderList.add(cp);
				logger.debug("Getting CourseProvider for " + cp.toString()
						+ ".");
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
			return "Filesystem: " + rootPath;
		else
			return providerName;

	}

	@Override
	public synchronized void setProviderName(String name) {

		this.providerName = name;

	}
	
	public synchronized void changeRootPath(String rootPath) {
		
		this.rootPath = rootPath;
		loadCollection();
	}
	
	public String toString() {
		
		return this.providerName; // + " (" + rootPath + ")";
	}

	public synchronized int getNumberOfNonograms() {
		
		int n = 0;
		
		for (CourseProvider cp : courseProviderList) {
			
			n += cp.getNumberOfNonograms();
		}
		
		return n;
	}

	public String getRootPath() {
		
		return rootPath;
	}

}
