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
package de.ichmann.markusw.java.apps.freenono.model;

import java.util.Date;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.freenono.event.*;

public class Game {

	private static Logger logger = Logger.getLogger(Game.class);

	private GameData data = null;
	private final GameFlow flow = new GameFlow(this);
	private final GameEventHelper eventHelper = new GameEventHelper(this);

	Game(Nonogram pattern) {

		if (pattern == null) {
			throw new NullPointerException("pattern parameter is null");
		}

		data = new GameData(this, pattern);

	}

	Game(Nonogram pattern, int maxFailCount) {

		this(pattern);

		flow.setMaxFailCount(maxFailCount);
	}

	Game(Nonogram pattern, long maxTime) {

		this(pattern);

		flow.setMaxTime(maxTime);

	}

	Game(Nonogram pattern, int maxFailCount, long maxTime) {

		this(pattern);

		flow.setMaxFailCount(maxFailCount);
		flow.setMaxTime(maxTime);

	}

	/*************** general game parts ***************/

	static Logger getLogger() {
		return logger;
	}

	GameData getData() {
		return data;
	}

	GameFlow getFlow() {
		return flow;
	}

	GameEventHelper getEventHelper() {
		return eventHelper;
	}

	/*************** events ***************/

	public void addGameListener(GameListener l) {
		eventHelper.addGameListener(l);
	}

	public void removeGameListener(GameListener l) {
		eventHelper.removeGameListener(l);
	}

	/*************** data ***************/

	public Nonogram getPattern() {

		return data.getPattern();

	}

	public int width() {

		return data.width();

	}

	public int height() {

		return data.height();

	}

	public Token getFieldValue(int x, int y) {

		return data.getFieldValue(x, y);

	}

	/*************** solved ***************/

	/**
	 * Returns whether this game is solved. It also calls the necessary event
	 * when it is
	 */
	public boolean isSolved() {

		boolean isSolved = data.isSolved();

		if (isSolved && !flow.isOver()) {
			flow.gameSolved();
		}

		return isSolved;
	}

	/**
	 * Solves the game. This functions sets all field values to the right values
	 * so that the nonogram is solved. This function should be called after
	 * {@link endGame()} to clear the field for a nice view.
	 */
	public void solveGame() {

		data.solveGame();
		isSolved();

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

		return data.canMark(x, y);

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

		return data.mark(x, y);

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

		return data.canOccupy(x, y);

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

		return data.occupy(x, y);

	}

	/*************** flow ***************/

	/**
	 * Starts the game.
	 */
	public void startGame() {

		flow.startGame();

	}

	/**
	 * Interrupts the game for a short period of time.
	 */
	public void pauseGame() {

		flow.pauseGame();

	}

	/**
	 * Restarts the game after it has been paused.
	 */
	public void resumeGame() {

		flow.resumeGame();

	}

	/**
	 * Stops the game.
	 */
	public void stopGame() {

		flow.stopGame();

	}

	public boolean isOver() {

		return flow.isOver();

	}

	public boolean isRunning() {

		return flow.isRunning();

	}

	public GameState getState() {
		return flow.getState();
	}

	/*************** end conditions ***************/

	public int getMaxFailCount() {

		return flow.getMaxFailCount();

	}

	void setMaxFailCount(int maxFailCount) {

		flow.setMaxFailCount(maxFailCount);

	}

	public long getMaxTime() {

		return flow.getMaxTime();

	}

	void setMaxTime(long maxTime) {

		flow.setMaxTime(maxTime);

	}

	public boolean usesMaxFailCount() {
		return flow.usesMaxFailCount();
	}

	public boolean usesMaxTime() {
		return flow.usesMaxTime();
	}

	/*************** time ***************/

	public Date getElapsedTime() {

		return flow.getElapsedTime();

	}

	public Date getTimeLeft() {

		return flow.getTimeLeft();

	}

	/*************** fail count ***************/
	
	public int getFailCountLeft() {
		if (flow.usesMaxFailCount()) {
			return flow.getMaxFailCount() - flow.getFailCount();
		} else {
			return 0;
		}
	}
	
	/*************** options ***************/

	/**
	 * Retrieves whether moves on invalid fields should mark them.
	 */
	public boolean getMarkInvalid() {

		return data.getMarkInvalid();

	}

	/**
	 * Sets whether moves on invalid fields should mark them.
	 */
	void setMarkInvalid(boolean markInvalid) {

		data.setMarkInvalid(markInvalid);

	}

	/**
	 * Retrieves whether marked fields should count during solve checks. If this
	 * value is true the solve check will also check, whether all invalid fields
	 * are marked.
	 * 
	 * @return
	 */
	public boolean getCountMarked() {

		return data.getCountMarked();

	}

	/**
	 * Sets whether marked fields should count during solve checks. If this
	 * value is set to true the solve check will also check, whether all invalid
	 * fields are marked.
	 * 
	 * @param countMarked
	 */
	void setCountMarked(boolean countMarked) {

		data.setCountMarked(countMarked);

	}

}
