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
public class GameEventMulticaster extends AWTEventMulticaster implements
        GameListener {

    protected GameEventMulticaster(EventListener a, EventListener b) {
        super(a, b);
    }

    public static GameListener add(GameListener a, GameListener b) {
        return (GameListener) addInternal(a, b);
    }

    public static GameListener remove(GameListener l, GameListener oldl) {
        return (GameListener) removeInternal(l, oldl);
    }

    public void occupyField(FieldControlEvent e) {
        if (a != null)
            ((GameListener) a).occupyField(e);
        if (b != null)
            ((GameListener) b).occupyField(e);
    }

    public void markField(FieldControlEvent e) {
        if (a != null)
            ((GameListener) a).markField(e);
        if (b != null)
            ((GameListener) b).markField(e);
    }

    public void changeActiveField(FieldControlEvent e) {
        if (a != null)
            ((GameListener) a).changeActiveField(e);
        if (b != null)
            ((GameListener) b).changeActiveField(e);
    }

    public void stateChanged(StateChangeEvent e) {
        if (a != null)
            ((GameListener) a).stateChanged(e);
        if (b != null)
            ((GameListener) b).stateChanged(e);
    }

    public void timerElapsed(StateChangeEvent e) {
        if (a != null)
            ((GameListener) a).timerElapsed(e);
        if (b != null)
            ((GameListener) b).timerElapsed(e);
    }

    public void optionsChanged(ProgramControlEvent e) {
        if (a != null)
            ((GameListener) a).optionsChanged(e);
        if (b != null)
            ((GameListener) b).optionsChanged(e);
    }

    public void wrongFieldOccupied(FieldControlEvent e) {
        if (a != null)
            ((GameListener) a).wrongFieldOccupied(e);
        if (b != null)
            ((GameListener) b).wrongFieldOccupied(e);
    }

    public void programControl(ProgramControlEvent e) {
        if (a != null)
            ((GameListener) a).programControl(e);
        if (b != null)
            ((GameListener) b).programControl(e);
    }

    protected static EventListener addInternal(EventListener a, EventListener b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new GameEventMulticaster(a, b);
    }

    protected EventListener remove(EventListener oldl) {
        if (oldl == a)
            return b;
        if (oldl == b)
            return a;
        EventListener a2 = removeInternal(a, oldl);
        EventListener b2 = removeInternal(b, oldl);
        if (a2 == a && b2 == b)
            return this;
        return addInternal(a2, b2);
    }

    @Override
    public void fieldOccupied(FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fieldUnoccupied(FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fieldMarked(FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fieldUnmarked(FieldControlEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFailCount(StateChangeEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTime(StateChangeEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void askQuestion(QuizEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateChanging(StateChangeEvent e) {
        // TODO Auto-generated method stub
        
    }
}
