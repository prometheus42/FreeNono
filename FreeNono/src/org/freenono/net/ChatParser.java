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
 * Parses a message entered by player into chat.
 * 
 * @author Christian Wichmann
 */
public class ChatParser {

    private boolean isCommand = false;
    private boolean wrongCommand = false;
    private StringBuilder returnMessage = new StringBuilder("*** ");

    /**
     * Instantiates a new chat message parser.
     * 
     * @param message
     *            message to be parsed
     * @param connection
     *            connection to NonoWeb
     */
    public ChatParser(final String message, final NonoWebConnection connection) {

        parseMessage(message, connection);
    }

    /**
     * Parses the message for given commands.
     * 
     * @param message
     *            message to be parsed
     * @param connection
     *            connection to NonoWeb
     */
    private void parseMessage(final String message,
            final NonoWebConnection connection) {

        String[] tokens = message.split(" ");
        if (tokens[0].length() > 0 && tokens[0].charAt(0) == '/') {
            isCommand = true;
            switch (tokens[0].substring(1).toLowerCase()) {
            case "info":
                returnMessage.append("FreeNono chat channel!");
                break;
            case "help":
                executeHelp();
                break;
            case "ison":
                executeIson(connection, tokens);
                break;
            case "users":
                executeUsers(connection);
                break;
            case "motd":
                returnMessage.append("There's always next time!");
                break;
            case "nick":
                executeNick(connection, tokens);
                break;
            case "stats":
                returnMessage.append("Statistics: 42\n");
                break;
            default:
                wrongCommand = true;
                break;
            }
        }
    }

    /**
     * Executes HELP command by writing to return message.
     */
    private void executeHelp() {

        returnMessage.append("COMMANDS:\n\\INFO - Give Information\n");
        returnMessage.append("\\HELP - Show help\n");
        returnMessage.append("\\ISON <nickname> - Look if user is present\n");
        returnMessage.append("\\USERS - List all users in chat\n");
        returnMessage.append("\\NICK <nickname> - Change to nickname\n");
    }

    /**
     * Executes ISON command by writing to return message.
     * 
     * @param connection
     *            connection to NonoWeb
     * @param tokens
     *            command tokens
     */
    private void executeIson(final NonoWebConnection connection,
            final String[] tokens) {

        if (tokens.length > 1) {
            if (connection.getAllPlayerNames().contains(tokens[1])) {
                returnMessage.append("Player " + tokens[1] + " is present");
            } else {
                returnMessage.append("Player " + tokens[1] + " is not present");
            }
        }
    }

    /**
     * Executes USERS command by writing to return message.
     * 
     * @param connection
     *            connection to NonoWeb
     */
    private void executeUsers(final NonoWebConnection connection) {

        returnMessage.append("USERS:\n");
        for (String player : connection.getAllPlayerNames()) {
            returnMessage.append(player + "\n");
        }
    }

    /**
     * Executes NICK command by writing to return message.
     * 
     * @param connection
     *            connection to NonoWeb
     * @param tokens
     *            command tokens
     */
    private void executeNick(final NonoWebConnection connection,
            final String[] tokens) {

        if (tokens.length > 1) {
            final String newNickname = tokens[1];
            connection.setRealPlayerName(newNickname);
            returnMessage.append("New nickname: " + newNickname + "\n");
        } else {
            wrongCommand = true;
        }
    }

    /**
     * Returns whether the entered message was a command.
     * 
     * @return true, if entered message was a command
     */
    public final boolean isCommand() {

        return isCommand;
    }

    /**
     * Returns whether the entered command was wrong.
     * 
     * @return true, if entered command was wrong
     */
    public final boolean isWrongCommand() {

        return wrongCommand;
    }

    /**
     * Returns the reply for given command.
     * 
     * @return reply for given command
     */
    public final String getReturnMessage() {

        returnMessage.append(" ***");
        return returnMessage.toString();
    }
}
