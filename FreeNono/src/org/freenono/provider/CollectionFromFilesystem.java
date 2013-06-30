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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;
import org.freenono.model.Course;
import org.freenono.serializer.CourseFormatException;
import org.freenono.serializer.CourseSerializer;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.XMLCourseSerializer;
import org.freenono.serializer.ZipCourseSerializer;

/**
 * Collection loaded from file system. Dependent on the parameter "concurrently"
 * of the constructor nonograms are loaded in a separate thread or not!
 * 
 * @author Christian Wichmann
 */
public class CollectionFromFilesystem implements CollectionProvider,
        Iterable<CourseProvider> {

    private static Logger logger = Logger
            .getLogger(CollectionFromFilesystem.class);

    private String rootPath = null;
    private String providerName = null;
    private boolean concurrently = false;
    private CourseSerializer xmlCourseSerializer = new XMLCourseSerializer();
    private CourseSerializer zipCourseSerializer = new ZipCourseSerializer();
    private List<Course> courseList = null;
    private List<CourseProvider> courseProviderList = null;

    /**
     * Initializes a collection of courses from files on the file system.
     * 
     * @param rootPath
     *            path to course files
     * @param name
     *            name of this provider
     * @param concurrently
     *            if this collection should be read concurrently
     */
    public CollectionFromFilesystem(final String rootPath, final String name,
            final boolean concurrently) {

        this.rootPath = rootPath;
        this.providerName = name;
        this.concurrently = concurrently;

        loadCollection();

        Collections.sort(courseProviderList,
                CourseProvider.NAME_ASCENDING_ORDER);
    }

    /**
     * Loads all courses in collection on the file system under the path
     * <code>rootPath</code>.
     */
    private void loadCollection() {

        if (rootPath == null) {
            throw new NullPointerException("Parameter rootPath is null");
        }

        if (concurrently) {

            // load files in separate thread
            Thread loadThread = new Thread() {
                public void run() {
                    try {
                        loadCourses(new File(rootPath));
                    } catch (FileNotFoundException e) {

                        logger.warn("No nonograms found at directory: "
                                + rootPath);
                    }
                    generateCourseProviderList();

                }
            };
            loadThread.setDaemon(true);
            loadThread.start();

        } else {

            // load files in this thread
            try {
                loadCourses(new File(rootPath));

            } catch (FileNotFoundException e) {

                logger.warn("No nonograms found at directory: " + rootPath);
            }
            generateCourseProviderList();
        }
    }

    /**
     * Loads one course from this collection and stores it in
     * <code>courseList</code>.
     * 
     * @param dir
     *            directory where course files can be found
     * @throws FileNotFoundException
     *             if parameter <code>dir</code> is not a directory or does not
     *             exist.
     */
    private synchronized void loadCourses(final File dir)
            throws FileNotFoundException {

        if (!dir.isDirectory()) {
            throw new FileNotFoundException("Parameter is no directory");
        }
        if (!dir.exists()) {
            throw new FileNotFoundException("Specified directory not found");
        }

        // List<Course> lst = new ArrayList<Course>();
        List<Course> lst = Collections
                .synchronizedList(new ArrayList<Course>());

        synchronized (lst) {

            for (File file : dir.listFiles()) {

                try {

                    Course c = null;

                    if (!file.getName().startsWith(".")) {

                        if (file.isDirectory()) {

                            c = xmlCourseSerializer.load(file);

                        } else {

                            if (file.getName()
                                    .endsWith(
                                            "."
                                                    + ZipCourseSerializer.DEFAULT_FILE_EXTENSION)) {
                                c = zipCourseSerializer.load(file);
                            }

                        }

                        if (c != null) {

                            lst.add(c);
                            logger.debug("loaded course \"" + file
                                    + "\" successfully");

                        } else {

                            logger.warn("unable to load file \"" + file + "\"");

                        }
                    }

                } catch (NullPointerException e) {
                    logger.error("loading course \"" + file
                            + "\" caused a NullPointerException");
                } catch (IOException e) {
                    logger.warn("loading course \"" + file
                            + "\" caused a IOException");
                } catch (NonogramFormatException e) {
                    logger.warn("loading course \"" + file
                            + "\" caused a NonogramFormatException");
                } catch (CourseFormatException e) {
                    logger.warn("loading course \"" + file
                            + "\" caused a CourseFormatException");
                }
            }
        }

        this.courseList = lst;
    }

    @Override
    public final synchronized List<String> getCourseList() {

        List<String> courses = new ArrayList<String>();

        for (Course c : courseList) {
            courses.add(c.getName());
        }

        return courses;
    }

    /**
     * Generates a list of providers for courses in this collection.
     */
    private synchronized void generateCourseProviderList() {

        logger.debug("Getting list of all CourseProvider.");

        courseProviderList = Collections
                .synchronizedList(new ArrayList<CourseProvider>());

        synchronized (courseProviderList) {

            if (courseList != null) {

                CourseProvider cp;

                for (Course c : courseList) {
                    cp = new CourseFromFilesystem(c);
                    courseProviderList.add(cp);
                    logger.debug("Getting CourseProvider for " + cp.toString()
                            + ".");
                }
            }
        }
    }

    @Override
    public final synchronized List<CourseProvider> getCourseProvider() {

        return courseProviderList;
    }

    @Override
    public final synchronized String getProviderName() {

        if (providerName == null) {
            return "Filesystem: " + rootPath;
        } else {
            return providerName;
        }

    }

    @Override
    public final synchronized void setProviderName(final String name) {

        this.providerName = name;

    }

    /**
     * Changes path to course files.
     * 
     * @param rootPath
     *            path to course files
     */
    public final synchronized void changeRootPath(final String rootPath) {

        this.rootPath = rootPath;
        loadCollection();
    }

    @Override
    public final String toString() {

        return this.providerName; // + " (" + rootPath + ")";
    }

    @Override
    public final synchronized int getNumberOfNonograms() {

        int n = 0;

        for (CourseProvider cp : courseProviderList) {

            n += cp.getNumberOfNonograms();
        }

        return n;
    }

    /**
     * Returns path of the course files for this provider.
     * 
     * @return path to course files
     */
    public final String getRootPath() {

        return rootPath;
    }

    @Override
    public final Iterator<CourseProvider> iterator() {

        return courseProviderList.iterator();
    }
}
