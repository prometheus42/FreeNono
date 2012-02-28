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
package de.ichmann.markusw.java.apps.freenono.event;

import java.util.EventListener;

import de.ichmann.markusw.java.apps.freenono.model.GameState;

public interface GameListener extends EventListener {
	
	void FieldOccupied(int x, int y);
	
	void FieldMarked(int x, int y);
	
	void ActiveFieldChanged(int x, int y);
	
	void StateChanged(GameState oldState, GameState newState);
	
	void Timer();
}
