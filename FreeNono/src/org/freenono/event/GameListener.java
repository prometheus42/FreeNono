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
 * @author Markus Wichmann, Christian Wichmann
 */
public interface GameListener extends EventListener {

    // field control events
    public abstract void occupyField(FieldControlEvent e);

    public abstract void markField(FieldControlEvent e);

    public abstract void changeActiveField(FieldControlEvent e);

    public abstract void fieldOccupied(FieldControlEvent e);

    public abstract void fieldUnoccupied(FieldControlEvent e);

    public abstract void fieldMarked(FieldControlEvent e);

    public abstract void fieldUnmarked(FieldControlEvent e);

    public abstract void wrongFieldOccupied(FieldControlEvent e);

    // state changed events
    public abstract void stateChanged(StateChangeEvent e);
    
    /**
     * Invoked when state of game is changing. Should be used for all
     * non-blocking tasks.
     * 
     * @param e
     *            Event including old and new state of game.
     */
    public abstract void stateChanging(StateChangeEvent e);

    public abstract void setTime(StateChangeEvent e);

    public abstract void timerElapsed(StateChangeEvent e);

    public abstract void setFailCount(StateChangeEvent e);

    // program control events
    public abstract void optionsChanged(ProgramControlEvent e);

    public abstract void programControl(ProgramControlEvent e);

    // quiz events
    public abstract void askQuestion(QuizEvent e);

}
