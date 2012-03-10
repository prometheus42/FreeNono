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
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Game.GameModeType;
import org.freenono.model.GameMode;
import org.freenono.model.Nonogram;
import org.freenono.ui.GameOverUI;
import org.freenono.ui.Messages;
import org.freenono.controller.Settings;

public class GameMode_Penalty extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_Penalty.class);

	//private boolean gamePaused = false;
	private long remainingGameTime = 0L;
	private int penaltyCount = 0;
	private boolean gameRunning = false;

	private Timer timer = new Timer();
	private Task tickTask;

	class Task extends TimerTask {
		@Override
		public void run() {
			timerElapsed();
		}
	}

	private GameAdapter gameAdapter = new GameAdapter() {

		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				stopGame();
				break;

			case solved:
				stopGame();
				break;

			case paused:
				pauseGame();
				break;

			case running:
				if (e.getOldState() == GameState.paused)
					resumeGame();
				break;

			default:
				break;
			}
		}

		public void MarkField(FieldControlEvent e) {

			if (gameRunning) {
				if (!gameBoard.canMark(e.getFieldColumn(), e.getFieldRow())) {

					// unable to mark field, maybe it is already occupied
					logger.debug("can not mark field (" + e.getFieldColumn()
							+ ", " + e.getFieldRow() + ")");

				} else {

					if (gameBoard.mark(e.getFieldColumn(), e.getFieldRow())) {
						eventHelper.fireFieldMarkedEvent(new FieldControlEvent(
								this, e.getFieldColumn(), e.getFieldRow()));
						logger.debug("field marked (" + e.getFieldColumn()
								+ ", " + e.getFieldRow() + ")");

					} else {
						eventHelper
								.fireFieldUnmarkedEvent(new FieldControlEvent(
										this, e.getFieldColumn(), e
												.getFieldRow()));
						logger.debug("field unmarked (" + e.getFieldColumn()
								+ ", " + e.getFieldRow() + ")");
					}
				}
			}
		}

		public void OccupyField(FieldControlEvent e) {

			if (gameRunning) {
				if (!gameBoard.canOccupy(e.getFieldColumn(), e.getFieldRow())) {

					// unable to mark field, maybe it is already occupied
					logger.debug("can not occupy field (" + e.getFieldColumn()
							+ ", " + e.getFieldRow() + ")");
					// TODO add user message
				} else {

					if (!gameBoard.occupy(e.getFieldColumn(), e.getFieldRow())) {

						// failed to mark field
						eventHelper
								.fireWrongFieldOccupiedEvent(new FieldControlEvent(
										this, e.getFieldColumn(), e
												.getFieldRow()));
						logger.debug("failed to occupy field ("
								+ e.getFieldColumn() + ", " + e.getFieldRow()
								+ ")");
						penalty();

						// dependent on the settings mark wrongly occupied
						// fields!
						if (settings.getMarkInvalid()) {
							gameBoard.mark(e.getFieldColumn(), e.getFieldRow());
							eventHelper
									.fireFieldMarkedEvent(new FieldControlEvent(
											this, e.getFieldColumn(), e
													.getFieldRow()));
						}
						// TODO add user message

					} else {
						eventHelper
								.fireFieldOccupiedEvent(new FieldControlEvent(
										this, e.getFieldColumn(), e
												.getFieldRow()));
						logger.debug("field (" + e.getFieldColumn() + ", "
								+ e.getFieldRow() + ") marked");
					}
				}
			}
		}

	};

	public GameMode_Penalty(GameEventHelper eventHelper, Nonogram nonogram,
			Settings settings) {

		super(eventHelper, nonogram, settings);

		eventHelper.addGameListener(gameAdapter);

		setGameModeType(GameModeType.GameMode_Penalty);

		// set internal state flag
		gameRunning = true;

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

		gameRunning = false;
	}

	@Override
	public void resumeGame() {

		gameRunning = true;
	}

	@Override
	public void stopGame() {

		gameRunning = false;
		if (tickTask != null) {
			tickTask.cancel();
			tickTask = null;
		}
	}

	@Override
	public void solveGame() {

		gameBoard.solveGame();
	}

	protected void penalty() {

		remainingGameTime -= 1000 * 60 * Math.min(8,
				(int) Math.pow(2, penaltyCount));
		eventHelper.fireSetTimeEvent(new StateChangeEvent(this,
				getRemainingTime()));
		penaltyCount++;
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
