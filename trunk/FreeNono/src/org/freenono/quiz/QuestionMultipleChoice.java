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
package org.freenono.quiz;

import org.apache.log4j.Logger;

/**
 * Stores a multiple choice question.
 * 
 * @author Christian Wichmann
 */
public class QuestionMultipleChoice extends Question {

    private static Logger logger = Logger
            .getLogger(QuestionMultipleChoice.class);

    private String[] answers = null;
    private int correctAnswer = 0;

    /**
     * Initializes a new multiple choice question with its answers and solution.
     * 
     * @param question
     *            Question.
     * @param answers
     *            Possible answers.
     * @param correctAnswer
     *            Correct answer.
     */
    public QuestionMultipleChoice(final String question,
            final String[] answers, final int correctAnswer) {

        super(question, answers[correctAnswer - 1]);

        this.answers = answers;
        this.correctAnswer = correctAnswer;
        
        // TODO save all answers and allow output of them...
    }

    /**
     * Initializes a new multiple choice question without the possible answers.
     * 
     * @param question
     *            Question.
     * @param answer
     *            Correct answer.
     */
    public QuestionMultipleChoice(final String question, final String answer) {

        super(question, answer);
    }

    @Override
    public final boolean checkAnswer(final String answer) {

        logger.debug("Checking if answer is correct..." + answer + "  "
                + correctAnswer);

        if (answer.equals(Integer.toString(correctAnswer))) {
            return true;
        }

        return false;
    }

    /**
     * Gets answers for this question.
     * 
     * @return Answers
     */
    public final String[] getAnswers() {

        return answers;
    }

    /**
     * Sets answers for this question.
     * 
     * @param answers
     *            Answers
     */
    public final void setAnswers(final String[] answers) {

        this.answers = answers;
    }
}
