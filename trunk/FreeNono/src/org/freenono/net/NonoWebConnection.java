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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.freenono.event.GameEvent;
import org.freenono.model.data.Nonogram;
import org.freenono.net.CoopGame.CoopGameType;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

/**
 * Stores all data and objects relevant for the connection to the NonoWeb. NonoWeb is implemented as
 * a Hazelcast cluster and provides different services.
 *
 * @author Christian Wichmann
 *
 */
class NonoWebConnection {

    private static Logger logger = Logger.getLogger(NonoWebConnection.class);

    public static final String NONOGRAM_COLLECTIONS_MAP = "FreeNonoNonograms";
    public static final String PLAYER_NAME_MAP = "FreeNonoPlayer";
    public static final String COOP_GAMES_MAP = "FreeNonoCoopGames";
    public static final String NONOGRAM_PATTERN_MAP = "FreeNonoCoopGameNonograms";
    public static final String CLUSTER_IP_SOURCE = "http://www.freenono.org/nonoweb/cluster";

    private final HazelcastInstance hz;
    private String clusterNodeIP = null;

    /**
     * Contains all Hazelcast Topic for chat channel IDs. It is used to later add message listeners
     * to these Topic.
     */
    private final Map<String, ITopic<String>> listOfChatChannels;

    /**
     * Maps listeners to their registration IDs that are necessary to remove the listeners later.
     */
    private final Map<MessageListener<String>, String> registrationIdForChatListener;

    /**
     * Maps listeners to their registration IDs that are necessary to remove the listeners later.
     */
    private final Map<MessageListener<GameEvent>, String> registrationIdForCoopGameListener;

    /**
     * Contains for every member name in the cluster a corresponding player name that was chosen by
     * the player himself when starting FreeNono.
     * <p>
     * Example: 'Member [192.168.10.1]:5701' -> 'Christian'
     */
    private final IMap<String, String> playerMap;

    /**
     * Contains an entry with the game name as key for every new coop game that a user wants to
     * start. The name consists of the initiating players name and the hash of the chosen nonogram
     * pattern. Value of this entry is a list of strings defining
     * <p>
     * The topic name within the Hazelcast cluster that is used to transfer event objects from one
     * instance to another is named like the given key above.
     */
    private final IMap<String, List<String>> coopMap;

    /**
     * Contains for every coop game ID the corresonding nonogram pattern, because may be not all
     * player have the same nonogram collections.
     */
    private final IMap<String, Nonogram> nonogramPatternMap;

    /**
     * Instantiates a new connection to NonoWeb network services via Hazelcast cluster.
     */
    public NonoWebConnection() {

        // connect to Hazelcast cluster
        final Config cfg = new Config();
        hz = Hazelcast.newHazelcastInstance(cfg);

        // set up data structures for different network services
        listOfChatChannels = new HashMap<>();
        registrationIdForChatListener = new HashMap<>();
        registrationIdForCoopGameListener = new HashMap<>();
        playerMap = hz.getMap(PLAYER_NAME_MAP);
        coopMap = hz.getMap(COOP_GAMES_MAP);
        nonogramPatternMap = hz.getMap(NONOGRAM_PATTERN_MAP);
    }

    /*
     * Methods concerning the chat system.
     */

    /**
     * Adds a chat channel with a given name via Hazelcast cluster.
     *
     * @param channel
     *            chat channel to be added to cluster
     */
    public void addChatChannel(final String channel) {

        if (channel == null) {
            throw new IllegalArgumentException("Argument channel should not be null.");
        }

        final ITopic<String> topic = hz.getTopic(channel);
        listOfChatChannels.put(channel, topic);

        logger.debug("Added chat channel '" + channel + "'.");
    }

    /**
     * Adds a new chat listener.
     *
     * @param channel
     *            chat channel to which chat listener should be added
     * @param messageListener
     *            chat listener to be added
     */
    public void addChatListener(final String channel, final MessageListener<String> messageListener) {

        if (channel == null) {
            throw new IllegalArgumentException("Argument channel should not be null.");
        }
        if (messageListener == null) {
            throw new IllegalArgumentException("Argument messageListener should not be null.");
        }

        if (listOfChatChannels.containsKey(channel)) {
            final String id = listOfChatChannels.get(channel).addMessageListener(messageListener);
            registrationIdForChatListener.put(messageListener, id);
        }

        logger.debug("Added chat message listener for channel '" + channel + "'.");
    }

