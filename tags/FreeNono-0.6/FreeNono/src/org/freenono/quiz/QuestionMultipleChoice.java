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

    public QuestionMultipleChoice(String question, String[] answers,
            int correctAnswer) {

        super(question, answers[correctAnswer - 1]);

        this.answers = answers;
        this.correctAnswer = correctAnswer;
        // TODO save all answers and allow output of them...
    }

    public QuestionMultipleChoice(String question, String answer) {

        super(question, answer);
    }

    public boolean checkAnswer(String answer) {

        logger.debug("Checking if answer is correct..." + answer + "  "
                + correctAnswer);

        if (answer.equals(Integer.toString(correctAnswer)))
            return true;

        return false;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }
}
