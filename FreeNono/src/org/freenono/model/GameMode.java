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
import org.freenono.event.StateChangeEvent;

/**
 * Abstract game mode class as base for all game modes. It provides methods
 * common to many game modes, e.g. check if game is solved by marking or
 * occupying and marking or occupying fields.
 * 
 * @author Christian Wichmann
 */
public abstract class GameMode {

    private static Logger logger = Logger.getLogger(GameMode.class);

    private GameEventHelper eventHelper = null;
    private GameBoard gameBoard = null;
    private Nonogram nonogram = null;
    //private Settings settings = null;
    private GameModeType gameModeType = null;
    private GameState state = GameState.none;
    private boolean markInvalid;

    private GameAdapter gameAdapter = new GameAdapter() {

        public void stateChanged(final StateChangeEvent e) {

            state = e.getNewState();
        }
    };

    /**
     * Initializes game mode super class.
     *  
     * @param eventHelper Game event helper for firing events.
     * @param nonogram Current nonogram pattern.
     * @param settings Settings object.
     */
    public GameMode(final GameEventHelper eventHelper, final Nonogram nonogram,
            final Settings settings) {

        this.nonogram = nonogram;
        //this.settings = settings;

        this.gameBoard = new GameBoard(nonogram);

        this.markInvalid = settings.getMarkInvalid();

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
     * 
     * @return True, if game is lost.
     */
    public abstract boolean isLost();

    /**
     * Solves the game and ???.
     */
    protected abstract void solveGame();

    /**
     * Pauses game, e.g. stops timer while game is paused.
     */
    protected abstract void pauseGame();

    /**
     * Resumes game when it was paused.
     */
    protected abstract void resumeGame();

    /**
     * Stops game and cleans up.
     */
    protected abstract void stopGame();

    /**
     * Quits current game.
     */
    protected void quitGame() {

        eventHelper.removeGameListener(gameAdapter);
    }

    /**
     * Calculates a game score based on the rules of current game mode.
     * 
     * @return Score for current game.
     */
    protected abstract int getGameScore();

    /**************** common methods for all GameModes ****************/

    /**
     * Checks whether all fields not part of the nonogram are marked, so that
     * the nonogram is essentially solved.
     * 
     * @return True, if nonogram is solved by marking all necessary fields.
     */
    protected final boolean isSolvedThroughMarked() {

        int y, x;
        boolean patternValue;
        Token fieldValue;

        for (y = 0; y < nonogram.height(); y++) {

            for (x = 0; x < nonogram.width(); x++) {

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
     * Checks if all fields belonging to the nonogram are occupied by the user.
     * 
     * @return True, if nonogram is solved by occupying all necessary fields.
     */
    protected final boolean isSolvedThroughOccupied() {

        int y, x;
        boolean patternValue;
        Token fieldValue;

        for (y = 0; y < nonogram.height(); y++) {

            for (x = 0; x < nonogram.width(); x++) {

                patternValue = nonogram.getFieldValue(x, y);
                fieldValue = gameBoard.getFieldValue(x, y);

                if (patternValue && fieldValue != Token.OCCUPIED) {

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Mark field indicated by FieldControlEvent.
     * 
     * @param e Field control event indicating which field to mark.
     */
    protected final void doMarkField(final FieldControlEvent e) {

        if (state == GameState.running) {

            if (!gameBoard.canMark(e.getFieldColumn(), e.getFieldRow())) {

                // unable to mark field, maybe it is already occupied
                logger.debug("can not mark field (" + e.getFieldColumn() + ", "
                        + e.getFieldRow() + ")");

            } else {

                if (gameBoard.mark(e.getFieldColumn(), e.getFieldRow())) {

                    eventHelper.fireFieldMarkedEvent(new FieldControlEvent(
                            this, e.getFieldColumn(), e.getFieldRow()));
                    logger.debug("field marked (" + e.getFieldColumn() + ", "
                            + e.getFieldRow() + ")");

                } else {

                    eventHelper.fireFieldUnmarkedEvent(new FieldControlEvent(
                            this, e.getFieldColumn(), e.getFieldRow()));
                    logger.debug("field unmarked (" + e.getFieldColumn() + ", "
                            + e.getFieldRow() + ")");
                }
            }
        } else {

            logger.debug("Field can not be marked because game is not running.");
        }
    }

    /**
     * Occupy field indicated by FieldControlEvent.
     * 
     * @param e Field control event indicating which field to occupy.
     */
    protected final void doOccupyField(final FieldControlEvent e) {

        if (state == GameState.running) {

            if (!gameBoard.canOccupy(e.getFieldColumn(), e.getFieldRow())) {

                // unable to mark field, maybe it is already occupied
                logger.debug("can not occupy field (" + e.getFieldColumn()
                        + ", " + e.getFieldRow() + ")");

            } else {

                if (!gameBoard.occupy(e.getFieldColumn(), e.getFieldRow())) {

                    // wrong field occupied because it does not belong to the
                    // nonogram
                    eventHelper
                            .fireWrongFieldOccupiedEvent(new FieldControlEvent(
                                    this, e.getFieldColumn(), e.getFieldRow()));
                    logger.debug("failed to occupy field ("
                            + e.getFieldColumn() + ", " + e.getFieldRow() + ")");

                    // dependent on the settings mark wrongly occupied fields!
                    if (markInvalid) {

                        gameBoard.mark(e.getFieldColumn(), e.getFieldRow());
                        eventHelper.fireFieldMarkedEvent(new FieldControlEvent(
                                this, e.getFieldColumn(), e.getFieldRow()));
                    }

                } else {

                    eventHelper.fireFieldOccupiedEvent(new FieldControlEvent(
                            this, e.getFieldColumn(), e.getFieldRow()));
                    logger.debug("field occupied (" + e.getFieldColumn() + ", "
                            + e.getFieldRow() + ")");
                }
            }
        } else {

            logger.debug("Field can not be occupied because game is not running.");
        }
    }

    /**
     * Gets type of game mode.
     * 
     * @return Type of game mode.
     */
    protected final GameModeType getGameModeType() {
        
        return gameModeType;
    }

    /**
     * Sets type of game mode.
     * 
     * @param gameModeType Type of game mode.
     */
    protected final void setGameModeType(final GameModeType gameModeType) {
        
        this.gameModeType = gameModeType;
    }

    /**
     * Gets game board.
     * 
     * @return Game board.
     */
    protected final GameBoard getGameBoard() {
        
        return gameBoard;
    }

    /**
     * Sets game board.
     * 
     * @param gameBoard Game board to set.
     */
    protected final void setGameBoard(final GameBoard gameBoard) {
        
        this.gameBoard = gameBoard;
    }

    /**
     * Gets current nonogram pattern.
     * 
     * @return Current nonogram pattern.
     */
    protected final Nonogram getNonogram() {
        
        return nonogram;
    }

    /**
     * Sets current nonogram pattern.
     * 
     * @param nonogram Nonogram pattern to set.
     */
    protected final void setNonogram(final Nonogram nonogram) {
        
        this.nonogram = nonogram;
    }

    /**
     * Gets if wrongly occupied fields should be marked. This is set according
     * to the game settings.
     * 
     * @return True, if wrongly occupied fields should be marked.
     */
    protected final boolean isMarkInvalid() {
        
        return markInvalid;
    }

    /**
     * Sets if wrongly occupied fields should be marked. This is set according
     * to the game settings.
     * 
     * @param markInvalid If wrongly occupied fields should be marked.
     */
    protected final void setMarkInvalid(final boolean markInvalid) {
        
        this.markInvalid = markInvalid;
    }

    /**
     * Gets game event helper to fire events.
     * 
     * @return the eventHelper Game event helper.
     */
    protected final GameEventHelper getEventHelper() {
        
        return eventHelper;
    }

    /**
     * Sets game event helper to fire events.
     * 
     * @param eventHelper Game event helper.
     */
    protected final void setEventHelper(final GameEventHelper eventHelper) {
        
        this.eventHelper = eventHelper;
    }

}
