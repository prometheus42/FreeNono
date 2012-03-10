/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Markus Wichmann, Christian Wichmann
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
import org.freenono.event.*;
import org.freenono.model.GameMode;

public class Game {

	private static Logger logger = Logger.getLogger(Game.class);

	private GameMode gameMode = null;
	private GameEventHelper eventHelper = null;
	private Settings settings;
	private Nonogram pattern;
	private GameState state = GameState.none;

	public enum GameModeType {
		GameMode_Penalty, GameMode_MaxTime, GameMode_MaxFail
	};

	public class GameModeException extends Exception {

		private static final long serialVersionUID = -5216243640288343983L;

	};

	private GameAdapter gameAdapter = new GameAdapter() {

		public void FieldOccupied(FieldControlEvent e) {

			checkGame();
		}

		public void FieldMarked(FieldControlEvent e) {

			checkGame();
		}

		public void FieldUnmarked(FieldControlEvent e) {

			checkGame();
		}

		public void WrongFieldOccupied(FieldControlEvent e) {

			checkGame();
		}

		public void SetTime(StateChangeEvent e) {

			checkGame();
		}

		public void Timer(StateChangeEvent e) {

			checkGame();
		}

		public void ProgramControl(ProgramControlEvent e) {
			switch (e.getPct()) {
			case START_GAME:
				break;

			case STOP_GAME:
				stopGame();
				break;

			case RESTART_GAME:
				restartGame();
				break;

			case PAUSE_GAME:
				pauseGame();
				break;

			case RESUME_GAME:
				resumeGame();
				break;

			case NONOGRAM_CHOSEN:
				break;

			case QUIT_PROGRAMM:
				break;
			}
		}
	};

	public Game(GameEventHelper eventHelper, Nonogram pattern, Settings settings) {

		this.pattern = pattern;
		this.settings = settings;

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);

		startGame();
	}

	/**
	 * Starts the game.
	 */
	public void startGame() {

		if (state == GameState.none || state == GameState.gameOver
				|| state == GameState.solved) {

			GameState oldState = state;
			state = GameState.running;

			switch (settings.getGameMode()) {
			case GameMode_Penalty:
				gameMode = new GameMode_Penalty(eventHelper, pattern, settings);
				break;

			case GameMode_MaxFail:
				break;

			case GameMode_MaxTime:
				break;
			}

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));

		} else if (state == GameState.paused) {

			// TODO implement this!
		} else if (state == GameState.running) {

			// TODO implement this!
		} else {

			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Restarts the game.
	 */
	public void restartGame() {

	}

	/**
	 * Interrupts the game for a short period of time.
	 */
	public void pauseGame() {

		if (state == GameState.running) {

			GameState oldState = state;

			state = GameState.paused;

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));

			// TODO do additional things here
		} else {
			// game is not in started state: do nothing? throw exception?
			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Restarts the game after it has been paused.
	 */
	public void resumeGame() {

		if (state == GameState.paused) {

			GameState oldState = state;

			state = GameState.running;

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));

		} else {

			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Stops the game.
	 */
	public void stopGame() {

		if (state == GameState.running || state == GameState.paused) {

			GameState oldState = state;

			state = GameState.userStop;

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));

		} else {

			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Checks whether the running game is solved or lost by the rules according
	 * to the loaded game mode!
	 */
	private void checkGame() {

		// if a current game mode is loaded...
		if (gameMode != null) {

			// and if the game is still running...
			if (state == GameState.running) {

				// check if game is solved or lost!
				if (gameMode.isSolved()) {

					GameState oldState = state;
					state = GameState.solved;
					eventHelper.fireStateChangedEvent(new StateChangeEvent(
							this, oldState, state));
				} else {

					if (gameMode.isLost()) {

						GameState oldState = state;
						state = GameState.gameOver;
						eventHelper.fireStateChangedEvent(new StateChangeEvent(
								this, oldState, state));
					}
				}
			}
		}
	}

}
