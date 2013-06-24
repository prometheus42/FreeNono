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

import java.util.Random;

import org.freenono.ui.Messages;

/**
 * Provides multiplication questions like "How much is 4 times 7?". If the given
 * difficulty (level) for the question is below 50 the multiplicants are between
 * 1 and 10, else between 10 and 20.
 * 
 * @author Christian Wichmann
 */
public class QuestionsProviderMultiplications extends QuestionsProvider {

    private Random rng = null;

    /**
     * Initializes a question provider delivering multiplication questions.
     */
    public QuestionsProviderMultiplications() {

        rng = new Random();
    }

    @Override
    public final Question getNextQuestion(final int level) {

        final int minNumber;
        final int maxNumber;
        final int levelThreshold = 50;

        if (level > levelThreshold) {
            minNumber = 11;
            maxNumber = 20;
        } else {
            minNumber = 1;
            maxNumber = 10;
        }

        int a = rng.nextInt(maxNumber - minNumber + 1) + minNumber;
        int b = rng.nextInt(maxNumber - minNumber + 1) + minNumber;

        Question q = new QuestionMultiplication(
                new String(
                        Messages.getString("QuestionsProviderMultiplications.QuestionText")
                                + a + ", " + b), new String(Integer.toString(a
                        * b)));

        return q;
    }

}
