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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.GameModeType;
import org.freenono.model.GameMode;
import org.freenono.model.GameTimeHelper.GameTimerDirection;
import org.freenono.model.Nonogram;
import org.freenono.controller.Settings;

public class GameMode_Penalty extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_Penalty.class);

	private GameTimeHelper gameTimeHelper = null;

	private List<Integer> penalties = Arrays.asList(1, 2, 4, 8);
	private int penaltyCount = 0;

	private GameAdapter gameAdapter = new GameAdapter() {

		public void WrongFieldOccupied(FieldControlEvent e) {

			penalty();
		}
	};

	public GameMode_Penalty(GameEventHelper eventHelper, Nonogram nonogram, 
			Settings settings) {

		super(eventHelper, nonogram, settings);

		setGameModeType(GameModeType.PENALTY);

		gameTimeHelper = new GameTimeHelper(eventHelper,
				GameTimerDirection.COUNT_DOWN,
				nonogram.getDuration() == 0 ? settings.getMaxTime() 
						: nonogram.getDuration() * 1000);
		gameTimeHelper.startTime();
		
		eventHelper.addGameListener(gameAdapter);
	}

	@Override
	public boolean isSolved() {

		boolean isSolved = false;

		if (isSolvedThroughMarked()) {
			isSolved = true;
			logger.debug("Game solved through marked.");
		}

		if (isSolvedThroughOccupied()) {
			isSolved = true;
			logger.debug("Game solved through occupied.");
		}

		return isSolved;
	}

	@Override
	public boolean isLost() {

		boolean isLost = false;

		if (gameTimeHelper.isTimeElapsed())
			isLost = true;

		return isLost;
	}

	@Override
	public void pauseGame() {

		gameTimeHelper.stopTime();
	}

	@Override
	public void resumeGame() {

		gameTimeHelper.startTime();
	}

	@Override
	public void stopGame() {

		gameTimeHelper.stopTime();
	}

	@Override
	public void solveGame() {

		gameBoard.solveGame();
	}

	@Override
	public void quitGame() {

		super.quitGame();

		if (gameTimeHelper != null) {
			gameTimeHelper.stopTimer();
			gameTimeHelper = null;
		}
		
		eventHelper.removeGameListener(gameAdapter);
	}
	
	private void penalty() {

		gameTimeHelper.subTime(
				penalties.get(Math.min(penaltyCount, penalties.size() - 1)), 0);

		penaltyCount++;

		eventHelper.fireSetTimeEvent(new StateChangeEvent(this, gameTimeHelper
				.getGameTime()));
	}

}
