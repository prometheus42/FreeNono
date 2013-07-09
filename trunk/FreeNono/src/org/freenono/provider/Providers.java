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

/**
 * Static factory class providing <code>CollectionProvider</code> for various
 * sources of nonogram collections.
 * 
 * @author Christian Wichmann
 */
public final class Providers {

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private Providers() {
    }

    /**
     * Provides a collection from file system by its path.
     * 
     * @param rootPath
     *            path to search for nonogram courses
     * @param name
     *            name of the provider
     * @param concurrently
     *            if data should be read in a seperate thread
     * @return a collection provider containing all courses from file system
     */
    public static CollectionProvider collectionFromFilesystem(
            final String rootPath, final String name, final boolean concurrently) {

        return new CollectionFromFilesystem(rootPath, name, concurrently);
    }

    /**
     * Provides a collection from file system by its path. The data is read
     * synchronously.
     * 
     * @param rootPath
     *            path to search for nonogram courses
     * @param name
     *            name of the provider
     * @return a collection provider containing all courses from file system
     */
    public static CollectionProvider collectionFromFilesystem(
            final String rootPath, final String name) {

        return new CollectionFromFilesystem(rootPath, name, false);
    }

    /**
     * Provides a collection from a single jar file.
     * 
     * @param jarPath
     *            jar file the contains nonogram courses
     * @param name
     *            name of the provider
     * @return a collection provider containing all courses from jar file
     */
    public static CollectionProvider collectionFromJar(final String jarPath,
            final String name) {

        return new CollectionFromJar(jarPath, name);
    }

    /**
     * Provides a collection from a single jar file. This collection searches
     * automatically for nonogram collections in a jar file and loads it.
     * 
     * @param name
     *            name of the provider
     * @return a collection provider containing all courses from jar file
     */
    public static CollectionProvider collectionFromJar(final String name) {

        return new CollectionFromJar(name);
    }

    /**
     * Provides a collection containing randomly generated nonograms.
     * 
     * @param name
     *            name of the provider
     * @return a collection provider containing randomly generated nonograms
     */
    public static CollectionProvider collectionFromSeed(final String name) {

        return new CollectionFromSeed(name);
    }

    /**
     * Provides a collection from a NonoServer by its url.
     * 
     * @param serverURL
     *            url of NonoServer with nonogram courses
     * @param name
     *            name of the provider
     * @return a collection provider containing all courses from NonoServer
     */
    public static CollectionProvider collectionFromServer(
            final String serverURL, final String name) {

        return new CollectionFromServer(serverURL, name);
    }
}
