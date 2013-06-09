/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 by FreeNono Development Team
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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;


/**
 * Organizes and controls the game timer and clocks all game times (play time,
 * pause time, etc.)
 * 
 * @author Christian Wichmann
 */
public class GameTimeHelper {

	//private static Logger logger = Logger.getLogger(GameTimeHelper.class);

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
	private Date pauseTime = null;
	private GameTime gameTime = null;

	private long accumulatedPauseDuration = 0L;
	private long loadedTime = 0L;
	private long offset = 0L;
	
	private boolean countingTime = false;

	
	public GameTimeHelper(GameEventHelper eventHelper, GameTimerDirection gtd,
			long loadTime) {

		this.eventHelper = eventHelper;
		this.gtd = gtd;
		this.loadedTime = loadTime;
		
		gameTime = new GameTime();
	}

	
	public synchronized void startTime() {

		// if this method is called the first time just start timing
		if (startTime == null) {
			
			// remember reference time for begin of the game
			startTime = new Date();
			pauseTime = new Date();
			
		// is else remember the last pause duration and save it in 
		// accumulatedPauseDuration and resume timing
		} else {

			Date now = new Date();
			long pauseDuration = now.getTime() - pauseTime.getTime();
			accumulatedPauseDuration += pauseDuration;
			pauseTime = null;
		}

		// start timer
		tickTask = new Task();
		timer.schedule(tickTask, 0, 1000);
					
		countingTime = true;
	}

	public synchronized void stopTime() {

		pauseTime = new Date();

		if (tickTask != null) {
			tickTask.cancel();
			tickTask = null;
		}

		countingTime = false;
	}

	
	public boolean isTimeElapsed() {

		return getGameTime().isZero();
	}

	@SuppressWarnings("deprecation")
	public synchronized GameTime getGameTime() {

		// dependent if game is running and game time is ticking the
		// game time is calculated...
		Date tmp = null;
		if (countingTime) {

			tmp = new Date(new Date().getTime() - startTime.getTime()
					- accumulatedPauseDuration);
		} else {

			tmp = new Date(pauseTime.getTime() - startTime.getTime()
					- accumulatedPauseDuration);
		}

		// calculate game time if counting down from loaded time...
		if (gtd == GameTimerDirection.COUNT_DOWN && loadedTime != 0) {

			tmp = new Date(Math.max(loadedTime + offset - tmp.getTime(), 0));

			// ..,and saved in a GameTime instance.
			gameTime.setHours(tmp.getHours() + tmp.getTimezoneOffset() / 60);
			gameTime.setMinutes(tmp.getMinutes());
			gameTime.setSeconds(tmp.getSeconds());
			// TODO switch from deprecated methods to calendar class!
		}
		// or counting up from loaded time!
		else if (gtd == GameTimerDirection.COUNT_UP) {
			
			tmp = new Date(Math.max(loadedTime + offset + tmp.getTime(), 0));

			// ..,and saved in a GameTime instance.
			gameTime.setHours(tmp.getHours() + tmp.getTimezoneOffset() / 60);
			gameTime.setMinutes(tmp.getMinutes());
			gameTime.setSeconds(tmp.getSeconds());
			// TODO switch from deprecated methods to calendar class!
		}
		else {
			
			gameTime.setMinutes(0);
			gameTime.setSeconds(0);
		}
		
		return gameTime;
	}

	public void addTime(int minutes, int seconds) {
		
		offset += ((minutes * 60 + seconds) * 1000);
	}

	public void subTime(int minutes, int seconds) {
		
		offset -= ((minutes * 60 + seconds) * 1000);
	}
	
	
	private void timerElapsed() {

		eventHelper.fireTimerEvent(new StateChangeEvent(this, getGameTime()));
	}
	
	
	public void stopTimer() {
		
		if (tickTask != null) {
			tickTask.cancel();
			tickTask = null;
		}
		timer.cancel();
		timer.purge();
	}


	protected void finalize() throws Throwable {

		try {
			
			stopTimer();
		} finally {
			
			super.finalize();
		}
	}

}
