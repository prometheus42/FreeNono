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

import java.util.Random;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.QuizEvent;

public class GameMode_Quiz extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_Quiz.class);

	private int failCount = 0;
	private boolean isLost = false;
	private Random rng = null;

	private GameAdapter gameAdapter = new GameAdapter() {

		public void WrongFieldOccupied(FieldControlEvent e) {

			processFailedMove();
		}
	};

	public GameMode_Quiz(GameEventHelper eventHelper, Nonogram nonogram,
			Settings settings) {

		super(eventHelper, nonogram, settings);

		eventHelper.addGameListener(gameAdapter);

		setGameModeType(GameModeType.QUIZ);

		rng = new Random();
	}

	protected void processFailedMove() {

		failCount++;
		eventHelper.fireQuizEvent(new QuizEvent(this, getQuizQuestion()));
	}
	
	public void checkAnswer(QuizQuestion q, String answer) {
		
		logger.debug("numbers: " + q.getAnswer() + ", " + answer);
		logger.debug("numbers: " + Integer.valueOf(q.getAnswer()) + ", " + Integer.valueOf(answer));
		if (Integer.valueOf(q.getAnswer()).compareTo(Integer.valueOf(answer)) != 0)
			isLost = true;
	}

	private QuizQuestion getQuizQuestion() {

		int a = rng.nextInt(20) + 1;
		int b = rng.nextInt(20) + 1;

		QuizQuestion q = new QuizQuestion(new String("Multiplizieren Sie " + a
				+ " mit " + b + "!"), new String(Integer.toString(a * b)));

		return q;
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

		logger.debug("Quiz is lost: " + isLost);
		return isLost;
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

}
