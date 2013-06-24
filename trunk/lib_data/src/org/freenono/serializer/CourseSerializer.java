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
package org.freenono.serializer;

import java.io.File;
import java.io.IOException;

import org.freenono.model.Course;

/**
 * @author Markus Wichmann
 */
public interface CourseSerializer {

    /**
     * Loads nonograms from a file into a course object.
     * @param f
     *            file to read nonograms from
     * @return course with all nonograms that were read from file
     * @throws IOException
     *             if file could not be opened
     * @throws CourseFormatException
     *             if file has wrong course format
     * @throws NonogramFormatException
     *             if nonogram in course has wrong format
     */
    Course load(File f) throws IOException, CourseFormatException,
            NonogramFormatException;

    /**
     * Saves a course including all nonograms into a given file.
     * @param f
     *            file to save course in
     * @param c
     *            course that should be saved in file
     * @throws IOException
     *             if file could not be opened
     */
    void save(File f, Course c) throws IOException;

}
