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
package org.freenono.controller;

import java.util.Date;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Game;
import org.freenono.model.Highscores;


public class HighscoreManager {

	private static Logger logger = Logger.getLogger(HighscoreManager.class);

	private GameEventHelper eventHelper;
	private Highscores highscores;
	
	private GameAdapter gameAdapter = new GameAdapter() {

		public void Timer(StateChangeEvent e) {

		}

		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				break;

			case solved:
				Game g = (Game) e.getSource();
				highscores.addScore(g.getGamePattern().getHash(), 
						g.getGameMode().toString(),
						(new Date()).toString(), System.getProperty("user.name"),
						g.getGameScore());
				highscores.printHighscores(g.getGameMode().toString());
				break;

			case paused:
				break;

			case running:
				break;

			default:
				break;
			}
		}
		
		public void ProgramControl(ProgramControlEvent e) {
			switch (e.getPct()) {
			case START_GAME:
				break;

			case STOP_GAME:
				break;

			case RESTART_GAME:
				break;

			case PAUSE_GAME:
				break;

			case RESUME_GAME:
				break;

			case NONOGRAM_CHOSEN:
				break;

			case QUIT_PROGRAMM:
				break;
			}
		}
	};


	public HighscoreManager(GameEventHelper eventHelper) {
	
		// connect to game event handler
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
		
		// load highscore for current player from file
		// TODO load highscore from file
		highscores = new Highscores();
		
	}
}
