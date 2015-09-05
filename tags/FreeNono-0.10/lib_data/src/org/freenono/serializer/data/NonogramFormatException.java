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
package org.freenono.serializer.data;

/**
 * Signals that an attempt to read a concrete nonogram has failed, because it doesn't match the
 * expected format.
 *
 * @author Markus Wichmann
 */
public class NonogramFormatException extends Exception {

    private static final long serialVersionUID = -8321639053112356638L;

    /**
     * Constructs a new {@code NonogramFormatException} with {@code null} as its detail message. The
     * cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}
     * .
     */
    public NonogramFormatException() {
        super();
    }

    /**
     * Constructs a new {@code NonogramFormatException} with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the
     *            {@link #getMessage()} method.
     */
    public NonogramFormatException(final String message) {
        super(message);
    }
}
