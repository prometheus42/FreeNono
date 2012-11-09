/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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

import java.util.ArrayList;
import java.util.List;

public class Highscores {

	private List<Score> highscores;

	public Highscores() {

		highscores = new ArrayList<Score>();
	}

	/**
	 * Commits a newly played score and checks if it has to be entered into the
	 * highscore for the chosen gamemode.
	 * 
	 * @param nonogram
	 * @param gamemode
	 * @param time
	 * @param player
	 * @param scoreValue
	 */
	public void addScore(String nonogram, String gamemode, String time,
			String player, Integer scoreValue) {

		highscores.add(new Score(nonogram, gamemode, time, player,
				scoreValue));
	}
	
	public void printHighscores(String gameMode) {
		
		System.out.println("*** GameMode Highscore **************************");
		System.out.println("* GameMode: "+gameMode);
		for (int i = 0; i < 36-gameMode.length(); i++) {
			System.out.println(" ");
		}
		System.out.println("*");
		System.out.println("*                                               *");
		for (Score score : highscores) {
			System.out.println("* " + score.getPlayer() + "  " + score.getTime()
					+ "  " + score.getScoreValue() + " *");
		}
		System.out.println("*                                               *");
		System.out.println("*************************************************");
	}

}
