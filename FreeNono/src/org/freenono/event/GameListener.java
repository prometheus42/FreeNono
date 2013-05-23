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

	// field control events
	public abstract void OccupyField(FieldControlEvent e);
	public abstract void MarkField(FieldControlEvent e);
	public abstract void ChangeActiveField(FieldControlEvent e);
	public abstract void FieldOccupied(FieldControlEvent e);
	public abstract void FieldUnoccupied(FieldControlEvent e);
	public abstract void FieldMarked(FieldControlEvent e);
	public abstract void FieldUnmarked(FieldControlEvent e);
	public abstract void WrongFieldOccupied(FieldControlEvent e);
	
	// state changed events
	public abstract void StateChanged(StateChangeEvent e);
	public abstract void SetTime(StateChangeEvent e);
	public abstract void Timer(StateChangeEvent e);
	public abstract void SetFailCount(StateChangeEvent e);
	
	// program control events
	public abstract void OptionsChanged(ProgramControlEvent e);
	public abstract void ProgramControl(ProgramControlEvent e);
	
	// quiz events
	public abstract void AskQuestion(QuizEvent e);
	
}
