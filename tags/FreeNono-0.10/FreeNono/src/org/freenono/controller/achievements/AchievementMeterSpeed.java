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

import org.apache.log4j.Logger;
import org.freenono.controller.SimpleStatistics;
import org.freenono.event.GameAdapter;
import org.freenono.event.StateChangeEvent;

/**
 * Provides an achievement based on the speed of the player. The one criteria
 * for this class of achievements is the occupy performance measured by the
 * Statistics facilities.
 * 
 * @author Christian Wichmann
 */
public class AchievementMeterSpeed extends AchievementMeter {

    private static Logger logger = Logger.getLogger(AchievementMeterSpeed.class);

    private int condition;
    private boolean achievementAlreadyAccomplished = false;

    private GameAdapter gameAdapter = new GameAdapter() {

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
     * Instantiates a new achievement meter for measuring the speed of a player.
     * 
     * @param achievement
     *            type of achievement that this object is checking
     * @param condition
     *            condition that has to be fulfilled to accomplish the
     *            achievement
     */
    public AchievementMeterSpeed(final Achievement achievement, final int condition) {

        super(achievement);

        this.condition = condition;

        // hook own game adapter into game event system to get informed about
        // changes
        AchievementManager.getInstance().getEventHelper().addGameListener(gameAdapter);
    }

    @Override
    public final boolean isAchievementAccomplished() {

        final Double performance = (Double) SimpleStatistics.getInstance().getValue("occupyPerformance");

        final boolean achievementCurrentlyAccomplished = performance > condition;
        logger.debug("Compare: " + performance + " and " + condition);
        if (achievementAlreadyAccomplished != achievementCurrentlyAccomplished) {
            achievementAlreadyAccomplished = achievementCurrentlyAccomplished;
            AchievementManager.getInstance().updateAchievements();
        }

        return achievementAlreadyAccomplished;
    }
}
