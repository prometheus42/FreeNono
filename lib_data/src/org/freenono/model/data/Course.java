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
package org.freenono.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Stores a course of Nonograms under a common course name.
 * 
 * @author Markus Wichmann
 */
public class Course {

    public static final Comparator<Course> NAME_ASCENDING_ORDER = new Comparator<Course>() {

        @Override
        public int compare(final Course c1, final Course c2) {

            if (c1 == null && c2 == null) {
                return 0;
            } else if (c1 == null) {
                return -1;
            } else if (c2 == null) {
                return 1;
            } else {
                return c1.getName().compareTo(c2.getName());
            }

        }
    };

    public static final Comparator<Course> NAME_DESCENDING_ORDER = new Comparator<Course>() {

        @Override
        public int compare(final Course c1, final Course c2) {

            if (c1 == null && c2 == null) {
                return 0;
            } else if (c1 == null) {
                return -1;
            } else if (c2 == null) {
                return 1;
            } else {
                return c1.getName().compareTo(c2.getName());
            }

        }
    };

    private String name = "";
    private List<Nonogram> nonograms = new ArrayList<Nonogram>();

    /**
     * Constructs a course from a list of nonograms under the given name.
     * 
     * @param name
     *            name of the new course
     * @param nonograms
     *            list of nonograms to include in this course
     */
    public Course(final String name, final List<Nonogram> nonograms) {

        if (name == null || nonograms == null) {
            throw new NullPointerException(
                    "Arguments of constructor should not be null.");
        }
        setName(name);
        setNonograms(nonograms);
    }

    /**
     * Gets name of this course.
     * 
     * @return name of this course
     */
    public final String getName() {

        return name;
    }

    /**
     * Sets name of this course. Course names can be used to identify them in
     * the user interface.
     * 
     * @param name
     *            name of this course
     */
    public final void setName(final String name) {

        this.name = name;
    }

    /**
     * Gets an array with all nonograms in this course.
     * 
     * @return list of all nonograms
     */
    public final Nonogram[] getNonograms() {

        return nonograms.toArray(new Nonogram[0]);
    }

    /**
     * Returns the number of nonograms in this course.
     * 
     * @return number of nonograms
     */
    public final int getNonogramCount() {

        assert nonograms != null;

        return nonograms.size();
    }

    /**
     * Gets nonogram by index. Index must be between 0 and
     * <code>getNonogramCount()</code>.
     * 
     * @param index
     *            index of nonogram to get
     * @return nonogram at index
     */
    public final Nonogram getNonogram(final int index) {

        if (index < 0 || index > getNonogramCount()) {
            throw new IndexOutOfBoundsException(
                    "Index not valid for nonogram list.");
        }
        return nonograms.get(index);
    }

    /**
     * Sets internal nonograms list.
     * 
     * @param n
     *            List containing nonograms to be set.
     */
    private void setNonograms(final List<Nonogram> n) {

        nonograms = n;
        Collections.sort(nonograms, Nonogram.LEVEL_ASCENDING_ORDER);
    }

    /**
     * Adds nonogram to course list.
     * 
     * @param n
     *            nonogram to be added.
     */
    public final void addNonogram(final Nonogram n) {

        nonograms.add(n);
    }

    /**
     * Removes given nonogram from course list.
     * 
     * @param n
     *            nonogram to be removed.
     */
    public final void removeNonogram(final Nonogram n) {

        nonograms.remove(n);
    }

    @Override
    public final String toString() {

        return getName();
    }

    /**
     * Returns the highest difficulty of any nonogram in this course.
     * 
     * @return highest difficulty in this course
     */
    public final DifficultyLevel getMaximumDifficulty() {

        DifficultyLevel maximum = DifficultyLevel.UNDEFINED;

        for (Nonogram n : nonograms) {

            if (n.getDifficulty().compareTo(maximum) > 0) {

                maximum = n.getDifficulty();
            }
        }
        return maximum;
    }

    /**
     * Returns the lowest difficulty of any nonogram in this course.
     * 
     * @return lowest difficulty in this course
     */
    public final DifficultyLevel getMinimumDifficulty() {

        DifficultyLevel minimum = DifficultyLevel.HARDEST;

        for (Nonogram n : nonograms) {

            if (n.getDifficulty().compareTo(minimum) < 0) {

                minimum = n.getDifficulty();
            }
        }
        return minimum;
    }
}
