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
package org.freenono.interfaces;

import java.util.Date;

import org.freenono.model.GameState;
import org.freenono.model.Nonogram;
import org.freenono.model.Token;

/**
 * @author Markus Wichmann
 * 
 */
public interface Game {

    // could be exchanged for the constructor
    void init(GameManager gameManager);

    Nonogram getPattern();

    int getWidth();

    int getHeight();

    Token getFieldValue(int x, int y);

    boolean isSolved();

    void solveGame();

    void gibeHint();

    boolean canMark(int x, int y);

    boolean mark(int x, int y);

    boolean canOccupy(int x, int y);

    boolean occupy(int x, int y);

    void startGame();

    void pauseGame();

    void resumeGame();

    void stopGame();

    boolean isOver();

    boolean isRunning();

    GameState getState();

    int getMaxFailCount();

    long getMaxTime();

    boolean getUsesMaxFailCount();

    boolean getUsesMaxTime();

    Date getElapsedTime();

    Date getTimeLeft();

    int getFailCountLeft();

    boolean getMarkInvalid();

    boolean getCountMarked();

}
