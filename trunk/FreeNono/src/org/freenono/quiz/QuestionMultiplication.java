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
 * Stores a multiplication question.
 * 
 * @author Christian Wichmann
 */
public class QuestionMultiplication extends Question {

    private static Logger logger = Logger
            .getLogger(QuestionMultiplication.class);

    /**
     * Initializes a new multiplication question with its correct answer.
     * 
     * @param question Multiplication question.
     * @param answer Correct answer.
     */
    public QuestionMultiplication(final String question, final String answer) {

        super(question, answer);
    }

    @Override
    public final boolean checkAnswer(final String answer) {

        try {
            logger.debug("numbers: " + getAnswer() + ", " + answer);
            logger.debug("numbers: " + Integer.valueOf(getAnswer()) + ", "
                    + Integer.valueOf(answer));

            return !(Integer.valueOf(getAnswer()).compareTo(
                    Integer.valueOf(answer)) != 0);

        } catch (NumberFormatException e) {

            logger.warn("Given answer not a number!");
        }

        return false;
    }
}
