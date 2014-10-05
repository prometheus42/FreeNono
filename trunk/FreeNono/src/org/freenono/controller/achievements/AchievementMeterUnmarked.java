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
package org.freenono.controller.achievements;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;

/**
 * Provides an achievement based on solving nonograms without marking fields on
 * the board.
 * 
 * @author Christian Wichmann
 */
public class AchievementMeterUnmarked extends AchievementMeter {

    private static Logger logger = Logger.getLogger(AchievementMeterUnmarked.class);

    private int condition = 1;
    private int markedFields = 0;
    private boolean gameWon = false;
    private boolean achievementAlreadyAccomplished = false;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void programControl(final ProgramControlEvent e) {
            switch (e.getPct()) {
            case START_GAME:
                markedFields = 0;
                gameWon = false;
                break;
            case STOP_GAME:
                break;
            case RESTART_GAME:
                markedFields = 0;
                gameWon = false;
                break;
            case PAUSE_GAME:
                break;
            case RESUME_GAME:
                break;
            case NONOGRAM_CHOSEN:
                break;
            case QUIT_PROGRAMM:
                break;
            case OPTIONS_CHANGED:
                break;
            case SHOW_ABOUT:
                break;
            case SHOW_OPTIONS:
                break;
            default:
                assert false : e.getPct();
                break;
            }
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                break;
            case SOLVED:
                gameWon = true;
                isAchievementAccomplished();
                break;
            case PAUSED:
                break;
            case RUNNING:
                break;
            case NONE:
                break;
            case USER_STOP:
                break;
            default:
                assert false : e.getNewState();
                break;
            }
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {
            markedFields++;
        }
    };

    /**
     * Instantiates a new achievement meter based on solving nonograms without
     * marking fields on the board.
     * 
     * @param achievement
     *            type of achievement that this object is checking
     * @param condition
     *            number of fields that can be marked and still accomplish this
     *            achievement
     */
    public AchievementMeterUnmarked(final Achievement achievement, final int condition) {

        super(achievement);

        this.condition = condition;

        // hook own game adapter into game event system to get informed about
        // changes
        AchievementManager.getInstance().getEventHelper().addGameListener(gameAdapter);
    }

    @Override
    public final boolean isAchievementAccomplished() {

        boolean achievementCurrentlyAccomplished = gameWon && (markedFields <= condition);
        if (achievementAlreadyAccomplished != achievementCurrentlyAccomplished) {
            achievementAlreadyAccomplished = achievementCurrentlyAccomplished;
            AchievementManager.getInstance().updateAchievements();
        }

        return achievementAlreadyAccomplished;
    }
}
