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

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;

/**
 * Provides an achievement based on how many games a player could win without
 * making an error.
 * 
 * @author Christian Wichmann
 */
public class AchievementMeterFaultlessness extends AchievementMeter {

    private int condition;
    private int numberOfFaultlessGames = 0;
    private int numberOfErrorsInCurrentGame = 0;

    private boolean achievementAlreadyAccomplished = false;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void timerElapsed(final StateChangeEvent e) {
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                // reset counter of faultless games
                numberOfFaultlessGames = 0;
                break;
            case SOLVED:
                // if game was won add game to counter when no error was made
                if (numberOfErrorsInCurrentGame == 0) {
                    numberOfFaultlessGames++;
                    isAchievementAccomplished();
                }
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
        public void programControl(final ProgramControlEvent e) {
            switch (e.getPct()) {
            case START_GAME:
                numberOfErrorsInCurrentGame = 0;
                break;
            case STOP_GAME:
                break;
            case RESTART_GAME:
                numberOfErrorsInCurrentGame = 0;
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
        public void wrongFieldOccupied(final FieldControlEvent e) {

            numberOfErrorsInCurrentGame++;
        }
    };

    /**
     * Instantiates a new achievement meter for measuring the faultlessness of
     * the player. As condition this class takes the number of games that have
     * to be won with no errors at all.
     * 
     * @param achievement
     *            type of achievement that this object is checking
     * @param condition
     *            condition that has to be fulfilled to accomplish the
     *            achievement
     */
    public AchievementMeterFaultlessness(final Achievement achievement, final int condition) {

        super(achievement);

        this.condition = condition;

        // hook own game adapter into game event system to get informed about
        // changes
        AchievementManager.getInstance().getEventHelper().addGameListener(gameAdapter);
    }

    @Override
    public final boolean isAchievementAccomplished() {

        final boolean achievementCurrentlyAccomplished = numberOfFaultlessGames >= condition;
        if (achievementAlreadyAccomplished != achievementCurrentlyAccomplished) {
            achievementAlreadyAccomplished = achievementCurrentlyAccomplished;
            AchievementManager.getInstance().updateAchievements();
        }

        return achievementAlreadyAccomplished;
    }
}
