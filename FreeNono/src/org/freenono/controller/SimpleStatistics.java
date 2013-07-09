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

import java.text.DecimalFormat;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Nonogram;
import org.freenono.model.game_modes.GameTime;
import org.freenono.ui.Messages;

/**
 * Calculates and outputs a simple statistic about field moves like marking,
 * occupying.
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
 * <td>course</td>
 * <td>Course of nonogram</td>
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
 * </table>
 * 
 * @author Christian Wichmann
 */
public final class SimpleStatistics implements Statistics {

    private static SimpleStatistics instance = new SimpleStatistics();

    private Nonogram nonogram = null;
    private GameEventHelper eventHelper = null;

    private DecimalFormat formatter = new DecimalFormat("0.0");

    private long lastStart = 0;
    private long lastStop = 0;
    private long gameTime = 0;
    private long pauseTime = 0;

    private int fieldsCorrectlyOccupied = 0;
    private int fieldsWronglyOccupied = 0;
    private int fieldsMarked = 0;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void fieldOccupied(final FieldControlEvent e) {

            fieldsCorrectlyOccupied++;
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {

            fieldsMarked++;
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {

        }

        @Override
        public void wrongFieldOccupied(final FieldControlEvent e) {

            fieldsWronglyOccupied++;
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case gameOver:
                handleGameStop();
                // outputStatistics();
                break;
            case solved:
                handleGameStop();
                // outputStatistics();
                break;
            case paused:
                handleGameStop();
                break;
            case running:
                handleGameStart();
                break;
            case userStop:
                handleGameStop();
                break;
            case none:
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
                resetStatistics();
            case OPTIONS_CHANGED:
                break;
            case PAUSE_GAME:
                break;
            case QUIT_PROGRAMM:
                break;
            case RESTART_GAME:
                resetStatistics();
                break;
            case RESUME_GAME:
                break;
            case SHOW_ABOUT:
                break;
            case SHOW_OPTIONS:
                break;
            case START_GAME:
                resetStatistics();
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
     * Private constructor so simple statistics class can not externally be
     * instantiated.
     */
    private SimpleStatistics() {

    }

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
     * Handles a start or resume of game by stopping pause time and starting
     * game time.
     */
    private void handleGameStart() {

        lastStart = System.nanoTime();
        if (lastStop != 0) {

            pauseTime += (lastStart - lastStop)
                    / GameTime.NANOSECONDS_PER_MILLISECOND;
            lastStop = 0;
        }
    }

    /**
     * Handles a stop or pause of game by starting pause time and stopping game
     * time.
     */
    private void handleGameStop() {

        lastStop = System.nanoTime();
        if (lastStart != 0) {

            gameTime += (lastStop - lastStart)
                    / GameTime.NANOSECONDS_PER_MILLISECOND;
            lastStart = 0;
        }
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

    @Override
    public void outputStatistics() {

        /*
         * TODO change and improve output (use Messages.getString()?)
         */

        System.out
                .printf("***** Game Statistics **************************************\n");
        System.out
                .printf("*                                                          *\n");
        System.out.printf("* Nonogram: %s", nonogram.getName());

        for (int i = 0; i < Math.max(0, 47 - nonogram.getName().length()); i++) {
            System.out.printf(" ");
        }

        System.out.printf("*\n");
        System.out
                .printf("*                                                          *\n");
        System.out
                .printf("* fields occupied:                      %4d fields        *\n",
                        fieldsCorrectlyOccupied);
        System.out
                .printf("* fields marked:                        %4d fields        *\n",
                        fieldsMarked);
        System.out
                .printf("* fields wrongly occupied:              %4d fields        *\n",
                        fieldsWronglyOccupied);
        System.out
                .printf("*                                                          *\n");
        System.out
                .printf("* fields occupied per minute:           %4d fields        *\n");
        System.out
                .printf("*                                                          *\n");
        System.out
                .printf("************************************************************\n");

    }

    @Override
    public String getValue(final String property) {

        if ("nonogramName".equals(property)) {
            if (nonogram != null) {
                return nonogram.getName();
            } else {
                return "";
            }
        } else if ("course".equals(property)) {
            return "";
        } else if ("gameTime".equals(property)) {
            if (gameTime != 0) {
                return "" + (gameTime / GameTime.MILLISECONDS_PER_SECOND) + " "
                        + Messages.getString("SimpleStatistics.Seconds");
            } else {
                return "";
            }
        } else if ("pauseTime".equals(property)) {
            if (pauseTime != 0) {
                return "" + (pauseTime / GameTime.MILLISECONDS_PER_SECOND)
                        + " " + Messages.getString("SimpleStatistics.Seconds");
            } else {
                return "";
            }
        } else if ("occupyPerformance".equals(property)) {
            if (gameTime != 0) {
                return calculateOccupyPerformance();
            } else {
                return "";
            }
        } else if ("markPerformance".equals(property)) {
            if (gameTime != 0) {
                return calculateMarkPerformance();
            } else {
                return "";
            }
        } else if ("wrongOccupied".equals(property)) {
            return "" + fieldsWronglyOccupied + " wrong fields";
        } else {
            return "";
        }
    }

    /**
     * Calculates performance for occupying fields.
     * 
     * @return performance in fields per minute
     */
    private String calculateOccupyPerformance() {

        double perf = fieldsCorrectlyOccupied
                / ((double) gameTime / GameTime.MILLISECONDS_PER_SECOND / GameTime.SECONDS_PER_MINUTE);

        return formatter.format(perf) + " "
                + Messages.getString("SimpleStatistics.FieldsPerMinute");
    }

    /**
     * Calculates performance for marking fields.
     * 
     * @return performance in fields per minute
     */
    private String calculateMarkPerformance() {

        double perf = fieldsMarked
                / ((double) gameTime / GameTime.MILLISECONDS_PER_SECOND / GameTime.SECONDS_PER_MINUTE);

        return formatter.format(perf) + " "
                + Messages.getString("SimpleStatistics.FieldsPerMinute");
    }

    /**
     * Returns always one and the same instance of SimpleStatistics.
     * 
     * @return instance of SimpleStatistics.
     */
    public static SimpleStatistics getInstance() {

        return instance;
    }
}
