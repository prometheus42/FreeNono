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
package org.freenono.controller;


/**
 * Stores all necessary information about a score. A score results from playing
 * a game and winning it. The score value is provided by GameMode class.
 * 
 * @author Christian Wichmann 
 */
public class Score {

	private String nonogram;
	private String gamemode;
	private String time;
	private String player;
	private Integer scoreValue;
	
	public Score() {
		
	}
	
	public Score(String nonogram, String gamemode, String time,
			String player, Integer scoreValue) {
		
		this.nonogram = nonogram;
		this.gamemode = gamemode;
		this.time = time;
		this.player = player;
		this.scoreValue = scoreValue;
	}

	public String getNonogram() {
		return nonogram;
	}
	public void setNonogram(String nonogram) {
		this.nonogram = nonogram;
	}
	public String getGamemode() {
		return gamemode;
	}
	public void setGamemode(String gamemode) {
		this.gamemode = gamemode;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public Integer getScoreValue() {
		return scoreValue;
	}
	public void setScoreValue(Integer scoreValue) {
		this.scoreValue = scoreValue;
	}
	
}
