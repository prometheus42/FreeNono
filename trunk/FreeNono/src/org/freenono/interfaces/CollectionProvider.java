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
package org.freenono.interfaces;

import java.util.List;

/**
 * This class provides access to a collection of courses and nonograms. The only
 * parameter is the given name of the resource.
 * 
 * @author Christian Wichmann
 * 
 */
// TODO: make class iterable!
public interface CollectionProvider {

	/**
	 * Gives back only a list of strings with the names of the courses. For this
	 * function no actual nonogram file has to be read. E.g. at the
	 * NonogramFromFilesystem provider, only the directory names are given.
	 * 
	 * @return List of Names for all courses.
	 */
	public List<String> getCourseList();

	/**
	 * Provides a list of handlers for all included courses.
	 * 
	 * @return
	 */
	public List<CourseProvider> getCourseProvider();

	/**
	 * Returns the given name for this collection resource.
	 * 
	 * @return
	 */
	public String getProviderName();

	/**
	 * Identifies this collection resource by a given name, which will be shown
	 * in the UI.
	 * 
	 * @return
	 */
	public void setProviderName(String name);

}
