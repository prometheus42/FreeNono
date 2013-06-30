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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;
import org.freenono.model.Course;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.ZipCourseSerializer;

/**
 * Loads a collection of courses and nonograms from a given jar file or by using
 * the getResources-mechanism of the class loader to find it in the class path.
 * Included courses are declared by a courseList file which has to exist in the
 * jar.
 * 
 * @author Christian Wichmann
 */
public class CollectionFromJar implements CollectionProvider {

    private static Logger logger = Logger.getLogger(CollectionFromJar.class);

    // private String jarPath = null;
    private String providerName = null;
    private ZipCourseSerializer zipCourseSerializer = new ZipCourseSerializer();
    private List<Course> courseList = new ArrayList<Course>();
    private List<CourseProvider> courseProviderList = null;

    /**
     * Loads courses from given jar file and building CourseProvider classes.
     * 
     * @param jarPath
     *            path to jar file containing courses
     * @param name
     *            given provider name
     */
    public CollectionFromJar(final String jarPath, final String name) {

        // this.jarPath = jarPath;
        this.providerName = name;

        // TODO implement loading courses from given jar file!
        // loadCollection();
    }

    /**
     * Loads courses from jar file in class path which includes a courseList
     * file and building CourseProvider classes.
     * 
     * @param name
     *            given provider name
     */
    public CollectionFromJar(final String name) {

        this.providerName = name;

        loadCollection();

        Collections.sort(courseProviderList,
                CourseProvider.NAME_ASCENDING_ORDER);
    }

    /**
     * Loads all courses in collection in a jar file under a given name.
     */
    private void loadCollection() {

        // find jar and load file list from jar
        try {

            Enumeration<URL> systemResources = getClass().getClassLoader()
                    .getResources("nonograms/courseList");

            InputStream content = null;

            while (systemResources.hasMoreElements()) {

                content = systemResources.nextElement().openConnection()
                        .getInputStream();
            }

            // split names from file list by line ending...
            Scanner s = new Scanner(content).useDelimiter("\n");

            // ...and load course from all given files
            while (s.hasNext()) {

                String courseFile = s.next();

                loadCourse(
                        getClass().getClassLoader().getResource(
                                "nonograms/" + courseFile),
                        courseFile.substring(0, courseFile.lastIndexOf('.')));
            }

        } catch (IOException e) {

            logger.warn("Could not load course resources from classpath.");
        }

        generateCourseProviderList();
    }

    /**
     * Loads course from collection in a jar file.
     * 
     * @param source
     *            where to get the courses from
     * @param courseName
     *            course to be read
     * @throws FileNotFoundException
     *             if course file can't be read
     */
    private synchronized void loadCourse(final URL source,
            final String courseName) throws FileNotFoundException {

        List<Course> lst = new ArrayList<Course>();

        try {

            Course c = null;

            if (source.getFile().endsWith(
                    "." + ZipCourseSerializer.DEFAULT_FILE_EXTENSION)) {

                c = zipCourseSerializer.load(source.openStream(), courseName);
            }

            if (c != null) {

                lst.add(c);
                logger.debug("loaded course \"" + source.getFile()
                        + "\" successfully");

            } else {

                logger.info("unable to load file \"" + source.getFile() + "\"");
            }

        } catch (NullPointerException e) {

            logger.warn("loading course \"" + source.getFile()
                    + "\" caused a NullPointerException");

        } catch (IOException e) {

            logger.warn("loading course \"" + source.getFile()
                    + "\" caused a IOException");

        } catch (NonogramFormatException e) {

            logger.warn("loading course \"" + source.getFile()
                    + "\" caused a NonogramFormatException");
        }

        this.courseList.addAll(lst);
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
                    cp = new CourseFromJar(c);
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
            return "JAR...";
        } else {
            return providerName;
        }

    }

    @Override
    public final synchronized void setProviderName(final String name) {

        this.providerName = name;
    }

    @Override
    public final String toString() {

        return providerName;
    }

    @Override
    public final synchronized int getNumberOfNonograms() {

        int n = 0;

        for (CourseProvider cp : courseProviderList) {

            n += cp.getNumberOfNonograms();
        }

        return n;
    }

    @Override
    public final Iterator<CourseProvider> iterator() {

        return courseProviderList.iterator();
    }

    // public synchronized void changeRootPath(String rootPath) {
    //
    // this.jarPath = rootPath;
    // loadCollection();
    // }

    // public String getRootPath() {
    //
    // return jarPath;
    // }
}
