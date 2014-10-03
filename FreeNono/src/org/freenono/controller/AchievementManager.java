/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2014 by FreeNono Development Team
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Achievement;
import org.freenono.model.data.Nonogram;
import org.freenono.ui.common.Tools;

/**
 * Manages the achievements and signals the user interface when one was
 * accomplished.
 * 
 * @author Christian Wichmann
 */
public final class AchievementManager {

    private static Logger logger = Logger.getLogger(AchievementManager.class);

    @SuppressWarnings("unused")
    private static final String DEFAULT_ACHIEVEMENT_FILE = System.getProperty("user.home") + Tools.FILE_SEPARATOR + ".FreeNono"
            + Tools.FILE_SEPARATOR + "achievements.xml";

    private static AchievementManager instance = new AchievementManager();
    @SuppressWarnings("unused")
    private static Settings currentSettings;
    @SuppressWarnings("unused")
    private static Nonogram currentNonogram;

    private static Random rnd = new Random();

    private final Map<Achievement, Boolean> achievementMap = new HashMap<>();

    private GameEventHelper eventHelper;

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
                logger.info("");
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
                currentNonogram = e.getPattern();
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
        public void changeActiveField(final FieldControlEvent e) {

        }
    };

    /**
     * Initializes a high score manager.
     */
    private AchievementManager() {

        // set up map with all achievements
        for (Achievement achievement : Achievement.values()) {
            achievementMap.put(achievement, false);
        }

        // TODO load achievements from file
    }

    /**
     * Handles exit of program by saving highscore data to file.
     */
    private void handleExit() {

        // TODO save achievements to file
    }

    /**
     * Sets event helper to receive game events.
     * 
     * @param eventHelper
     *            game event helper
     */
    public void setEventHelper(final GameEventHelper eventHelper) {

        if (eventHelper == null) {
            throw new IllegalArgumentException("Argument eventHelper should not be null.");
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
     * Checks whether a given achievement was already accomplished prior to the
     * call of this method. Regularly achievements can NOT be unaccomplished!
     * 
     * @param achievement
     *            achievement that should be checked
     * @return whether the given achievement was already accomplished
     */
    public boolean isAchievementAccomplished(final Achievement achievement) {

        return rnd.nextBoolean();
    }

    /**
     * Checks whether all currently supported achievements were already
     * accomplished prior to the call of this method. Regularly achievements can
     * NOT be unaccomplished!
     * 
     * @return whether all achievements were already accomplished
     */
    public boolean areAllAchievementAccomplished() {

        for (Achievement achievement : Achievement.values()) {
            if (!isAchievementAccomplished(achievement)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns always one and the same instance of AchievementManager and sets
     * settings object once for all future calls of this method.
     * 
     * @param settings
     *            settings object
     * @return instance of AchievementManager
     */
    public static AchievementManager getInstance(final Settings settings) {

        currentSettings = settings;
        return instance;
    }

    /**
     * Returns always one and the same instance of AchievementManager.
     * 
     * @return instance of AchievementManager
     */
    public static AchievementManager getInstance() {

        return instance;
    }
}
