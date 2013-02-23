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
import java.util.Comparator;
import java.util.List;

import org.freenono.model.Course;

public interface CourseProvider {

	public static final Comparator<CourseProvider> NAME_ASCENDING_ORDER = 
			new Comparator<CourseProvider>() {

		@Override
		public int compare(CourseProvider c1, CourseProvider c2) {

			if (c1 == null && c2 == null) {
				return 0;
			} else if (c1 == null) {
				return -1;
			} else if (c2 == null) {
				return 1;
			} else {
				return c1.getCourseName().compareTo(c2.getCourseName());
			}

		}
	};
	
	/**
	 * Gives back a list of all nonograms in this course. For this operation the
	 * nonograms do not have actually been read from filesystem or network.
	 * 
	 * @return List of all nonograms in this course.
	 */
	public List<String> getNonogramList();

	/**
	 * Provides a list of handlers for all included nonograms in this course.
	 * 
	 * @return List of handlers for all included nonograms.
	 */
	public Collection<NonogramProvider> getNonogramProvider();

	/**
	 * Ignoring the nonogram provider this function returns a hole course data
	 * structure with all nonograms of the course included.
	 * 
	 * @return Course class with all nonograms of this course.
	 */
	public Course fetchCourse();
	
	public String getCourseName();
	
	public String toString();

}
