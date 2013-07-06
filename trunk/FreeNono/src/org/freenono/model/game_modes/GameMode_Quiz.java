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
package org.freenono.model.game_modes;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.QuizEvent;
import org.freenono.model.Nonogram;
import org.freenono.quiz.QuestionsProvider;
import org.freenono.quiz.QuestionsProvider.QuestionProviderTypes;
import org.freenono.quiz.Question;
import org.freenono.quiz.QuestionsProviderMultipleChoice;
import org.freenono.quiz.QuestionsProviderMultiplications;

/**
 * Implements the game mode "Quiz".
 * 
 * @author Christian Wichmann
 */
public class GameMode_Quiz extends GameMode {

    private static Logger logger = Logger.getLogger(GameMode_Quiz.class);

    private QuestionsProvider qp = null;

    private int failCount = 0;
    private boolean isLost = false;

    private GameAdapter gameAdapter = new GameAdapter() {

        public void wrongFieldOccupied(final FieldControlEvent e) {

            processFailedMove();
        }

        public void markField(final FieldControlEvent e) {

            doMarkField(e);
        }

        public void occupyField(final FieldControlEvent e) {

            doOccupyField(e);
        }
    };

    /**
     * Initializes the game mode "quiz".
     * 
     * @param eventHelper
     *            Game event helper to fire events.
     * @param nonogram
     *            Current nonogram pattern.
     * @param settings
     *            Settings object.
     */
    public GameMode_Quiz(final GameEventHelper eventHelper,
            final Nonogram nonogram, final Settings settings) {

        super(eventHelper, nonogram, settings);

        eventHelper.addGameListener(gameAdapter);

        setGameModeType(GameModeType.QUIZ);

        qp = QuestionsProvider
                .getInstance(QuestionProviderTypes.QUESTION_PROVIDER_MULTIPLICATIONS);
    }

    /**
     * Increases fail count when move on board failed, generate a quiz question
     * and fire a quiz event for the ui to ask user the question.
     */
    protected final void processFailedMove() {

        final int maximumMultipleChoiceLevel = 15;
        final int maximumMultiplicationLevel = 100;
        final int multiplicationLevelMultiplicator = 10;

        failCount++;

        if (qp instanceof QuestionsProviderMultipleChoice) {

            getEventHelper().fireQuizEvent(
                    new QuizEvent(this, qp.getNextQuestion(Math.min(failCount,
                            maximumMultipleChoiceLevel))));

        } else if (qp instanceof QuestionsProviderMultiplications) {

            getEventHelper().fireQuizEvent(
                    new QuizEvent(this, qp.getNextQuestion(Math.min(failCount
                            * multiplicationLevelMultiplicator,
                            maximumMultiplicationLevel))));
        }
    }

    /**
     * Checks whether an answer given by player is the correct asnwer for a
     * given question.
     * 
     * @param question
     *            Question for player.
     * @param answer
     *            Answer given by player.
     */
    public final void checkAnswer(final Question question, final String answer) {

        /*
         * TODO Make this method private and use event to deliver answer to this
         * game mode class.
         */

        if (question.checkAnswer(answer)) {

            isLost = false;

        } else {

            isLost = true;
        }
    }

    @Override
    public final boolean isSolved() {

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
    public final boolean isLost() {

        logger.debug("Quiz is lost: " + isLost);
        return isLost;
    }

    @Override
    public void solveGame() {
    }

    @Override
    public void pauseGame() {
    }

    @Override
    public void resumeGame() {
    }

    @Override
    public void stopGame() {
    }

    @Override
    public final void quitGame() {

        super.removeEventHelper();

        getEventHelper().removeGameListener(gameAdapter);
    }

    @Override
    public final int getGameScore() {

        int score = 0;

        // TODO implement this

        logger.info("highscore for game mode quiz calculated: " + score);
        return score;
    }

}
