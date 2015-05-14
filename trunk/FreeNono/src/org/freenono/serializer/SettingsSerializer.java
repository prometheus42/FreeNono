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

import org.freenono.controller.Settings;

/**
 * Interface defining a serializer to store settings as a file.
 *
 * @author Markus Wichmann
 */
public interface SettingsSerializer {

    /**
     * Loads settings from a given file. If file can not be opened or does not exist a default
     * <code>Settings</code> object is returned. When file contains invalid data a
     * <code>SettingsFormatException</code> is thrown.
     *
     * @param f
     *            file to load settings from
     * @return settings object loaded from file or default settings object when file could not be
     *         loaded
     * @throws SettingsFormatException
     *             if settings file has wrong file format
     */
    Settings load(File f) throws SettingsFormatException;

    /**
     * Saves settings to file.
     *
     * @param s
     *            settings object
     * @param f
     *            file to save settings to
     */
    void save(Settings s, File f);

}
