/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.GameModeType;
import org.freenono.model.GameBoard;
import org.freenono.model.Nonogram;
import org.freenono.model.Token;

public abstract class GameMode {

	private static Logger logger = Logger.getLogger(GameMode.class);

	protected GameEventHelper eventHelper = null;
	protected GameBoard gameBoard = null;
	protected Nonogram nonogram = null;
	protected Settings settings = null;
	protected GameModeType gameModeType = null;
	protected GameState state = GameState.none;

	
	private GameAdapter gameAdapter = new GameAdapter() {

		public void StateChanged(StateChangeEvent e) {
			
			state = e.getNewState(); 
		}
		
		public void MarkField(FieldControlEvent e) {

			if (state == GameState.running) {
				if (!gameBoard.canMark(e.getFieldColumn(), e.getFieldRow())) {

					// unable to mark field, maybe it is already occupied
					logger.debug("can not mark field (" + e.getFieldColumn()
							+ ", " + e.getFieldRow() + ")");

				} else {

					if (gameBoard.mark(e.getFieldColumn(), e.getFieldRow())) {
						eventHelper.fireFieldMarkedEvent(new FieldControlEvent(
								this, e.getFieldColumn(), e.getFieldRow()));
						logger.debug("field marked (" + e.getFieldColumn()
								+ ", " + e.getFieldRow() + ")");

					} else {
						eventHelper
								.fireFieldUnmarkedEvent(new FieldControlEvent(
										this, e.getFieldColumn(), e
												.getFieldRow()));
						logger.debug("field unmarked (" + e.getFieldColumn()
								+ ", " + e.getFieldRow() + ")");
					}
				}
			}
			else {
				logger.debug("Field can not be marked because game is not running.");
			}
		}

		public void OccupyField(FieldControlEvent e) {

			if (state == GameState.running) {
				if (!gameBoard.canOccupy(e.getFieldColumn(), e.getFieldRow())) {

					// unable to mark field, maybe it is already occupied
					logger.debug("can not occupy field (" + e.getFieldColumn()
							+ ", " + e.getFieldRow() + ")");
					// TODO add user message
				} else {

					if (!gameBoard.occupy(e.getFieldColumn(), e.getFieldRow())) {

						// wrong field occupied because it does not belong to the nonogram 
						eventHelper
								.fireWrongFieldOccupiedEvent(new FieldControlEvent(
										this, e.getFieldColumn(), e
												.getFieldRow()));
						logger.debug("failed to occupy field ("
								+ e.getFieldColumn() + ", " + e.getFieldRow()
								+ ")");

						// dependent on the settings mark wrongly occupied fields!
						if (settings.getMarkInvalid()) {
							gameBoard.mark(e.getFieldColumn(), e.getFieldRow());
							eventHelper
									.fireFieldMarkedEvent(new FieldControlEvent(
											this, e.getFieldColumn(), e
													.getFieldRow()));
						}
						// TODO add user message

					} else {
						eventHelper
								.fireFieldOccupiedEvent(new FieldControlEvent(
										this, e.getFieldColumn(), e
												.getFieldRow()));
						logger.debug("field occupied (" + e.getFieldColumn() + ", "
								+ e.getFieldRow() + ")");
					}
				}
			}
			else {
				logger.debug("Field can not be occupied because game is not running.");
			}
		}
	};

	
	public GameMode(GameEventHelper eventHelper, Nonogram nonogram, Settings settings) {

		this.nonogram = nonogram;
		this.settings = settings;
		
		this.gameBoard = new GameBoard(nonogram);

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	/**
	 * Calculates according to the rules of the game mode if the board stands as
	 * solved or not. The conditions for solving a game can be that all
	 * necessary fields of the nonogram are occupied or that all fields not
	 * belonging to the nonogram are marked.
	 * 
	 * @return whether the game stands as solved or not.
	 */
	public abstract boolean isSolved();

	/**
	 * Tests if game is lost by the rules defined in the concrete GameMode
	 * class. Possible tests are time constraint, penalty counts or any other
	 * parameter.
	 */
	public abstract boolean isLost();

	protected abstract void solveGame();

	protected abstract void pauseGame();

	protected abstract void resumeGame();

	protected abstract void stopGame();
	
	protected void quitGame() {
		
		eventHelper.removeGameListener(gameAdapter);
	}
	
	protected abstract Integer getGameScore(); 

	
	/**************** common methods for all GameModes ****************/

	/**
	 * Checks whether all fields not part of the nonogram are marked, so
	 * that the nonogram is essentially solved.
	 * @return
	 */
	protected boolean isSolvedThroughMarked() {

		int y;
		int x;
		int height = nonogram.height();
		int width = nonogram.width();
		boolean patternValue;
		Token fieldValue;

		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {

				patternValue = nonogram.getFieldValue(x, y);
				fieldValue = gameBoard.getFieldValue(x, y);

				if (patternValue && fieldValue == Token.MARKED) {
					return false;
				} else if (!patternValue && fieldValue == Token.FREE) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks if all fields belonging to the nonogram are occupied by the
	 * user.
	 * @return
	 */
	protected boolean isSolvedThroughOccupied() {

		int y;
		int x;
		int height = nonogram.height();
		int width = nonogram.width();
		boolean patternValue;
		Token fieldValue;

		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {

				patternValue = nonogram.getFieldValue(x, y);
				fieldValue = gameBoard.getFieldValue(x, y);

				if (patternValue && fieldValue != Token.OCCUPIED) {
					return false;
				}
			}
		}

		return true;
	}

	public GameModeType getGameModeType() {
		return gameModeType;
	}

	public void setGameModeType(GameModeType gameModeType) {
		this.gameModeType = gameModeType;
	}

}
