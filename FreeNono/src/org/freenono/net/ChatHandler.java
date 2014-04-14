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
class ChatHandler {

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
    public void sendMessage(final String message) {

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
    public void receiveMessageBy(final MessageListener<String> messageListener) {

        if (messageListener != null) {
            currentMessageListener = messageListener;
            connection.addChatListener(chatChannel, currentMessageListener);
        }
    }

    /**
     * Closes this chat. After calling this method no more messages are received
     * by NonoWeb.
     */
    public void closeChat() {

        connection.removeChatListener(chatChannel, currentMessageListener);
        currentMessageListener = null;
    }
}
