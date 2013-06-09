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
package org.freenono.interfaces;

import org.freenono.event.GameEvent;
import org.freenono.event.GameListener;

/**
 * @author Markus Wichmann
 *
 */
public interface GameEventHelper {

	// TODO: function list out of date!
	void addGameListener(GameListener l);
	void removeGameListener(GameListener l);
	void fireFieldOccupiedEvent(GameEvent e);
	void fireFieldMarkedEvent(GameEvent e);
	void fireActiveFieldChangedEvent(GameEvent e);
	void fireStateChangedEvent(GameEvent e);
	void fireTimerEvent(GameEvent e);
	void fireOptionsChangedEvent(GameEvent e);
	void fireWrongFieldOccupiedEvent(GameEvent e);
	void fireProgramControlEvent(GameEvent e);
	
}
