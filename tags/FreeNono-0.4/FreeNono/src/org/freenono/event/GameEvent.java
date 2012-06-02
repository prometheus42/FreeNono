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
package org.freenono.event;

import java.util.EventObject;


/*
 * The class GameEvent is the superclass for all event types in FreeNono.
 * GameEvent itself should not be used. Instead the three subclasses should
 * be instantiated: 
 * - ProgrammControlEvent: events that affect the whole application and its 
 *   control flow. This events are called by the UI.
 * - FieldControlEvent: events concerning a specific and current game/nonogram,
 *   e.g. changes in the fields of the board. These events are both the calls
 *   from UI by changing a field and the "answer" by the game model.
 * - StateChangeEvent: events signaling a change in the state in which the 
 *   game is currently. All Timer events belong also in this category.
 */
public class GameEvent extends EventObject {

	private static final long serialVersionUID = 854958592468069527L;

	public enum GameEventType {
		ProgramControlEvent, FieldControlEvent, StateChangeEvent, QuizEvent
	};

	protected GameEventType gameEventType = null; 
	protected String comment;
	protected int failCount;

	public GameEvent(Object source, GameEventType gameEventType) {
		super(source);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public GameEventType getGameEventType() {
		return gameEventType;
	}

	public void setGameEventType(GameEventType gameEventType) {
		this.gameEventType = gameEventType;
	}

}
