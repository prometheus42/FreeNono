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
package org.freenono.event;

import org.freenono.quiz.Question;

/**
 * Event used by quiz game mode to exchange questions between game model and
 * user interface.
 * 
 * @author Christian Wichmann
 */
public class QuizEvent extends GameEvent {

    private static final long serialVersionUID = -314426749970621992L;

    private Question question = null;

    /**
     * Initializes a quiz event.
     * 
     * @param source Source where event was fired.
     * @param question Question the user should be asked.
     */
    public QuizEvent(final Object source, final Question question) {

        super(source, GameEventType.QuizEvent);
        this.setQuestion(question);
    }

    /**
     * Gets the question for this event.
     * 
     * @return Question for this event.
     */
    public final Question getQuestion() {
        
        return question;
    }

    /**
     * Sets question for this event.
     * 
     * @param question Question for this event.
     */
    public final void setQuestion(final Question question) {
        
        this.question = question;
    }

}
