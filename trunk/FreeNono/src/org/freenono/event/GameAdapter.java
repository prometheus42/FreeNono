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

/**
 * Abstract adapter class to prevent the necessity to implement every Listener
 * function in every GameListener.
 */
public class GameAdapter implements GameListener {

    @Override
    public void markField(final FieldControlEvent e) {
    }

    @Override
    public void occupyField(final FieldControlEvent e) {
    }

    @Override
    public void changeActiveField(final FieldControlEvent e) {
    }

    @Override
    public void wrongFieldOccupied(final FieldControlEvent e) {
    }

    @Override
    public void crossOutCaption(final FieldControlEvent e) {
    }

    @Override
    public void fieldOccupied(final FieldControlEvent e) {
    }

    @Override
    public void fieldUnoccupied(final FieldControlEvent e) {
    }

    @Override
    public void fieldMarked(final FieldControlEvent e) {
    }

    @Override
    public void fieldUnmarked(final FieldControlEvent e) {
    }

    @Override
    public void stateChanged(final StateChangeEvent e) {
    }

    @Override
    public void stateChanging(final StateChangeEvent e) {
    }

    @Override
    public void setTime(final StateChangeEvent e) {
    }

    @Override
    public void timerElapsed(final StateChangeEvent e) {
    }

    @Override
    public void setFailCount(final StateChangeEvent e) {
    }

    @Override
    public void optionsChanged(final ProgramControlEvent e) {
    }

    @Override
    public void programControl(final ProgramControlEvent e) {
    }

    @Override
    public void askQuestion(final QuizEvent e) {
    }

}
