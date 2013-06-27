/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono.controller;

/**
 * Stores all necessary information about a score. A score results from playing
 * a game and winning it. The score value is provided by GameMode class.
 * 
 * @author Christian Wichmann
 */
public class Score {

    private String nonogram;
    private String gamemode;
    private String time;
    private String player;
    private int scoreValue;

    /**
     * Default constructor. Sets no values.
     */
    public Score() {

    }

    /**
     * Construct Score object and set given values.
     * @param nonogram
     *            Nonogram to set.
     * @param gamemode
     *            Gamemode to set.
     * @param time
     *            Time to set.
     * @param player
     *            Player to set.
     * @param scoreValue
     *            Score value to set.
     */
    public Score(final String nonogram, final String gamemode,
            final String time, final String player, final int scoreValue) {

        this.nonogram = nonogram;
        this.gamemode = gamemode;
        this.time = time;
        this.player = player;
        this.scoreValue = scoreValue;
    }

    /**
     * Getter nonogram.
     * @return Nonogram
     */
    public final String getNonogram() {
        return nonogram;
    }

    /**
     * Setter nonogram.
     * @param nonogram
     *            Nonogram name
     */
    public final void setNonogram(final String nonogram) {
        this.nonogram = nonogram;
    }

    /**
     * Getter gamemode.
     * @return Gamemode
     */
    public final String getGamemode() {
        return gamemode;
    }

    /**
     * Setter gamemode.
     * @param gamemode
     *            Gamemode
     */
    public final void setGamemode(final String gamemode) {
        this.gamemode = gamemode;
    }

    /**
     * Getter time.
     * @return Time
     */
    public final String getTime() {
        return time;
    }

    /**
     * Setter time.
     * @param time
     *            Time
     */
    public final void setTime(final String time) {
        this.time = time;
    }

    /**
     * Getter player.
     * @return Player
     */
    public final String getPlayer() {
        return player;
    }

    /**
     * Setter player.
     * @param player
     *            Player
     */
    public final void setPlayer(final String player) {
        this.player = player;
    }

    /**
     * Getter score value.
     * @return Score Value
     */
    public final int getScoreValue() {
        return scoreValue;
    }

    /**
     * Setter score value.
     * @param scoreValue
     *            Score value
     */
    public final void setScoreValue(final int scoreValue) {
        this.scoreValue = scoreValue;
    }

}
