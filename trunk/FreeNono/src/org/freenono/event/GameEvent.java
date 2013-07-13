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

import java.util.EventObject;

/**
 * The class GameEvent is an abstract superclass for all event types in
 * FreeNono. GameEvent itself should not be used. Instead the subclasses defined
 * as enumeration <code>GameEventType</code> should be instantiated depending on
 * the necessary event that should be fired.
 * 
 * @author Markus Wichmann, Christian Wichmann
 */
abstract class GameEvent extends EventObject {

    private static final long serialVersionUID = 854958592468069527L;

    /**
     * Types of game events. All events inherit from this base class.
     * 
     * @author Christian Wichmann
     */
    public enum GameEventType {
        /**
         * ProgrammControlEvent: events that affect the whole application and
         * its control flow. This events are called by the UI.
         */
        PROGRAM_CONTROL_EVENT,

        /**
         * FieldControlEvent: events concerning a specific and current
         * game/nonogram, e.g. changes in the fields of the board. These events
         * are both the calls from UI by changing a field and the "answer" by
         * the game model.
         */
        FIELD_CONTROL_EVENT,

        /**
         * StateChangeEvent: events signaling a change in the state in which the
         * game is currently. All Timer events belong also in this category.
         */
        STATE_CHANGE_EVENT,

        /**
         * QuizEvent: events signaling and exchanging questions between game
         * model and user interface.
         */
        QUIZ_EVENT
    };

    private GameEventType gameEventType = null;
    private String comment;

    /**
     * Initializes a game event as super class for all event types defined in
     * the GameEventType enum.
     * 
     * @param source
     *            Source where event is fired.
     * @param eventType
     *            Type of this game event.
     */
    protected GameEvent(final Object source, final GameEventType eventType) {

        super(source);

        setGameEventType(eventType);
        setComment("");
    }

    /**
     * Gets comment for this game event.
     * 
     * @return Comment for this event.
     */
    public final String getComment() {

        return comment;
    }

    /**
     * Sets comment for this game event.
     * 
     * @param comment
     *            Comment for this game event.
     */
    private void setComment(final String comment) {

        this.comment = comment;
    }

    /**
     * Gets type of game event.
     * 
     * @return Type of this game event.
     */
    public final GameEventType getGameEventType() {

        return gameEventType;
    }

    /**
     * Sets game event type for this event.
     * 
     * @param gameEventType
     *            Type of this game event.
     */
    private void setGameEventType(final GameEventType gameEventType) {

        this.gameEventType = gameEventType;
    }
}
