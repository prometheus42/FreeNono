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
package org.freenono.serializer;

import java.io.File;
import java.io.IOException;

import org.freenono.model.Nonogram;

/**
 * Serializes one or more nonograms to file.
 * 
 * @author Markus Wichmann
 */
public interface NonogramSerializer {

    /**
     * Load nonogram from file.
     * @param f
     *            File handle.
     * @return Array of nonograms
     * @throws IOException Thrown if 'file' is directory.
     * @throws NonogramFormatException Thrown if file is not well formed
     */
    Nonogram[] load(File f) throws IOException, NonogramFormatException;

    /**
     * Save nonograms to file.
     * @param f File handle.
     * @param n One or multiple nonograms.
     * @throws IOException if file could not be written
     */
    void save(File f, Nonogram... n) throws IOException;
}
