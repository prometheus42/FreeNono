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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.provider.CollectionListener.CollectionEvent;
import org.freenono.serializer.data.CourseFormatException;
import org.freenono.serializer.data.CourseSerializer;
import org.freenono.serializer.data.NonogramFormatException;
import org.freenono.serializer.data.XMLCourseSerializer;
import org.freenono.serializer.data.ZipCourseSerializer;

/**
 * Collection loaded from file system. Dependent on the parameter "concurrently" of the constructor
 * nonograms are loaded in a separate thread or not!
 *
 * @author Christian Wichmann
 */
public class CollectionFromFilesystem implements CollectionProvider, Iterable<CourseProvider> {

    private static Logger logger = Logger.getLogger(CollectionFromFilesystem.class);

    // private String rootPath = null;
    private Path collectionDirectory = null;
    private String providerName = null;
    private boolean concurrently = false;
    private final CourseSerializer xmlCourseSerializer = new XMLCourseSerializer();
    private final CourseSerializer zipCourseSerializer = new ZipCourseSerializer();
    private List<Course> courseList = null;
    private List<CourseProvider> courseProviderList = null;

    private final EventListenerList listenerList = new EventListenerList();
    private CollectionEvent collectionEvent = null;
    private int numberOfCourses = 0;
    private int alreadyLoadedCourses = 0;

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
    public CollectionFromFilesystem(final String rootPath, final String name, final boolean concurrently) {

        if (rootPath == null) {
            throw new NullPointerException("Parameter rootPath is null");
        }

        providerName = name;
        this.concurrently = concurrently;

        collectionDirectory = Paths.get(rootPath);
    }

    /**
     * Starts loading courses of this collection.
     *
     * @param listener
     *            collection listener to be informed of changes
     */
    public final void startLoading(final CollectionListener listener) {

        listenerList.add(CollectionListener.class, listener);

        loadCollection();

        Collections.sort(courseProviderList, CourseProvider.NAME_ASCENDING_ORDER);

        setupFileSystemWatch();
    }

    /*
     * Methods actually loading data from and handling changes in file system.
     */

    /**
     * Loads all courses in collection on the file system under the path <code>rootPath</code>.
     */
    private void loadCollection() {

        if (concurrently) {
            // load files in separate thread
            final Thread loadThread = new Thread() {
                @Override
                public void run() {
                    try {
                        loadCourses(collectionDirectory.toFile());
                    } catch (final FileNotFoundException e) {
                        logger.warn("No nonograms found at directory: " + collectionDirectory.toString());
                    }
                    generateCourseProviderList();
                    Collections.sort(courseProviderList, CourseProvider.NAME_ASCENDING_ORDER);
                }
            };
            loadThread.setDaemon(true);
            loadThread.start();

        } else {

            // load files in this thread
            try {
                loadCourses(collectionDirectory.toFile());
            } catch (final FileNotFoundException e) {
                logger.warn("No nonograms found at directory: " + collectionDirectory.toString());
            }
            generateCourseProviderList();
            Collections.sort(courseProviderList, CourseProvider.NAME_ASCENDING_ORDER);
        }
    }

    /**
     * Loads one course from this collection and stores it in <code>courseList</code>.
     *
     * @param dir
     *            directory where course files can be found
     * @throws FileNotFoundException
     *             if parameter <code>dir</code> is not a directory or does not exist.
     */
    private synchronized void loadCourses(final File dir) throws FileNotFoundException {

        if (!dir.isDirectory()) {
            throw new FileNotFoundException("Parameter is no directory");
        }
        if (!dir.exists()) {
            throw new FileNotFoundException("Specified directory not found");
        }

        final String ext = "." + ZipCourseSerializer.DEFAULT_FILE_EXTENSION;
        final List<Course> lst = Collections.synchronizedList(new ArrayList<Course>());

        synchronized (lst) {
            final File[] listOfFiles = dir.listFiles();
            if (listOfFiles == null) {
                // just stop loading when there are no files
                courseList = lst;
                return;
            }

            // count courses
            numberOfCourses = 0;
            alreadyLoadedCourses = 0;
            for (final File file : listOfFiles) {
                if (!file.getName().startsWith(".") && (file.isDirectory() || file.getName().endsWith(ext))) {
                    numberOfCourses++;
                }
            }
            fireCollectionLoadingEvent();

            for (final File file : listOfFiles) {
                final Course c = loadSingleCourse(file);
                if (c != null) {
                    lst.add(c);
                    logger.debug("loaded course \"" + file + "\" successfully");
                } else {
                    logger.warn("unable to load file \"" + file + "\"");
                }
            }
        }

        courseList = lst;
    }

