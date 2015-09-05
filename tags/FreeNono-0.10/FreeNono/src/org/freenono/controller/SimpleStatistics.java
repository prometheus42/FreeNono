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

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;
import org.freenono.model.game_modes.GameTime;
import org.freenono.ui.Messages;

/**
 * Calculates and outputs a simple statistic about field moves like marking, occupying.
 * <p>
 * Currently values for following keys are provided:
 *
 * <table>
 * <tr>
 * <td>key</td>
 * <td>value</td>
 * </tr>
 * <tr>
 * <td>nonogramName</td>
 * <td>Name of nonogram</td>
 * </tr>
 * <tr>
 * <td>nonogramDifficulty</td>
 * <td>Difficulty level of nonogram</td>
 * </tr>
 * <tr>
 * <td>course</td>
 * <td>Course of nonogram</td>
 * </tr>
 * <tr>
 * <td>played_#hash</td>
 * <td>number of times nonogram with given #hash was played</td>
 * </tr>
 * <tr>
 * <td>won_#hash</td>
 * <td>number of times nonogram with given #hash was won</td>
 * </tr>
 * <tr>
 * <td>gameTime</td>
 * <td>Added time game was played</td>
 * </tr>
 * <tr>
 * <td>pauseTime</td>
 * <td>Sum of times game was paused</td>
 * </tr>
 * <tr>
 * <td>occupyPerformance</td>
 * <td>Number of occupied fields per second</td>
 * </tr>
 * <tr>
 * <td>markPerformance</td>
 * <td>Number of marked fields per second</td>
 * </tr>
 * <tr>
 * <td>wrongOccupied</td>
 * <td>Number of wrongly occupied fields.</td>
 * </tr>
 * <tr>
 * <td>overallCorrectlyOccupied</td>
 * <td>Number of overall correctly occupied fields</td>
 * </tr>
 * <tr>
 * <td>overallWronglyOccupied</td>
 * <td>Number of overall wrongly occupied fields</td>
 * </tr>
 * <tr>
 * <td>overallMarked</td>
 * <td>Number of overall marked fields.</td>
 * </tr>
 *
 * </table>
 *
 * @author Christian Wichmann
 */
public final class SimpleStatistics implements Statistics {

    private static Logger logger = Logger.getLogger(SimpleStatistics.class);

    private static SimpleStatistics instance = new SimpleStatistics();

    private Nonogram nonogram = null;
    private GameEventHelper eventHelper = null;

    private long lastStart = 0;
    private long lastStop = 0;
    private long gameTime = 0;
    private long pauseTime = 0;

    private final StatisticsDataStore dataStore;

    private int fieldsCorrectlyOccupied = 0;
    private int fieldsWronglyOccupied = 0;
    private int fieldsMarked = 0;

    private final GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void fieldOccupied(final FieldControlEvent e) {

            if (lastStart != 0) {
                fieldsCorrectlyOccupied++;
                dataStore.incrementFieldsCorrectlyOccupied();
            }
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {

            if (lastStart != 0) {
                fieldsMarked++;
                dataStore.incrementFieldsMarked();
            }
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {

        }

        @Override
        public void wrongFieldOccupied(final FieldControlEvent e) {

            if (lastStart != 0) {
                fieldsWronglyOccupied++;
                dataStore.incrementFieldsWronglyOccupied();
            }
        }

        @Override
        public void stateChanging(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                addOneGame(false);
                handleGameStop();
                break;
            case SOLVED:
                addOneGame(true);
                handleGameStop();
                break;
            case PAUSED:
                handleGameStop();
                break;
            case RUNNING:
                handleGameStart();
                break;
            case USER_STOP:
                handleGameStop();
                break;
            case NONE:
                break;
            default:
                assert false : e.getNewState();
                break;
            }
        }

        @Override
        public void timerElapsed(final StateChangeEvent e) {

        }

        @Override
        public void optionsChanged(final ProgramControlEvent e) {

        }

        @Override
        public void programControl(final ProgramControlEvent e) {

            switch (e.getPct()) {
            case NONOGRAM_CHOSEN:
                nonogram = e.getPattern();
                break;
            case OPTIONS_CHANGED:
                break;
            case PAUSE_GAME:
                break;
            case QUIT_PROGRAMM:
                break;
            case RESTART_GAME:
                resetStatistics();
                lastStart = System.nanoTime();
                break;
            case RESUME_GAME:
                break;
            case SHOW_ABOUT:
                break;
            case SHOW_OPTIONS:
                break;
            case START_GAME:
                resetStatistics();
                lastStart = System.nanoTime();
                break;
            case STOP_GAME:
                break;
            default:
                assert false : e.getPct();
                break;
            }
        }
    };

