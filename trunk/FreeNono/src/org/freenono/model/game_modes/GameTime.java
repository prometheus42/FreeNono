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
public class GameTime {

    // private static Logger logger = Logger.getLogger(GameTime.class);

    private int minutes = 0;
    private int seconds = 0;
    private int hours = 0;

    public GameTime() {

    }

    public GameTime(int minutes, int seconds) {

        this.minutes = minutes;
        this.seconds = seconds;
    }

    public boolean isZero() {

        return (minutes == 0 && seconds == 0);
    }

    public String toString() {

        DecimalFormat df = new DecimalFormat("##00");
        return (df.format(minutes + hours * 60) + ":" + df.format(seconds));
    }

    public int getMinutes() {

        return minutes;
    }

    public void setMinutes(int minutes) {

        this.minutes = minutes;
    }

    public int getSeconds() {

        return seconds;
    }

    public void setSeconds(int seconds) {

        this.seconds = seconds;
    }

    public int getHours() {

        return hours;
    }

    public void setHours(int hours) {

        this.hours = hours;
    }

}
