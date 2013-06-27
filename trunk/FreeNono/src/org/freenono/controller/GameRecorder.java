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

import java.util.Map;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.QuizEvent;
import org.freenono.event.StateChangeEvent;

/**
 * Static class for all recording functions. A new recording can be started
 * under a new name and all game board events are recorded. ONLY the board
 * event, NOT the events fired by the UI. When a replay is started, first a
 * board clean event will be fired and then all board events with a separation
 * time given through the constructor or the {@link #setSeparationTime(int)}
 * method.
 * 
 * @author Christian Wichmann
 */
@SuppressWarnings("unused")
public final class GameRecorder {

    // TODO: should this class really be static?! If yes, the constructor
    // (currenty commented out) should be removed.

    private static GameEventHelper eventHelper;
    private static Map<String, GameRecord> gameRecords;
    private static boolean listening = false;
    private static boolean replayRunning = false;
    private static GameRecord currentRecord;
    private static int separationTime;

    private static GameAdapter gameAdapter = new GameAdapter() {

        public void optionsChanged(final ProgramControlEvent e) {
        }

        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case gameOver:
                break;

            case solved:
                break;

            case userStop:
                break;

            case paused:
                break;

            case running:
                break;

            default:
                break;
            }

        }

        public void fieldOccupied(final FieldControlEvent e) {
        }

        public void fieldMarked(final FieldControlEvent e) {
        }

        public void fieldUnmarked(final FieldControlEvent e) {
        }

        public void changeActiveField(final FieldControlEvent e) {
        }

        public void askQuestion(final QuizEvent e) {
        }

    };

    /*
     * public GameRecorder(GameEventHelper eventHelper) {
     * GameRecorder.eventHelper = eventHelper;
     * eventHelper.addGameListener(gameAdapter); gameRecords = new
     * HashMap<String, GameRecord>(); buildReplayThread(); }
     */

    /**
     * Hide utility class constructor.
     */
    private GameRecorder() {
    }

    /**
     * Set event helper.
     * @param eventHelper
     *            Event helper
     */
    public static void setEventHelper(final GameEventHelper eventHelper) {

        if (eventHelper != null) {

            eventHelper.removeGameListener(gameAdapter);
        }

        GameRecorder.eventHelper = eventHelper;

        eventHelper.addGameListener(gameAdapter);
    }

    /**
     * TODO.
     */
    private void buildReplayThread() {

        // TODO
    }

    /**
     * Start recording of game.
     * @param gameName
     *            ???
     */
    public static void startRecording(final String gameName) {

        if (gameRecords.containsKey(gameName)) {
            currentRecord = gameRecords.get(gameName);
        } else {
            currentRecord = new GameRecord();
            gameRecords.put(gameName, currentRecord);
        }
        listening = true;
    }

    /**
     * Stop the recording.
     */
    public static void stopRecording() {

        listening = false;
    }

    /**
     * Replay a recording.
     * @param gameName
     *            ???
     */
    public static void replayRecording(final String gameName) {

        replayRunning = true;
    }

    /**
     * Stop the replay.
     */
    public static void stopReplay() {

        replayRunning = false;
    }

    /**
     * Getter seperation time.
     * @return Seperation time
     */
    public static int getSeparationTime() {

        return separationTime;
    }

    /**
     * Setter seperation time.
     * @param separationTime
     *            Seperation time
     */
    public static void setSeparationTime(final int separationTime) {

        GameRecorder.separationTime = separationTime;
    }

}
