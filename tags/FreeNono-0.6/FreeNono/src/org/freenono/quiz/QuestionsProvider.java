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

public abstract class QuestionsProvider {

    public static enum QuestionProviderTypes {
        QUESTION_PROVIDER_MULTIPLICATIONS, QUESTION_PROVIDER_MULTIPLE_CHOICE
    };

    /**
     * Provides next question of given level (difficulty).
     * 
     * @param level
     *            difficulty of the question (0-100)
     * @return next question
     */
    public Question getNextQuestion(int level) {
        return null;
    };

    public static QuestionsProvider getInstance(QuestionProviderTypes qpt) {

        switch (qpt) {
        case QUESTION_PROVIDER_MULTIPLICATIONS:
            return new QuestionsProviderMultiplications();

        case QUESTION_PROVIDER_MULTIPLE_CHOICE:
            return new QuestionsProviderMultipleChoice();

        default:
            return null;
        }
    }
}
