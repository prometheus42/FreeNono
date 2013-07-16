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

import java.util.Date;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Game;

/**
 * Manages the highscore by listening for events and updating highscore list.
 * 
 * @author Christian Wichmann
 */
public class HighscoreManager {

    private static Logger logger = Logger.getLogger(HighscoreManager.class);

    private GameEventHelper eventHelper;
    private Highscores highscores;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void timerElapsed(final StateChangeEvent e) {
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                break;

            case SOLVED:
                Game g = (Game) e.getSource();

                logger.debug("Adding score to highscore list: "
                        + g.getGameScore());

                // TODO Use game-wide player name instead of 'user.name'
                // property
                highscores.addScore(g.getGamePattern().getHash(), g
                        .getGameMode().toString(), (new Date()).toString(),
                        System.getProperty("user.name"), g.getGameScore());
                highscores.printHighscores(g.getGameMode().toString());
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
                break;

            case STOP_GAME:
                break;

            case RESTART_GAME:
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
    };

    /**
     * Default Constuctor.
     * @param eventHelper
     *            Game event helper
     */
    public HighscoreManager(final GameEventHelper eventHelper) {

        // connect to game event handler
        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);

        // load highscore for current player from file
        // TODO load highscore from file
        highscores = new Highscores();
    }

    /**
     * Setter event helper.
     * @param eventHelper
     *            Event helper
     */
    public final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    /**
     * Remove the event helper from this object.
     */
    public final void removeEventHelper() {

        if (eventHelper != null) {
            eventHelper.removeGameListener(gameAdapter);
            this.eventHelper = null;
        }
    }
}
