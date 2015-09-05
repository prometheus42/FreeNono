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
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.event.GameListener;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.QuizEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.data.Nonogram;
import org.freenono.net.CoopGame.CoopGameType;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Handles coop games between multiple players via NonoWeb.
 * <p>
 * All running games are stored in a distributed map. Before starting a game it has to be made
 * public in this map. Then other player can connect to the game by bridging their game event
 * handler with this coop handler.
 * <p>
 * While the first player who in initiated the game runs the entire game logic field events from
 * other players instances are tunneled through and recognized by the game logic.
 * <p>
 * One instance of CoopHandler can be used once to create and handle a coop game. For the next game
 * a new instance has to be generated!
 * <p>
 * The both sides of a coop game are called the initiating instance and the joining instance.
 *
 * @author Christian Wichmann
 */
public class CoopHandler {

    private static Logger logger = Logger.getLogger(CoopHandler.class);

    private NonoWebConnection connection = null;
    private GameEventHelper eventHelper = null;
    private MessageListener<GameEvent> messageListener;
    private GameListener bridgingEventHelper;
    private CoopGame coopGame;

    /**
     * Handles messages sent from other NonoWeb instances via Hazelcast and responds accordingly.
     *
     * @author Christian Wichmann
     */
    private final class GameEventMessageListener implements MessageListener<GameEvent> {
        @Override
        public void onMessage(final Message<GameEvent> gameEvent) {

            final String realPlayerName = connection.getRealPlayerName(gameEvent.getPublishingMember().toString());
            final boolean isNotOwnEvent = realPlayerName.equals(connection.getOwnRealPlayerName());

            if (gameEvent.getMessageObject() instanceof FieldControlEvent) {
                handleFieldControlEvent(gameEvent, isNotOwnEvent);
            }

            if (gameEvent.getMessageObject() instanceof StateChangeEvent) {
                handleStateChangeEvent(gameEvent, isNotOwnEvent);
            }

            if (gameEvent.getMessageObject() instanceof ProgramControlEvent) {
                handleProgramControlEvent(gameEvent);
            }
        }

        /**
         * Handles a field control event send via NonoWeb.
         *
         * @param gameEvent
         *            game event to be handled
         * @param isNotOwnEvent
         *            whether this is actually a event sent by somebody else
         */
        private void handleFieldControlEvent(final Message<GameEvent> gameEvent, final boolean isNotOwnEvent) {
            final FieldControlEvent event = (FieldControlEvent) gameEvent.getMessageObject();
            if (isNotOwnEvent) {
                switch (event.getFieldControlType()) {
                case CROSS_OUT_CAPTION:
                    bridgingEventHelper.crossOutCaption(event);
                    break;
                case FIELD_MARKED:
                    bridgingEventHelper.fieldMarked(event);
                    break;
                case FIELD_OCCUPIED:
                    bridgingEventHelper.fieldOccupied(event);
                    break;
                case FIELD_UNMARKED:
                    bridgingEventHelper.fieldUnmarked(event);
                    break;
                case FIELD_UNOCCUPIED:
                    bridgingEventHelper.fieldUnoccupied(event);
                    break;
                case MARK_FIELD:
                    bridgingEventHelper.markField(event);
                    break;
                case OCCUPY_FIELD:
                    logger.debug(connection.getOwnRealPlayerName() + ": Getting remote event!");
                    bridgingEventHelper.occupyField(event);
                    break;
                case WRONG_FIELD_OCCUPIED:
                    bridgingEventHelper.wrongFieldOccupied(event);
                    break;
                case ACTIVE_FIELD_CHANGED:
                    assert false : "Active field changes should not be sent over NonoWeb.";
                    break;
                case NONE:
                    assert false : "Not a valid field control event type.";
                    break;
                default:
                    assert false : "Not a valid field control event type.";
                    break;
                }
            }
        }
    }

    /**
     * Handles a state change event send via NonoWeb.
     *
     * @param gameEvent
     *            game event to be handled
     * @param isNotOwnEvent
     *            whether this is actually a event sent by somebody else
     */
    private void handleStateChangeEvent(final Message<GameEvent> gameEvent, final boolean isNotOwnEvent) {
        final StateChangeEvent event = (StateChangeEvent) gameEvent.getMessageObject();
        switch (event.getStateChangeType()) {
        case SET_FAIL_COUNT:
            if (isNotOwnEvent) {
                bridgingEventHelper.setFailCount(event);
            }
            break;
        case SET_TIME:
            if (isNotOwnEvent) {
                bridgingEventHelper.setTime(event);
            }
            break;
        case STATE_CHANGED:
            if (isNotOwnEvent) {
                bridgingEventHelper.stateChanged(event);
            }
            break;
        case STATE_CHANGING:
            if (isNotOwnEvent) {
                bridgingEventHelper.stateChanging(event);
            }
            break;
        case TIMER:
            if (isNotOwnEvent) {
                bridgingEventHelper.timerElapsed(event);
            }
            break;
        default:
            assert false : "Not a valid state change event type.";
            break;
        }
    }

    /**
     * Handles a program control event send via NonoWeb.
     *
     * @param gameEvent
     *            game event to be handled
     */
    private void handleProgramControlEvent(final Message<GameEvent> gameEvent) {
        final ProgramControlEvent event = (ProgramControlEvent) gameEvent.getMessageObject();
        switch (event.getPct()) {
        case NONOGRAM_CHOSEN:
            break;
        case OPTIONS_CHANGED:
            break;
        case PAUSE_GAME:
            break;
        case QUIT_PROGRAMM:
            break;
        case RESTART_GAME:
            break;
        case RESUME_GAME:
            break;
        case SHOW_ABOUT:
            break;
        case SHOW_OPTIONS:
            break;
        case START_GAME:
            break;
        case STOP_GAME:
            break;
        default:
            assert false : "Not a valid program control event type.";
            break;
        }
    }