    /**
     * Private constructor so simple statistics class can not externally be instantiated.
     */
    private SimpleStatistics() {

        logger.debug("Instantiate simple statistics provider.");
        dataStore = StatisticsDataStore.getInstance();
    }

    @Override
    public void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    @Override
    public void removeEventHelper() {

        eventHelper.removeGameListener(gameAdapter);
        this.eventHelper = null;
    }

    /*
     * ===== Methods logging statistical information =====
     */

    /**
     * Resets all internal fields to its start value.
     */
    private void resetStatistics() {

        lastStart = 0;
        lastStop = 0;
        gameTime = 0;
        pauseTime = 0;

        fieldsCorrectlyOccupied = 0;
        fieldsWronglyOccupied = 0;
        fieldsMarked = 0;
    }

    /**
     * Handles a start or resume of game by stopping pause time and starting game time.
     */
    private void handleGameStart() {

        lastStart = System.nanoTime();
        if (lastStop != 0) {

            pauseTime += (lastStart - lastStop) / GameTime.NANOSECONDS_PER_MILLISECOND;
            lastStop = 0;
        }
    }

    /**
     * Handles a stop or pause of game by starting pause time and stopping game time.
     */
    private void handleGameStop() {

        lastStop = System.nanoTime();
        if (lastStart != 0) {

            gameTime += (lastStop - lastStart) / GameTime.NANOSECONDS_PER_MILLISECOND;
            lastStart = 0;
        }
    }

    /**
     * Adds for last chosen nonogram a one on list of played nonograms. If game was won a one is
     * also added to list of won games.
     *
     * @param gameWon
     *            if game was won
     */
    private void addOneGame(final boolean gameWon) {

        final String hash = nonogram.getHash();
        dataStore.incrementTimesPlayedForNonogram(hash);
        if (gameWon) {
            dataStore.incrementTimesWonForNonogram(hash);
        } else {
            dataStore.incrementTimesLostForNonogram(hash);
        }
        dataStore.saveStatisticsToFile();
    }

    /*
     * ===== Methods returning values for statistical properties =====
     */

    @Override
    public Object getValue(final String property) {

        if ("nonogramName".equals(property)) {
            if (nonogram != null) {
                return nonogram.getName();
            } else {
                return "";
            }
        } else if ("nonogramDifficulty".equals(property)) {
            if (nonogram != null) {
                return getLocalizedDifficulty(nonogram.getDifficulty());
            } else {
                return "";
            }
        } else if ("course".equals(property)) {
            return "";
        } else if (property.startsWith("played_")) {
            return getValueForPlayed(property);
        } else if (property.startsWith("won_")) {
            return getValueForWon(property);
        } else if ("gameTime".equals(property)) {
            return getValueForGameTime();
        } else if ("pauseTime".equals(property)) {
            return getValueForPauseTime();
        } else if ("occupyPerformance".equals(property)) {
            return getValueForOccupyPerformance();
        } else if ("markPerformance".equals(property)) {
            return getValueForMarkPerformance();
        } else if ("wrongOccupied".equals(property)) {
            return "" + fieldsWronglyOccupied + " wrong fields";
        } else if ("overallCorrectlyOccupied".equals(property)) {
            return "" + dataStore.getFieldsCorrectlyOccupied();
        } else if ("overallWronglyOccupied".equals(property)) {
            return "" + dataStore.getFieldsWronglyOccupied();
        } else if ("overallMarked".equals(property)) {
            return "" + dataStore.getFieldsMarked();
        } else {
            return "";
        }
    }

