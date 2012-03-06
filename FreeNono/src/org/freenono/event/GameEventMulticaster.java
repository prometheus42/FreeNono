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

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

/**
 * EventMulticast to dispatch events to event listeners all over the program.
 * 
 * (copied from http://www.javaworld.com/javaworld/javatips/jw-javatip35.html)
 * 
 */
public class GameEventMulticaster 
		extends AWTEventMulticaster 
		implements GameListener {
	
	protected GameEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public static GameListener add(GameListener a, GameListener b) {
		return (GameListener) addInternal(a, b);
	}

	public static GameListener remove(GameListener l, GameListener oldl) {
		return (GameListener) removeInternal(l, oldl);
	}

	public void OccupyField(GameEvent e) {
		if (a != null)
			((GameListener) a).OccupyField(e);
		if (b != null)
			((GameListener) b).OccupyField(e);
	}

	public void MarkField(GameEvent e) {
		if (a != null)
			((GameListener) a).MarkField(e);
		if (b != null)
			((GameListener) b).MarkField(e);
	}

	public void ChangeActiveField(GameEvent e) {
		if (a != null)
			((GameListener) a).ChangeActiveField(e);
		if (b != null)
			((GameListener) b).ChangeActiveField(e);
	}

	public void StateChanged(GameEvent e) {
		if (a != null)
			((GameListener) a).StateChanged(e);
		if (b != null)
			((GameListener) b).StateChanged(e);
	}

	public void Timer(GameEvent e) {
		if (a != null)
			((GameListener) a).Timer(e);
		if (b != null)
			((GameListener) b).Timer(e);
	}
	
	public void OptionsChanged(GameEvent e) {
		if (a != null)
			((GameListener) a).OptionsChanged(e);
		if (b != null)
			((GameListener) b).OptionsChanged(e);
	}

	public void WrongFieldOccupied(GameEvent e) {
		if (a != null)
			((GameListener) a).WrongFieldOccupied(e);
		if (b != null)
			((GameListener) b).WrongFieldOccupied(e);
	}
	
	public void ProgramControl(GameEvent e) {
		if (a != null)
			((GameListener) a).ProgramControl(e);
		if (b != null)
			((GameListener) b).ProgramControl(e);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new GameEventMulticaster(a, b);
	}

	protected EventListener remove(EventListener oldl) {
		if (oldl == a)
			return b;
		if (oldl == b)
			return a;
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b)
			return this;
		return addInternal(a2, b2);
	}

	@Override
	public void FieldOccupied(GameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void FieldMarked(GameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void FieldUnmarked(GameEvent e) {
		// TODO Auto-generated method stub
		
	}

}
