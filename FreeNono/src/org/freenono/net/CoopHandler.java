/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2014 by FreeNono Development Team
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
package org.freenono.net;

import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.event.GameListener;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.QuizEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.data.Nonogram;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Handles coop games between multiple players via NonoWeb.
 * <p>
 * All running games are stored in a distributed map. Before starting a game it
 * has to be made public in this map. Then other player can connect to the game
 * by bridging their game event handler with this coop handler.
 * <p>
 * While the first player who in initiated the game runs the entire game logic
 * field events from other players instances are tunneled through and recognized
 * by the game logic.
 * <p>
 * One instance of CoopHandler can be used once to create and handle a coop
 * game. For the next game a new instance has to be generated!
 * <p>
 * The both sides of a coop game are called the initiating instance and the
 * joining instance.
 * 
 * @author Christian Wichmann
 */
public class CoopHandler {

    private static Logger logger = Logger.getLogger(CoopHandler.class);

    private NonoWebConnection connection = null;
    private GameEventHelper eventHelper = null;
    private MessageListener<String> messageListener;
    private GameListener gameEventHelper;
    private String coopGameId;

    /**
     * Instantiates a new handler for coop games via NonoWeb.
     * 
     * @param connection
     *            connection to be used by this coop handler
     */
    protected CoopHandler(final NonoWebConnection connection) {

        if (connection == null) {
            throw new IllegalArgumentException(
                    "Argument connection should not be null.");
        }

        this.connection = connection;
    }

    /*
     * Methods for initiating a coop game.
     */

    /**
     * Announces a new coop game with a given nonogram pattern. As return value
     * this method generates a identifier which can be used to hook into the
     * game by {@link #joinRunningCoopGame(String)}.
     * 
     * @param pattern
     *            nonogram pattern that should be played in coop mode
     * @return identifier of newly announced coop game
     */
    public final String announceCoopGame(final Nonogram pattern) {

        if (pattern == null) {
            throw new IllegalArgumentException(
                    "Argument pattern should not be null.");
        }

        String coopGameId = connection.announceCoopGame(
                connection.getOwnRealPlayerName(), pattern.getHash());

        connection.registerNonogramPattern(coopGameId, pattern);

        logger.debug("Announce new coop game with nonogram '"
                + pattern.getName() + "'.");

        return coopGameId;
    }

    /**
     * Initiate a already announced coop game. After other players have joined
     * an announced coop game the initiating instance has to call this method to
     * begin tunneling events between the game instances.
     * 
     * @param coopGameId
     *            game ID of the game to initiate
     * @param eventHelper
     *            game event helper of the joining instance
     * 
     */
    public final void initiateCoopGame(final String coopGameId,
            final GameEventHelper eventHelper) {

        if (coopGameId == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameId should not be null.");
        }
        if (eventHelper == null) {
            throw new IllegalArgumentException(
                    "Argument eventHelper should not be null.");
        }
        this.eventHelper = eventHelper;
        this.coopGameId = coopGameId;
    }

    /*
     * Methods for joining an announced coop game.
     */

    /**
     * Returns a list of all coop games currently available.
     * 
     * @return list of all coop games
     */
    public final List<CoopGame> listAllCoopGames() {

        /*
         * TODO Change this to use Observer to inform others of new announced
         * games. Use EntryListener on distributed map.
         */
        return connection.listAllCoopGames();
    }

    /**
     * Join a already announced and running coop game. The specific game is
     * given by the coop game ID.
     * 
     * @param coopGameId
     *            game ID of the game to be join to
     * @param eventHelper
     *            game event helper of the initiating instance
     * @return nonogram pattern from initiating instance
     */
    public final Nonogram joinRunningCoopGame(final String coopGameId,
            final GameEventHelper eventHelper) {

        if (coopGameId == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameId should not be null.");
        }
        if (eventHelper == null) {
            throw new IllegalArgumentException(
                    "Argument eventHelper should not be null.");
        }
        this.eventHelper = eventHelper;
        this.coopGameId = coopGameId;

        registerListener(coopGameId);

        return connection.getNonogramPattern(coopGameId);
    }

    /*
     * Common methods.
     */

    /**
     * Registers listeners for both the game event handler of the local instance
     * and the message handler to the NonoWeb cluster.
     * 
     * @param coopGameId
     *            game ID of the game to be join to
     */
    private void registerListener(final String coopGameId) {

        gameEventHelper = new GameListener() {

            @Override
            public void wrongFieldOccupied(final FieldControlEvent e) {
            }

            @Override
            public void timerElapsed(final StateChangeEvent e) {
            }

            @Override
            public void stateChanging(final StateChangeEvent e) {
            }

            @Override
            public void stateChanged(final StateChangeEvent e) {
            }

            @Override
            public void setTime(final StateChangeEvent e) {
            }

            @Override
            public void setFailCount(final StateChangeEvent e) {
            }

            @Override
            public void programControl(final ProgramControlEvent e) {
            }

            @Override
            public void optionsChanged(final ProgramControlEvent e) {
            }

            @Override
            public void occupyField(final FieldControlEvent e) {
            }

            @Override
            public void markField(final FieldControlEvent e) {
            }

            @Override
            public void fieldUnoccupied(final FieldControlEvent e) {
            }

            @Override
            public void fieldUnmarked(final FieldControlEvent e) {
            }

            @Override
            public void fieldOccupied(final FieldControlEvent e) {
            }

            @Override
            public void fieldMarked(final FieldControlEvent e) {
            }

            @Override
            public void crossOutCaption(final FieldControlEvent e) {
            }

            @Override
            public void changeActiveField(final FieldControlEvent e) {
            }

            @Override
            public void askQuestion(final QuizEvent e) {
                // FIXME Solve how to handle quiz game mode when playing coop
                // games.
            }
        };
        eventHelper.addGameListener(gameEventHelper);

        messageListener = new MessageListener<String>() {
            @Override
            public void onMessage(final Message<String> arg0) {
            }
        };
        connection.addCoopGameListener(coopGameId, messageListener);

    }

    /**
     * Closes this coop game.
     */
    public final void closeGame() {

        if (eventHelper != null) {
            eventHelper.removeGameListener(gameEventHelper);
        }
        if (messageListener != null) {
            connection.removeCoopGameListener(coopGameId, messageListener);
        }
    }
}
