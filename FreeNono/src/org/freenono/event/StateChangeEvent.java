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
package org.freenono.event;

import org.freenono.model.GameState;
import org.freenono.model.game_modes.GameTime;

/**
 * Event type describing a change in the state of the game. E.g. when game is started or stopped by
 * the user. These events are reactions from the game model on ProgramChangeEvents fired by the user
 * interface or input that was processed by the game mode like game time changes or fail count
 * changes.
 *
 * @author Christian Wichmann
 */
public final class StateChangeEvent extends GameEvent {

    private static final long serialVersionUID = -918706308224647567L;

    /**
     * Types of state change events.
     *
     * @author Christian Wichmann
     */
    public enum StateChangeType {
        STATE_CHANGED, STATE_CHANGING, TIMER, SET_TIME, SET_FAIL_COUNT
    };

    private StateChangeType stateChangeType;
    private GameState oldState;
    private GameState newState;
    private GameTime gameTime;
    private int gameScore;
    private int failCount;

    /**
     * Initializes a game event informing that the state of game has changed.
     *
     * @param source
     *            Source where event was fired.
     * @param oldState
     *            Old state of game.
     * @param newState
     *            New state of game.
     */
    public StateChangeEvent(final Object source, final GameState oldState, final GameState newState) {

        super(source, GameEventType.STATE_CHANGE_EVENT);

        setOldState(oldState);
        setNewState(newState);
        setGameTime(new GameTime());
        setGameScore(0);
    }

    /**
     * Initializes a game event informing that the state of game has changed.
     *
     * @param source
     *            Source where event was fired.
     * @param oldState
     *            Old state of game.
     * @param newState
     *            New state of game.
     * @param gameScore
     *            current game score
     */
    public StateChangeEvent(final Object source, final GameState oldState, final GameState newState, final int gameScore) {

        super(source, GameEventType.STATE_CHANGE_EVENT);

        setOldState(oldState);
        setNewState(newState);
        setGameTime(new GameTime());
        setGameScore(gameScore);
    }

    /**
     * Initializes a game event informing that the state of game has changed.
     *
     * @param source
     *            Source where event was fired.
     * @param gameTime
     *            Current game time for this event.
     */
    public StateChangeEvent(final Object source, final GameTime gameTime) {

        super(source, GameEventType.STATE_CHANGE_EVENT);

        // set state variables of event to default value
        setOldState(GameState.NONE);
        setNewState(GameState.NONE);
        setGameTime(gameTime);
        setGameScore(0);
    }

    /**
     * Initializes a game event informing that the state of game has changed.
     *
     * @param source
     *            Source where event was fired.
     * @param failCount
     *            Current fail count for this event.
     */
    public StateChangeEvent(final Object source, final int failCount) {

        super(source, GameEventType.STATE_CHANGE_EVENT);

        setOldState(GameState.NONE);
        setNewState(GameState.NONE);
        setFailCount(failCount);
        setGameTime(new GameTime());
        setGameScore(0);
    }

    /**
     * Gets the old state from which is changed.
     *
     * @return Old state of game.
     */
    public GameState getOldState() {

        return oldState;
    }

    /**
     * Sets the old state from which is changed.
     *
     * @param oldState
     *            Old state of game.
     */
    private void setOldState(final GameState oldState) {

        this.oldState = oldState;
    }

    /**
     * Gets the new state to which is changed.
     *
     * @return New state of game.
     */
    public GameState getNewState() {

        return newState;
    }

    /**
     * Sets the new state to which is changed.
     *
     * @param newState
     *            New state of game.
     */
    private void setNewState(final GameState newState) {

        this.newState = newState;
    }

    /**
     * Gets current game time for this event.
     *
     * @return Current game time.
     */
    public GameTime getGameTime() {

        return gameTime;
    }

    /**
     * Sets current game time for this event.
     *
     * @param gameTime
     *            Current game time to be set.
     */
    private void setGameTime(final GameTime gameTime) {

        this.gameTime = gameTime;
    }

    /**
     * Gets fail count of game event.
     *
     * @return current fail count
     */
    public int getFailCount() {

        return failCount;
    }

    /**
     * Sets fail count of game event.
     *
     * @param failCount
     *            current fail count
     */
    private void setFailCount(final int failCount) {

        this.failCount = failCount;
    }

    /**
     * Gets current game score.
     *
     * @return current game score
     */
    public int getGameScore() {

        return gameScore;
    }

    /**
     * Sets current game score.
     *
     * @param gameScore
     *            current game score
     */
    private void setGameScore(final int gameScore) {

        this.gameScore = gameScore;
    }

    /**
     * Gets type of state change event.
     *
     * @return Type of state change event.
     */
    public StateChangeType getStateChangeType() {

        return stateChangeType;
    }

    /**
     * Sets type of state change event.
     *
     * @param stateChangeType
     *            Type of state change event.
     */
    protected void setStateChangeType(final StateChangeType stateChangeType) {

        this.stateChangeType = stateChangeType;
    }
}
