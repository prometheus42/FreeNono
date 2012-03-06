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

import java.util.EventListener;

/**
 * The listener interface for receiving Game Events.
 * 
 */
public interface GameListener extends EventListener {

	// events fired by the ui
	public abstract void OccupyField(GameEvent e);
	public abstract void MarkField(GameEvent e);
	public abstract void ChangeActiveField(GameEvent e);
	
	// events fired by the model
	public abstract void FieldOccupied(GameEvent e);
	public abstract void FieldMarked(GameEvent e);
	public abstract void FieldUnmarked(GameEvent e);
	public abstract void WrongFieldOccupied(GameEvent e);
	public abstract void StateChanged(GameEvent e);

	// program wide events
	public abstract void Timer(GameEvent e);
	public abstract void OptionsChanged(GameEvent e);
	public abstract void ProgramControl(GameEvent e);
	
	// TODO: Add more events!
}
