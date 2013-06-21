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
import org.freenono.model.GameTime;

public class StateChangeEvent extends GameEvent {

    private static final long serialVersionUID = -918706308224647567L;

    // TODO: STATE_CHANGED, TIMER

    private GameState oldState;
    private GameState newState;
    private GameTime gameTime;

    public StateChangeEvent(Object source, GameState oldState,
            GameState newState) {
        super(source, GameEventType.StateChangeEvent);
        this.oldState = oldState;
        this.newState = newState;
    }

    public StateChangeEvent(Object source, GameTime gameTime) {
        super(source, GameEventType.StateChangeEvent);
        this.oldState = null;
        this.newState = null;
        this.gameTime = gameTime;
    }

    public StateChangeEvent(Object source, int failCount) {
        super(source, GameEventType.StateChangeEvent);
        this.failCount = failCount;
    }

    // TODO: Is this constructor really necessary? -> GameFlow.timerElapsed()
    // public StateChangeEvent(Object source) {
    // super(source, GameEventType.StateChangeEvent);
    // }

    public GameState getOldState() {
        return oldState;
    }

    public void setOldState(GameState oldState) {
        this.oldState = oldState;
    }

    public GameState getNewState() {
        return newState;
    }

    public void setNewState(GameState newState) {
        this.newState = newState;
    }

    /**
     * @return the gameTime
     */
    public GameTime getGameTime() {
        return gameTime;
    }

    /**
     * @param gameTime
     *            the gameTime to set
     */
    public void setGameTime(GameTime gameTime) {
        this.gameTime = gameTime;
    }

    /**
     * @return the failCount
     */
    public int getFailCount() {
        return failCount;
    }

    /**
     * @param failCount
     *            the failCount to set
     */
    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

}
