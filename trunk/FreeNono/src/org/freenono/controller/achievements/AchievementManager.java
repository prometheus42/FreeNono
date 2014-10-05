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
package org.freenono.controller.achievements;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.model.data.Nonogram;
import org.freenono.ui.common.Tools;

/**
 * Manages the achievements and signals the user interface when one was
 * accomplished.
 * <p>
 * Implemented as a Singleton. Only one instance can exist at a time. When a new
 * instance is requested and the event helper class that is given differs from
 * the old one, a new instance is created.
 * 
 * @author Christian Wichmann
 */
public final class AchievementManager {

    private static Logger logger = Logger.getLogger(AchievementManager.class);

    @SuppressWarnings("unused")
    private static final String DEFAULT_ACHIEVEMENT_FILE = System.getProperty("user.home") + Tools.FILE_SEPARATOR + ".FreeNono"
            + Tools.FILE_SEPARATOR + "achievements.xml";

    private static AchievementManager instance = null;
    @SuppressWarnings("unused")
    private static Settings currentSettings;
    @SuppressWarnings("unused")
    private static Nonogram currentNonogram;

    private final Map<Achievement, Boolean> achievementMap = new HashMap<>();
    private final Map<Achievement, AchievementMeter> achievementMeterMap = new HashMap<>();

    private GameEventHelper eventHelper;

    private GameAdapter gameAdapter = new GameAdapter() {
        @Override
        public void programControl(final ProgramControlEvent e) {
            if (e.getPct() == ProgramControlType.QUIT_PROGRAMM) {
                handleExit();
            }
        }
    };

    /**
     * Initializes a new achievement manager.
     */
    private AchievementManager() {

        // TODO load achievements from file
    }

    /**
     * Sets up all meters to measure achievement accomplishment.
     */
    private void setupAchievementMeters() {

        // set up map with all achievements and their meters
        for (Achievement achievement : Achievement.values()) {
            achievementMap.put(achievement, false);

            AchievementMeter newMeter = null;
            switch (achievement) {
            case HIGH_SPEED_SOLVING:
                newMeter = new AchievementMeterSpeed(achievement, 25);
                break;
            case COURSE_COMPLETED:
                newMeter = new AchievementMeterFaultlessness(achievement, 10);
                break;
            case FIVE_WITHOUT_ERROR:
                newMeter = new AchievementMeterFaultlessness(achievement, 5);
                break;
            case ONE_WITHOUT_ERROR:
                newMeter = new AchievementMeterFaultlessness(achievement, 1);
                break;
            case THREE_WITHOUT_ERROR:
                newMeter = new AchievementMeterFaultlessness(achievement, 3);
                break;
            case ULTRA_HIGH_SPEED_SOLVING:
                newMeter = new AchievementMeterSpeed(achievement, 75);
                break;
            case VERY_HIGH_SPEED_SOLVING:
                newMeter = new AchievementMeterSpeed(achievement, 50);
                break;
            default:
                assert false : "Achievement " + achievement + " not valid!";
                break;
            }

            achievementMeterMap.put(achievement, newMeter);
        }

        updateAchievements();
    }

    /**
     * Handles exit of program by saving highscore data to file.
     */
    private void handleExit() {

        // TODO save achievements to file
    }

    /**
     * Returns always one and the same instance of AchievementManager and sets
     * settings object once for all future calls of this method.
     * 
     * @return instance of AchievementManager
     */
    public static AchievementManager getInstance() {

        if (instance == null) {
            throw new IllegalArgumentException("Call getInstance() with game event helper first!");
        }

        return instance;
    }

    /**
     * Returns always one and the same instance of AchievementManager.
     * 
     * @param eventHelper
     *            game event helper instance
     * @return instance of AchievementManager
     */
    public static AchievementManager getInstance(final GameEventHelper eventHelper) {

        if (instance == null || instance.eventHelper != eventHelper) {
            logger.debug("Creating new instance of achievement manager.");
            instance = new AchievementManager();
            instance.setEventHelper(eventHelper);
        }
        return instance;
    }

    /**
     * Sets event helper to receive game events.
     * 
     * @param eventHelper
     *            game event helper
     */
    void setEventHelper(final GameEventHelper eventHelper) {

        if (eventHelper == null) {
            throw new IllegalArgumentException("Argument eventHelper should not be null.");
        }

        removeEventHelper();

        eventHelper.addGameListener(gameAdapter);
        this.eventHelper = eventHelper;

        setupAchievementMeters();
    }

    /**
     * Gets event helper to receive game events.
     * 
     * @return game event helper
     */
    GameEventHelper getEventHelper() {

        return eventHelper;
    }

    /**
     * Removes the event helper from this object.
     */
    private void removeEventHelper() {

        if (eventHelper != null) {
            eventHelper.removeGameListener(gameAdapter);
            eventHelper = null;
        }
    }

    /**
     * Updates the information about what achievements have been accomplished.
     * This method should only be called by the measuring class (
     * <code>AchievementMeter</code>) when one achievement changed its status
     * from unaccomplished to accomplished!
     */
    void updateAchievements() {

        for (Achievement achievement : Achievement.values()) {
            // only change an achievement if it is not already accomplished
            if (!achievementMap.get(achievement)) {
                final boolean achievementAccomplished = achievementMeterMap.get(achievement).isAchievementAccomplished();
                achievementMap.put(achievement, achievementAccomplished);
            }
            logger.trace(achievement + " is " + (achievementMap.get(achievement) ? "" : " NOT ") + "accomplished");
        }
    }

    /*
     * Methods that can be called from outside this package (e.g. the user
     * interface) to check achievements.
     */

    /**
     * Checks whether a given achievement was already accomplished prior to the
     * call of this method. Regularly achievements can NOT be unaccomplished!
     * <p>
     * This method does NOT request AchievementMeter instances to check whether
     * some achievements have been accomplished! It shows the information after
     * the last update.
     * 
     * @param achievement
     *            achievement that should be checked
     * @return whether the given achievement was already accomplished
     */
    public boolean isAchievementAccomplished(final Achievement achievement) {

        return achievementMap.get(achievement);
    }

    /**
     * Checks whether all currently supported achievements were already
     * accomplished prior to the call of this method. Regularly achievements can
     * NOT be unaccomplished!
     * <p>
     * This method does NOT request AchievementMeter instances to check whether
     * some achievements have been accomplished! It shows the information after
     * the last update.
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
}
