/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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

/**
 * This enumeration specifies which game modes are available. Each game
 * mode is implemented by a subclass of GameMode, providing especially two 
 * methods: isLost() and isSolved(). 
 * 
 * @author Christian Wichmann
 * 
 */
public enum GameModeType {
	
	/**
	 * This game mode decreases the game time at each failed field by an 
	 * penalty. With every error the penalty is increasing.
	 */
	GameMode_Penalty, 
	
	/**
	 * The MaxTime game mode is simply timing the game and declares it as lost
	 * if the given time is elapsed.
	 */
	GameMode_MaxTime, 
	
	/**
	 * MaxFail only counts the errors of the player. The game is lost if too
	 * much errors are made. No time restrictions are given.
	 */
	GameMode_MaxFail
}

