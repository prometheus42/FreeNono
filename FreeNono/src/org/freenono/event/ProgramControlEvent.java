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

import org.freenono.model.data.Nonogram;

/**
 * Event used for all program control commands like starting or stopping a game.
 * These events are all fired by the user interface.
 * 
 * @author Christian Wichmann
 */
public class ProgramControlEvent extends GameEvent {

    private static final long serialVersionUID = -6463642216777461887L;

    /**
     * Types of program control events.
     * 
     * @author Christian Wichmann
     */
    public enum ProgramControlType {
        START_GAME, STOP_GAME, PAUSE_GAME, RESTART_GAME, RESUME_GAME, 
        QUIT_PROGRAMM, SHOW_OPTIONS, SHOW_ABOUT, NONOGRAM_CHOSEN, 
        OPTIONS_CHANGED
    };

    private ProgramControlType pct = null;
    private Nonogram pattern = null;

    
    /**
     * Initializes a ProgramControlEvent to inform all listeners of a program
     * change like starting or stopping a game. 
     * 
     * @param source Source where event was fired.
     * @param pct Type of program control event.
     */
    public ProgramControlEvent(final Object source, final ProgramControlType pct) {
        
        super(source, GameEventType.PROGRAM_CONTROL_EVENT);
        
        this.setPct(pct);
        this.setPattern(null);
    }

    /**
     * Second constructor for use with the ProgrammControlTypes START_GAME,
     * NONOGRAM_CHOSEN and RESTART_GAME. The passed value currentNonogram is the
     * new started, restarted or chosen nonogram.
     * 
     * @param source Source where event was fired.
     * @param pct Type of program control event.
     * @param currentNonogram Nonogram pattern for this event.
     */
    public ProgramControlEvent(final Object source, final ProgramControlType pct,
            final Nonogram currentNonogram) {
        
        super(source, GameEventType.PROGRAM_CONTROL_EVENT);
        
        setPct(pct);
        setPattern(currentNonogram);
    }

    /**
     * Gets type of ProgramControlEvent.
     * 
     * @return The type of ProgramControlEvent.
     */
    public final ProgramControlType getPct() {
        
        return pct;
    }

    /**
     * Sets type of ProgramControlEvent.
     * 
     * @param pct
     *            Type of ProgramControlEvent for this event.
     */
    public final void setPct(final ProgramControlType pct) {
        
        this.pct = pct;
    }

    /**
     * Gets nonogram pattern for this event.
     * 
     * @return Nonogram pattern for this event.
     */
    public final Nonogram getPattern() {
        
        return pattern;
    }

    /**
     * Sets nonogram pattern for this event.
     * 
     * @param pattern
     *            nonogram pattern for this event.
     */
    public final void setPattern(final Nonogram pattern) {
        
        this.pattern = pattern;
    }
}
