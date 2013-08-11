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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.Nonogram;

/**
 * Provides a course from the file system.
 * 
 * @author Christian Wichmann
 */
public class CourseFromFilesystem implements CourseProvider {

    /*
     * TODO change this function to only fetch the course from file system when
     * fetchCourse() is explicitly called.
     * 
     * TODO make this class iterable to iterate over nonograms in course.
     */

    private static Logger logger = Logger.getLogger(CourseFromFilesystem.class);

    private Course course = null;
    private List<NonogramProvider> nonogramProvider = null;

    /**
     * Initializes a course read from filesystem.
     * 
     * @param c
     *            Course that is provided.
     */
    public CourseFromFilesystem(final Course c) {

        this.course = c;

        generateNonogramProviderList();

    }

    @Override
    public final List<String> getNonogramList() {

        List<String> nonograms = new ArrayList<String>();

        if (course != null) {

            for (Nonogram n : course.getNonograms()) {

                nonograms.add(n.getName());
            }
        }
        return nonograms;
    }

    /**
     * Generates a list ao all nonogram providers in this course.
     */
    private void generateNonogramProviderList() {

        logger.debug("Getting list of all NonogramProvider.");

        nonogramProvider = new ArrayList<NonogramProvider>();

        if (course != null) {

            NonogramProvider np = null;

            for (Nonogram n : course.getNonograms()) {

                np = new NonogramFromFilesystem(n, this);
                nonogramProvider.add(np);
            }
        }
    }

    @Override
    public final Collection<NonogramProvider> getNonogramProvider() {

        return Collections.unmodifiableList(nonogramProvider);
    }

    @Override
    public final Course fetchCourse() {

        assert course != null;
        return course;
    }

    @Override
    public final String toString() {

        if (course == null) {

            return "";

        } else {

            return course.getName();
        }
    }

    @Override
    public final int getNumberOfNonograms() {

        return nonogramProvider.size();
    }

    @Override
    public final String getCourseName() {

        if (course != null) {
            return course.getName();

        } else {
            return "";
        }
    }

    /**
     * Gets the next nonogram for a given NonogramProvider.
     * 
     * @param np
     *            NonogramProvider for which next nonogram should be found.
     * @return NonogramProvider for next nonogram or null if no next nonogram
     *         exists.
     */
    protected final NonogramProvider getNextNonogram(final NonogramProvider np) {

        NonogramProvider next = null;

        try {

            final int index = nonogramProvider.indexOf(np) + 1;
            next = nonogramProvider.get(index);

        } catch (IndexOutOfBoundsException e) {

            logger.debug("No next nonogram available.");
        }

        return next;
    }

    /**
     * Gets the previous nonogram for a given NonogramProvider.
     * 
     * @param np
     *            NonogramProvider for which previous nonogram should be found.
     * @return NonogramProvider for previous nonogram or null if no previous
     *         nonogram exists.
     */
    protected final NonogramProvider getPreviousNonogram(
            final NonogramProvider np) {

        NonogramProvider previous = null;

        try {

            final int index = nonogramProvider.indexOf(np) - 1;
            previous = nonogramProvider.get(index);

        } catch (IndexOutOfBoundsException e) {

            logger.debug("No previous nonogram available.");
        }

        return previous;
    }
}
