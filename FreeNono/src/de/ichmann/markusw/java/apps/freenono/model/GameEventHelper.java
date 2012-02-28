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
package de.ichmann.markusw.java.apps.freenono.model;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.freenono.event.GameListener;
import de.ichmann.markusw.java.apps.freenono.sound.AudioProvider;

class GameEventHelper {

	private static Logger logger = Logger.getLogger(GameEventHelper.class);

	private EventListenerList componentListeners = new EventListenerList();

	GameEventHelper(Game game) {
	}

	public GameListener[] getComponentListeners() {
		return componentListeners.getListeners(GameListener.class);
	}

	public void addGameListener(GameListener l) {
		componentListeners.add(GameListener.class, l);
	}

	public void removeGameListener(GameListener l) {
		componentListeners.remove(GameListener.class, l);
	}

	public void fireFieldOccupedEvent(int x, int y) {
		GameListener[] games = getComponentListeners();
		for (GameListener listener : games) {
			listener.FieldOccupied(x, y);
		}
	}

	public void fireFieldMarkedEvent(int x, int y) {
		GameListener[] games = getComponentListeners();
		for (GameListener listener : games) {
			listener.FieldMarked(x, y);
		}
	}
	
	public void fireStateChangedEvent(GameState oldState, GameState newState) {
		GameListener[] games = getComponentListeners();
		for (GameListener listener : games) {
			listener.StateChanged(oldState, newState);
		}
		logger.debug("Game state changed from "+oldState+" to "+newState);
	}

	public void fireTimerEvent() {
		GameListener[] games = getComponentListeners();
		for (GameListener listener : games) {
			listener.Timer();
		}
	}
}
