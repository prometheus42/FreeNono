/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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
package org.freenono.model;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

/**
 * The GameTime class stores the current game time as two integer
 * for minutes and seconds. It is used to pass this time to other
 * components of the program. No further calculations are made in
 * this class except the formatted output as string. 
 * 
 * @author Christian Wichmann
 *
 */
public class GameTime {

	private static Logger logger = Logger.getLogger(GameTime.class);

	private int minutes = 0;
	private int seconds = 0;

	
	public GameTime() {

	}

	public GameTime(int minutes, int seconds) {
		
		this.minutes = minutes;
		this.seconds = seconds;
	}

	
	// public void addGameTime(GameTime value) {
	//
	// setMilliseconds(getMilliseconds() + value.getMilliseconds());
	// }
	//
	// public void subGameTime(GameTime value) {
	//
	// setMilliseconds(getMilliseconds() - value.getMilliseconds());
	// }
	//
	// public void addTime(int minutes, int seconds) {
	//
	// setMilliseconds(getMilliseconds() + ((minutes * 60 + seconds) * 1000));
	// }
	//
	// public void subTime(int minutes, int seconds) {
	//
	// setMilliseconds(getMilliseconds() - ((minutes * 60 + seconds) * 1000));
	// }

	// public int getMinutesSinceStart() {
	//
	// calculateTimeComponents();
	// return minutes;
	// }
	//
	// public int getSecondsSinceStart() {
	//
	// calculateTimeComponents();
	// return seconds;
	// }
	//
	// private void calculateTimeComponents() {
	//
	// long duration = milliseconds - gameEpochBase;
	//
	// int secs = (int) duration / 1000;
	//
	// if (secs < 60) {
	//
	// this.minutes = 0;
	// this.seconds = secs;
	// } else {
	//
	// int mins = secs / 60;
	//
	// this.minutes = mins;
	// this.seconds = secs - mins * 60;
	// }
	// }
	
	public boolean isZero() {
		
		return (minutes == 0 && seconds == 0);
	}
	
	public String toString() {
		
		DecimalFormat df = new DecimalFormat("00");
		return (df.format(minutes) + ":" + df.format(seconds));
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

}
