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
 * Organizes and controls the game timer and clocks all game times (play time,
 * pause time, etc.)
 * 
 */
// TODO Implement this!
public class GameTime {

	// private Date startTime = null;
	// private Date endTime = null;
	// private Date pauseTime = null;
	// private long accumulatedPauseDuration = 0L;

	public void isSolved() {
		// endTime = new Date();
		// timer.cancel();
	}

	public void pauseGame() {
		// pauseTime = new Date();
		// tickTask.cancel();
		// tickTask = null;
	}

	public void resumeGame() {
		// Date now = new Date();
		// long pauseDuration = now.getTime() - pauseTime.getTime();
		// accumulatedPauseDuration += pauseDuration;
		// pauseTime = null;
		// tickTask = new Task();
		// timer.schedule(tickTask, 0, 1000);
	}

	public void stopGame() {
		// endTime = new Date();
	}
}
