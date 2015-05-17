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

import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.StateChangeEvent;
import org.freenono.provider.CollectionProvider;
import org.freenono.provider.CollectionTools;
import org.freenono.provider.CourseProvider;

/**
 * Provides an achievement based on how many courses have been completely solved.
 * <p>
 * This achievement meter needs access to the list with all collection provider. The included
 * courses are not going to be altered!
 *
 * @author Christian Wichmann
 */
public class AchievementMeterCompleteness extends AchievementMeter {

    private static Logger logger = Logger.getLogger(AchievementMeterCompleteness.class);

    private int condition = 1;
    private boolean achievementAlreadyAccomplished = false;
    private final List<CollectionProvider> nonogramProvider;

    private final GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                break;
            case SOLVED:
                // if game was won check if achievement has been accomplished
                isAchievementAccomplished();
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
    };

    /**
     * Instantiates a new achievement meter for measuring how many courses have been completely
     * solved.
     *
     * @param achievement
     *            type of achievement that this object is checking
     * @param nonogramProvider
     *            list of all nonogram collection provider
     * @param condition
     *            number of courses that have to be completely solved to accomplish this achievement
     */
    public AchievementMeterCompleteness(final Achievement achievement, final List<CollectionProvider> nonogramProvider, final int condition) {

        super(achievement);

        this.condition = condition;
        this.nonogramProvider = nonogramProvider;

        // hook own game adapter into game event system to get informed about
        // changes
        AchievementManager.getInstance().getEventHelper().addGameListener(gameAdapter);
    }

    @Override
    public final boolean isAchievementAccomplished() {

        int completedCourses = 0;
        for (final CollectionProvider collectionProvider : nonogramProvider) {
            for (final CourseProvider courseProvider : collectionProvider) {
                if (CollectionTools.checkIfCourseWasCompleted(courseProvider)) {
                    completedCourses++;
                }
            }
        }
        logger.trace("Number of completed courses: " + completedCourses);

        final boolean achievementCurrentlyAccomplished = completedCourses >= condition;
        if (achievementAlreadyAccomplished != achievementCurrentlyAccomplished) {
            achievementAlreadyAccomplished = achievementCurrentlyAccomplished;
            AchievementManager.getInstance().updateAchievements();
        }

        return achievementAlreadyAccomplished;
    }
}
