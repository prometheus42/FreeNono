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

import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;

/**
 * Provides a nonogram from a jar in the classpath.
 * 
 * @author Christian Wichmann
 */
public class NonogramFromJar implements NonogramProvider {

    private Nonogram nonogram = null;
    private CourseFromJar course = null;
    private NonogramProvider nextNonogram = null;
    private NonogramProvider previousNonogram = null;

    /**
     * Initializes a nonogram provider from a jar file.
     * 
     * @param n
     *            Nonogram hold by this provider.
     * @param c
     *            Course which contains this nonogram.
     */

    public NonogramFromJar(final Nonogram n, final CourseFromJar c) {

        nonogram = n;
        course = c;
    }

    @Override
    public final Nonogram fetchNonogram() {

        return nonogram;

    }

    @Override
    public final String getName() {

        return fetchNonogram().getName();

    }

    @Override
    public final String getDescription() {

        return fetchNonogram().getDescription();

    }

    @Override
    public final DifficultyLevel getDifficulty() {

        return fetchNonogram().getDifficulty();

    }

    @Override
    public final String getAuthor() {

        return fetchNonogram().getAuthor();
    }

    @Override
    public final long getDuration() {

        return fetchNonogram().getDuration();
    }

    @Override
    public final String toString() {

        return getName();

    }

    @Override
    public final int width() {

        return fetchNonogram().width();
    }

    @Override
    public final int height() {

        return fetchNonogram().height();
    }

    @Override
    public final NonogramProvider getNextNonogram() {

        if (nextNonogram == null) {

            nextNonogram = course.getNextNonogram(this);
        }
        return nextNonogram;
    }

    @Override
    public final NonogramProvider getPreviousNonogram() {

        if (previousNonogram == null) {

            previousNonogram = course.getPreviousNonogram(this);
        }
        return previousNonogram;
    }

}
