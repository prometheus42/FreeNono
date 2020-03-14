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

/**
 * Signals that an error occurred during loading or saving of highscore data
 * to/from file. This exception is the only one given up to the caller, all
 * internal exceptions like file not found or io exception are caught
 * internally.
 *
 * @author Christian Wichmann
 */
public class HighscoreFormatException extends Exception {

	/*
	 * TODO Is this class inheriting from right exception type?
	 */

	private static final long serialVersionUID = 9160173663118132834L;

	/**
	 * Constructs a new <code>HighscoreFormatException</code> with a message.
	 *
	 * @param message error message
	 */
	public HighscoreFormatException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new <code>HighscoreFormatException</code> with no detail
	 * message.
	 */
	public HighscoreFormatException() {
		super();
	}
}
