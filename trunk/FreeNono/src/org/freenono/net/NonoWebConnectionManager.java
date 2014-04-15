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

/**
 * Provides handles for different services available via NonoWeb.
 * <p>
 * NonoWeb is a distributed data store being used to deliver chat messages to
 * all listening FreeNono instances. Furthermore it allows the sharing of
 * nonograms supplied by FreeNono and coop games where more than one player
 * solves a game cooperative.
 * <p>
 * Handler can be accessed by getting a instance of the according class by the
 * getter methods e.g. {@link #getChatHandler()}.
 * <p>
 * Currently the following services are available:
 * <ul>
 * <li>Chat ({@link ChatHandler})</li>
 * <li>Coop games ({@link CoopHandler})</li>
 * </ul>
 * 
 * @author Christian Wichmann
 */
public final class NonoWebConnectionManager {

    private static NonoWebConnectionManager instance;
    private static NonoWebConnection connection;

    private static ChatHandler chatHandler;
    private CoopHandler coopHandler;

    /**
     * Hide utility class constructor.
     */
    private NonoWebConnectionManager() {

        if (connection == null) {
            connection = new NonoWebConnection();
        }
    }

    /**
     * Returns an instance of the {@link NonoWebConnectionManager} to get
     * handles for different NonoWeb services.
     * 
     * @return instance of <code>NonoWebConnectionManager</code>
     */
    public static NonoWebConnectionManager getInstance() {

        if (instance == null) {
            instance = new NonoWebConnectionManager();
        }
        return instance;
    }

    /**
     * Returns a handler for sending and receiving chat messages via NonoWeb.
     * 
     * @return chat handler for sending and receiving chat messages
     */
    public ChatHandler getChatHandler() {

        if (chatHandler == null) {
            chatHandler = new ChatHandler(connection);
        }
        return chatHandler;
    }

    /**
     * Returns a handler for sending and receiving chat messages via NonoWeb.
     * 
     * @return chat handler for sending and receiving chat messages
     */
    public CoopHandler getCoopHandler() {

        if (coopHandler != null) {
            coopHandler.closeGame();
        }
        coopHandler = new CoopHandler(connection);
        return coopHandler;
    }
}
