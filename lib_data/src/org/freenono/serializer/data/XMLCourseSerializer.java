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
package org.freenono.serializer.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.Nonogram;
import org.freenono.ui.common.Tools;

/**
 * Serializes a course from a directory of files.
 *
 * @author Markus Wichmann
 */
public class XMLCourseSerializer implements CourseSerializer {

    private static Logger logger = Logger.getLogger(XMLCourseSerializer.class);

    private final StAXNonogramSerializer xmlNonogramSerializer = new StAXNonogramSerializer();

    private final SimpleNonogramSerializer simpleNonogramSerializer = new SimpleNonogramSerializer();

    /* load methods */

    @Override
    public final Course load(final File f) throws IOException, CourseFormatException, NonogramFormatException {

        // do some parameter checks
        if (f == null) {
            // unable to use a file that is null ;-)
            throw new NullPointerException("File parameter is null");
        }
        if (!f.isDirectory()) {
            // unable to use a file to load a course
            throw new IOException("unable to use a file to load a course");
        }
        if (!f.exists()) {
            // unable to use a none existent directory
            throw new FileNotFoundException("specified directory doesn't exist");
        }

        Course c;
        final List<Nonogram> nonograms = new ArrayList<Nonogram>();

        final String name = f.getName();
        final File[] listOfNonogramFiles = f.listFiles();
        if (listOfNonogramFiles == null) {
            // check whether listFiles() really returns a list of files
            throw new FileNotFoundException("An error occured during XML nonogram import.");
        }
        for (final File file : listOfNonogramFiles) {
            loadNonogramFile(file, nonograms);
        }

        if (nonograms.isEmpty()) {
            throw new CourseFormatException("specified directory is empty");
        }

        c = new Course(name, nonograms);

        return c;
    }

    /**
     * Loads all nonograms from a single file and appends them to the list of nonograms for their
     * course.
     *
     * @param file
     *            file that should be loaded
     * @param nonograms
     *            list of all nonograms for this course
     * @throws IOException
     *             , if file could not be accessed
     * @throws NonogramFormatException
     *             , if file format is not valid
     */
    private void loadNonogramFile(final File file, final List<Nonogram> nonograms) throws IOException, NonogramFormatException {
        if (file.isDirectory()) {
            // directories will be spared
            return;
        }

        Nonogram[] n = null;
        if (file.getName().endsWith("." + StAXNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
            // load nonograms with the xml serializer
            n = xmlNonogramSerializer.load(file);
        } else if (file.getName().endsWith("." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
            // load nonograms with the simple serializer
            n = simpleNonogramSerializer.load(file);
        }

        if (n != null) {
            for (int i = 0; i < n.length; i++) {
                // set reference to origin of nonogram
                n[i].setOriginPath(file.toURI().toURL());
            }
            nonograms.addAll(Arrays.asList(n));
        }
    }

    /* save methods */

    @Override
    public final void save(final File f, final Course c) throws IOException {

        // do some parameter checks
        if (f == null) {
            // unable to use a file that is null ;-)
            throw new NullPointerException("File parameter is null");
        }
        if (!f.isDirectory()) {
            // unable to use a file to save a course
            throw new IOException("unable to use a file to save a course");
        }
        if (c == null) {
            // there is no course to save
            throw new NullPointerException("Course parameter is null");
        }

        if (f.exists()) {
            // at least trigger a log message, if the file already exists
            logger.warn("specified output directory already exists, some files may be overwritten");
        }

        final File courseDir = new File(f, c.getName() + Tools.FILE_SEPARATOR);

        if (!courseDir.mkdirs()) {
            throw new IOException("Unable to create directories");
        }

        for (final Nonogram n : c.getNonograms()) {
            final File nonogramFile = new File(courseDir, n.getName() + ".nonogram");
            xmlNonogramSerializer.save(nonogramFile, n);
        }
    }

}
