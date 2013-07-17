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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.freenono.model.game_modes.GameModeType;

/**
 * Data holding class for HighscoreManager. <code>Highscores</code> stores
 * <code>Scores</code> resulting from games won by a player. Which scores are
 * actually stored and how long they are stored depends on ???
 * 
 * @author Christian Wichmann
 */
public final class Highscores {

    private static Logger logger = Logger.getLogger(Highscores.class);

    private final List<Score> highscores;

    /**
     * Default constructor instantiating a empty list.
     */
    public Highscores() {

        highscores = new ArrayList<Score>();
    }

    /**
     * Commits a newly played score and checks if it has to be entered into the
     * highscore for the chosen gamemode.
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
    public void addScore(final String nonogram, final GameModeType gamemode,
            final long time, final String player, final int scoreValue) {

        highscores.add(new Score(nonogram, gamemode, time, player, scoreValue));
    }

    /**
     * Returns a list of all scores that are saved. Actually this method returns
     * only an unmodifiable copy of the internal stored list of scores.
     * 
     * @return list of all highscores
     */
    public List<Score> getHighscoreList() {

        return Collections.unmodifiableList(highscores);
    }

    /**
     * Print highscore summary to console. This method is used as debugging
     * tool.
     * 
     * @param gameMode
     *            game mode to print highscore for
     */
    public void printHighscores(final GameModeType gameMode) {

        logger.info("********** Highscore **********");
        logger.info("GameMode: " + gameMode);
        logger.info("time\t\t\t\t\tplayer\t\tscore");
        logger.info("-----------------------------------------------------------");
        for (Score score : highscores) {
            Date time = new Date(score.getTime());
            logger.info(time.toString() + "\t" + score.getPlayer() + "\t"
                    + score.getScoreValue());
        }
        logger.info("*******************************");
    }
}
