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

/**
 * Saves a move in the game. A move can be the marking or occupation of a field
 * on the board or the reversal of that. Each move is recorded via its game
 * event which is send by the UI when playing. This event can later be used to
 * replay the recorded move.
 * 
 * @author Christian Wichmann
 */
public class GameRecord {

    /**
     * Default constructor.
     */
    public GameRecord() {

    }
}
