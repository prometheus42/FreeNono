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
 * Signals if data belongs to the captions on the rows or for the columns. Used by
 * <code>BoradTileSetCaption</code> instances to define whether it paints row or column captions.
 * Also it is used by the model when firing a cross-out-caption event.
 *
 * @author Christian Wichmann
 */
public enum CaptionOrientation {

    /**
     * Captions belong to columns.
     */
    ORIENTATION_COLUMN,

    /**
     * Captions belong to rows.
     */
    ORIENTATION_ROW
}
