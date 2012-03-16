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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Game.GameModeType;
import org.freenono.model.GameMode;
import org.freenono.model.Nonogram;
import org.freenono.controller.Settings;

public class GameMode_MaxTime extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_Penalty.class);

	// private boolean gamePaused = false;
	private long remainingGameTime = 0L;

	private Timer timer = new Timer();
	private Task tickTask;

	class Task extends TimerTask {
		@Override
		public void run() {
			timerElapsed();
		}
	}

	private GameAdapter gameAdapter = new GameAdapter() {

	};

	public GameMode_MaxTime(GameEventHelper eventHelper, Nonogram nonogram,
			Settings settings) {

		super(eventHelper, nonogram, settings);

		eventHelper.addGameListener(gameAdapter);

		setGameModeType(GameModeType.GameMode_Penalty);

		// initialize timer
		remainingGameTime = settings.getMaxTime();

		tickTask = new Task();
		timer.schedule(tickTask, 0, 1000);

		eventHelper.fireSetTimeEvent(new StateChangeEvent(this,
				getRemainingTime()));
	}

	@Override
	public boolean isSolved() {

		boolean isSolved = false;

		if (isSolvedThroughMarked()) {
			isSolved = true;
			logger.debug("solved marked");
		}

		if (isSolvedThroughOccupied()) {
			isSolved = true;
			logger.debug("solved occupied");
		}

		return isSolved;
	}

	@Override
	public boolean isLost() {

		boolean isLost = false;

		if (remainingGameTime <= 0)
			isLost = true;

		return isLost;
	}

	@Override
	public void pauseGame() {

	}

	@Override
	public void resumeGame() {

	}

	@Override
	public void stopGame() {

		if (tickTask != null) {
			tickTask.cancel();
			tickTask = null;
		}
	}

	@Override
	public void solveGame() {

		gameBoard.solveGame();
	}

	private void timerElapsed() {

		if (gameRunning)
			remainingGameTime -= 1000;

		eventHelper.fireTimerEvent(new StateChangeEvent(this,
				getRemainingTime()));

		isLost();
	}

	private Date getRemainingTime() {

		if (remainingGameTime < 0)
			remainingGameTime = 0;

		return (new Date(remainingGameTime));
	}

}
