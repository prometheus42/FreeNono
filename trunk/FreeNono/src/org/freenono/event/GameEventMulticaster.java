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

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

/**
 * EventMulticast to dispatch events to event listeners all over the program.
 *
 * (copied from http://www.javaworld.com/javaworld/javatips/jw-javatip35.html)
 *
 */
public class GameEventMulticaster extends AWTEventMulticaster implements GameListener {

    /**
     *
     * @param a
     *            event listener
     * @param b
     *            event listener
     */
    protected GameEventMulticaster(final EventListener a, final EventListener b) {
        super(a, b);
    }

    /**
     *
     * @param a
     *            game listener
     * @param b
     *            game listener
     * @return game listener
     */
    public static GameListener add(final GameListener a, final GameListener b) {
        return (GameListener) addInternal(a, b);
    }

    /**
     *
     * @param l
     *            game listener
     * @param oldl
     *            game listener
     * @return game listener
     */
    public static GameListener remove(final GameListener l, final GameListener oldl) {
        return (GameListener) removeInternal(l, oldl);
    }

    /**
     *
     * @param e
     *            field control event
     */
    @Override
    public final void occupyField(final FieldControlEvent e) {
        if (a != null) {
            ((GameListener) a).occupyField(e);
        }
        if (b != null) {
            ((GameListener) b).occupyField(e);
        }
    }

    /**
     *
     * @param e
     *            field control event
     */
    @Override
    public final void markField(final FieldControlEvent e) {
        if (a != null) {
            ((GameListener) a).markField(e);
        }
        if (b != null) {
            ((GameListener) b).markField(e);
        }
    }

    /**
     *
     * @param e
     *            field control event
     */
    @Override
    public final void changeActiveField(final FieldControlEvent e) {
        if (a != null) {
            ((GameListener) a).changeActiveField(e);
        }
        if (b != null) {
            ((GameListener) b).changeActiveField(e);
        }
    }

    /**
     *
     * @param e
     *            state change event
     */
    @Override
    public final void stateChanged(final StateChangeEvent e) {
        if (a != null) {
            ((GameListener) a).stateChanged(e);
        }
        if (b != null) {
            ((GameListener) b).stateChanged(e);
        }
    }

    /**
     *
     * @param e
     *            state change event
     */
    @Override
    public final void timerElapsed(final StateChangeEvent e) {
        if (a != null) {
            ((GameListener) a).timerElapsed(e);
        }
        if (b != null) {
            ((GameListener) b).timerElapsed(e);
        }
    }

    /**
     *
     * @param e
     *            program control event
     */
    @Override
    public final void optionsChanged(final ProgramControlEvent e) {
        if (a != null) {
            ((GameListener) a).optionsChanged(e);
        }
        if (b != null) {
            ((GameListener) b).optionsChanged(e);
        }
    }

    /**
     *
     * @param e
     *            field control event
     */
    @Override
    public final void wrongFieldOccupied(final FieldControlEvent e) {
        if (a != null) {
            ((GameListener) a).wrongFieldOccupied(e);
        }
        if (b != null) {
            ((GameListener) b).wrongFieldOccupied(e);
        }
    }

    /**
     *
     * @param e
     *            field control event
     */
    @Override
    public final void crossOutCaption(final FieldControlEvent e) {
        if (a != null) {
            ((GameListener) a).crossOutCaption(e);
        }
        if (b != null) {
            ((GameListener) b).crossOutCaption(e);
        }
    }

    /**
     *
     * @param e
     *            program control event
     */
    @Override
    public final void programControl(final ProgramControlEvent e) {
        if (a != null) {
            ((GameListener) a).programControl(e);
        }
        if (b != null) {
            ((GameListener) b).programControl(e);
        }
    }

    /**
     *
     * @param a
     *            event listener
     * @param b
     *            event listener
     * @return event listener
     */
    protected static EventListener addInternal(final EventListener a, final EventListener b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new GameEventMulticaster(a, b);
    }

    /**
     *
     * @param oldl
     *            event listener
     * @return event listener
     */
    @Override
    protected final EventListener remove(final EventListener oldl) {
        if (oldl == a) {
            return b;
        }
        if (oldl == b) {
            return a;
        }
        final EventListener a2 = removeInternal(a, oldl);
        final EventListener b2 = removeInternal(b, oldl);
        if (a2 == a && b2 == b) {
            return this;
        }
        return addInternal(a2, b2);
    }

    @Override
    public void fieldOccupied(final FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fieldUnoccupied(final FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fieldMarked(final FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fieldUnmarked(final FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFailCount(final StateChangeEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTime(final StateChangeEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void askQuestion(final QuizEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateChanging(final StateChangeEvent e) {
        // TODO Auto-generated method stub

    }
}
