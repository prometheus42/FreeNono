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
import org.freenono.model.data.Nonogram;
import org.freenono.model.game_modes.GameTimeHelper.GameTimerDirection;

/**
 * Implements the game mode "Max Time".
 *
 * @author Christian Wichmann
 */
public class GameMode_MaxTime extends GameMode {

    private static Logger logger = Logger.getLogger(GameMode_Penalty.class);

    private GameTimeHelper gameTimeHelper = null;

    private final GameAdapter gameAdapter = new GameAdapter() {

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
     * Initializes the game mode "maximum time".
     *
     * @param eventHelper
     *            Game event helper to fire events.
     * @param nonogram
     *            Current nonogram pattern.
     * @param settings
     *            Settings to get start time for this game mode.
     */
    public GameMode_MaxTime(final GameEventHelper eventHelper, final Nonogram nonogram, final Settings settings) {

        super(eventHelper, nonogram, settings);

        eventHelper.addGameListener(gameAdapter);

        setGameModeType(GameModeType.MAX_TIME);

        gameTimeHelper =
                new GameTimeHelper(eventHelper, GameTimerDirection.COUNT_DOWN, nonogram.getDuration() == 0 ? settings.getMaxTime()
                        : nonogram.getDuration() * GameTime.MILLISECONDS_PER_SECOND);
        gameTimeHelper.startTime();

        // mark never wrongly occupied fields for this game mode
        setMarkInvalid(false);
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

        boolean isLost = false;

        if (gameTimeHelper.isTimeElapsed()) {

            isLost = true;
        }

        return isLost;
    }

    @Override
    public final void pauseGame() {

        gameTimeHelper.stopTime();
    }

    @Override
    public final void resumeGame() {

        gameTimeHelper.startTime();
    }

    @Override
    public final void stopGame() {

        if (gameTimeHelper != null) {
            gameTimeHelper.stopTime();
        }
    }

    @Override
    public final void solveGame() {

        getGameBoard().solveGame();
    }

    @Override
    public final void quitGame() {

        super.removeEventHelper();

        if (gameTimeHelper != null) {
            gameTimeHelper.stopTime();
            gameTimeHelper.stopTimer();
            gameTimeHelper = null;
        }

        getEventHelper().removeGameListener(gameAdapter);
    }

    @Override
    public final int getGameScore() {

        int score = 0;

        if (gameTimeHelper.isTimeElapsed()) {

            score = 0;

        } else {

            score = gameTimeHelper.getGameTime().getMinutes() * GameTime.SECONDS_PER_MINUTE + gameTimeHelper.getGameTime().getSeconds();
            assert score > 0 : "Score of solved game should never be zero.";
        }

        logger.info("highscore for game mode maxtime calculated: " + score);

        return score;
    }
}
