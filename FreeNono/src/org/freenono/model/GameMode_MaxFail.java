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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;


/**
 * Implements the game mode "Max Fail".
 * 
 * @author Christian Wichmann
 */
public class GameMode_MaxFail extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_MaxFail.class);

	private int failCount = 0;

	private GameAdapter gameAdapter = new GameAdapter() {

		public void WrongFieldOccupied(FieldControlEvent e) {
			
			processFailedMove();
		}
		
		public void MarkField(FieldControlEvent e) {

			doMarkField(e);
		}

		public void OccupyField(FieldControlEvent e) {

			doOccupyField(e);
		}
	};

	
	public GameMode_MaxFail(GameEventHelper eventHelper, Nonogram nonogram,
			Settings settings) {
		
		super(eventHelper, nonogram, settings);

		eventHelper.addGameListener(gameAdapter);

		setGameModeType(GameModeType.MAX_FAIL);

		failCount = settings.getMaxFailCount();
		
		eventHelper.fireSetFailCountEvent(new StateChangeEvent(this, failCount));
	}

	protected void processFailedMove() {
		
		failCount--;
		eventHelper.fireSetFailCountEvent(new StateChangeEvent(this, failCount));
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
		
		return (failCount <= 0);
	}

	@Override
	protected void solveGame() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void pauseGame() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void resumeGame() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopGame() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void quitGame() {
		
		super.quitGame();

		eventHelper.removeGameListener(gameAdapter);
	}

	@Override
	protected int getGameScore() {
		
		int score = 0;
		
		score = failCount;

		logger.info("highscore for game mode maxfail calculated: "+score);
		return score;
	}

}
