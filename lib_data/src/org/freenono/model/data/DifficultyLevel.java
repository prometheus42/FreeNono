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

/**
 * This enumeration specifies the difficulty levels of a nonogram.
 * 
 * @author Markus Wichmann
 */
public enum DifficultyLevel {

    /**
     * The nonogram has no defined difficulty.
     */
    UNDEFINED,

    /**
     * The nonogram is very easy to solve.
     */
    EASIEST,

    /**
     * The nonogram is easy to solve.
     */
    EASY,

    /**
     * The nonogram has a avarage difficulty.
     */
    NORMAL,

    /**
     * The nonogram is hard to solve.
     */
    HARD,

    /**
     * The nonogram is very hard to solve.
     */
    HARDEST;
}
