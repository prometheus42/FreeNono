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
 * This enumeration specifies the difficulty levels of a nonogram.
 * 
 * @author Markus Wichmann
 */
public enum DifficultyLevel {

    /**
     * The nonogram has no defined difficulty.
     */
    undefined,

    /**
     * The nonogram is very easy to solve.
     */
    easiest,

    /**
     * The nonogram is easy to solve.
     */
    easy,

    /**
     * The nonogram has a avarage difficulty.
     */
    normal,

    /**
     * The nonogram is hard to solve.
     */
    hard,

    /**
     * The nonogram is very hard to solve.
     */
    hardest;
}