    /**
     * Removes a chat listener.
     *
     * @param channel
     *            chat channel from which chat listener should be removed
     * @param messageListener
     *            chat listener to be removed
     */
    public void removeChatListener(final String channel, final MessageListener<String> messageListener) {

        if (channel == null) {
            throw new IllegalArgumentException("Argument channel should not be null.");
        }
        if (messageListener == null) {
            throw new IllegalArgumentException("Argument messageListener should not be null.");
        }

        if (listOfChatChannels.containsKey(channel)) {
            final String id = registrationIdForChatListener.get(messageListener);
            listOfChatChannels.get(channel).removeMessageListener(id);
        }
    }

    /**
     * Sends a message on a given chat channel via NonoWeb.
     *
     * @param channel
     *            chat channel to which message should be send
     * @param message
     *            message to be sent
     */
    public void sendChatMessage(final String channel, final String message) {

        if (channel == null) {
            throw new IllegalArgumentException("Argument channel should not be null.");
        }
        if (message == null) {
            throw new IllegalArgumentException("Argument message should not be null.");
        }

        if (listOfChatChannels.containsKey(channel)) {
            listOfChatChannels.get(channel).publish(message);
        }
    }

    /*
     * Methods concerning player names.
     */
    /**
     * Sets own real player name and links it to the member name given by Hazelcast to each node.
     * Every player can only set his own name with this method!
     *
     * @param playerName
     *            real player name to be set
     */
    public void setRealPlayerName(final String playerName) {

        /*
         * Use local member name as key to manage player names. Strip string "this" at the end of
         * the member name because it appears only on the own machine and not on others.
         */
        String memberName = hz.getCluster().getLocalMember().toString();
        memberName = memberName.replaceAll(" this", "");
        playerMap.put(memberName, playerName);
        logger.debug("Adding user '" + memberName + "' with player name '" + playerName + "'.");
    }

    /**
     * Returns the real player name from Hazelcast cluster by his/her member name in the cluster.
     *
     * @param memberName
     *            member name inside the Hazelcast cluster
     * @return real player name
     */
    public String getRealPlayerName(final String memberName) {

        String realName = "";
        final String memberNameStripped = memberName.replaceAll(" this", "");

        if (playerMap.containsKey(memberNameStripped)) {
            realName = playerMap.get(memberNameStripped);
        } else {
            realName = "Anonymous";
        }
        // logger.debug("Resolving user: '" + memberNameStripped + "' to "
        // + realName + "'.");

        return realName;
    }

    /**
     * Returns own real player name from Hazelcast cluster.
     *
     * @return own real player name
     */
    public String getOwnRealPlayerName() {

        final String memberName = hz.getCluster().getLocalMember().toString();
        return getRealPlayerName(memberName);
    }

    /**
     * Gets all player names currently connected to NonoWeb. Only the self given player names are
     * returned.
     *
     * @return list of all player names
     */
    public List<String> getAllPlayerNames() {

        return Collections.unmodifiableList((List<String>) playerMap.values());
    }

    /*
     * Methods concerning coop games.
     */

    /**
     * Announces a new coop game with a given nonogram pattern. As return value this method
     * generates a identifier which can be used to add listeners for this game or send game events
     * to other player of the same game.
     *
     * @param playerName
     *            name of the player who initiated the new coop game
     * @param nonogramHash
     *            hash of the nonogram to be played
     * @return identifier of newly announced coop game
     */
    public String announceCoopGame(final String playerName, final String nonogramHash) {

        final String coopGameId = playerName + "@" + nonogramHash;
        coopMap.put(coopGameId, new ArrayList<String>());

        return coopGameId;
    }

    /**
     * Registers and stores the nonogram pattern for a given coop game. Other instances can use this
     * to join the game without having the original nonogram pattern in their collections.
     *
     * @param coopGameId
     *            game ID for which to store nonogram pattern
     * @param pattern
     *            nonogram pattern to be stored
     */
    public void registerNonogramPattern(final String coopGameId, final Nonogram pattern) {

        if (pattern == null) {
            throw new IllegalArgumentException("Argument pattern should not be null.");
        }

        nonogramPatternMap.put(coopGameId, pattern);
    }

