/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CourseProvider;
import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.Course;
import org.freenono.model.Nonogram;

/**
 * Provides a course from the file system.
 * 
 * @author Christian Wichmann
 */
/*
 * TODO: change this function to only fetch the course from file system when
 * fetchCourse() is explicitly called.
 * 
 * TODO: make this class iterable to iterate over nonograms in course.
 */
public class CourseFromFilesystem implements CourseProvider {

    private static Logger logger = Logger.getLogger(CourseFromFilesystem.class);

    private Course course = null;
    private List<NonogramProvider> nonogramProvider = null;

    public CourseFromFilesystem() {

    }

    public CourseFromFilesystem(Course c) {

        this();

        this.course = c;

        generateNonogramProviderList();

    }

    @Override
    public List<String> getNonogramList() {

        List<String> nonograms = new ArrayList<String>();

        if (course != null) {
            for (Nonogram n : course.getNonograms()) {
                nonograms.add(n.getName());
            }
        }
        return nonograms;
    }

    private void generateNonogramProviderList() {

        logger.debug("Getting list of all NonogramProvider.");

        nonogramProvider = new ArrayList<NonogramProvider>();

        if (course != null) {

            NonogramProvider np = null;

            for (Nonogram n : course.getNonograms()) {
                np = new NonogramFromFilesystem(n);
                nonogramProvider.add(np);
                // logger.debug("Getting NonogramProvider for " + np.toString()+
                // ".");
            }
        }
    }

    @Override
    public Collection<NonogramProvider> getNonogramProvider() {

        return nonogramProvider;
    }

    @Override
    public Course fetchCourse() {

        return course;

    }

    public String toString() {

        if (course == null)
            return "";
        else
            return course.getName();

    }

    public int getNumberOfNonograms() {

        return nonogramProvider.size();
    }

    @Override
    public String getCourseName() {
        if (course != null)
            return course.getName();
        else
            return null;
    }
}
