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

/**
 * This enumeration specifies certain states, a game can be in.
 * 
 * @author Markus Wichmann
 */
public enum GameState {
    /**
     * The game is in an undefined state, e.g. it is created but not yet
     * started.
     */
    none,

    /**
     * The game has been started and is currently running. This state is also
     * used after a pause has ended.
     */
    running,

    /**
     * The game has been started, but is currently paused.
     */
    paused,

    /**
     * The game is over, because the user aborted the it during runtime.
     */
    userStop,

    /**
     * The game is over, because some of the end conditions are met.
     */
    gameOver,

    /**
     * The game is over, because it was solved.
     */
    solved,
}
