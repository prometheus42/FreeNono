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

import java.text.DecimalFormat;

/**
 * The GameTime class stores the current game time as two integer for minutes
 * and seconds. It is used to pass this time to other components of the program.
 * No further calculations are made in this class except the formatted output as
 * string.
 * 
 * @author Christian Wichmann
 */
public final class GameTime {

    // private static Logger logger = Logger.getLogger(GameTime.class);

    private int minutes = 0;
    private int seconds = 0;
    private int hours = 0;

    /**
     * Initializes a game time with all fields set to zero.
     */
    public GameTime() {

    }

    /**
     * Initializes a game time with minutes and seconds.
     * 
     * @param minutes
     *            minutes to be set
     * @param seconds
     *            seconds to be set
     */
    public GameTime(final int minutes, final int seconds) {

        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Returns whether this game time equals zero.
     * 
     * @return true, if game time is zero
     */
    public boolean isZero() {

        return (minutes == 0 && seconds == 0 && hours == 0);
    }

    @Override
    public String toString() {

        DecimalFormat df = new DecimalFormat("##00");
        return (df.format(minutes + hours * GameTimeHelper.MINUTES_PER_HOUR)
                + ":" + df.format(seconds));
    }

    /**
     * Gets minutes of game time.
     * 
     * @return minutes of game time
     */
    public int getMinutes() {

        return minutes;
    }

    /**
     * Sets minutes of game time.
     * 
     * @param minutes
     *            minutes of game time
     */
    public void setMinutes(final int minutes) {

        this.minutes = minutes;
    }

    /**
     * Gets seconds of game time.
     * 
     * @return seconds of game time
     */
    public int getSeconds() {

        return seconds;
    }

    /**
     * Sets seconds of game time.
     * 
     * @param seconds
     *            seconds of game time
     */
    public void setSeconds(final int seconds) {

        this.seconds = seconds;
    }

    /**
     * Gets hours of game time.
     * 
     * @return hours of game time
     */
    public int getHours() {

        return hours;
    }

    /**
     * Sets hours for game time.
     * 
     * @param hours
     *            hours to be set
     */
    public void setHours(final int hours) {

        this.hours = hours;
    }

}
