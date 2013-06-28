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

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

public class GameEventHelper {

    private static Logger logger = Logger.getLogger(GameEventHelper.class);

    private EventListenerList listeners = new EventListenerList();

    // private GameListener gameListener = null;

    public GameEventHelper() {
    }

    /**
     * Adds an {@code GameListener} to the helper class.
     * 
     * @param l
     *            the {@code GameListener} to be added
     */
    public synchronized void addGameListener(GameListener l) {
        listeners.add(GameListener.class, l);
    }

    /**
     * Removes an {@code GameListener} from the helper class.
     * 
     * @param l
     *            the listener to be removed
     */
    public synchronized void removeGameListener(GameListener l) {
        listeners.remove(GameListener.class, l);
    }

    // public synchronized void addGameListener(GameListener l) {
    // gameListener = GameEventMulticaster.add(gameListener, l);
    // }
    //
    // public synchronized void removeGameListener(GameListener l) {
    // gameListener = GameEventMulticaster.remove(gameListener, l);
    // }
    //
    // public void fireFieldOccupiedEvent(GameEvent e) {
    // if (gameListener != null) {
    // gameListener.FieldOccupied(new GameEvent());
    // }
    // }

    public synchronized void fireOccupyFieldEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.occupyField(e);
    }

    public synchronized void fireMarkFieldEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.markField(e);
    }

    public synchronized void fireChangeActiveFieldEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.changeActiveField(e);
    }

    public synchronized void fireFieldOccupiedEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.fieldOccupied(e);
    }

    public synchronized void fireFieldUnoccupiedEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.fieldUnoccupied(e);
    }

    public synchronized void fireFieldMarkedEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.fieldMarked(e);
    }

    public synchronized void fireFieldUnmarkedEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.fieldUnmarked(e);
    }

    public synchronized void fireWrongFieldOccupiedEvent(FieldControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.wrongFieldOccupied(e);
    }

    public synchronized void fireStateChangedEvent(StateChangeEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.stateChanged(e);
        logger.debug("Game state changed from " + e.getOldState() + " to "
                + e.getNewState());
    }

    public synchronized void fireTimerEvent(StateChangeEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.timerElapsed(e);
    }

    public synchronized void fireSetTimeEvent(StateChangeEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.setTime(e);
    }

    public synchronized void fireSetFailCountEvent(StateChangeEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.setFailCount(e);
    }

    public synchronized void fireOptionsChangedEvent(ProgramControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.optionsChanged(e);
    }

    public synchronized void fireProgramControlEvent(ProgramControlEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.programControl(e);
    }

    public synchronized void fireQuizEvent(QuizEvent e) {
        for (GameListener l : listeners.getListeners(GameListener.class))
            l.askQuestion(e);
    }

}