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

import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;

/**
 * Provides a nonogram from the file system.
 * 
 * @author Christian Wichmann
 */
public class NonogramFromFilesystem implements NonogramProvider {

    /*
     * TODO change class so that the nonogram is only loaded from filesystem, if
     * this function is called!
     */

    private Nonogram nonogram = null;
    private NonogramProvider nextNonogram = null;
    private NonogramProvider previousNonogram = null;
    private CourseFromFilesystem course = null;

    /**
     * Initializes a provider for a nonogram from filesystem.
     * 
     * @param n
     *            Nonogram hold by this provider.
     * @param c
     *            Course which contains this nonogram.
     */
    public NonogramFromFilesystem(final Nonogram n, final CourseFromFilesystem c) {

        nonogram = n;
        course = c;
    }

    @Override
    public final Nonogram fetchNonogram() {

        return nonogram;

    }

    @Override
    public final String getName() {

        // TODO change this function to not read nonogram. Instead use filename
        // directly to get name of nonogram.

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
