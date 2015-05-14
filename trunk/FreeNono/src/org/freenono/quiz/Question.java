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

/**
 * Abstract class representing a question and its correct answer.
 *
 * @author Christian Wichmann
 */
public abstract class Question {

    private String question = null;
    private String correctAnswer = null;

    /**
     * Initializes a new question with the question and its answer.
     *
     * @param question
     *            Question
     * @param answer
     *            Answer
     */
    public Question(final String question, final String answer) {

        this.question = question;
        this.correctAnswer = answer;
    }

    /**
     * Gets the question itself.
     *
     * @return Question
     */
    public final String getQuestion() {

        return question;
    }

    /**
     * Sets the question itself.
     *
     * @param question
     *            Question
     */
    public final void setQuestion(final String question) {

        this.question = question;
    }

    /**
     * Gets the answer for this question.
     *
     * @return Answer
     */
    public final String getAnswer() {

        return correctAnswer;
    }

    /**
     * Sets the answer for this question.
     *
     * @param answer
     *            Answer
     */
    public final void setAnswer(final String answer) {

        this.correctAnswer = answer;
    }

    /**
     * Returns a String object representing this question. Usually only the question itself without
     * answers.
     *
     * @return String object representing this question.
     */
    @Override
    public final String toString() {

        return question;
    }

    /**
     * Checks whether the given parameter is the correct answer.
     *
     * @param answer
     *            answer given by user
     * @return true if given answer is correct
     */
    public abstract boolean checkAnswer(final String answer);

}
