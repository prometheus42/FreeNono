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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;
import org.freenono.model.Token;

/**
 * Implements the game mode "Pen and Paper".
 * 
 * @author Christian Wichmann
 */
public class GameMode_PenAndPaper extends GameMode {

    private static Logger logger = Logger.getLogger(GameMode_Quiz.class);

    private Token[][] field = null;

    private GameAdapter gameAdapter = new GameAdapter() {

        public void wrongFieldOccupied(final FieldControlEvent e) {

        }

        public void markField(final FieldControlEvent e) {

            // TODO move this code to GameBoard or separate data class?!

            // mark or unmark field (independent of being the correct move!)
            if (field[e.getFieldRow()][e.getFieldColumn()] == Token.FREE) {

                field[e.getFieldRow()][e.getFieldColumn()] = Token.MARKED;
                getEventHelper().fireFieldMarkedEvent(
                        new FieldControlEvent(this, e.getFieldColumn(), e
                                .getFieldRow()));
            } else if (field[e.getFieldRow()][e.getFieldColumn()] == Token.MARKED) {

                field[e.getFieldRow()][e.getFieldColumn()] = Token.FREE;
                getEventHelper().fireFieldUnmarkedEvent(
                        new FieldControlEvent(this, e.getFieldColumn(), e
                                .getFieldRow()));
            }
        }

        public void occupyField(final FieldControlEvent e) {

            // occupy or unoccupy field (independent of being the correct move!)
            if (field[e.getFieldRow()][e.getFieldColumn()] == Token.FREE) {

                field[e.getFieldRow()][e.getFieldColumn()] = Token.OCCUPIED;
                getEventHelper().fireFieldOccupiedEvent(
                        new FieldControlEvent(this, e.getFieldColumn(), e
                                .getFieldRow()));
            } else if (field[e.getFieldRow()][e.getFieldColumn()] == Token.OCCUPIED) {

                field[e.getFieldRow()][e.getFieldColumn()] = Token.FREE;
                getEventHelper().fireFieldUnoccupiedEvent(
                        new FieldControlEvent(this, e.getFieldColumn(), e
                                .getFieldRow()));
            }
        }
    };

    /**
     * Initializes the game mode "pen and paper".
     * 
     * @param eventHelper
     *            Game event helper to fire events.
     * @param nonogram
     *            Current nonogram pattern.
     * @param settings
     *            Settings object.
     */
    public GameMode_PenAndPaper(final GameEventHelper eventHelper,
            final Nonogram nonogram, final Settings settings) {

        super(eventHelper, nonogram, settings);

        eventHelper.addGameListener(gameAdapter);

        setGameModeType(GameModeType.PEN_AND_PAPER);

        // deactivate marking of wrongly occupied fields
        setMarkInvalid(false);

        this.field = new Token[nonogram.height()][nonogram.width()];

        for (int i = 0; i < this.field.length; i++) {

            for (int j = 0; j < this.field[i].length; j++) {

                this.field[i][j] = Token.FREE;
            }
        }
    }

    @Override
    public final boolean isSolved() {

        int y, x;
        boolean patternValue;
        Token fieldValue;

        for (y = 0; y < getNonogram().height(); y++) {

            for (x = 0; x < getNonogram().width(); x++) {

                patternValue = getNonogram().getFieldValue(x, y);
                fieldValue = field[y][x];

                if (patternValue && fieldValue != Token.OCCUPIED) {
                    return false;
                }

                if (!patternValue && fieldValue == Token.OCCUPIED) {
                    return false;
                }
            }
        }

        logger.debug("Game mode PenAndPaper is won!");

        return true;
    }

    @Override
    public final boolean isLost() {

        return false;
    }

    @Override
    public void solveGame() {

    }

    @Override
    public void pauseGame() {

    }

    @Override
    public void resumeGame() {

    }

    @Override
    public void stopGame() {

    }

    @Override
    public final void quitGame() {

        super.removeEventHelper();

        getEventHelper().removeGameListener(gameAdapter);
    }

    @Override
    public final int getGameScore() {

        // score is always zero!
        return 0;
    }
}
