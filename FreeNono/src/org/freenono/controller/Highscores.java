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
     * only an unmodifiable copy of the internal stored list of scores. Per
     * default the returned list of nonograms is sorted by score.
     * <p>
     * For different sorting orders use Comparators in Score class
     * (SCORE_DESCENDING_ORDER, SCORE_ASCENDING_ORDER, TIME_DESCENDING_ORDER,
     * TIME_ASCENDING_ORDER).
     * <p>
     * 
     * @return list of all highscores
     */
    public List<Score> getHighscoreList() {

        Collections.sort(highscores, Score.SCORE_DESCENDING_ORDER);

        return Collections.unmodifiableList(highscores);
    }

    /**
     * Returns a list of all scores that are saved for a given game mode. Per
     * default the returned list of nonograms is sorted by score.
     * <p>
     * For different sorting orders use Comparators in Score class
     * (SCORE_DESCENDING_ORDER, SCORE_ASCENDING_ORDER, TIME_DESCENDING_ORDER,
     * TIME_ASCENDING_ORDER).
     * <p>
     * 
     * @param gameModeType
     *            game mode type that all scores should be returned
     * @return list of all highscores for given game mode
     */
    public List<Score> getHighscoreListForGameMode(
            final GameModeType gameModeType) {

        if (gameModeType == null) {
            throw new IllegalArgumentException(
                    "Argument gameModeType should not be null.");
        }

        final List<Score> listOfScores = new ArrayList<Score>();

        for (Score score : highscores) {
            if (score.getGamemode().equals(gameModeType)) {
                listOfScores.add(score);
            }
        }

        Collections.sort(listOfScores, Score.SCORE_DESCENDING_ORDER);

        return listOfScores;
    }

    /**
     * Returns a list of all scores that are saved for a given game mode and a
     * nonogram. Per default the returned list of nonograms is sorted by score.
     * <p>
     * For different sorting orders use Comparators in Score class
     * (SCORE_DESCENDING_ORDER, SCORE_ASCENDING_ORDER, TIME_DESCENDING_ORDER,
     * TIME_ASCENDING_ORDER).
     * <p>
     * 
     * @param nonogramHash
     *            hash of the nonogram
     * @param gameModeType
     *            game mode type that all scores should be returned
     * @return list of all highscores for given game mode and nonogram
     */
    public List<Score> getHighscoreListForNonogram(final String nonogramHash,
            final GameModeType gameModeType) {

        if (nonogramHash == null) {
            throw new IllegalArgumentException(
                    "Argument nonogramHash should not be null.");
        }

        if (gameModeType == null) {
            throw new IllegalArgumentException(
                    "Argument gameModeType should not be null.");
        }

        final List<Score> listOfScores = new ArrayList<Score>();

        for (Score score : highscores) {
            if (score.getGamemode().equals(gameModeType)
                    && score.getNonogram().equals(nonogramHash)) {
                listOfScores.add(score);
            }
        }

        Collections.sort(listOfScores, Score.SCORE_DESCENDING_ORDER);

        return listOfScores;
    }

    /**
     * Returns a list of all scores that are saved for a given nonogram pattern.
     * Per default the returned list of nonograms is sorted by time when played.
     * <p>
     * For different sorting orders use Comparators in Score class
     * (SCORE_DESCENDING_ORDER, SCORE_ASCENDING_ORDER, TIME_DESCENDING_ORDER,
     * TIME_ASCENDING_ORDER).
     * <p>
     * 
     * @param nonogramHash
     *            hash of the nonogram
     * @return list of all highscores for given game mode and nonogram
     */
    public List<Score> getHighscoreListForNonogram(final String nonogramHash) {

        if (nonogramHash == null) {
            throw new IllegalArgumentException(
                    "Argument nonogramHash should not be null.");
        }

        final List<Score> listOfScores = new ArrayList<Score>();

        for (Score score : highscores) {
            if (score.getNonogram().equals(nonogramHash)) {
                listOfScores.add(score);
            }
        }

        Collections.sort(listOfScores, Score.TIME_DESCENDING_ORDER);

        return listOfScores;
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
        logger.info("time\t\t\t\tplayer\t\tscore");
        logger.info("-----------------------------------------------------------");
        for (Score score : highscores) {
            final Date time = new Date(score.getTime());
            logger.info(time.toString() + "\t" + score.getPlayer() + "\t"
                    + score.getScoreValue());
        }
        logger.info("*******************************");
    }
}
