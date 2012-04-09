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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CourseProvider;
import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.Course;
import org.restlet.resource.ResourceException;

public class CourseFromServer implements CourseProvider {

	private static Logger logger = Logger.getLogger(CourseFromServer.class);

	private String courseName = null;
	private ServerProviderHelper serverProviderHelper = null;
	private List<String> nonogramList = null;
	private List<NonogramProvider> nonogramProviderList = null;

	public CourseFromServer(String courseName,
			ServerProviderHelper serverProviderHelper) {

		this.courseName = courseName;
		this.serverProviderHelper = serverProviderHelper;

		prepareNonogramProviders();
	}

	private void prepareNonogramProviders() {

		logger.debug("Preparing all NonogramProviders.");

		nonogramProviderList = new ArrayList<NonogramProvider>();

		// create nonogramProvider
		List<String> nonogramList = null;
		try {
			nonogramList = serverProviderHelper.getNonogramList(courseName);
		} catch (ResourceException e) {
			logger.error("Server under given URL not responding.");
		} catch (IOException e) {
			logger.error("Server under given URL not responding.");
		}
		for (String n : nonogramList) {
			nonogramProviderList.add(new NonogramFromServer(n, courseName,
					serverProviderHelper));
		}
	}

	@Override
	public List<String> getNonogramList() {

		return nonogramList;
	}

	@Override
	public Collection<NonogramProvider> getNonogramProvider() {

		return nonogramProviderList;
	}

	@Override
	public Course fetchCourse() {

		// TODO: implement the build of a course class!
		return null;
	}

	public String toString() {

		return courseName;
	}

	@Override
	public String getCourseName() {

		return courseName;
	}
}
