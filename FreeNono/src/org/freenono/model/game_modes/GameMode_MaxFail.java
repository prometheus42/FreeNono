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
import org.freenono.event.StateChangeEvent;
import org.freenono.model.data.Nonogram;

/**
 * Implements the game mode "Max Fail".
 * 
 * @author Christian Wichmann
 */
public class GameMode_MaxFail extends GameMode {

    private static Logger logger = Logger.getLogger(GameMode_MaxFail.class);

    private int failCount = 0;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void wrongFieldOccupied(final FieldControlEvent e) {
            processFailedMove();
        }

        @Override
        public void markField(final FieldControlEvent e) {
            doMarkField(e);
        }

        @Override
        public void occupyField(final FieldControlEvent e) {
            doOccupyField(e);
        }
    };

    /**
     * Initializes the game mode "maximum fail".
     * 
     * @param eventHelper
     *            Game event helper to fire events.
     * @param nonogram
     *            Current nonogram pattern.
     * @param settings
     *            Settings to get maximum fail count for this game mode.
     */
    public GameMode_MaxFail(final GameEventHelper eventHelper,
            final Nonogram nonogram, final Settings settings) {

        super(eventHelper, nonogram, settings);

        eventHelper.addGameListener(gameAdapter);

        setGameModeType(GameModeType.MAX_FAIL);

        failCount = settings.getMaxFailCount();

        eventHelper
                .fireSetFailCountEvent(new StateChangeEvent(this, failCount));
    }

    /**
     * Subtracts one failed move from fail count and fires a set fail count
     * event.
     */
    protected final void processFailedMove() {

        failCount--;
        getEventHelper().fireSetFailCountEvent(
                new StateChangeEvent(this, failCount));
    }

    @Override
    public final boolean isSolved() {

        boolean isSolved = false;

        if (isSolvedThroughMarked()) {
            isSolved = true;
            logger.debug("Game solved through marked.");
        }

        if (isSolvedThroughOccupied()) {
            isSolved = true;
            logger.debug("Game solved through occupied.");
        }

        return isSolved;
    }

    @Override
    public final boolean isLost() {

        return (failCount <= 0);
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

        int score = 0;

        score = failCount;

        logger.info("highscore for game mode maxfail calculated: " + score);
        return score;
    }

}
