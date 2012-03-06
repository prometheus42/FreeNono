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
package org.freenono.model;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.freenono.event.GameEvent;


class GameFlow {

	class Task extends TimerTask {

		@Override
		public void run() {
			timerElapsed();
		}
		
	}
	
	private Game game;
	private Task lastTask;

	GameFlow(Game game) {
		this.game = game;
	}

	/*************** game flow control ***************/

	private GameState state = GameState.none;

	/**
	 * Starts the game.
	 */
	public void startGame() {

		if (state == GameState.none) {

			GameState oldState = state;
			
			state = GameState.running;
			startTime = new Date();
			lastTask = new Task();
			timer.schedule(lastTask, 0, 1000);

			game.getEventHelper().fireStateChangedEvent(
					new GameEvent(this, oldState, state));

			// TODO do additional things here
		} else {
			// game is already started: do nothing? throw exception?
			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Interrupts the game for a short period of time.
	 */
	public void pauseGame() {

		if (state == GameState.running) {

			GameState oldState = state;
			
			state = GameState.paused;
			pauseTime = new Date();
			lastTask.cancel();
			lastTask = null;
			
			game.getEventHelper().fireStateChangedEvent(
					new GameEvent(this, oldState, state));

			// TODO do additional things here
		} else {
			// game is not in started state: do nothing? throw exception?
			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Restarts the game after it has been paused.
	 */
	public void resumeGame() {

		if (state == GameState.paused) {

			GameState oldState = state;
			
			state = GameState.running;
			Date now = new Date();
			long pauseDuration = now.getTime() - pauseTime.getTime();
			accumulatedPauseDuration += pauseDuration;
			pauseTime = null;
			lastTask = new Task();
			timer.schedule(lastTask, 0, 1000);
			
			game.getEventHelper().fireStateChangedEvent(
					new GameEvent(this, oldState, state));
			

			// TODO do additional things here
		} else {
			// game is not paused: do nothing? throw exception?
			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Stops the game.
	 */
	public void stopGame() {

		if (state == GameState.running || state == GameState.paused) {

			GameState oldState = state;

			state = GameState.userStop;
			endTime = new Date();
			if (lastTask != null) {
				lastTask.cancel();
				lastTask = null;
			}

			game.getEventHelper().fireStateChangedEvent(
					new GameEvent(this, oldState, state));

			// TODO do additional things here
		} else {
			// game is not running or in paused: do nothing? throw exception?
			// TODO check what to do here
			// TODO add log message here
		}
	}

	/**
	 * Informs the game flow that the game is solved
	 */
	void gameSolved() {

		if (state == GameState.running) {

			GameState oldState = state;
			
			state = GameState.solved;
			endTime = new Date();
			timer.cancel();
			
			game.getEventHelper().fireStateChangedEvent(
					new GameEvent(this, oldState, state));

			// TODO do additional things here
		} else {
			// game is not running: do nothing? throw exception?
			// TODO check what to do here
			// TODO add log message here
		}
	}

	public boolean isOver(){
		switch (getState()) {
		case userStop:
		case gameOver:
		case solved:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isRunning() {

		switch (getState()) {
		case running:
			return true;
		default:
			return false;
		}

	}
	
	public GameState getState() {
		return state;
	}

	/*************** end conditions ***************/

	private boolean useMaxFailCount = false;
	private int maxFailCount = 0;

	private boolean useMaxTime = false;
	private long maxTime = 0L;

	public int getMaxFailCount() {
		return maxFailCount;
	}

	public void setMaxFailCount(int maxFailCount) {

		if (maxFailCount > 0) {
			this.useMaxFailCount = true;
		} else {
			this.useMaxFailCount = false;
		}
		this.maxFailCount = maxFailCount;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		if (maxTime > 0) {
			this.useMaxTime = true;
		} else {
			this.useMaxTime = false;
		}
		this.maxTime = maxTime;
	}

	public boolean usesMaxFailCount() {
		return useMaxFailCount;
	}

	public boolean usesMaxTime() {
		return useMaxTime;
	}

	void checkEndConditions() {

		if (useMaxFailCount) {
			if (failCount >= maxFailCount && !isOver()) {
				GameState oldState = state;
				state = GameState.gameOver;
				endTime = new Date();
				game.getEventHelper().fireStateChangedEvent(
						new GameEvent(this, oldState, state));
			}
		}

		if (useMaxTime) {
			if (getTimeLeft().getTime() <= 0 && !isOver()) {
				GameState oldState = state;
				state = GameState.gameOver;
				endTime = new Date();
				game.getEventHelper().fireStateChangedEvent(
						new GameEvent(this, oldState, state));
			}
		}

	}

	/*************** move statistic ***************/

	private int failCount = 0;
	private int successCount = 0;
	private int markCount = 0;
	private int unmarkCount = 0;

	public int getFailCount() {
		return failCount;
	}

	public void increaseFailCount() {
		this.failCount++;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void increaseSuccessCount() {
		this.successCount++;
	}

	public int getMarkCount() {
		return markCount;
	}

	public void increaseMarkCount() {
		this.markCount++;
	}

	public int getUnmarkCount() {
		return unmarkCount;
	}

	public void increaseUnmarkCount() {
		this.unmarkCount++;
	}

	public int getActionCount() {
		return successCount + failCount + markCount + unmarkCount;
	}

	public int getMoveCount() {
		return successCount + failCount;
	}

	/**
	 * Calculates a Score for the current state of the game.
	 * 
	 * @return
	 */
	public int getScore() {

		// TODO please implement me
		// TODO add some kind of "successfullyDone" variable to the game
		return (int) (this.getSuccessCount() - this.getFailCount() - this
				.getUnmarkCount() * 0.2);

	}

	/*************** time functionality ***************/

	private Date startTime = null;
	private Date endTime = null;
	private Date pauseTime = null;
	private long accumulatedPauseDuration = 0L;
	private Timer timer = new Timer();

	public Date getElapsedTime() {
		switch (state) {
		case running:
			return new Date(new Date().getTime() - startTime.getTime()
					- accumulatedPauseDuration);
		case paused:
			return new Date(pauseTime.getTime() - startTime.getTime()
					- accumulatedPauseDuration);
		case userStop:
		case gameOver:
		case solved:
			return new Date(endTime.getTime() - startTime.getTime()
					- accumulatedPauseDuration);
		default:
			return new Date(0L);
		}
	}

	public Date getTimeLeft() {
		if (usesMaxTime()) {
			return new Date(Math.max(maxTime - getElapsedTime().getTime(), 0));
		} else {
			return new Date(0L);
		}
	}

	private void timerElapsed() {
		game.getEventHelper().fireTimerEvent(new GameEvent(this));
		checkEndConditions();
	}
}
