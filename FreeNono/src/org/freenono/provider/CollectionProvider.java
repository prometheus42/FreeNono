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

import java.util.List;

/**
 * This class provides access to a collection of courses and nonograms. The only parameter is the
 * given name of the resource.
 *
 * @author Christian Wichmann
 */
public interface CollectionProvider extends Iterable<CourseProvider> {

    /**
     * Gives back only a list of strings with the names of the courses. For this function no actual
     * nonogram file has to be read. E.g. at the NonogramFromFilesystem provider, only the directory
     * names are given.
     *
     * @return List of Names for all courses.
     */
    List<String> getCourseList();

    /**
     * Provides a list of handlers for all included courses. The returned list for all
     * implementations should be an <code>unmodifiableList</code> to protect internal data.
     *
     * @return List of course providers.
     */
    List<CourseProvider> getCourseProvider();

    /**
     * Returns the given name for this collection resource.
     *
     * @return Given name for this collection resource.
     */
    String getProviderName();

    /**
     * Identifies this collection resource by a given name, which will be shown in the UI.
     *
     * @param name
     *            Name to be used for this collection.
     */
    void setProviderName(String name);

    /**
     * Returns a string object representing this collection. Most implementations should return the
     * given provider name.
     *
     * @return String representation of collection.
     */
    @Override
    String toString();

    /**
     * Gets number of nonogram in this collection.
     *
     * @return Number of nonogram in collection.
     */
    int getNumberOfNonograms();

    /**
     * Adds a listener for this nonogram collection that will be informed of all changes.
     *
     * @param l
     *            listener to be added
     */
    void addCollectionListener(final CollectionListener l);

    /**
     * Removes a listener from this nonogram collection.
     *
     * @param l
     *            listener to be removed
     */
    void removeCollectionListener(final CollectionListener l);

}
