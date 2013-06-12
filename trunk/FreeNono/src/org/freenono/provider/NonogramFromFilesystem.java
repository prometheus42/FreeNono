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

import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;

/**
 * Provides a nonogram from the file system.
 * 
 * @author Christian Wichmann
 */
public class NonogramFromFilesystem implements NonogramProvider {

    private Nonogram nonogram = null;

    public NonogramFromFilesystem() {

    }

    public NonogramFromFilesystem(Nonogram n) {

        this();

        this.nonogram = n;

    }

    // TODO: change class so that the nonogram is only loaded from filesystem,
    // if this function is called!
    @Override
    public Nonogram fetchNonogram() {

        return nonogram;

    }

    @Override
    public String getName() {
        // TODO: change this function to not read nonogram. Instead use filename
        // directly to get name of nonogram.

        return fetchNonogram().getName();

    }

    @Override
    public String getDescription() {

        return fetchNonogram().getDescription();

    }

    @Override
    public DifficultyLevel getDifficulty() {

        return fetchNonogram().getDifficulty();

    }

    public String toString() {

        return getName();

    }

    @Override
    public int width() {
        return fetchNonogram().width();
    }

    @Override
    public int height() {
        return fetchNonogram().height();
    }

}
