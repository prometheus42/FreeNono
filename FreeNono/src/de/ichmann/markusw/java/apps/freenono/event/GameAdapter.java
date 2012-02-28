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

/**
 * Abstract adapter class to prevent the necessity to implement every Listener
 * function in every GameListener.
 * 
 */
public class GameAdapter implements GameListener {

	@Override
	public void FieldMarked(GameEvent e) {
	}

	@Override
	public void FieldOccupied(GameEvent e) {
	}

	@Override
	public void ActiveFieldChanged(GameEvent e) {
	}

	@Override
	public void StateChanged(GameEvent e) {
	}

	@Override
	public void Timer(GameEvent e) {
	}

	@Override
	public void OptionsChanged(GameEvent e) {
	}

	@Override
	public void WrongFieldOccupied(GameEvent e) {
	}

}