    /**
     * Returns the statistical value for property "markPerformance".
     *
     * @return value for property "markPerformance"
     */
    private Double getValueForMarkPerformance() {

        if (gameTime != 0) {
            return calculateMarkPerformance();
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the statistical value for property "occupyPerformance".
     *
     * @return value for property "occupyPerformance"
     */
    private Double getValueForOccupyPerformance() {

        if (gameTime != 0) {
            return calculateOccupyPerformance();
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the statistical value for property "pauseTime".
     *
     * @return value for property "pauseTime"
     */
    private String getValueForPauseTime() {

        if (pauseTime != 0) {
            return "" + (pauseTime / GameTime.MILLISECONDS_PER_SECOND) + " " + Messages.getString("SimpleStatistics.Seconds");
        } else {
            return "";
        }
    }

    /**
     * Returns the statistical value for property "gameTime".
     *
     * @return value for property "gameTime"
     */
    private String getValueForGameTime() {

        if (gameTime != 0) {
            return "" + (gameTime / GameTime.MILLISECONDS_PER_SECOND) + " " + Messages.getString("SimpleStatistics.Seconds");
        } else {
            return "";
        }
    }

    /**
     * Returns the statistical value for property "won_#hash".
     *
     * @param property
     *            string given by caller
     * @return value for property "won_#hash"
     */
    private String getValueForWon(final String property) {

        final String hash = property.substring(4);
        return Integer.toString(dataStore.getTimesWonForNonogram(hash));
    }

    /**
     * Returns the statistical value for property "played_#hash".
     *
     * @param property
     *            string given by caller
     * @return value for property "played_#hash"
     */
    private String getValueForPlayed(final String property) {

        final String hash = property.substring(7);
        return Integer.toString(dataStore.getTimesPlayedForNonogram(hash));
    }

    /**
     * Calculates performance for occupying fields.
     *
     * @return performance in fields per minute
     */
    private Double calculateOccupyPerformance() {

        final double perf = fieldsCorrectlyOccupied / ((double) gameTime / GameTime.MILLISECONDS_PER_SECOND / GameTime.SECONDS_PER_MINUTE);

        return perf;
    }

    /**
     * Calculates performance for marking fields.
     *
     * @return performance in fields per minute
     */
    private Double calculateMarkPerformance() {

        final double perf = fieldsMarked / ((double) gameTime / GameTime.MILLISECONDS_PER_SECOND / GameTime.SECONDS_PER_MINUTE);

        return perf;
    }

    /**
     * Returns a localized string describing a given difficulty level depending on the current
     * locale.
     *
     * @param d
     *            difficulty level to find localized string for
     * @return localized string describing a difficulty level
     */
    private String getLocalizedDifficulty(final DifficultyLevel d) {

        // TODO move this method to DifficultyLevel class
        String localizedName = "";
        switch (d) {
        case UNDEFINED:
            localizedName = Messages.getString("DifficultyLevel.UNDEFINED");
            break;
        case EASIEST:
            localizedName = Messages.getString("DifficultyLevel.EASIEST");
            break;
        case EASY:
            localizedName = Messages.getString("DifficultyLevel.EASY");
            break;
        case NORMAL:
            localizedName = Messages.getString("DifficultyLevel.NORMAL");
            break;
        case HARD:
            localizedName = Messages.getString("DifficultyLevel.HARD");
            break;
        case HARDEST:
            localizedName = Messages.getString("DifficultyLevel.HARDEST");
            break;
        default:
            assert false;
            break;
        }
        return localizedName;
    }

    /*
     * ===== Miscellaneous methods =====
     */

    /**
     * Returns always one and the same instance of SimpleStatistics.
     *
     * @return instance of SimpleStatistics.
     */
    public static SimpleStatistics getInstance() {

        return instance;
    }

    @Override
    public void outputStatistics() {

        /*
         * TODO change and improve output (use Messages.getString()?)
         */

        System.out.printf("***** Game Statistics **************************************\n");
        System.out.printf("*                                                          *\n");
        System.out.printf("* Nonogram: %s", nonogram.getName());

        for (int i = 0; i < Math.max(0, 47 - nonogram.getName().length()); i++) {
            System.out.printf(" ");
        }

        System.out.printf("*\n");
        System.out.printf("*                                                          *\n");
        System.out.printf("* fields occupied:                      %4d fields        *\n", fieldsCorrectlyOccupied);
        System.out.printf("* fields marked:                        %4d fields        *\n", fieldsMarked);
        System.out.printf("* fields wrongly occupied:              %4d fields        *\n", fieldsWronglyOccupied);
        System.out.printf("*                                                          *\n");
        System.out.printf("* fields occupied per minute:           %4f fields        *\n", getValueForOccupyPerformance());
        System.out.printf("*                                                          *\n");
        System.out.printf("************************************************************\n");
    }
}
