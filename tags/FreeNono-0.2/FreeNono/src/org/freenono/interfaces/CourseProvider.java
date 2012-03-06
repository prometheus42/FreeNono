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

import java.util.Collection;
import java.util.List;

import org.freenono.model.Course;

public interface CourseProvider {

	/**
	 * Gives back a list of all nonograms in this course. For this operation the
	 * nonograms do not have actually been read from filesystem or network.
	 * 
	 * @return
	 */
	public List<String> getNonogramList();

	/**
	 * Provides a list of handlers for all included nonograms in this course.
	 * 
	 * @return
	 */
	public Collection<NonogramProvider> getNonogramProvider();

	/**
	 * Ignoring the nonogram provider this function returns a hole course data
	 * structure with all nonograms of the course included.
	 * 
	 * @return
	 */
	public Course fetchCourse();
	
	public String getCourseName();
	
	public String toString();

}
