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
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Game.GameModeType;
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

	private GameAdapter gameAdapter = new GameAdapter() {

		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				break;

			case solved:
				solveGame();
				break;

			case paused:
				pauseGame();
				break;

			case running:
				resumeGame();
				break;

			default:
				break;
			}
		}
	};

	public GameMode(GameEventHelper eventHelper, Nonogram nonogram,
			Settings settings) {

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);

		this.nonogram = nonogram;
		this.settings = settings;

		this.gameBoard = new GameBoard(nonogram);
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

	/**************** common methods for all GameModes ****************/

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