    /**
     * Loads a single course from a given file.
     *
     * @param file
     *            file containing nonogram course to be loaded
     * @return loaded course or NULL if course could not be loaded
     */
    private Course loadSingleCourse(final File file) {

        final String ext = "." + ZipCourseSerializer.DEFAULT_FILE_EXTENSION;
        Course c = null;
        try {
            if (!file.getName().startsWith(".")) {
                if (file.isDirectory()) {
                    c = xmlCourseSerializer.load(file);
                    alreadyLoadedCourses++;
                    fireCollectionLoadingEvent();
                } else {
                    if (file.getName().endsWith(ext)) {
                        c = zipCourseSerializer.load(file);
                        alreadyLoadedCourses++;
                        fireCollectionLoadingEvent();
                    }
                }
            }
        } catch (final NullPointerException e) {
            logger.error("loading course \"" + file + "\" caused a NullPointerException");
        } catch (final IOException e) {
            logger.warn("loading course \"" + file + "\" caused a IOException");
        } catch (final NonogramFormatException e) {
            logger.warn("loading course \"" + file + "\" caused a NonogramFormatException");
        } catch (final CourseFormatException e) {
            logger.warn("loading course \"" + file + "\" caused a CourseFormatException");
        }
        return c;
    }

    /**
     * Adds watches for file system changes of all courses in this collection. The individual
     * courses can be either a ZIP file or a directory.
     */
    private void setupFileSystemWatch() {

        final ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                // create the new WatchService
                try (WatchService watcher = collectionDirectory.getFileSystem().newWatchService()) {

                    // register the paths of the nonogram collection
                    registerCollectionDirectoryWatch(collectionDirectory, watcher);

                    watchCollection(collectionDirectory, watcher);

                } catch (final IOException ioe) {
                    logger.error("An error occured during watching of collection directory.");
                } catch (final InterruptedException e) {
                    logger.error("Watching of collection directory was interrupted.");
                }
            }
        });
    }

    /**
     * Watches the collection and all its files/directories and reacts accordingly.
     *
     * @param collectionDirectory
     *            root directory of collection
     * @param watcher
     *            watch service to register collection directories with
     * @throws InterruptedException
     *             throws if watching is interrupted
     * @throws IOException
     *             throws if an error occurs while watching collection
     */
    private void watchCollection(final Path collectionDirectory, final WatchService watcher) throws InterruptedException, IOException {

        // start the infinite polling loop
        while (true) {
            // wait for key to be signaled
            WatchKey key;
            key = watcher.take();

            for (final WatchEvent<?> watchEvent : key.pollEvents()) {

                // get the type of the event
                final Kind<?> kind = watchEvent.kind();

                // overflow event can occur when events are lost
                if (kind == OVERFLOW) {
                    continue;
                }

                // filter all repeating events out
                if (watchEvent.count() > 1) {
                    continue;
                }

                // get the filename of the event
                final WatchEvent<Path> ev = castWatchEvent(watchEvent);
                final Path changedFilename = ev.context();
                logger.debug("Watched file/dir: " + changedFilename);

                // resolve the filename against the directory
                final Path directoryWithChange = (Path) key.watchable();
                final Path changedPath = directoryWithChange.resolve(changedFilename);
                logger.debug("Watched path: " + changedPath);

                // check if file/directory that was added/modified/deleted
                // was REALLY a nonogram course
                final String courseName;
                if (Files.isDirectory(changedPath) && directoryWithChange.equals(collectionDirectory)) {
                    // change relates to new directory inside the
                    // collections root directory
                    courseName = changedFilename.toString();

                    if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                        logger.debug("Course added/modified: " + courseName);
                        // add new course directory to watcher service
                        changedPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                        // TODO load new/changed course
                        loadCollection();
                        fireCollectionChangedEvent();

                    } else if (kind == ENTRY_DELETE) {
                        logger.debug("Course deleted: " + courseName);
                        // cancel watch on the deleted directory
                        // key.cancel();
                        deleteCourseFromList(courseName);
                        fireCollectionChangedEvent();
                    }
                } else if (!Files.isDirectory(changedPath) && !directoryWithChange.equals(collectionDirectory)) {
                    // change relates to a new nonogram in a course
                    // directory inside the collections root directory
                    courseName = collectionDirectory.relativize(directoryWithChange).toString();

                    if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY || kind == ENTRY_DELETE) {
                        logger.debug("Course modified: " + courseName);
                        // TODO load new/changed course
                        loadCollection();
                        fireCollectionChangedEvent();
                    }

                } else if (changedFilename.toString().toLowerCase().endsWith(".nonopack")
                        && directoryWithChange.equals(collectionDirectory)) {
                    // change relates to new nonopack file inside the
                    // collections root directory
                    courseName = changedFilename.toString().replace(".nonopack", "");

                    if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                        logger.debug("Course added/modified: " + courseName);
                        // TODO load new/changed course
                        loadCollection();
                        fireCollectionChangedEvent();

                    } else if (kind == ENTRY_DELETE) {
                        logger.debug("Course deleted: " + courseName);
                        deleteCourseFromList(courseName);
                        fireCollectionChangedEvent();
                    }

                } else {
                    continue;
                }
            }

            /*
             * Reset the key to receive further watch events. If the key is no longer valid, the
             * directory is inaccessible so exit the loop.
             */
            final boolean valid = key.reset();
            if (!valid) {
                continue;
            }
        }
    }

    /**
     * Registers all directories that contain nonograms from this collection. The nonograms could be
     * either ZIP files or separate directories.
     *
     * @param collectionDirectory
     *            root directory of collection
     * @param watcher
     *            watch service to register collection directories with
     * @throws IOException
     *             throws if directory could not be accessed
     */
    private synchronized void registerCollectionDirectoryWatch(final Path collectionDirectory, final WatchService watcher)
            throws IOException {

        collectionDirectory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        final DirectoryStream<Path> stream = Files.newDirectoryStream(collectionDirectory);
        for (final Path entry : stream) {
            // add all subdirectories if nonograms are not provided as
            // nonopack files
            if (Files.isDirectory(entry)) {
                entry.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            }
        }
    }

    /**
     * Deletes a course defined by its name from the internal course provider list of this
     * collection.
     *
     * @param courseName
     *            name of the course to be deleted
     */
    private synchronized void deleteCourseFromList(final String courseName) {

        CourseProvider toBeDeleted = null;
        for (final CourseProvider course : courseProviderList) {
            if (course.getCourseName().equals(courseName)) {
                toBeDeleted = course;
            } else {
                logger.debug("Still in collection: " + course.getCourseName());
            }
        }

        if (toBeDeleted != null) {
            courseProviderList.remove(toBeDeleted);
        }
    }

    /**
     * Utility cast method for preventing unchecked cast type error.
     *
     * @param <T>
     *            type of WatchEvent that should be cast
     * @param event
     *            event that should be cast
     * @return event cast to <code>WatchEvent\<Path\></code>
     */
    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> castWatchEvent(final WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /*
     * Getter and setter for this collection.
     */

    @Override
    public final synchronized List<String> getCourseList() {

        final List<String> courses = new ArrayList<String>();

        for (final Course c : courseList) {
            courses.add(c.getName());
        }

        return courses;
    }

    /**
     * Generates a list of providers for courses in this collection.
     */
    private synchronized void generateCourseProviderList() {

        logger.debug("Getting list of all CourseProvider.");

        courseProviderList = Collections.synchronizedList(new ArrayList<CourseProvider>());

        if (courseList != null) {
            CourseProvider cp;

            for (final Course c : courseList) {
                cp = new CourseFromFilesystem(c);
                courseProviderList.add(cp);
                logger.debug("Getting CourseProvider for " + cp.toString() + ".");
            }
        }
    }

    @Override
    public final synchronized List<CourseProvider> getCourseProvider() {

        return Collections.unmodifiableList(courseProviderList);
    }

    @Override
    public final synchronized String getProviderName() {

        if (providerName == null) {
            return "Filesystem: " + collectionDirectory.toString();
        } else {
            return providerName;
        }
    }

    @Override
    public final synchronized void setProviderName(final String name) {

        providerName = name;
    }

    /**
     * Changes path to course files.
     *
     * @param rootPath
     *            path to course files
     */
    public final synchronized void changeRootPath(final String rootPath) {

        if (rootPath == null) {
            throw new NullPointerException("Parameter rootPath is null");
        }
        collectionDirectory = Paths.get(rootPath);

        loadCollection();
    }

    @Override
    public final String toString() {

        return providerName; // + " (" + rootPath + ")";
    }

    @Override
    public final synchronized int getNumberOfNonograms() {

        int n = 0;

        for (final CourseProvider cp : courseProviderList) {

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

        return collectionDirectory.toString();
    }

    @Override
    public final Iterator<CourseProvider> iterator() {

        /*
         * Just return an iterator for a wrapper class from collections library and let the list
         * implementation do the work!
         */
        return Collections.unmodifiableList(courseProviderList).iterator();
    }

    /*
     * Methods concerning handling of events and listeners
     */

    @Override
    public final void addCollectionListener(final CollectionListener l) {

        listenerList.add(CollectionListener.class, l);
    }

    @Override
    public final void removeCollectionListener(final CollectionListener l) {

        listenerList.remove(CollectionListener.class, l);
    }

    /**
     * Notifies all listeners that this collection has loaded another course.
     */
    private void fireCollectionLoadingEvent() {

        // guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // process the listeners last to first, notifying those that are
        // interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CollectionListener.class) {
                // create always new collection event because course is still
                // loading
                collectionEvent = new CollectionEvent(this, alreadyLoadedCourses, numberOfCourses, false);
                ((CollectionListener) listeners[i + 1]).collectionLoading(collectionEvent);
            }
        }
    }

    /**
     * Notifies all listeners that this collection has changed, either a course changed, was added
     * or removed.
     */
    private void fireCollectionChangedEvent() {

        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CollectionListener.class) {
                // Lazily create the event:
                if (collectionEvent == null) {
                    collectionEvent = new CollectionEvent(this, alreadyLoadedCourses, numberOfCourses, true);
                }
                ((CollectionListener) listeners[i + 1]).collectionChanged(collectionEvent);
            }
        }
    }
}
