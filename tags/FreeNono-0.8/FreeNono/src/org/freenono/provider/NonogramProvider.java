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

import java.util.Comparator;

import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;

/**
 * Provides a nonogram inside a course inside a collection from some source.
 * Lazy loading can be implemented because the nonogram is only fetched when a
 * property is requested or method fetchNonogram() is called.
 * 
 * @author Christian Wichmann
 */
public interface NonogramProvider {

    Comparator<NonogramProvider> NAME_ASCENDING_ORDER = new Comparator<NonogramProvider>() {

        @Override
        public int compare(final NonogramProvider n1, final NonogramProvider n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                return n1.getName().compareTo(n2.getName());
            }
        }
    };

    Comparator<NonogramProvider> LEVEL_ASCENDING_ORDER = new Comparator<NonogramProvider>() {

        @Override
        public int compare(final NonogramProvider n1, final NonogramProvider n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                int level1 = n1.fetchNonogram().getLevel();
                int level2 = n2.fetchNonogram().getLevel();
                return new Integer(level1).compareTo(new Integer(level2));
            }
        }
    };

    /**
     * Fetches nonogram from whatever source it comes from.
     * 
     * @return Nonogram of this provider.
     */
    Nonogram fetchNonogram();

    /**
     * Returns name of this nonogram.
     * 
     * @return Name of nonogram.
     */
    String getName();

    /**
     * Returns description of this nonogram.
     * 
     * @return description of nonogram
     */
    String getDescription();

    /**
     * Returns width of this nonogram.
     * 
     * @return width of nonogram
     */
    int width();

    /**
     * Returns height of this nonogram.
     * 
     * @return height of nonogram
     */
    int height();

    /**
     * Returns difficulty of this nonogram.
     * 
     * @return difficulty of nonogram
     */
    DifficultyLevel getDifficulty();

    /**
     * Returns author of this nonogram.
     * 
     * @return author of nonogram
     */
    String getAuthor();

    /**
     * Returns duration for this nonogram.
     * 
     * @return duration for nonogram
     */
    long getDuration();

    /**
     * Returns a string object representing this nonogram. Most implementations
     * should return the name of the nonogram.
     * 
     * @return String representation of nonogram
     */
    String toString();

    /**
     * Returns a <code>NonogramProvider</code> for the next nonogram in the
     * course of this nonogram. Which nonogram is chosen isn't determined but
     * each implementation should respect level attribute of nonograms.
     * 
     * @return NonogramProvider for next nonogram or <code>null</code> if no
     *         next nonogram exists
     */
    NonogramProvider getNextNonogram();

    /**
     * Returns a <code>NonogramProvider</code> for the previous nonogram in the
     * course of this nonogram. Which nonogram is chosen isn't determined but
     * each implementation should respect 'level' attribute of nonograms.
     * 
     * @return NonogramProvider for previous nonogram or <code>null</code> if no
     *         previous nonogram exists
     */
    NonogramProvider getPreviousNonogram();
}
