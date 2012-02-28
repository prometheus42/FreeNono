/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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
 * This enumeration specifies what states a single token eg. a field in the
 * nonogram can be in.
 * 
 * @author Markus Wichmann
 * @author Christian Wichmann
 * 
 */
public enum Token {
	/**
	 * The field in the nonogram is free, not yet selected
	 */
	FREE,
	/**
	 * The field is correctly identified by the user as part of the nonogram
	 */
	OCCUPIED,
	/**
	 * The field is marked to be clear
	 */
	MARKED
}
