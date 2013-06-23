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

import java.util.EventListener;

/**
 * Listener interface for receiving Game Events.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public interface GameListener extends EventListener {

    /*
     * field control events
     */

    /**
     * Invoked when a field should be occupied.
     * 
     * @param e
     *            Field control event.
     */
    void occupyField(FieldControlEvent e);

    /**
     * Invoked when a field should be marked.
     * 
     * @param e
     *            Field control event.
     */
    void markField(FieldControlEvent e);

    /**
     * Invoked when the active field on the board changed.
     * 
     * @param e
     *            Field control event.
     */
    void changeActiveField(FieldControlEvent e);

    /**
     * Invoked when a field was occupied in the game model.
     * 
     * @param e
     *            Field control event.
     */
    void fieldOccupied(FieldControlEvent e);

    /**
     * Invoked when a field was unoccupied in the game model. Only possible in
     * pen-and-paper game mode.
     * 
     * @param e
     *            Field control event.
     */
    void fieldUnoccupied(FieldControlEvent e);

    /**
     * Invoked when a field was marked in the game model.
     * 
     * @param e
     *            Field control event.
     */
    void fieldMarked(FieldControlEvent e);

    /**
     * Invoked when a field was unmarked in the game model.
     * 
     * @param e
     *            Field control event.
     */
    void fieldUnmarked(FieldControlEvent e);

    /**
     * Invoked when a wrong field was occupied. Fired by the game model.
     * 
     * @param e
     *            Field control event.
     */
    void wrongFieldOccupied(FieldControlEvent e);

    /*
     * state changed events
     */

    /**
     * Invoked when state of game is changing. Should be used for all
     * non-blocking tasks.
     * 
     * @param e
     *            State change event including old and new state of game.
     */
    void stateChanging(StateChangeEvent e);

    /**
     * Invoked when state of game has changed. This event is fired after
     * {@code stateChanging}.
     * 
     * @param e
     *            State change event including old and new state of game.
     */
    void stateChanged(StateChangeEvent e);

    /**
     * Invoked when time of game is set like at the begin of a game.
     * 
     * @param e
     *            State change event including current game time.
     */
    void setTime(StateChangeEvent e);

    /**
     * Invoked when timer has elapsed to refresh all ui components.
     * 
     * @param e
     *            State change event including current game time.
     */
    void timerElapsed(StateChangeEvent e);

    /**
     * Invoked when fail count of game is set.
     * 
     * @param e
     *            State change event including games fail count.
     */
    void setFailCount(StateChangeEvent e);

    /*
     * program control events
     */

    /**
     * Invoked when options have changed.
     * 
     * @param e
     *            Program change event.
     */
    void optionsChanged(ProgramControlEvent e);

    /**
     * Invoked when a program control event like starting or stopping a game has
     * occured.
     * 
     * @param e
     *            Program change event.
     */
    void programControl(ProgramControlEvent e);

    /*
     * quiz events
     */

    /**
     * Invoked when user should answer a quiz question.
     * 
     * @param e
     *            Quiz event including quiz question.
     */
    void askQuestion(QuizEvent e);
}
