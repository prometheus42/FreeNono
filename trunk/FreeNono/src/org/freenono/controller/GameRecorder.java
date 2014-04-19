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

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;

/**
 * Provides all recording functions. A new recording can be started under a new
 * name and all game board events are recorded. ONLY the board event, NOT the
 * events fired by the UI. When a replay is started, first a board clean event
 * will be fired and then all board events with a separation time given through
 * the {@link #setSeparationTime(int)} method.
 * 
 * @author Christian Wichmann
 */
public final class GameRecorder {

    private static Logger logger = Logger.getLogger(GameRecorder.class);

    private static GameRecorder gameRecorder;
    private GameEventHelper eventHelper;
    private Map<String, GameRecord> gameRecords;
    private GameRecord currentRecord;
    private boolean listening = false;

    private int separationTime = 125;
    private static final boolean REPLAY_MARKED_FIELDS = false;

    private final ScheduledExecutorService replayExecutor = Executors
            .newScheduledThreadPool(10);
    private ScheduledFuture<?> replayFuture;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void programControl(final ProgramControlEvent e) {
            switch (e.getPct()) {
            case NONOGRAM_CHOSEN:
                currentRecord.clearRecord();
                break;
            case OPTIONS_CHANGED:
                break;
            case PAUSE_GAME:
                break;
            case QUIT_PROGRAMM:
                replayExecutor.shutdown();
                break;
            case RESTART_GAME:
                currentRecord.clearRecord();
                break;
            case RESUME_GAME:
                break;
            case SHOW_ABOUT:
                break;
            case SHOW_OPTIONS:
                break;
            case START_GAME:
                currentRecord.clearRecord();
                break;
            case STOP_GAME:
                break;
            default:
                break;
            }
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {
            switch (e.getNewState()) {
            case GAME_OVER:
                listening = false;
                stopReplay();
                break;
            case SOLVED:
                listening = false;
                buildReplayThread();
                break;
            case USER_STOP:
                listening = false;
                stopReplay();
                break;
            case PAUSED:
                listening = false;
                break;
            case RUNNING:
                listening = true;
                stopReplay();
                break;
            case NONE:
                break;
            default:
                assert false : e.getNewState();
                break;
            }
        }

        @Override
        public void fieldOccupied(final FieldControlEvent e) {
            if (listening) {
                currentRecord.addEventToGame(e);
            }
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {
            if (listening) {
                currentRecord.addEventToGame(e);
            }
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {
            if (listening) {
                currentRecord.addEventToGame(e);
            }
        }
    };

    /**
     * Initializes the game recorder instance.
     */
    private GameRecorder() {

        gameRecords = new HashMap<String, GameRecord>();
    }

    /**
     * Returns one instance of GameRecorder.
     * 
     * @return an instance of GameRecorder
     */
    public static GameRecorder getInstance() {

        if (gameRecorder == null) {
            gameRecorder = new GameRecorder();
        }
        return gameRecorder;
    }

    /**
     * Set game event helper.
     * 
     * @param eventHelper
     *            game event helper to record events from
     */
    public void setEventHelper(final GameEventHelper eventHelper) {

        if (eventHelper != null) {
            eventHelper.removeGameListener(gameAdapter);
        }

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    /**
     * Build a new thread for replaying events of last game.
     */
    private void buildReplayThread() {

        /**
         * Runs inside the replay thread provided by ScheduledExecutorService
         * and sends events back to the game.
         * 
         * @author Christian Wichmann
         */
        class ReplayRunnable implements Runnable {

            private Queue<GameEvent> eventQueue;

            /**
             * Instantiates a new Runnable for sending events to the game.
             * 
             * @param eventQueue
             *            event queue
             */
            public ReplayRunnable(final Queue<GameEvent> eventQueue) {

                this.eventQueue = eventQueue;
            }

            @Override
            public void run() {

                if (!eventQueue.isEmpty()) {
                    dispatchEvent(eventQueue.poll());
                }
            }
        }

        ReplayRunnable replaying = new ReplayRunnable(
                currentRecord.getEventQueue());

        replayFuture = replayExecutor.scheduleAtFixedRate(replaying,
                separationTime, separationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Dispatched game event back.
     * 
     * @param event
     *            game event to dispatch.
     */
    protected void dispatchEvent(final GameEvent event) {

        if (event instanceof FieldControlEvent) {
            FieldControlEvent fieldEvent = (FieldControlEvent) event;
            switch (fieldEvent.getFieldControlType()) {
            case ACTIVE_FIELD_CHANGED:
                break;
            case CROSS_OUT_CAPTION:
                break;
            case FIELD_MARKED:
                if (REPLAY_MARKED_FIELDS) {
                    eventHelper.fireFieldMarkedEvent(fieldEvent);
                }
                break;
            case FIELD_OCCUPIED:
                eventHelper.fireFieldOccupiedEvent(fieldEvent);
                break;
            case FIELD_UNMARKED:
                if (REPLAY_MARKED_FIELDS) {
                    eventHelper.fireFieldUnmarkedEvent(fieldEvent);
                }
                break;
            case FIELD_UNOCCUPIED:
                eventHelper.fireFieldUnoccupiedEvent(fieldEvent);
                break;
            case MARK_FIELD:
                break;
            case NONE:
                break;
            case OCCUPY_FIELD:
                break;
            case WRONG_FIELD_OCCUPIED:
                break;
            default:
                break;
            }
        }
    }

    /**
     * Start recording of game. If game identifier already used all event will
     * be appended onto this list.
     * 
     * @param gameName
     *            identifier under which to record the game
     */
    public void startRecording(final String gameName) {

        if (gameRecords.containsKey(gameName)) {
            currentRecord = gameRecords.get(gameName);
        } else {
            currentRecord = new GameRecord();
            gameRecords.put(gameName, currentRecord);
        }
        // listening = true;
        logger.debug("Starting new recording...");
    }

    /**
     * Stop the current recording. If no recording is currently running this
     * method does nothing. Recordings will also automatically stopped when the
     * current game was solved or player has lost.
     */
    public void stopRecording() {

        listening = false;
        logger.debug("Stopping recording...");
    }

    /**
     * Replay a recording.
     * 
     * @param gameName
     *            identifier defining which game to replay
     */
    public void replayRecording(final String gameName) {
    }

    /**
     * Stops the replay.
     */
    public void stopReplay() {

        if (replayFuture != null) {
            replayFuture.cancel(false);
            replayFuture = null;
        }
    }

    /**
     * Gets separation time in milliseconds.
     * 
     * @return separation time in milliseconds
     */
    public int getSeparationTime() {

        return separationTime;
    }

    /**
     * Sets separation time in milliseconds.
     * 
     * @param separationTime
     *            separation time in milliseconds
     */
    public void setSeparationTime(final int separationTime) {

        this.separationTime = separationTime;
    }
}
