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
package de.ichmann.markusw.java.apps.freenono.event;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.freenono.model.GameState;

public class GameEventHelper {

	private static Logger logger = Logger.getLogger(GameEventHelper.class);

	private EventListenerList listeners = new EventListenerList();

	public GameEventHelper() {
	}

	/**
	 * Adds an {@code GameListener} to the helper class.
	 * 
	 * @param l
	 *            the {@code GameListener} to be added
	 */
	public void addGameListener(GameListener l) {
		listeners.add(GameListener.class, l);
	}

	/**
	 * Removes an {@code GameListener} from the helper class.
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void removeGameListener(GameListener l) {
		listeners.remove(GameListener.class, l);
	}

	public synchronized void fireFieldOccupiedEvent(int x, int y) {
		for (GameListener l : listeners.getListeners(GameListener.class))
			l.FieldOccupied(x, y);
	}

	public synchronized void fireFieldMarkedEvent(int x, int y) {
		for (GameListener l : listeners.getListeners(GameListener.class))
			l.FieldMarked(x, y);
	}

	public synchronized void fireActiveFieldChangedEvent(int x, int y) {
		for (GameListener l : listeners.getListeners(GameListener.class))
			l.ActiveFieldChanged(x, y);
	}

	public synchronized void fireStateChangedEvent(GameState oldState,
			GameState newState) {
		for (GameListener l : listeners.getListeners(GameListener.class))
			l.StateChanged(oldState, newState);
		logger.debug("Game state changed from " + oldState + " to " + newState);
	}

	public synchronized void fireTimerEvent() {
		for (GameListener l : listeners.getListeners(GameListener.class))
			l.Timer();
	}

}
