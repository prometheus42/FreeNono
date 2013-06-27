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
 * Provides a course from a jar in the classpath.
 * 
 * @author Christian Wichmann
 */
public class CourseFromJar implements CourseProvider {

    private static Logger logger = Logger.getLogger(CourseFromJar.class);

    private Course course = null;
    private List<NonogramProvider> nonogramProvider = null;

    /**
     * Initializes a course from a jar file.
     * 
     * @param c
     *            Course which is provided.
     */
    public CourseFromJar(final Course c) {

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
     * Generates a list of nonogram providers.
     */
    private void generateNonogramProviderList() {

        logger.debug("Getting list of all NonogramProvider.");

        nonogramProvider = new ArrayList<NonogramProvider>();

        if (course != null) {

            NonogramProvider np = null;

            for (Nonogram n : course.getNonograms()) {

                np = new NonogramFromJar(n, this);
                nonogramProvider.add(np);
            }
        }
    }

    @Override
    public final Collection<NonogramProvider> getNonogramProvider() {

        return nonogramProvider;
    }

    @Override
    public final Course fetchCourse() {

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
            return null;
        }
    }

    /**
     * Gets the next nonogram for a given NonogramProvider.
     * 
     * @param np
     *            NonogramProvider for which next nonogram should be found.
     * @return NonogramProvider for next nonogram.
     */
    protected final NonogramProvider getNextNonogram(final NonogramProvider np) {

        NonogramProvider next;

        try {

            final int index = nonogramProvider.indexOf(np) + 1;
            next = nonogramProvider.get(index);

        } catch (IndexOutOfBoundsException e) {

            logger.debug("No next nonogram available.");

        } finally {

            next = null;
        }

        return next;
    }

    /**
     * Gets the previous nonogram for a given NonogramProvider.
     * 
     * @param np
     *            NonogramProvider for which previous nonogram should be found.
     * @return NonogramProvider for previous nonogram.
     */
    protected final NonogramProvider getPreviousNonogram(
            final NonogramProvider np) {

        NonogramProvider previous;

        try {

            final int index = nonogramProvider.indexOf(np) - 1;
            previous = nonogramProvider.get(index);

        } catch (IndexOutOfBoundsException e) {

            logger.debug("No previous nonogram available.");

        } finally {

            previous = null;
        }

        return previous;
    }
}