    /**
     * Returns the nonogram pattern for a given coop game ID when it has already been stored by the
     * initiating instance of the game.
     *
     * @param coopGameId
     *            game ID for which to get nonogram pattern
     * @return nonogram pattern for given coop game, or <code>null</code> if no pattern has been
     *         stored for this game
     */
    public Nonogram getNonogramPattern(final String coopGameId) {

        return nonogramPatternMap.get(coopGameId);
    }

    /**
     * Returns a list of the player names of all coop games currently available.
     *
     * @return list of the player names of all coop games
     */
    public List<CoopGame> listAllCoopGames() {

        final List<CoopGame> listOfGames = new ArrayList<>();

        for (final String string : coopMap.keySet()) {
            final Nonogram nonogram = getNonogramPattern(string);
            if (nonogram != null) {
                final CoopGame game = new CoopGame(CoopGameType.JOINING, string, nonogram);
                listOfGames.add(game);
            }
        }

        return listOfGames;
    }

    /**
     * Adds a new coop game listener.
     *
     * @param coopGameId
     *            coop game ID
     * @param messageListener
     *            message listener to be added
     */
    public void addCoopGameListener(final String coopGameId, final MessageListener<GameEvent> messageListener) {

        if (coopGameId == null) {
            throw new IllegalArgumentException("Argument coopGameId should not be null.");
        }
        if (messageListener == null) {
            throw new IllegalArgumentException("Argument messageListener should not be null.");
        }

        // TODO Save all game IDs ever used and keep track of the Topic!

        final ITopic<GameEvent> game = hz.getTopic(coopGameId);
        final String id = game.addMessageListener(messageListener);
        registrationIdForCoopGameListener.put(messageListener, id);

        logger.debug("Added coop message listener for game '" + coopGameId + "'.");
    }

    /**
     * Removes a coop game listener.
     *
     * @param coopGameId
     *            coop game ID from which to remove message listener
     * @param messageListener
     *            message listener to be removed
     */
    public void removeCoopGameListener(final String coopGameId, final MessageListener<GameEvent> messageListener) {

        if (coopGameId == null) {
            throw new IllegalArgumentException("Argument coopGameId should not be null.");
        }
        if (messageListener == null) {
            throw new IllegalArgumentException("Argument messageListener should not be null.");
        }

        final ITopic<GameEvent> game = hz.getTopic(coopGameId);
        final String id = registrationIdForCoopGameListener.get(messageListener);
        game.removeMessageListener(id);
    }

    /**
     * Sends a game event for a given coop game ID via NonoWeb.
     *
     * @param coopGameId
     *            coop game ID to which the event should be send
     * @param gameEvent
     *            game event to be sent
     */
    public void sendCoopGameEvent(final String coopGameId, final GameEvent gameEvent) {

        if (coopGameId == null) {
            throw new IllegalArgumentException("Argument coopGameId should not be null.");
        }
        if (gameEvent == null) {
            throw new IllegalArgumentException("Argument gameEvent should not be null.");
        }

        final ITopic<GameEvent> game = hz.getTopic(coopGameId);
        game.publish(gameEvent);
    }

    /*
     * Miscellaneous methods.
     */

    /**
     * Gets one IP of a cluster node to connect NonoWeb to.
     */
    @SuppressWarnings("unused")
    private void getClusterNodeIP() {

        if (clusterNodeIP != null) {

            final StringBuilder nodeIP = new StringBuilder();

            try {
                final URL url = new URL(CLUSTER_IP_SOURCE);

                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // connection.setDoOutput(true);
                connection.setRequestMethod("GET");

                final InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                // read from input stream to string
                final Scanner s = new Scanner(reader);
                s.useDelimiter("\\A");
                nodeIP.append(s.next());
                s.close();
                reader.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    clusterNodeIP = nodeIP.toString();
                    System.out.println("got cluster ip: " + clusterNodeIP);

                } else {
                    logger.error("Server returned HTTP error code.");
                }

            } catch (final MalformedURLException e) {
                logger.error("URL for NonoWeb cluster node was not valid.");

            } catch (final IOException e) {
                logger.error("Could not access NonoWeb cluster node.");
            }
        }
    }
}
