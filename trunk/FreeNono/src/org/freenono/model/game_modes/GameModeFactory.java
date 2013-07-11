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
import org.freenono.event.GameEventHelper;
import org.freenono.model.data.Nonogram;

/**
 * Instantiate game mode classes.
 * 
 * @author Christian Wichmann
 */
public final class GameModeFactory {

    private static Logger logger = Logger.getLogger(GameModeFactory.class);

    /**
     * Private constructor for utility class.
     */
    private GameModeFactory() {

    }

    /**
     * Returns a game mode.
     * 
     * @param eventHelper
     *            game event helper
     * @param pattern
     *            nonogram pattern
     * @param settings
     *            settings object
     * @return game mode
     */
    public static GameMode getGameMode(final GameEventHelper eventHelper,
            final Nonogram pattern, final Settings settings) {

        GameMode gm = null;

        /*
         * Defensive copying because game mode related options should not change
         * while game runs. After starting or restarting a nonogram GameMode
         * classes are newly instantiated and option changes take effect.
         */
        final Settings gameSettings = new Settings(settings);

        switch (gameSettings.getGameMode()) {
        case PENALTY:
            gm = new GameMode_Penalty(eventHelper, pattern, gameSettings);
            logger.info("GameMode_Penalty instantiated.");
            break;

        case MAX_FAIL:
            gm = new GameMode_MaxFail(eventHelper, pattern, gameSettings);
            logger.info("GameMode_MaxFail instantiated.");
            break;

        case MAX_TIME:
            gm = new GameMode_MaxTime(eventHelper, pattern, gameSettings);
            logger.info("GameMode_MaxTime instantiated.");
            break;

        case COUNT_TIME:
            gm = new GameMode_CountTime(eventHelper, pattern, gameSettings);
            logger.info("GameMode_CountTime instantiated.");
            break;

        case QUIZ:
            gm = new GameMode_Quiz(eventHelper, pattern, gameSettings);
            logger.info("GameMode_Quiz instantiated.");
            break;

        case PEN_AND_PAPER:
            gm = new GameMode_PenAndPaper(eventHelper, pattern, gameSettings);
            logger.info("GameMode_PenAndPaper instantiated.");
            break;

        default:
            assert false : "Chosen game mode not implemented yet!";
        }

        return gm;
    }

}
