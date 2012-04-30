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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.GameEventHelper;

public class GameModeFactory {

	private static Logger logger = Logger.getLogger(GameModeFactory.class);

	public GameMode getGameMode(GameEventHelper eventHelper,
			Nonogram pattern, Settings settings) {

		GameMode gm = null;

		switch (settings.getGameMode()) {
		case PENALTY:
			gm = new GameMode_Penalty(eventHelper, pattern, settings);
			logger.info("GameMode_Penalty instantiated.");
			break;

		case MAX＿FAIL:
			gm = new GameMode_MaxFail(eventHelper, pattern, settings);
			logger.info("GameMode_MaxFail instantiated.");
			break;

		case MAX_TIME:
			gm = new GameMode_MaxTime(eventHelper, pattern, settings);
			logger.info("GameMode_MaxTime instantiated.");
			break;
			
		case COUNT_TIME:
			gm = new GameMode_CountTime(eventHelper, pattern, settings);
			logger.info("GameMode_CountTime instantiated.");
			break;
			
		case QUIZ:
			gm = new GameMode_Quiz(eventHelper, pattern, settings);
			logger.info("GameMode_Quiz instantiated.");
			break;

		default:
			logger.error("Chosen game mode not implemented yet!");
		}

		return gm;
	}

}