    /**
     * Instantiates a new handler for coop games via NonoWeb.
     *
     * @param connection
     *            connection to be used by this coop handler
     */
    protected CoopHandler(final NonoWebConnection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Argument connection should not be null.");
        }

        this.connection = connection;
    }

    /*
     * Methods for initiating a coop game.
     */

    /**
     * Announces a new coop game with a given nonogram pattern. As return value this method
     * generates a identifier which can be used to hook into the game by
     * {@link #joinRunningCoopGame(String)}.
     *
     * @param pattern
     *            nonogram pattern that should be played in coop mode
     * @return identifier of newly announced coop game
     */
    public final CoopGame announceCoopGame(final Nonogram pattern) {

        if (pattern == null) {
            throw new IllegalArgumentException("Argument pattern should not be null.");
        }

        final String coopGameId = connection.announceCoopGame(connection.getOwnRealPlayerName(), pattern.getHash());

        connection.registerNonogramPattern(coopGameId, pattern);

        final CoopGame announcedCoopGame = new CoopGame(CoopGameType.INITIATING, coopGameId, pattern);

        logger.debug("Announce new coop game with nonogram '" + pattern.getName() + "'.");

        return announcedCoopGame;
    }

    /**
     * Initiate a already announced coop game. After other players have joined an announced coop
     * game the initiating instance has to call this method to begin tunneling events between the
     * game instances.
     *
     * @param coopGame
     *            coop game to initiate
     * @param eventHelper
     *            game event helper of the joining instance
     *
     */
    public final void initiateCoopGame(final CoopGame coopGame, final GameEventHelper eventHelper) {

        if (coopGame == null) {
            throw new IllegalArgumentException("Argument coopGame should not be null.");
        }
        if (eventHelper == null) {
            throw new IllegalArgumentException("Argument eventHelper should not be null.");
        }
        this.eventHelper = eventHelper;
        this.coopGame = coopGame;

        registerGameListener();
        registerRemoteListener();
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
         * TODO Change this to use Observer to inform others of new announced games. Use
         * EntryListener on distributed map.
         */
        return connection.listAllCoopGames();
    }

    /**
     * Join a already announced and running coop game. The specific game is given by the coop game
     * ID.
     *
     * @param coopGame
     *            game ID of the game to be join to
     * @param eventHelper
     *            game event helper of the initiating instance
     * @return nonogram pattern from initiating instance
     */
    public final CoopGame joinRunningCoopGame(final CoopGame coopGame, final GameEventHelper eventHelper) {

        if (coopGame == null) {
            throw new IllegalArgumentException("Argument coopGame should not be null.");
        }
        if (eventHelper == null) {
            throw new IllegalArgumentException("Argument eventHelper should not be null.");
        }
        this.eventHelper = eventHelper;
        this.coopGame = coopGame;

        registerGameListener();
        registerRemoteListener();

        return coopGame;
    }

    /*
     * Common methods.
     */

    /**
     * Registers listener for the game event handler of the local instance.
     */
    private void registerGameListener() {

        bridgingEventHelper = new GameListener() {

            @Override
            public void wrongFieldOccupied(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void timerElapsed(final StateChangeEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void stateChanging(final StateChangeEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void stateChanged(final StateChangeEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void setTime(final StateChangeEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void setFailCount(final StateChangeEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void programControl(final ProgramControlEvent e) {
                // FIXME Handle pause, resume and stop.
                if (e.getPct() == ProgramControlType.QUIT_PROGRAMM) {
                    closeGame();
                }
            }

            @Override
            public void optionsChanged(final ProgramControlEvent e) {
                /*
                 * Settings that are changed while running a coop game will NOT effect the running
                 * game. When one of the player of a coop game changes her/his settings the other
                 * players settings will NOT be changed!
                 */
            }

            @Override
            public void occupyField(final FieldControlEvent e) {

                logger.debug(connection.getOwnRealPlayerName() + ": Getting local event!");
                if (coopGame.getCoopGameType() == CoopGameType.JOINING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void markField(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.JOINING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void fieldUnoccupied(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void fieldUnmarked(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void fieldOccupied(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void fieldMarked(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void crossOutCaption(final FieldControlEvent e) {

                if (coopGame.getCoopGameType() == CoopGameType.INITIATING) {
                    connection.sendCoopGameEvent(coopGame.getCoopGameId(), e);
                }
            }

            @Override
            public void changeActiveField(final FieldControlEvent e) {
                /*
                 * All changes of the active field will be limited to the local instance. This event
                 * will never be transmitted over NonoWeb.
                 */
            }

            @Override
            public void askQuestion(final QuizEvent e) {
                // TODO Solve how to handle quiz game mode when playing coop
                // games.
            }
        };
        eventHelper.addGameListener(bridgingEventHelper);
    }

    /**
     * Registers listener for the message handler to the NonoWeb cluster.
     */
    private void registerRemoteListener() {

        messageListener = new GameEventMessageListener();
        connection.addCoopGameListener(coopGame.getCoopGameId(), messageListener);
    }

    /**
     * Closes this coop game.
     */
    public final void closeGame() {

        if (eventHelper != null) {
            eventHelper.removeGameListener(bridgingEventHelper);
        }
        if (messageListener != null && coopGame != null) {
            connection.removeCoopGameListener(coopGame.getCoopGameId(), messageListener);
        }
    }
}
