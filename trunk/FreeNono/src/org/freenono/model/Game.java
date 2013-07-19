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
import org.freenono.model.data.Nonogram;
import org.freenono.model.game_modes.GameMode;
import org.freenono.model.game_modes.GameModeFactory;

/**
 * Represents a game with a given game mode. It instantiates the correct game
 * mode with the nonogram pattern. Furthermore it controls the game state and
 * fires the necessary state change events to inform all other components of it.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class Game {

    private static Logger logger = Logger.getLogger(Game.class);

    private GameMode gameMode = null;
    private GameEventHelper eventHelper = null;
    private Settings settings;
    private Nonogram pattern;
    private GameState state = GameState.NONE;

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

        @Override
        public void fieldOccupied(final FieldControlEvent e) {
            checkGame();
        }

        @Override
        public void fieldUnoccupied(final FieldControlEvent e) {
            checkGame();
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {
            checkGame();
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {
            checkGame();
        }

        @Override
        public void wrongFieldOccupied(final FieldControlEvent e) {
            checkGame();
        }

        @Override
        public void setTime(final StateChangeEvent e) {
            checkGame();
        }

        @Override
        public void timerElapsed(final StateChangeEvent e) {
            checkGame();
        }

        @Override
        public void programControl(final ProgramControlEvent e) {
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
                assert false : e.getPct();
                break;
            }
        }
    };

    /**
     * Initializes a new game. The game class instantiates then the
     * GameModeFactory that gets the game mode according to game settings.
     * 
     * @param eventHelper
     *            Game event helper to fire events.
     * @param pattern
     *            Current nonogram pattern.
     * @param settings
     *            Settings to get start time for this game mode.
     */
    public Game(final GameEventHelper eventHelper, final Nonogram pattern,
            final Settings settings) {

        this.pattern = pattern;
        this.settings = settings;

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
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

        if (state == GameState.NONE || state == GameState.GAME_OVER
                || state == GameState.SOLVED || state == GameState.USER_STOP
                || state == GameState.PAUSED) {

            GameState oldState = state;
            state = GameState.RUNNING;

            // get game mode class from factory defined in settings
            gameMode = GameModeFactory.getGameMode(eventHelper, pattern,
                    settings);

            eventHelper.fireStateChangingEvent(new StateChangeEvent(this,
                    oldState, state));
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
        state = GameState.RUNNING;

        // get game mode class from factory defined in settings
        gameMode = GameModeFactory.getGameMode(eventHelper, pattern, settings);

        eventHelper.fireStateChangingEvent(new StateChangeEvent(this, oldState,
                state));
        eventHelper.fireStateChangedEvent(new StateChangeEvent(this, oldState,
                state));
        logger.info("Game restarted...");
    }

    /**
     * Interrupts the game for a short period of time.
     */
    public final void pauseGame() {

        if (state == GameState.RUNNING) {

            GameState oldState = state;

            state = GameState.PAUSED;

            gameMode.pauseGame();

            eventHelper.fireStateChangingEvent(new StateChangeEvent(this,
                    oldState, state));
            eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
                    oldState, state));
            logger.info("Game paused...");
        }
    }

    /**
     * Restarts the game after it has been paused.
     */
    public final void resumeGame() {

        if (state == GameState.PAUSED) {

            GameState oldState = state;

            state = GameState.RUNNING;

            gameMode.resumeGame();

            eventHelper.fireStateChangingEvent(new StateChangeEvent(this,
                    oldState, state));
            eventHelper.fireStateChangedEvent(new StateChangeEvent(this,
                    oldState, state));
            logger.info("Game resumed...");
        }
    }

    /**
     * Stops the game.
     */
    public final void stopGame() {

        if (state == GameState.RUNNING || state == GameState.PAUSED) {

            GameState oldState = state;

            state = GameState.USER_STOP;

            gameMode.stopGame();
            gameMode.quitGame();
            gameMode = null;

            eventHelper.fireStateChangingEvent(new StateChangeEvent(this,
                    oldState, state));
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
            if (state == GameState.RUNNING) {

                GameState oldState = state;

                // check if game is solved or lost!
                if (gameMode.isSolved()) {

                    state = GameState.SOLVED;
                    eventHelper.fireStateChangingEvent(new StateChangeEvent(
                            this, oldState, state));
                    eventHelper.fireStateChangedEvent(new StateChangeEvent(
                            this, oldState, state));
                    quitGame();

                } else if (gameMode.isLost()) {

                    state = GameState.GAME_OVER;
                    eventHelper.fireStateChangingEvent(new StateChangeEvent(
                            this, oldState, state));
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

        if (state == GameState.NONE || state == GameState.PAUSED
                || state == GameState.RUNNING || state == GameState.USER_STOP) {
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
