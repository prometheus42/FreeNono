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

import org.freenono.ui.Messages;

/**
 * Specifies achievements that can be won or accomplished while playing this
 * game.
 * <p>
 * Currently the following achievements are supported:
 * <ul>
 * <li>HIGH_SPEED_SOLVING
 * <li>VERY_HIGH_SPEED_SOLVING
 * <li>ULTRA_HIGH_SPEED_SOLVING
 * <li>ONE_WITHOUT_ERROR
 * <li>THREE_WITHOUT_ERROR
 * <li>FIVE_WITHOUT_ERROR
 * <li>COURSE_COMPLETED
 * <li>UNMARKED
 * </ul>
 * 
 * @author Christian Wichmann
 */
public enum Achievement {

    /**
     * Solved a nonogram with at least 30 occupied fields per minute. (See also
     * <code>SimpleStatistics</code>)
     */
    HIGH_SPEED_SOLVING,

    /**
     * Solved a nonogram with at least 60 occupied fields per minute. (See also
     * <code>SimpleStatistics</code>)
     */
    VERY_HIGH_SPEED_SOLVING,

    /**
     * Solved a nonogram with at least 90 occupied fields per minute. (See also
     * <code>SimpleStatistics</code>)
     */
    ULTRA_HIGH_SPEED_SOLVING,

    /**
     * Played one nonogram without a single falsely occupied field.
     */
    ONE_WITHOUT_ERROR,

    /**
     * Played three nonogram without a single falsely occupied field.
     */
    THREE_WITHOUT_ERROR,

    /**
     * Played five nonogram without a single falsely occupied field.
     */
    FIVE_WITHOUT_ERROR,

    /**
     * All nonograms of one course were played and won.
     */
    COURSE_COMPLETED,

    /**
     * Solved a nonogram without marking one field.
     */
    UNMARKED;

    /*
     * More ideas: complete course, complete course in one day, all nonograms in
     * all courses, one without marking, single fields without marking around
     * (and three or five single fields after another), at least 15x15 nonogram
     * in under five minutes (real) time, in under three minutes and in under
     * one minute,
     */

    private final String explanation;

    /**
     * Get explanation for achievement and store it in enum constant.
     */
    Achievement() {

        explanation = Messages.getString("AchievementDialog." + this.name());
    }

    @Override
    public String toString() {

        // TODO Upper case first letters of each word (use Apache Commons???).
        String achievementName = this.name().replace("_", " ");
        return achievementName + ": " + explanation;
    }
}
