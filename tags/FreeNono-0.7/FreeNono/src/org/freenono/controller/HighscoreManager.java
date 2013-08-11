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
package org.freenono.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Game;
import org.freenono.model.game_modes.GameModeType;
import org.freenono.serializer.HighscoreFormatException;
import org.freenono.serializer.XMLHighscoreSerializer;
import org.freenono.ui.common.Tools;

/**
 * Manages the highscore by listening for events and updating highscore list.
 * Also it loads and saves highscore data from/to file.
 * 
 * @author Christian Wichmann
 */
public final class HighscoreManager {

    private static Logger logger = Logger.getLogger(HighscoreManager.class);

    private static final String DEFAULT_HIGHSCORE_FILE = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "highscore.xml";

    private static HighscoreManager instance = new HighscoreManager();

    private GameEventHelper eventHelper;
    private Highscores highscores;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void timerElapsed(final StateChangeEvent e) {
        }

        @Override
        public void stateChanging(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                break;

            case SOLVED:
                Game g = (Game) e.getSource();

                logger.debug("Adding score to highscore list: "
                        + g.getGameScore());

                // TODO Use game-wide player name instead of 'user.name'
                // property
                highscores.addScore(g.getGamePattern().getHash(), g
                        .getGameMode().getGameModeType(), (new Date())
                        .getTime(), System.getProperty("user.name"), g
                        .getGameScore());
                highscores.printHighscores(g.getGameMode().getGameModeType());
                break;

            case PAUSED:
                break;

            case RUNNING:
                break;

            case NONE:
                break;

            case USER_STOP:
                break;

            default:
                assert false : e.getNewState();
                break;
            }
        }

        @Override
        public void programControl(final ProgramControlEvent e) {
            switch (e.getPct()) {
            case START_GAME:
                break;

            case STOP_GAME:
                break;

            case RESTART_GAME:
                break;

            case PAUSE_GAME:
                break;

            case RESUME_GAME:
                break;

            case NONOGRAM_CHOSEN:
                break;

            case QUIT_PROGRAMM:
                handleExit();
                break;

            case OPTIONS_CHANGED:
                break;

            case SHOW_ABOUT:
                break;

            case SHOW_OPTIONS:
                break;

            default:
                assert false : e.getPct();
                break;
            }
        }
    };

    /**
     * Initializes a highscore manager.
     */
    private HighscoreManager() {

        // load highscore from file
        try {
            highscores = XMLHighscoreSerializer.loadHighscores(new File(
                    DEFAULT_HIGHSCORE_FILE));

        } catch (HighscoreFormatException e) {
            logger.warn("Highscore file could not be loaded: " + e.getMessage());

        } finally {
            if (highscores == null) {
                highscores = new Highscores();
            }
        }
        assert highscores != null;
    }

    /**
     * Handles exit of program by saving highscore data to file.
     */
    private void handleExit() {

        try {
            XMLHighscoreSerializer.saveHighscores(highscores, new File(
                    DEFAULT_HIGHSCORE_FILE));

        } catch (HighscoreFormatException e) {
            logger.warn("Highscore file could not be saved!");
        }
    }

    /**
     * Sets event helper to receive game events.
     * 
     * @param eventHelper
     *            game event helper
     */
    public void setEventHelper(final GameEventHelper eventHelper) {

        if (eventHelper == null) {
            throw new IllegalArgumentException(
                    "Argument eventHelper should not be null.");
        }

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    /**
     * Removes the event helper from this object.
     */
    public void removeEventHelper() {

        if (eventHelper != null) {
            eventHelper.removeGameListener(gameAdapter);
            this.eventHelper = null;
        }
    }

    /**
     * Returns a list of all scores that are saved. Actually this method returns
     * only an unmodifiable copy of the internal stored list of scores.
     * 
     * @return list of all highscores
     */
    public List<Score> getHighscoreList() {

        return highscores.getHighscoreList();
    }

    /**
     * Returns a list of all scores that are saved for a given game mode.
     * 
     * @param gameModeType
     *            game mode type that all scores should be returned
     * @return list of all highscores for given game mode
     */
    public List<Score> getHighscoreListForGameMode(
            final GameModeType gameModeType) {

        return highscores.getHighscoreListForGameMode(gameModeType);
    }

    /**
     * Returns a list of all scores that are saved for a given game mode and a
     * nonogram.
     * 
     * @param nonogramHash
     *            hash of the nonogram
     * @param gameModeType
     *            game mode type that all scores should be returned
     * @return list of all highscores for given game mode and nonogram
     */
    public List<Score> getHighscoreListForNonogram(final String nonogramHash,
            final GameModeType gameModeType) {

        return highscores.getHighscoreListForNonogram(nonogramHash,
                gameModeType);
    }

    /**
     * Returns always one and the same instance of HighscoreManager.
     * 
     * @return instance of HighscoreManager.
     */
    public static HighscoreManager getInstance() {

        return instance;
    }
}
