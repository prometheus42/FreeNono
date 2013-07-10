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

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Stores a representation of the game board as user plays the game.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class GameBoard {

    private static Logger logger = Logger.getLogger(GameBoard.class);

    private Nonogram pattern = null;
    private Token[][] field = null;

    /**
     * Default constructor initializing a GameBoard with free fields and storing
     * the pattern of the current nonogram for future references.
     * 
     * @param nonogram
     *            current nonogram which should be played
     */
    public GameBoard(final Nonogram nonogram) {

        logger.debug("New GameBoard object instantiated.");

        pattern = nonogram;

        field = new Token[pattern.height()][pattern.width()];
        for (Token[] row : field) {
            Arrays.fill(row, Token.FREE);
        }
    }

    /**
     * Returns the value of a specific field on the game board.
     * 
     * @param x
     *            x-coordinate for field
     * @param y
     *            y-coordinate for field
     * @return field value as Token
     */
    public final Token getFieldValue(final int x, final int y) {

        if (x < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (x >= width()) {
            throw new IndexOutOfBoundsException();
        }
        if (y < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (y >= height()) {
            throw new IndexOutOfBoundsException();
        }

        return this.field[y][x];
    }

    /**
     * Returns the width of current nonogram.
     * @return width of stored nonogram pattern
     */
    private int width() {

        return pattern.width();
    }

    /**
     * Returns the height of current nonogram.
     * @return height of stored nonogram pattern
     */
    private int height() {

        return pattern.height();
    }

    /**
     * Checks whether a field can be marked. It can not be marked if it is
     * already occupied.
     * 
     * @param x
     *            Specifies the horizontal index of the field.
     * @param y
     *            Specifies the vertical index of the field.
     * @return true, if the field was marked, false if it was unmarked.
     * 
     */
    public final boolean canMark(final int x, final int y) {

        if (getFieldValue(x, y) == Token.OCCUPIED) {
            return false;
        }
        return true;

    }

    /**
     * Try to mark a field.
     * 
     * @param x
     *            Specifies the horizontal index of the field.
     * @param y
     *            Specifies the vertical index of the field.
     * @return true, if the field was marked, false if it was unmarked.
     * 
     */
    public final boolean mark(final int x, final int y) {

        switch (getFieldValue(x, y)) {
        case FREE:
            field[y][x] = Token.MARKED;
            return true;
        case MARKED:
            field[y][x] = Token.FREE;
            return false;
        case OCCUPIED:
            break;
        default:
            assert false : getFieldValue(x, y);
            break;
        }

        return false;
    }

    /**
     * Checks if the specified field could be target of the next move. It
     * returns false, if the specified field is marked or already occupied.
     * 
     * @param x
     *            Specifies the horizontal index of the field.
     * @param y
     *            Specifies the vertical index of the field.
     * @return true, if the specified field is valid for the next move.
     * 
     */
    public final boolean canOccupy(final int x, final int y) {

        // can not occupy if field is already marked or occupied
        switch (getFieldValue(x, y)) {
        case MARKED:
            logger.debug("marked");
            return false;
        case OCCUPIED:
            logger.debug("occupied");
            return false;
        case FREE:
            break;
        default:
            assert false : getFieldValue(x, y);
            break;
        }

        return true;
    }

    /**
     * Try to make a move and occupy a field.
     * 
     * @param x
     *            Specifies the horizontal index of the field.
     * @param y
     *            Specifies the vertical index of the field.
     * 
     * @return true, if the move was valid and the field successfully occupied.
     * 
     */
    public final boolean occupy(final int x, final int y) {

        if (pattern.getFieldValue(x, y)) {
            field[y][x] = Token.OCCUPIED;
            return true;
        } else {
            // field[y][x] = Token.MARKED;
            return false;
        }
    }

    /**
     * Solves the game. This functions sets all field values to the right values
     * so that the nonogram is solved. This function should be called after to
     * clear the field for a nice view.
     */
    public final void solveGame() {

        int y;
        int x;
        int height = pattern.height();
        int width = pattern.width();

        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {

                if (pattern.getFieldValue(x, y)) {
                    field[y][x] = Token.OCCUPIED;
                } else {
                    // field[y][x] = Token.MARKED;
                    field[y][x] = Token.FREE;
                }
            }
        }
    }
}
