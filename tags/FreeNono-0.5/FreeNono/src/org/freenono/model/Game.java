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

/**
 * This class constructs instantiates a game mode class. Furthermore it 
 * administrats the game state and fires the necessary state change events
 * to inform all other components of the program. 
 */
public class Game {

	private static Logger logger = Logger.getLogger(Game.class);

	private GameMode gameMode = null;
	private GameEventHelper eventHelper = null;
	private GameModeFactory gameModeFactory = null;
	private Settings settings;
	private Nonogram pattern;
	private GameState state = GameState.none;
	

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
				startGame();
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
				pattern = e.getPattern();
				break;

			case QUIT_PROGRAMM:
				quitGame();
				break;
			}
		}
	};

	public Game(GameEventHelper eventHelper, Nonogram pattern, Settings settings) {

		this.pattern = pattern;
		this.settings = settings;

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
		
		gameModeFactory = new GameModeFactory();
	}

	/**
	 * Starts the game.
	 */
	public void startGame() {

		if (gameMode != null) {
			
			gameMode.stopGame();
			gameMode.quitGame();
			gameMode = null;
		}

		if (state == GameState.none || state == GameState.gameOver
				|| state == GameState.solved || state == GameState.userStop
				|| state == GameState.paused) {

			GameState oldState = state;
			state = GameState.running;

			// get game mode class from factory defined in settings
			gameMode = gameModeFactory.getGameMode(eventHelper, pattern, settings);

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));
			logger.info("Game started...");

		} else if (state == GameState.running) {

			// if game is already running do nothing whatsoever, yet!
			
		} else {

			logger.error("Illegal game state!");
		}
	}

	/**
	 * Restarts the game.
	 */
	public void restartGame() {
			
		if (gameMode != null) {
			
			gameMode.stopGame();
			gameMode.quitGame();
			gameMode = null;
		}

		GameState oldState = state;
		state = GameState.running;

		// get game mode class from factory defined in settings
		gameMode = gameModeFactory.getGameMode(eventHelper, pattern, settings);

		eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
				oldState, state));
		logger.info("Game restarted...");
	}

	/**
	 * Interrupts the game for a short period of time.
	 */
	public void pauseGame() {

		if (state == GameState.running) {

			GameState oldState = state;

			state = GameState.paused;
			
			gameMode.pauseGame();

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));
			logger.info("Game paused...");

		} else {
			
			// if game is not started, do nothing
		}
	}

	/**
	 * Restarts the game after it has been paused.
	 */
	public void resumeGame() {

		if (state == GameState.paused) {

			GameState oldState = state;

			state = GameState.running;
			
			gameMode.resumeGame();

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));
			logger.info("Game resumed...");

		} else {

			// do nothing if game is not currently paused
		}
	}

	/**
	 * Stops the game.
	 */
	public void stopGame() {

		if (state == GameState.running || state == GameState.paused) {

			GameState oldState = state;

			state = GameState.userStop;
			
			gameMode.stopGame();
			gameMode.quitGame();
			gameMode = null;

			eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
					oldState, state));
			logger.info("Game stopped...");

		} else {

			// do nothing if game is already stopped or won/lost.
		}
	}

	private void quitGame() {

		if (gameMode != null) {

			gameMode.stopGame();
			gameMode.solveGame();
			gameMode.quitGame();
		}
	}
	
	public void removeEventHelper() {
		
		if (eventHelper != null)
			eventHelper.removeGameListener(gameAdapter);
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

				GameState oldState = state;

				// check if game is solved or lost!
				if (gameMode.isSolved()) {

					state = GameState.solved;
					eventHelper.fireStateChangedEvent(new StateChangeEvent(
							this, oldState, state));
					quitGame();

				} else if (gameMode.isLost()) {

					state = GameState.gameOver;
					eventHelper.fireStateChangedEvent(new StateChangeEvent(
							this, oldState, state));
					quitGame();
				}
			}
		}
	}
	
	/**
	 * Gets score for stopped game from game mode.
	 * 
	 * @return Score for recently stopped game. Value dependent on calculation
	 *         by game mode class. Returns a zero if game is still running.
	 */
	public Integer getGameScore() {

		if (state == GameState.none || state == GameState.paused
				|| state == GameState.running || state == GameState.userStop)
			return 0;
		else
			return gameMode.getGameScore();
	}

	public Nonogram getGamePattern() {
		
		return pattern;
	}

	public GameMode getGameMode() {
		
		return gameMode;
	}

}