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
     * Number of nanoseconds in one millisecond.
     */
    public static final int NANOSECONDS_PER_MILLISECOND = 1000000;

    /**
     * Number of milliseconds in one second.
     */
    public static final int MILLISECONDS_PER_SECOND = 1000;

    /**
     * Number of minutes in one hour.
     */
    public static final int MINUTES_PER_HOUR = 60;

    /**
     * Number of seconds in one minute.
     */
    public static final int SECONDS_PER_MINUTE = 60;

    /**
     * Initializes a game time with all fields set to zero.
     */
    public GameTime() {
    }

    /**
     * Initializes a game time with minutes and seconds.
     * 
     * @param minutes
     *            minutes to be set, only values 0 - 59 are valid
     * @param seconds
     *            seconds to be set, only values 0 - 59 are valid
     */
    public GameTime(final int minutes, final int seconds) {

        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException(
                    "Value of parameter minutes not valid.");
        }
        if (seconds < 0 || seconds > 59) {
            throw new IllegalArgumentException(
                    "Value of parameter second not valid.");
        }

        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Initializes a game time with seconds and converts to hours, minutes and
     * seconds.
     * 
     * @param seconds
     *            seconds to be set, only positive numbers are allowed
     */
    public GameTime(final int seconds) {

        if (seconds < 0) {
            throw new IllegalArgumentException(
                    "Parameter seconds should not be negative.");
        }
        convertSeconds(seconds);
    }

    /**
     * Initializes a game time with seconds and converts to hours, minutes and
     * seconds. For convenience this constructor takes a long argument instead
     * of integer.
     * 
     * @param seconds
     *            seconds to be set, only positive numbers are allowed
     */
    public GameTime(final long seconds) {

        this((int) seconds);
    }

    /**
     * Initializes a game time with hours, minutes and seconds.
     * 
     * @param hours
     *            hours to be set, only positive values are valid
     * @param minutes
     *            minutes to be set, only values 0 - 59 are valid
     * @param seconds
     *            seconds to be set, only values 0 - 59 are valid
     */
    public GameTime(final int hours, final int minutes, final int seconds) {

        if (hours < 0) {
            throw new IllegalArgumentException(
                    "Value of parameter hours not valid.");
        }
        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException(
                    "Value of parameter minutes not valid.");
        }
        if (seconds < 0 || seconds > 59) {
            throw new IllegalArgumentException(
                    "Value of parameter second not valid.");
        }

        this.hours = hours;
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
        return (df.format(minutes + hours * GameTime.MINUTES_PER_HOUR) + ":" + df
                .format(seconds));
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
     * @param newMinutes
     *            minutes of game time, only values 0 - 59 are valid
     * @return new game time with changed minutes
     */
    public GameTime changeMinutes(final int newMinutes) {

        if (newMinutes < 0 || newMinutes > 59) {
            throw new IllegalArgumentException(
                    "Value of parameter newMinutes not valid.");
        }

        return new GameTime(newMinutes, seconds);
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
     * @param newSeconds
     *            seconds of game time, only values 0 - 59 are valid
     * @return new game time with changed seconds
     */
    public GameTime changeSeconds(final int newSeconds) {

        if (newSeconds < 0 || newSeconds > 59) {
            throw new IllegalArgumentException(
                    "Value of parameter newSeconds not valid.");
        }
        return new GameTime(minutes, newSeconds);
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
     * @param newHours
     *            hours to be set, only positive values are valid
     * @return new game time with changed hours
     */
    public GameTime setHours(final int newHours) {

        if (newHours < 0) {
            throw new IllegalArgumentException(
                    "Value of parameter newHours not valid.");
        }
        return new GameTime(newHours, 0, 0);
    }

    /**
     * Converts from only seconds to hours, minutes and seconds.
     * 
     * @param givenSeconds
     *            seconds to be converted, only positive numbers are allowed
     */
    private void convertSeconds(final int givenSeconds) {

        assert givenSeconds >= 0;

        int secondCount = givenSeconds;
        int newHours = 0;
        int newMinutes = 0;
        int newSeconds = 0;

        if (secondCount >= GameTime.SECONDS_PER_MINUTE
                * GameTime.MINUTES_PER_HOUR) {

            newHours = secondCount
                    / (GameTime.SECONDS_PER_MINUTE * GameTime.MINUTES_PER_HOUR);

            secondCount = secondCount
                    - (newHours * GameTime.SECONDS_PER_MINUTE * GameTime.MINUTES_PER_HOUR);
        }

        if (secondCount >= GameTime.SECONDS_PER_MINUTE) {

            newMinutes = secondCount / GameTime.SECONDS_PER_MINUTE;
            newSeconds = secondCount - newMinutes * GameTime.SECONDS_PER_MINUTE;
        }

        assert newMinutes < GameTime.MINUTES_PER_HOUR;
        assert newSeconds < GameTime.SECONDS_PER_MINUTE;

        hours = newHours;
        minutes = newMinutes;
        seconds = newSeconds;
    }

    /**
     * Adds time and returns new <code>GameTime</code> object.
     * 
     * @param addedMinutes
     *            minutes to add
     * @param addedSeconds
     *            seconds to add
     * @return new game time with added values
     */
    public GameTime addTime(final int addedMinutes, final int addedSeconds) {

        return new GameTime(hours * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
                + (minutes + addedMinutes) * MINUTES_PER_HOUR + seconds
                + addedSeconds);
    }

    /**
     * Subtracts time and returns new <code>GameTime</code> object.
     * 
     * @param subtractedMinutes
     *            minutes to subtract
     * @param subtractedSeconds
     *            seconds to subtract
     * @return new game time with subtracted values
     */
    public GameTime subTime(final int subtractedMinutes,
            final int subtractedSeconds) {

        return new GameTime(hours * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
                + (minutes - subtractedMinutes) * MINUTES_PER_HOUR + seconds
                - subtractedSeconds);
    }
}
