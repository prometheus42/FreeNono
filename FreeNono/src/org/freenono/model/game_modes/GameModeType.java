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
package org.freenono.model.game_modes;

import org.freenono.ui.Messages;

/**
 * This enumeration specifies which game modes are available. Each game mode is implemented by a
 * subclass of GameMode, providing especially two methods: isLost() and isSolved().
 * <p>
 * By using the <code>toString</code> method this enum returns a localized string as caption for
 * every game mode.
 *
 * @author Christian Wichmann
 */
public enum GameModeType {

    /**
     * This game mode decreases the game time at each failed field by an penalty. With every error
     * the penalty is increasing.
     */
    PENALTY,

    /**
     * The MaxTime game mode is simply timing the game and declares it as lost if the given time is
     * elapsed.
     */
    MAX_TIME,

    /**
     * MaxFail only counts the errors of the player. The game is lost if too much errors are made.
     * No time restrictions are given.
     */
    MAX_FAIL,

    /**
     * The CountTime game mode allows the user to play the game as long as the nonogram is not
     * solved and counts up the time the user needs. For every error a penalty is added to the
     * time???
     */
    COUNT_TIME,

    /**
     * At the quiz mode after every wrong occupied field a quistion is asked and the game is lost if
     * the user answers wrong.
     */
    QUIZ,

    /**
     * The pen and paper game mode allows the player to play without any hints by the program. No
     * wrongly occupied fields will marked or any help given. Only when the nonogram is completely
     * solved the game is won.
     */
    PEN_AND_PAPER;

    @Override
    public String toString() {

        switch (this) {
        case PENALTY:
            return Messages.getString("GameMode.Penalty");
        case MAX_TIME:
            return Messages.getString("GameMode.MaxTime");
        case MAX_FAIL:
            return Messages.getString("GameMode.MaxFail");
        case COUNT_TIME:
            return Messages.getString("GameMode.CountTime");
        case QUIZ:
            return Messages.getString("GameMode.Quiz");
        case PEN_AND_PAPER:
            return Messages.getString("GameMode.PenAndPaper");
        default:
            assert false : this;
            return "";
        }
    }
}
