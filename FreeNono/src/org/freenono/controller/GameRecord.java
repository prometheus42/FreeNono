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

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.freenono.event.GameEvent;

/**
 * Saves all moves in the game. A move can be the marking or occupation of a
 * field on the board or the reversal of that. Each move is recorded via its
 * game event which is send by the UI when playing. This event can later be used
 * to replay the recorded move.
 * 
 * @author Christian Wichmann
 */
public class GameRecord implements Iterable<GameEvent> {

    private static Logger logger = Logger.getLogger(GameRecord.class);

    private Queue<GameEvent> eventList;

    /**
     * Initializes a new GameRecord.
     */
    public GameRecord() {

        eventList = new LinkedBlockingQueue<>();
    }

    /**
     * Adds a game event to this GameRecord and saves it.
     * 
     * @param event
     *            game event to be saved
     */
    public final void addEventToGame(final GameEvent event) {

        eventList.add(event);
        logger.debug("Adding new event to recorder: " + event);
    }

    /**
     * Clears this game record and deletes all recorded events.
     */
    public final void clearRecord() {

        eventList.clear();
    }

    /**
     * Returns a copy of the internal stored event queue. All changes on this
     * queue have no effect on the data stored in this <code>GameRecord</code>.
     * 
     * @return a copy of the internal stored event queue
     */
    public final Queue<GameEvent> getEventQueue() {

        return new LinkedBlockingQueue<>(eventList);
    }

    @Override
    public final Iterator<GameEvent> iterator() {

        return eventList.iterator();
    }
}
