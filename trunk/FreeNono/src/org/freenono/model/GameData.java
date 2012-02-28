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

import org.freenono.event.GameEvent;

class GameData {

	private Game game;

	private Nonogram pattern = null;
	private Token[][] field = null;

	private boolean markInvalid = true;
	private boolean countMarked = true;

	GameData(Game game, Nonogram nonogram) {

		this.game = game;
		this.pattern = nonogram;

		this.field = new Token[pattern.height()][pattern.width()];
		for (int i = 0; i < this.field.length; i++) {
			for (int j = 0; j < this.field[i].length; j++) {
				this.field[i][j] = Token.FREE;
			}
		}
	}

	/*************** game data ***************/

	public Nonogram getPattern() {
		return pattern;
	}

	public int width() {
		return pattern.width();
	}

	public int height() {
		return pattern.height();
	}

	public Token getFieldValue(int x, int y) {

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

	/*************** options ***************/

	/**
	 * Retrieves whether moves on invalid fields should mark them.
	 */
	public boolean getMarkInvalid() {

		return markInvalid;

	}

	/**
	 * Sets whether moves on invalid fields should mark them.
	 */
	public void setMarkInvalid(boolean markInvalid) {

		this.markInvalid = markInvalid;

	}

	/**
	 * Retrieves whether marked fields should count during solve checks. If this
	 * value is true the solve check will also check, whether all invalid fields
	 * are marked.
	 * 
	 * @return
	 */
	public boolean getCountMarked() {

		return countMarked;

	}

	/**
	 * Sets whether marked fields should count during solve checks. If this
	 * value is set to true the solve check will also check, whether all invalid
	 * fields are marked.
	 * 
	 * @param countMarked
	 */
	public void setCountMarked(boolean countMarked) {

		this.countMarked = countMarked;

	}

	/*************** moves ***************/

	/**
	 * Checks if the specified field could be target of a mark action. It
	 * returns false, if the game is already over or the specified field is
	 * occupied.
	 * 
	 * @param x
	 *            Specifies the horizontal index of the field.
	 * @param y
	 *            Specifies the vertical index of the field.
	 * @return true, if the specified field is valid for move action.
	 * 
	 */
	public boolean canMark(int x, int y) {

		// can't mark a field if game is over...
		if (game.getFlow().isOver()) {
			return false;
		}
		// or if it is not running, e.g. paused
		if (!game.getFlow().isRunning()) {
			return false;
		}

		switch (getFieldValue(x, y)) {
		case OCCUPIED:
			return false;
		}

		return true;
	}

	/**
	 * Try to mark a field. You must call {@link canMark} previously. Otherwise
	 * it is impossible to determine the reason for a negative return value.
	 * 
	 * @param x
	 *            Specifies the horizontal index of the field.
	 * @param y
	 *            Specifies the vertical index of the field.
	 * @return true, if the field successfully marked.
	 * 
	 */
	public boolean mark(int x, int y) {

		if (!canMark(x, y)) {
			return false;
		}

		switch (field[y][x]) {
		case FREE:
			game.getFlow().increaseMarkCount();
			field[y][x] = Token.MARKED;
			game.isSolved();
			game.getEventHelper().fireFieldMarkedEvent(
					new GameEvent(this, x, y));
			return true;
		case MARKED:
			game.getFlow().increaseUnmarkCount();
			field[y][x] = Token.FREE;
			game.isSolved();
			game.getEventHelper().fireFieldUnmarkedEvent(
					new GameEvent(this, x, y));
			return true;
		}
		
		game.isSolved();
		game.getFlow().checkEndConditions();

		return false;
	}

	/**
	 * Checks if the specified field could be target of the next move. It
	 * returns false, if the game is already over, the specified field is marked
	 * or already occupied.
	 * 
	 * @param x
	 *            Specifies the horizontal index of the field.
	 * @param y
	 *            Specifies the vertical index of the field.
	 * @return true, if the specified field is valid for the next move.
	 * 
	 */
	public boolean canOccupy(int x, int y) {

		// can't occupy a field if game is over...
		if (game.getFlow().isOver()) {
			return false;
		}
		// or if it is not running, e.g. paused
		if (!game.getFlow().isRunning()) {
			return false;
		}

		switch (getFieldValue(x, y)) {
		case MARKED:
			return false;
		case OCCUPIED:
			return false;
		}

		return true;
	}

	/**
	 * Try to make a move and occupy a field. You must call {@link canOccupy}
	 * previously. Otherwise it is impossible to determine the reason for a
	 * negative return value.
	 * 
	 * @param x
	 *            Specifies the horizontal index of the field.
	 * @param y
	 *            Specifies the vertical index of the field.
	 * @param markInvalid
	 *            A boolean value, that specifies if a invalid move should mark
	 *            the field.
	 * @return true, if the move was valid and the field successfully occupied.
	 * 
	 */
	public boolean occupy(int x, int y) {

		// TODO change markInvalid to a member and add getter/setter?

		if (!canOccupy(x, y)) {
			return false;
		}

		if (this.pattern.getFieldValue(x, y)) {
			field[y][x] = Token.OCCUPIED;
			game.getFlow().increaseSuccessCount();
			game.isSolved();
			game.getEventHelper().fireFieldOccupiedEvent(
					new GameEvent(this, x, y));
			return true;
		} else {
			if (this.markInvalid) {
				field[y][x] = Token.MARKED;
			}
			game.isSolved();
			game.getFlow().increaseFailCount();
			game.getFlow().checkEndConditions();
			// send out event WrongFieldOccupied
			game.getEventHelper().fireWrongFieldOccupiedEvent(
					new GameEvent(this, x, y));
			return false;
		}
	}

	/*************** solved ***************/

	public boolean isSolved() {

		if (this.countMarked && isSolvedThroughMarked()) {
			return true;
		}
		if (isSolvedThroughOccupied()) {
			return true;
		}

		return false;
	}

	private boolean isSolvedThroughMarked() {

		int y;
		int x;
		int height = height();
		int width = width();
		boolean patternValue;
		Token fieldValue;

		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {

				patternValue = pattern.getFieldValue(x, y);
				fieldValue = field[y][x];

				if (patternValue && fieldValue == Token.MARKED) {
					return false;
				} else if (!patternValue && fieldValue == Token.FREE) {
					return false;
				}
			}
		}

		return true;

	}

	private boolean isSolvedThroughOccupied() {

		int y;
		int x;
		int height = height();
		int width = width();
		boolean patternValue;
		Token fieldValue;

		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {

				patternValue = pattern.getFieldValue(x, y);
				fieldValue = field[y][x];

				if (patternValue && fieldValue != Token.OCCUPIED) {
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * Solves the game. This functions sets all field values to the right values
	 * so that the nonogram is solved. This function should be called after
	 * {@link endGame()} to clear the field for a nice view.
	 */
	public void solveGame() {

		int y;
		int x;
		int height = height();
		int width = width();

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
