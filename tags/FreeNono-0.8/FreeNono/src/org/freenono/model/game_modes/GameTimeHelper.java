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
package org.freenono.model.game_modes;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;

/**
 * Organizes and controls the game timer and clocks all game times (play time,
 * pause time, etc.).
 * 
 * @author Christian Wichmann
 */
public class GameTimeHelper {

    private static Logger logger = Logger.getLogger(GameTimeHelper.class);

    private GameEventHelper eventHelper = null;

    /**
     * Enum defining if game timer should count up or down.
     * 
     * @author Christian Wichmann
     */
    public enum GameTimerDirection {

        /**
         * Count timer up from zero.
         */
        COUNT_UP,

        /**
         * Count timer down to zero.
         */
        COUNT_DOWN
    };

    private GameTimerDirection gtd = GameTimerDirection.COUNT_DOWN;

    private Timer timer = new Timer();
    private Task tickTask;

    /**
     * Timer that is called every second to fire an timer event.
     */
    class Task extends TimerTask {
        @Override
        public void run() {
            timerElapsed();
        }
    }

    /*
     * All long variables that store times contain a value in nanoseconds!
     */
    private long startTime = 0L;
    private long pauseTime = 0L;
    private GameTime gameTime = null;

    private long accumulatedPauseDuration = 0L;
    private long loadedTime = 0L;
    private long offset = 0L;

    private boolean countingTime = false;

    /**
     * Initializes a game time helper class that provides timer functionality.
     * 
     * @param eventHelper
     *            Game event helper to fire timer events.
     * @param gtd
     *            Direction in which timer should count.
     * @param loadTime
     *            Time to be loaded as start value.
     */
    public GameTimeHelper(final GameEventHelper eventHelper,
            final GameTimerDirection gtd, final long loadTime) {

        this.eventHelper = eventHelper;
        this.gtd = gtd;
        this.loadedTime = loadTime;

        logger.info("New GameTimeHelper loaded with " + loadedTime
                + " ms and counting " + gtd + ".");

        gameTime = new GameTime();
    }

    /**
     * Starts counting time.
     */
    public final synchronized void startTime() {

        // if this method is called the first time just start timing
        if (startTime == 0) {

            // remember reference time for begin of the game
            startTime = System.nanoTime();
            pauseTime = System.nanoTime();

            // is else remember the last pause duration and save it in
            // accumulatedPauseDuration and resume timing
        } else {

            long pauseDuration = System.nanoTime() - pauseTime;
            accumulatedPauseDuration += pauseDuration;
            pauseTime = 0;
        }

        // start timer
        tickTask = new Task();
        timer.schedule(tickTask, 0, GameTime.MILLISECONDS_PER_SECOND);

        countingTime = true;
    }

    /**
     * Stops counting time.
     */
    public final synchronized void stopTime() {

        pauseTime = System.nanoTime();

        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }

        countingTime = false;
    }

    /**
     * Checks if game time is elapsed when counting down.
     * 
     * @return True, if game time is up.
     */
    public final boolean isTimeElapsed() {

        return getGameTime().isZero();
    }

    /**
     * Calculates current game time.
     * 
     * @return Current game time.
     */
    public final synchronized GameTime getGameTime() {

        // dependent if game is running and game time is ticking the
        // running time in nanoseconds is calculated...
        long tmp = 0;
        if (countingTime) {
            tmp = System.nanoTime() - startTime - accumulatedPauseDuration;
        } else {
            tmp = pauseTime - startTime - accumulatedPauseDuration;
        }

        if (gtd == GameTimerDirection.COUNT_DOWN && loadedTime != 0) {

            /*
             * calculate game time if counting down from loaded time...
             */

            tmp = (int) ((double) tmp / GameTime.NANOSECONDS_PER_MILLISECOND);
            tmp = Math.max(loadedTime + offset - tmp, 0);

            // ...and saved in a GameTime instance.
            tmp = (int) ((double) tmp / GameTime.MILLISECONDS_PER_SECOND);
            gameTime = new GameTime(tmp);

        } else if (gtd == GameTimerDirection.COUNT_UP) {

            /*
             * or counting up from loaded time!
             */

            tmp = (int) ((double) tmp / GameTime.NANOSECONDS_PER_MILLISECOND);
            tmp = Math.max(loadedTime + offset + tmp, 0);

            // ...and saved in a GameTime instance.
            tmp = (int) ((double) tmp / GameTime.MILLISECONDS_PER_SECOND);
            gameTime = new GameTime(tmp);

        } else {
            gameTime = new GameTime(0, 0);
        }

        return gameTime;
    }

    /**
     * Adds some time to current game time.
     * 
     * @param minutes
     *            Minutes to add.
     * @param seconds
     *            Seconds to add.
     */
    public final void addTime(final int minutes, final int seconds) {

        offset += ((minutes * GameTime.SECONDS_PER_MINUTE + seconds) * GameTime.MILLISECONDS_PER_SECOND);
    }

    /**
     * Subtracts some time from current game time.
     * 
     * @param minutes
     *            Minutes to subtract.
     * @param seconds
     *            Seconds to subtract.
     */
    public final void subTime(final int minutes, final int seconds) {

        offset -= ((minutes * GameTime.SECONDS_PER_MINUTE + seconds) * GameTime.MILLISECONDS_PER_SECOND);
    }

    /**
     * Fired timer event when timer task calls it (every second).
     */
    private void timerElapsed() {

        eventHelper.fireTimerEvent(new StateChangeEvent(this, getGameTime()));
    }

    /**
     * Stops timer so time can no longer be counted.
     */
    public final void stopTimer() {

        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        timer.cancel();
        timer.purge();
    }

    /**
     * Stops timer when object is destroyed.
     * 
     * @throws Throwable
     *             when super does it.
     */
    protected final void finalize() throws Throwable {

        try {
            stopTime();
            stopTimer();

        } finally {
            super.finalize();
        }
    }
}
