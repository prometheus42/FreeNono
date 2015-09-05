/*****************************************************************************
 * NonoWeb - A distributed nonogram database
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
package org.freenono.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.LogManager;

/**
 * Sends messages via NonoWeb continuously to test other clients.
 *
 * @author Christian Wichmann
 */
final class ChatBot {

    /**
     * Instantiates a new chat bot.
     */
    private ChatBot() {

        final ChatHandler ch = NonoWebConnectionManager.getInstance().getChatHandler();

        while (true) {

            InputStream is = null;

            try {
                is = Runtime.getRuntime().exec("fortune").getInputStream();
            } catch (final IOException e1) {
                e1.printStackTrace();
            }

            final Scanner s = new Scanner(is);
            s.useDelimiter("\\A");
            ch.sendMessage(s.next());
            s.close();

            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main method to start chat bot.
     *
     * @param args
     *            arguments
     */
    public static void main(final String[] args) {

        LogManager.getLogManager().reset();
        new ChatBot();
    }
}
