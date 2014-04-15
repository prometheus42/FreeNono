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

import com.hazelcast.core.MessageListener;

/**
 * Handles a chat service via NonoWeb. By default the main chat channel of
 * FreeNono is used. Otherwise a chat identifier has to be given when
 * instantiating this handler.
 * <p>
 * 
 * 
 * @author Christian Wichmann
 */
public class ChatHandler {

    public static final String FREENONO_CHAT_IDENTIFIER = "FreeNonoChat";

    private NonoWebConnection connection;
    private String chatChannel;
    private MessageListener<String> currentMessageListener;

    /**
     * Instantiates a new handler for chat service via NonoWeb.
     * 
     * @param connection
     *            connection to be used by this chat handler.
     */
    protected ChatHandler(final NonoWebConnection connection) {

        this(connection, FREENONO_CHAT_IDENTIFIER);
    }

    /**
     * Instantiates a new handler for chat service via NonoWeb.
     * 
     * @param connection
     *            connection to be used by this chat handler
     * @param chatChannelId
     *            identifier for chat channel to be handled
     */
    protected ChatHandler(final NonoWebConnection connection,
            final String chatChannelId) {

        if (connection == null) {
            throw new IllegalArgumentException(
                    "Argument connection should not be null.");
        }
        if (chatChannelId == null) {
            throw new IllegalArgumentException(
                    "Argument chatChannelId should not be null.");
        }

        this.connection = connection;
        this.chatChannel = chatChannelId;

        connection.addChatChannel(chatChannelId);
    }

    /**
     * Sends a message through NonoWeb.
     * 
     * @param message
     *            message to be send via NonoWeb
     */
    public final void sendMessage(final String message) {

        if (message != null) {
            connection.sendChatMessage(chatChannel, message);
        }
    }

    /**
     * Adds a listener to receive chat messages from NonoWeb.
     * 
     * @param messageListener
     *            listener to be added
     */
    public final void receiveMessageBy(
            final MessageListener<String> messageListener) {

        if (messageListener != null) {
            currentMessageListener = messageListener;
            connection.addChatListener(chatChannel, currentMessageListener);
        }
    }

    /**
     * Closes this chat. After calling this method no more messages are received
     * by NonoWeb.
     */
    public final void closeChat() {

        assert connection != null : "Connection has to be set when initializing ChatHandler, but is Null!";

        connection.removeChatListener(chatChannel, currentMessageListener);
        currentMessageListener = null;
    }

    /**
     * Sets chat name for current player. This name is used whenever messages
     * are sent by this handler.
     * 
     * @param chatName
     *            chat name to be set, should not be Null
     */
    public final void setOwnChatName(final String chatName) {

        if (chatName != null) {
            connection.setRealPlayerName(chatName);
        }
    }

    /**
     * Resolves a given member name to the real player name.
     * 
     * @param memberName
     *            member name
     * @return real player name
     */
    public final String resolveChatName(final String memberName) {

        assert connection != null : "Connection has to be set when initializing ChatHandler, but is Null!";

        return connection.getRealPlayerName(memberName);
    }
}
