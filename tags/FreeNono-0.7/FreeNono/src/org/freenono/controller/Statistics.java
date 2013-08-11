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
package org.freenono.controller;

import org.freenono.event.GameEventHelper;

/**
 * Interface for classes providing some kind of game statistics. Implementations
 * should get a game event helper and will then react to all game events.
 * 
 * At each time while playing or after game was won or lost properties can be
 * read. Which properties a specific implementation has is not defined.
 * 
 * Statistics begins with receiving a ProgramControlEvent with 'NONOGRAM_CHOSEN'
 * type.
 * 
 * @author Christian Wichmann
 */
public interface Statistics {

    /*
     * TODO Rethink this interface and define what statistics should be in
     * relation to Score/Highscore classes?!
     */

    /**
     * Outputs statistic information in a not defined form. Only for debug
     * reasons.
     */
    void outputStatistics();

    /**
     * Returns value for a property like 'playedTime', 'occupiedFields' or
     * 'FieldsPerMinute'.
     * 
     * @param property
     *            Property for which a value should be given. Every implemented
     *            property should give some value. Which properties are
     *            implemented may vary.
     * @return Value for given property.
     */
    Object getValue(String property);

    /**
     * Sets event helper to which statistics class should listen for events.
     * 
     * @param eventHelper
     *            Game event helper to listen for events.
     */
    void setEventHelper(GameEventHelper eventHelper);

    /**
     * Removes current event helper.
     */
    void removeEventHelper();

}
