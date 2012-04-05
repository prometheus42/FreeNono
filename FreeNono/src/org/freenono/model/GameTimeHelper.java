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

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;


/**
 * Organizes and controls the game timer and clocks all game times (play time,
 * pause time, etc.)
 * 
 */
public class GameTimeHelper {

	private GameEventHelper eventHelper = null;
	
	public enum GameTimerDirection {COUNT_UP, COUNT_DOWN};
	
	private GameTimerDirection gtd = GameTimerDirection.COUNT_DOWN;

	private Timer timer = new Timer();
	private Task tickTask;

	class Task extends TimerTask {
		@Override
		public void run() {
			timerElapsed();
		}
	}

	private Date startTime = null;
	private Date endTime = null;
	private Date pauseTime = null;
	private long accumulatedPauseDuration = 0L;
	private long loadedTime = 0L;
	private long offsetTime = 0L;

	
	public GameTimeHelper(GameEventHelper eventHelper, GameTimerDirection gtd) {
		
		this.eventHelper = eventHelper;
		this.gtd = gtd;
	}

	
	public void loadTime(long loadTime) {
		
		loadedTime = loadTime;
	}
	
	public void startTime() {
		
		startTime = new Date();
		
		tickTask = new Task();
		timer.schedule(tickTask, 0, 1000);
	}

	public void stopTime() {
		
		endTime = new Date();
		timer.cancel();
		
		if (tickTask != null) {
			tickTask.cancel();
			tickTask = null;
		}
	}
	
	public void pauseTime () {
	
		pauseTime = new Date();
		
		tickTask.cancel();
		tickTask = null;
	}
	
	public void resumeTime () {
		
		Date now = new Date();
		long pauseDuration = now.getTime() - pauseTime.getTime();
		accumulatedPauseDuration += pauseDuration;
		pauseTime = null;
		
		tickTask = new Task();
		timer.schedule(tickTask, 0, 1000);
	}
	
	/**
	 * Adds time to internal timer. Parameter time is the number of milliseconds
	 * which should be added to the timer.
	 * @param time number of milliseconds
	 */
	public void addTime(long time) {
		
		offsetTime += time;
	}
	
	/**
	 * Subtracts time from internal timer. Parameter time is the number of 
	 * milliseconds which should be added to the timer.
	 * @param time number of milliseconds
	 */
	public void subtractTime(long time) {
		
		offsetTime -= time;
	}
	
	public boolean isTimeElapsed() {
		
		return false;//(getGameTime()) <= 0);
	}
	
	public Date getGameTime() {

		if (loadedTime < 0)
			loadedTime = 0;

		return (new Date(loadedTime));
	}
	
	// public Date getElapsedTime() {
	// switch (state) {
	// case running:
	// return new Date(new Date().getTime() - startTime.getTime()
	// - accumulatedPauseDuration);
	// case paused:
	// return new Date(pauseTime.getTime() - startTime.getTime()
	// - accumulatedPauseDuration);
	// case userStop:
	// case gameOver:
	// case solved:
	// return new Date(endTime.getTime() - startTime.getTime()
	// - accumulatedPauseDuration);
	// default:
	// return new Date(0L);
	// }
	// }

	// public Date getTimeLeft() {
	// if (usesMaxTime()) {
	// return new Date(Math.max(maxTime - getElapsedTime().getTime(), 0));
	// } else {
	// return new Date(0L);
	// }
	// }
	
	private void timerElapsed() {

		eventHelper.fireTimerEvent(new StateChangeEvent(this,
				getGameTime()));
	}
	
}
