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

import java.util.ArrayList;
import java.util.List;

import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;


public class CollectionFromSeed implements CollectionProvider {

	private String providerName = null;
	private List<String> courseList = null;
	private List<CourseProvider> courseProviderList = null;
	
	public CollectionFromSeed(String name) {
		this.providerName = name;

		courseProviderList = new ArrayList<CourseProvider>();
		courseProviderList.add(new CourseFromSeed());
		courseList = new ArrayList<String>();
		courseList.add(courseProviderList.get(0).toString());	
	}
	
	@Override
	public List<String> getCourseList() {
		return courseList;
	}

	@Override
	public String getProviderName() {
		return providerName;
	}

	@Override
	public List<CourseProvider> getCourseProvider() {
		return courseProviderList;
	}

	@Override
	public void setProviderName(String name) {
		this.providerName = name;
	}

}
