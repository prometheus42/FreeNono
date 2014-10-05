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

/**
 * Provides an interface for all achievement meter classes. The classes
 * represent a common description for measuring the accomplishment of an
 * achievement.
 * 
 * @author Christian Wichmann
 */
public abstract class AchievementMeter {

    private Achievement achievement;

    /**
     * Instantiates a new measuring object for checking if an achievement has
     * been accomplished.
     * 
     * @param achievement
     *            type of achievement that this object is checking
     */
    public AchievementMeter(final Achievement achievement) {

        this.achievement = achievement;
    }

    /**
     * Checks whether a given achievement is accomplished.
     * 
     * @return true, if given achievement is accomplished
     */
    public abstract boolean isAchievementAccomplished();

    /**
     * Gets the type of achievement of this achievement meter.
     * 
     * @return type of achievement of this achievement meter
     */
    public final Achievement getAchievement() {

        return achievement;
    }
}
