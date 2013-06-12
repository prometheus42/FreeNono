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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;

/**
 * This class constructs instantiates a game mode class. Furthermore it
 * administrats the game state and fires the necessary state change events to
 * inform all other components of the program.
 *
 * @author Christian Wichmann, Markus Wichmann
 */
public class Game {

    private static Logger logger = Logger.getLogger(Game.class);

    private GameMode gameMode = null;
    private GameEventHelper eventHelper = null;
    private GameModeFactory gameModeFactory = null;
    private Settings settings;
    private Nonogram pattern;
    private GameState state = GameState.none;

    /**
     * Exception concerning the game mode class for a game.
     * 
     * @author Christian Wichmann
     */
    public class GameModeException extends Exception {

        private static final long serialVersionUID = -5216243640288343983L;
    };

    /**
     * GameAdapter controlling the flow of the game class and checking if
     * current game is lost/won according to the rules of the chosen game mode.
     */
    private GameAdapter gameAdapter = new GameAdapter() {

        public void fieldOccupied(FieldControlEvent e) {

            checkGame();
        }

        public void fieldUnoccupied(FieldControlEvent e) {

            checkGame();
        }

        public void fieldMarked(FieldControlEvent e) {

            checkGame();
        }

        public void fieldUnmarked(FieldControlEvent e) {

            checkGame();
        }

        public void wrongFieldOccupied(FieldControlEvent e) {

            checkGame();
        }

        public void setTime(StateChangeEvent e) {

            checkGame();
        }

        public void timerElapsed(StateChangeEvent e) {

            checkGame();
        }

        public void programControl(ProgramControlEvent e) {
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

            case OPTIONS_CHANGED:
                break;

            case SHOW_ABOUT:
                break;

            case SHOW_OPTIONS:
                break;

            default:
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
    public final void startGame() {

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
            gameMode = gameModeFactory.getGameMode(eventHelper, pattern,
                    settings);

            eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
                    oldState, state));
            logger.info("Game started...");

            // } else if (state == GameState.running) {
            // if game is already running do nothing whatsoever, yet!

        } else {

            logger.error("Illegal game state!");
        }
    }

    /**
     * Restarts the game.
     */
    public final void restartGame() {

        if (gameMode != null) {

            gameMode.stopGame();
            gameMode.quitGame();
            gameMode = null;
        }

        GameState oldState = state;
        state = GameState.running;

        // get game mode class from factory defined in settings
        gameMode = gameModeFactory.getGameMode(eventHelper, pattern, settings);

        eventHelper.fireStateChangedEvent(new StateChangeEvent(this, oldState,
                state));
        logger.info("Game restarted...");
    }

    /**
     * Interrupts the game for a short period of time.
     */
    public final void pauseGame() {

        if (state == GameState.running) {

            GameState oldState = state;

            state = GameState.paused;

            gameMode.pauseGame();

            eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
                    oldState, state));
            logger.info("Game paused...");
        }
    }

    /**
     * Restarts the game after it has been paused.
     */
    public final void resumeGame() {

        if (state == GameState.paused) {

            GameState oldState = state;

            state = GameState.running;

            gameMode.resumeGame();

            eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
                    oldState, state));
            logger.info("Game resumed...");
        }
    }

    /**
     * Stops the game.
     */
    public final void stopGame() {

        if (state == GameState.running || state == GameState.paused) {

            GameState oldState = state;

            state = GameState.userStop;

            gameMode.stopGame();
            gameMode.quitGame();
            gameMode = null;

            eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
                    oldState, state));
            logger.info("Game stopped...");
        }
    }

    /**
     * Quits a game, stopping the running game mode and quitting its game.
     */
    private void quitGame() {

        if (gameMode != null) {

            gameMode.stopGame();
            gameMode.solveGame();
            gameMode.quitGame();
        }
    }

    /**
     * Remove event helper for this class. Be aware that Game will no longer
     * evaluate or fire events!
     */
    public final void removeEventHelper() {

        if (eventHelper != null) {

            eventHelper.removeGameListener(gameAdapter);
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
    public final int getGameScore() {

        if (state == GameState.none || state == GameState.paused
                || state == GameState.running || state == GameState.userStop) {
            return 0;
        } else {
            return gameMode.getGameScore();
        }
    }

    /**
     * Returns the current nonogram for this game instance.
     *
     * @return Nonogram for which this Game instance was started.
     */
    public final Nonogram getGamePattern() {

        return pattern;
    }

    /**
     * Returns the current GameMode instance for this game.
     *
     * @return GameMode controlling current game.
     */
    public final GameMode getGameMode() {

        return gameMode;
    }

}
