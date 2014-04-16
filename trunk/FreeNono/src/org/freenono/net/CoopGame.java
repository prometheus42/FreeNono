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

import org.freenono.model.data.Nonogram;

/**
 * Represents a coop game. It stores the coop game ID, the nonogram pattern for
 * this game and if instance is initiating or joining a game.
 * <p>
 * If an instance is initiating a game the CoopGame class contains the nonogram
 * pattern chosen by the user. At instances who join an already announced game
 * it includes the coop game ID of the game to join. Whether the one or the
 * other case is relevant is stored as coop game type.
 * 
 * @author Christian Wichmann
 */
public class CoopGame {

    /**
     * Enumerates what role an instances plays in a coop game.
     * 
     * @author Christian Wichmann
     */
    public enum CoopGameType {
        /**
         * Instance is initiating a new coop game.
         */
        INITIATING,

        /**
         * Instance is joining a already announced coop game.
         */
        JOINING
    }

    private CoopGameType coopGameType;
    private String coopGameId = "";
    private Nonogram pattern = null;

    /**
     * Instantiates a new CoopGame.
     * 
     * @param coopGameType
     *            type of role the instance plays in coop game
     * @param coopGameId
     *            id of coop game
     */
    public CoopGame(final CoopGameType coopGameType, final String coopGameId) {

        if (coopGameType == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameType should not be null.");
        }
        if (coopGameId == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameId should not be null.");
        }

        this.coopGameType = coopGameType;
        this.coopGameId = coopGameId;
    }

    /**
     * Instantiates a new CoopGame.
     * 
     * @param coopGameType
     *            type of role the instance plays in coop game
     * @param pattern
     *            nonogram pattern to play
     */
    public CoopGame(final CoopGameType coopGameType, final Nonogram pattern) {

        if (coopGameType == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameType should not be null.");
        }
        if (pattern == null) {
            throw new IllegalArgumentException(
                    "Argument pattern should not be null.");
        }

        this.coopGameType = coopGameType;
        this.pattern = pattern;
    }

    /**
     * Instantiates a new CoopGame.
     * 
     * @param coopGameType
     *            type of role the instance plays in coop game
     * @param coopGameId
     *            id of coop game
     * @param pattern
     *            nonogram pattern to play
     */
    public CoopGame(final CoopGameType coopGameType, final String coopGameId,
            final Nonogram pattern) {

        if (coopGameType == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameType should not be null.");
        }
        if (coopGameId == null) {
            throw new IllegalArgumentException(
                    "Argument coopGameId should not be null.");
        }
        if (pattern == null) {
            throw new IllegalArgumentException(
                    "Argument pattern should not be null.");
        }

        this.coopGameType = coopGameType;
        this.coopGameId = coopGameId;
        this.pattern = pattern;
    }

    /*
     * TODO Store components of game ID separate or use string.split("@") for
     * toString method?!
     */

    @Override
    public final String toString() {

        return coopGameId;
    }

    /**
     * Returns the type of role this instance plays in coop game.
     * 
     * @return type of role this instance plays in coop game
     */
    public final CoopGameType getCoopGameType() {

        return coopGameType;
    }

    /**
     * Returns the nonogram pattern of this coop game.
     * 
     * @return nonogram pattern of this coop game
     */
    public final Nonogram getPattern() {

        return pattern;
    }

    /**
     * Returns the coop game ID of this game.
     * 
     * @return coop game ID
     */
    public final String getCoopGameId() {

        return coopGameId;
    }
}
