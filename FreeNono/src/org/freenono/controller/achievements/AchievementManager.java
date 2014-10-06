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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.freenono.controller.StatisticsDataStore;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.provider.CollectionProvider;

/**
 * Manages the achievements and signals the user interface when one was
 * accomplished. For each achievement that is defined a measuring object has to
 * be instantiated - a achievement meter. These classes can be implemented by
 * using the abstract class AchievementMeter as base.
 * <p>
 * Only the methods <code>isAchievementAccomplished()</code>,
 * <code>areAllAchievementAccomplished()</code> and
 * <code>resetAllAchievements()</code> should be called from outside this
 * object.
 * <p>
 * The achievement data is not stored in a separate file. Instead the loading
 * and saving of this data is managed by the StatisticalDataStore class which is
 * also an singleton. Because this AchievementManager is instantiated after the
 * statistics objects the achievement data has than already been loaded from
 * file.
 * <p>
 * Implemented as a Singleton. Only one instance can exist at a time. When a new
 * instance is requested and the event helper class that is given differs from
 * the old one, a new instance is created.
 * 
 * @author Christian Wichmann
 */
public final class AchievementManager {

    private static Logger logger = Logger.getLogger(AchievementManager.class);

    private static AchievementManager instance = null;
    private List<CollectionProvider> nonogramProvider;

    // TODO Check if Set would be better than Map!
    private final Map<Achievement, Boolean> achievementMap = new HashMap<>();
    private final Map<Achievement, Boolean> achievementMapOfLastTime = new HashMap<>();
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

        // initialize map of all achievements with false
        for (Achievement achievement : Achievement.values()) {
            achievementMap.put(achievement, false);
            achievementMapOfLastTime.put(achievement, false);
        }

        loadAchievementDataFromStore();
    }

    /**
     * Sets up all meters to measure achievement accomplishment.
     */
    private void setupAchievementMeters() {

        // set up map with all achievement meters
        for (Achievement achievement : Achievement.values()) {

            AchievementMeter newMeter = null;
            switch (achievement) {
            case HIGH_SPEED_SOLVING:
                newMeter = new AchievementMeterSpeed(achievement, 30);
                break;
            case COURSE_COMPLETED:
                newMeter = new AchievementMeterCompleteness(achievement, nonogramProvider, 1);
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
                newMeter = new AchievementMeterSpeed(achievement, 90);
                break;
            case VERY_HIGH_SPEED_SOLVING:
                newMeter = new AchievementMeterSpeed(achievement, 60);
                break;
            case UNMARKED:
                newMeter = new AchievementMeterUnmarked(achievement, 0);
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
     * Handles exit of program by saving achievement data to file.
     */
    private void handleExit() {

        StatisticsDataStore.getInstance().saveStatisticsToFile();
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
     * @param nonogramProvider
     *            list of all nonogram collection provider
     * @return instance of AchievementManager
     */
    public static AchievementManager getInstance(final GameEventHelper eventHelper, final List<CollectionProvider> nonogramProvider) {

        if (instance == null || instance.eventHelper != eventHelper) {
            logger.debug("Creating new instance of achievement manager.");
            instance = new AchievementManager();
            instance.nonogramProvider = Collections.unmodifiableList(nonogramProvider);
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
     * Loads achievements from statistical data store that has loaded them from
     * default statistics data file.
     */
    private void loadAchievementDataFromStore() {

        achievementMap.putAll(StatisticsDataStore.getInstance().getAchievementAccomplishment());
    }

    /**
     * Updates the information about what achievements have been accomplished.
     * This method should only be called by the measuring class (
     * <code>AchievementMeter</code>) when one achievement changed its status
     * from unaccomplished to accomplished!
     */
    void updateAchievements() {

        boolean changed = false;

        for (Achievement achievement : Achievement.values()) {
            // only change an achievement if it is not already accomplished
            if (achievementMap.containsKey(achievement) && !achievementMap.get(achievement)) {
                final boolean achievementAccomplished = achievementMeterMap.get(achievement).isAchievementAccomplished();
                achievementMap.put(achievement, achievementAccomplished);

                if (achievementAccomplished) {
                    changed = true;
                }
            }
        }

        if (changed) {
            StatisticsDataStore stats = StatisticsDataStore.getInstance();
            stats.setAchievementAccomplishment(achievementMap);
            stats.saveStatisticsToFile();
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

        return achievementMap.containsKey(achievement) && achievementMap.get(achievement);
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

    /**
     * Overwrites the setting for all given achievements with the value stored
     * in the parameter map.
     * <p>
     * <b>Warning:</b> All values that were stored before will be overwritten
     * and no copy is kept!
     * 
     * @param values
     *            map with all achievement values that should be overwritten
     */
    @SuppressWarnings("unused")
    private void overrideAchievementSettings(final Map<Achievement, Boolean> values) {

        achievementMap.putAll(values);
    }

    /**
     * Resets the setting of all achievements as they were not accomplished. The
     * old data will be overwritten!
     */
    public void resetAllAchievements() {

        for (Achievement achievement : Achievement.values()) {
            achievementMap.put(achievement, false);
        }
    }

    /**
     * Checks whether a new achievement has been accomplished since last call of
     * this method. Only ONE instance in the entire program should EVER call
     * this method to get valid data.
     * 
     * @return map with all changed achievements or an empty map when no changes
     *         occurred
     */
    public Map<Achievement, Boolean> checkForAccomplishedAchievements() {

        Map<Achievement, Boolean> changes = new HashMap<Achievement, Boolean>();

        // find all changes between the current and the stored achievements
        for (Achievement achievement : Achievement.values()) {
            // achievementMap.containsKey(achievement) &&
            // achievementMapOfLastTime.containsKey(achievement)
            logger.debug("vergleich: " + achievementMap.get(achievement) + achievementMapOfLastTime.get(achievement));
            if (achievementMap.get(achievement) != achievementMapOfLastTime.get(achievement)) {

                changes.put(achievement, achievementMap.get(achievement));
            }
        }

        // store all achievement data from current map into backup for next call
        // of this method
        achievementMapOfLastTime.putAll(achievementMap);

        return changes;
    }
}
