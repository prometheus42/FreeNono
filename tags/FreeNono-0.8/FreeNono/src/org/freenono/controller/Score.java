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

import java.util.Comparator;

import org.freenono.model.game_modes.GameModeType;

/**
 * Stores all necessary information about a score. A score results from playing
 * a game and winning it. The score value is provided by GameMode class. This
 * class is immutable and provides only one constructor because all stored
 * informations are <i>required</i>, not optional.
 * 
 * @author Christian Wichmann
 */
public final class Score {

    /**
     * Compares two scores by their score value in ascending order.
     * <code>Null</code> as parameter is <b>not</b> valid and will result in a
     * <code>NullPointerException</code>.
     */
    public static final Comparator<Score> SCORE_ASCENDING_ORDER = new Comparator<Score>() {

        @Override
        public int compare(final Score s1, final Score s2) {

            Integer i1 = (Integer) s1.getScoreValue();
            Integer i2 = (Integer) s2.getScoreValue();
            return i1.compareTo(i2);
        }
    };

    /**
     * Compares two scores by their score value in descending order.
     * <code>Null</code> as parameter is <b>not</b> valid and will result in a
     * <code>NullPointerException</code>.
     */
    public static final Comparator<Score> SCORE_DESCENDING_ORDER = new Comparator<Score>() {

        @Override
        public int compare(final Score s1, final Score s2) {

            Integer i1 = (Integer) s1.getScoreValue();
            Integer i2 = (Integer) s2.getScoreValue();
            return i2.compareTo(i1);
        }
    };

    private final String nonogram;
    private final GameModeType gamemode;
    private final String player;
    private final long time;
    private final int scoreValue;

    /**
     * Constructs <code>Score</code> object and set given values.
     * 
     * @param nonogram
     *            nonogram for which this score was achieved
     * @param gamemode
     *            game mode when this score was achieved
     * @param time
     *            date/time when this score was achieved
     * @param player
     *            player that achieved this score
     * @param scoreValue
     *            achieved score
     */
    public Score(final String nonogram, final GameModeType gamemode,
            final long time, final String player, final int scoreValue) {

        this.nonogram = nonogram;
        this.gamemode = gamemode;
        this.time = time;
        this.player = player;
        this.scoreValue = scoreValue;
    }

    /**
     * Gets nonogram for which this <code>Score</code> was achieved.
     * 
     * @return nonogram for which this score was achieved
     */
    public String getNonogram() {
        return nonogram;
    }

    /**
     * Gets game mode for this <code>Score</code>.
     * 
     * @return game mode when this score was achieved
     */
    public GameModeType getGamemode() {

        return gamemode;
    }

    /**
     * Gets date/time for this <code>Score</code>.
     * 
     * @return date/time when this score was achieved
     */
    public long getTime() {

        return time;
    }

    /**
     * Gets player for this <code>Score</code>.
     * 
     * @return player that achieved this score
     */
    public String getPlayer() {

        return player;
    }

    /**
     * Gets the score value for this <code>Score</code>.
     * 
     * @return achieved score, this value depends on game mode which calculates
     *         it
     */
    public int getScoreValue() {

        return scoreValue;
    }
}
