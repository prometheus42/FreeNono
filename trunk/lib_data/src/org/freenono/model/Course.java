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
package org.freenono.model;

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
        public int compare(final Course c1, Course c2) {

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

    public Course(final String name, final List<Nonogram> nonograms) {
        setName(name);
        setNonograms(nonograms);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Nonogram[] getNonograms() {
        return nonograms.toArray(new Nonogram[0]);
    }

    public int getNonogramCount() {
        return nonograms != null ? nonograms.size() : 0;
    }

    public Nonogram getNonogram(int index) {
        return nonograms.get(index);
    }

    void setNonograms(final List<Nonogram> n) {
        nonograms = n;
        Collections.sort(nonograms, Nonogram.LEVEL_ASCENDING_ORDER);
    }

    public void addNonogram(final Nonogram n) {
        nonograms.add(n);
    }

    public void removeNonogram(final Nonogram n) {
        nonograms.remove(n);
    }

    @Override
    public String toString() {
        return getName();
    }

}
