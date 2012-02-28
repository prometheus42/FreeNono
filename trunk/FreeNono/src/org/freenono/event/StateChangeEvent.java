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
package org.freenono.event;

import java.util.Date;

import org.freenono.model.GameState;

public class StateChangeEvent extends GameEvent {

	private static final long serialVersionUID = -918706308224647567L;
	
	// TODO: STATE_CHANGED, TIMER
	
	private GameState oldState;
	private GameState newState;
	private Date gameTime;
	
	
	public StateChangeEvent(Object source, GameState oldState, GameState newState) {
		super(source, GameEventType.StateChangeEvent);
		this.oldState = oldState;
		this.newState = newState;
	}
	
	public StateChangeEvent(Object source, Date gameTime) {
		super(source, GameEventType.StateChangeEvent);
		this.oldState = null;
		this.newState = null;
		this.gameTime = gameTime;
	}

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
	public Date getGameTime() {
		return gameTime;
	}

	/**
	 * @param gameTime
	 *            the gameTime to set
	 */
	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}

}
