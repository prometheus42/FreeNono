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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

/**
 * Stores all data and objects relevant for the connection to the NonoWeb.
 * NonoWeb is implemented as a Hazelcast cluster and provides different
 * services.
 * 
 * @author Christian Wichmann
 * 
 */
class NonoWebConnection {

    private static Logger logger = Logger.getLogger(NonoWebConnection.class);

    // public static final String NONOGRAM_MAP_IDENTIFIER = "FreeNonoNonograms";
    public static final String PLAYER_NAME_MAP = "FreeNonoPlayer";
    public static final String CLUSTER_IP_SOURCE = "http://www.freenono.org/nonoweb/cluster";

    private HazelcastInstance hz;
    private String clusterNodeIP = null;
    private Map<String, ITopic<String>> listOfChatServices;
    private Map<MessageListener<String>, String> registrationIdForListener;
    private IMap<String, String> playerMap;

    /**
     * Instantiates a new connection to NonoWeb network services via Hazelcast
     * cluster.
     */
    public NonoWebConnection() {

        // connect to Hazelcast cluster
        Config cfg = new Config();
        hz = Hazelcast.newHazelcastInstance(cfg);

        // set up data structures for different network services
        listOfChatServices = new HashMap<>();
        registrationIdForListener = new HashMap<>();
        playerMap = hz.getMap(PLAYER_NAME_MAP);
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
            throw new IllegalArgumentException(
                    "Argument channel should not be null.");
        }

        ITopic<String> topic = hz.getTopic(channel);
        listOfChatServices.put(channel, topic);

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
    public void addChatListener(final String channel,
            final MessageListener<String> messageListener) {

        if (channel == null) {
            throw new IllegalArgumentException(
                    "Argument channel should not be null.");
        }
        if (messageListener == null) {
            throw new IllegalArgumentException(
                    "Argument messageListener should not be null.");
        }

        if (listOfChatServices.containsKey(channel)) {
            String id = listOfChatServices.get(channel).addMessageListener(
                    messageListener);
            registrationIdForListener.put(messageListener, id);
        }

        logger.debug("Added chat message listener for channel '" + channel
                + "'.");
    }

    /**
     * Removes a chat listener.
     * 
     * @param channel
     *            chat channel to which chat listener should be added
     * @param messageListener
     *            chat listener to be added
     */
    public void removeChatListener(final String channel,
            final MessageListener<String> messageListener) {

        if (channel == null) {
            throw new IllegalArgumentException(
                    "Argument channel should not be null.");
        }
        if (messageListener == null) {
            throw new IllegalArgumentException(
                    "Argument messageListener should not be null.");
        }

        if (listOfChatServices.containsKey(channel)) {
            String id = registrationIdForListener.get(messageListener);
            listOfChatServices.get(channel).removeMessageListener(id);
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
            throw new IllegalArgumentException(
                    "Argument channel should not be null.");
        }
        if (message == null) {
            throw new IllegalArgumentException(
                    "Argument message should not be null.");
        }

        if (listOfChatServices.containsKey(channel)) {
            listOfChatServices.get(channel).publish(message);
        }
    }

    /*
     * Methods concerning player names.
     */
    /**
     * Sets own real player name and links it to the member name given by
     * Hazelcast to each node. Every player can only set his own name with this
     * method!
     * 
     * @param playerName
     *            real player name to be set
     */
    public void setRealPlayerName(final String playerName) {

        String memberName = hz.getCluster().getLocalMember().toString();
        playerMap.put(memberName, playerName);
        logger.debug("Adding user '" + memberName + "' with player name '"
                + playerName + "'.");
    }

    /**
     * Returns the real player name from Hazelcast cluster by his/her member
     * name in the cluster.
     * 
     * @param memberName
     *            member name inside the Hazelcast cluster
     * @return real player name
     */
    public String getRealPlayerName(final String memberName) {

        logger.debug("Resolving user: " + memberName);
        if (playerMap.containsKey(memberName)) {
            return playerMap.get(memberName);
        } else {
            return "Anonymous";
        }
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

            StringBuilder nodeIP = new StringBuilder();

            try {
                URL url = new URL(CLUSTER_IP_SOURCE);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                // connection.setDoOutput(true);
                connection.setRequestMethod("GET");

                InputStreamReader reader = new InputStreamReader(
                        connection.getInputStream());

                // read from input stream to string
                Scanner s = new Scanner(reader);
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

            } catch (MalformedURLException e) {
                logger.error("URL for NonoWeb cluster node was not valid.");

            } catch (IOException e) {
                logger.error("Could not access NonoWeb cluster node.");
            }
        }
    }
}
