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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.QuizEvent;
import org.freenono.quiz.QuestionsProvider;
import org.freenono.quiz.QuestionsProvider.QuestionProviderTypes;
import org.freenono.quiz.QuizQuestion;


public class GameMode_Quiz extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_Quiz.class);

	private QuestionsProvider qp = null;
	
	private int failCount = 0;
	private boolean isLost = false;

	
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

		qp = QuestionsProvider
				.getInstance(QuestionProviderTypes.QUESTION_PROVIDER_MULTIPLICATIONS);
	}

	
	protected void processFailedMove() {

		failCount++;
		eventHelper.fireQuizEvent(new QuizEvent(this, qp.getNextQuestion()));
	}

	public void checkAnswer(QuizQuestion question, String answer) {
	
		if (question.checkAnswer(answer))
			isLost = false;
		else 
			isLost = true;
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
	}

	@Override
	protected void pauseGame() {
	}

	@Override
	protected void resumeGame() {
	}

	@Override
	protected void stopGame() {
	}

	@Override
	protected void quitGame() {

		super.quitGame();

		eventHelper.removeGameListener(gameAdapter);
	}

}
