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
import org.freenono.event.FieldControlEvent.FieldControlType;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.StateChangeEvent.StateChangeType;

/**
 * Provides methods for firing events.
 *
 * @author Christian Wichmann, Markus Wichmann
 */
public final class GameEventHelper {

    private static Logger logger = Logger.getLogger(GameEventHelper.class);

    private final EventListenerList listeners = new EventListenerList();

    /**
     * Default constructor doing nothing.
     */
    public GameEventHelper() {

    }

    /**
     * Adds an {@code GameListener} to the helper class.
     *
     * @param l
     *            the {@code GameListener} to be added
     */
    public synchronized void addGameListener(final GameListener l) {

        listeners.add(GameListener.class, l);
    }

    /**
     * Removes an {@code GameListener} from the helper class.
     *
     * @param l
     *            the listener to be removed
     */
    public synchronized void removeGameListener(final GameListener l) {

        listeners.remove(GameListener.class, l);
    }

    /**
     * Reports that a specific field on the board <i>should be occupied</i>.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireOccupyFieldEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.OCCUPY_FIELD);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.occupyField(e);
        }
    }

    /**
     * Reports that a specific field on the board <i>should be marked</i>.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireMarkFieldEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.MARK_FIELD);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.markField(e);
        }
    }

    /**
     * Reports that the active field on board has changed.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireChangeActiveFieldEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.ACTIVE_FIELD_CHANGED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.changeActiveField(e);
        }
    }

    /**
     * Reports that a specific field on the board <i>was occupied </i>.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireFieldOccupiedEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.FIELD_OCCUPIED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.fieldOccupied(e);
        }
    }

    /**
     * Reports that a specific field on the board <i>was unoccupied </i>. Only possible in some game
     * modes.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireFieldUnoccupiedEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.FIELD_UNOCCUPIED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.fieldUnoccupied(e);
        }
    }

    /**
     * Reports that a specific field on the board <i>was marked</i>.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireFieldMarkedEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.FIELD_MARKED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.fieldMarked(e);
        }
    }

    /**
     * Reports that a specific field on the board <i>was unmarked</i>.
     *
     * @param e
     *            field control event defining field
     */
    public synchronized void fireFieldUnmarkedEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.FIELD_UNMARKED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.fieldUnmarked(e);
        }
    }

    /**
     * Reports that a specific field on the board <i>was wrongly occupied</i>.
     *
     * @param e
     *            field control event defining field.
     */
    public synchronized void fireWrongFieldOccupiedEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.WRONG_FIELD_OCCUPIED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.wrongFieldOccupied(e);
        }
    }

    /**
     * Reports that a caption can be crossed out because its block was completely uncovered.
     *
     * @param e
     *            field control event defining field.
     */
    public synchronized void fireCrossOutCaptionEvent(final FieldControlEvent e) {

        e.setFieldControlType(FieldControlType.CROSS_OUT_CAPTION);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.crossOutCaption(e);
        }
    }

    /**
     * Reports that state of game <i>has changed</i>.
     *
     * @param e
     *            state change event defining old and new state of game
     */
    public synchronized void fireStateChangedEvent(final StateChangeEvent e) {

        e.setStateChangeType(StateChangeType.STATE_CHANGED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.stateChanged(e);
        }
        logger.debug("Game state changed from " + e.getOldState() + " to " + e.getNewState());
    }

    /**
     * Reports that state of game <i>will change</i>.
     *
     * @param e
     *            state change event defining old and new state of game
     */
    public synchronized void fireStateChangingEvent(final StateChangeEvent e) {

        e.setStateChangeType(StateChangeType.STATE_CHANGING);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.stateChanging(e);
        }
    }

    /**
     * Reports that a second has passed since last call of this method.
     *
     * @param e
     *            state change event
     */
    public synchronized void fireTimerEvent(final StateChangeEvent e) {

        e.setStateChangeType(StateChangeType.TIMER);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.timerElapsed(e);

            // TODO use this maybe???
            // EventQueue.invokeLater(new Runnable() {
            // @Override
            // public void run() {
            // l.timerElapsed(e);
            // }});
        }
    }

    /**
     * Reports that game time has changed other than its regular clock. This method is used by game
     * modes that support time penalties of some kind and have to change game time accordingly.
     *
     * @param e
     *            state change event including new game time
     */
    public synchronized void fireSetTimeEvent(final StateChangeEvent e) {

        e.setStateChangeType(StateChangeType.SET_TIME);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.setTime(e);
        }
    }

    /**
     * Reports that fail count of game has changed. It is used by game modes that support a maximum
     * given amount of allowed errors.
     *
     * @param e
     *            state change event including new fail count
     */
    public synchronized void fireSetFailCountEvent(final StateChangeEvent e) {

        e.setStateChangeType(StateChangeType.SET_FAIL_COUNT);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.setFailCount(e);
        }
    }

    /**
     * Reports that at least on option has changed.
     *
     * @param e
     *            program control event
     */
    public synchronized void fireOptionsChangedEvent(final ProgramControlEvent e) {

        e.setPct(ProgramControlType.OPTIONS_CHANGED);

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.optionsChanged(e);
        }
    }

    /**
     * Reports that a program control event has occurred. Program control events represent a action
     * by the user like starting or stopping a game through the user interface.
     *
     * @param e
     *            program control event including which event occurred
     */
    public synchronized void fireProgramControlEvent(final ProgramControlEvent e) {

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.programControl(e);
        }
    }

    /**
     * Reports that the user interface has to ask the user a question provided by
     * <code>QuizEvent</code>.
     *
     * @param e
     *            quiz event including question to be asked
     */
    public synchronized void fireQuizEvent(final QuizEvent e) {

        for (final GameListener l : listeners.getListeners(GameListener.class)) {
            l.askQuestion(e);
        }
    }
}
