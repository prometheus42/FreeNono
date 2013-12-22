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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.Nonogram;
import org.restlet.resource.ResourceException;

/**
 * Provides a course from a Nonoserver.
 * 
 * @author Christian Wichmann
 */
public class CourseFromServer implements CourseProvider {

    private static Logger logger = Logger.getLogger(CourseFromServer.class);

    private String courseName = null;
    private ServerProviderHelper serverProviderHelper = null;
    private List<String> nonogramList = null;
    private List<NonogramProvider> nonogramProviderList = null;

    /**
     * Initializes a course that is available on a NonoServer.
     * 
     * @param courseName
     *            name of course to get from server
     * @param serverProviderHelper
     *            helper instance to get data from server
     */
    public CourseFromServer(final String courseName,
            final ServerProviderHelper serverProviderHelper) {

        this.courseName = courseName;
        this.serverProviderHelper = serverProviderHelper;

        prepareNonogramProviders();
    }

    /**
     * Prepares nonopram providers for all nonograms in this course.
     */
    private void prepareNonogramProviders() {

        logger.debug("Preparing all NonogramProviders.");

        nonogramProviderList = new ArrayList<NonogramProvider>();

        // create nonogramProvider
        List<String> nonogramList = null;
        try {
            nonogramList = serverProviderHelper.getNonogramList(courseName);
        } catch (ResourceException e) {
            logger.error("Server under given URL not responding.");
        }
        for (String n : nonogramList) {
            nonogramProviderList.add(new NonogramFromServer(n, courseName,
                    serverProviderHelper));
        }
    }

    @Override
    public final List<String> getNonogramList() {

        return nonogramList;
    }

    @Override
    public final List<NonogramProvider> getNonogramProvider() {

        return Collections.unmodifiableList(nonogramProviderList);
    }

    @Override
    public final Course fetchCourse() {

        // TODO implement the build of a course class!
        List<Nonogram> nonogramList = new ArrayList<Nonogram>();
        Course course = new Course(getCourseName(), nonogramList);
        assert course != null;
        return course;
    }

    @Override
    public final String toString() {

        return courseName;
    }

    @Override
    public final int getNumberOfNonograms() {

        return nonogramProviderList.size();
    }

    @Override
    public final String getCourseName() {

        return courseName;
    }
}
