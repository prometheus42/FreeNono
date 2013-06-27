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
import java.util.List;

/**
 * Data holding class for HighscoreManager. Highscores stores Scores resulting
 * from won games by a user.
 * 
 * @author Christian Wichmann
 */
public class Highscores {

    private List<Score> highscores;

    /**
     * Default Highscores constructor.
     */
    public Highscores() {
        highscores = new ArrayList<Score>();
    }

    /**
     * Commits a newly played score and checks if it has to be entered into the
     * highscore for the chosen gamemode.
     * @param nonogram
     *            Nonogram of score.
     * @param gamemode
     *            Gamemode of score.
     * @param time
     *            Time of score.
     * @param player
     *            Player of score.
     * @param scoreValue
     *            Score value of score.
     */
    public final void addScore(final String nonogram, final String gamemode,
            final String time, final String player, final int scoreValue) {

        highscores.add(new Score(nonogram, gamemode, time, player, scoreValue));
    }

    /**
     * Print highscore summary to console.
     * @param gameMode
     *            Gamemode of highscore.
     */
    public final void printHighscores(final String gameMode) {
        final int lineLength = 36;

        System.out.println("*** GameMode Highscore **************************");
        System.out.println("* GameMode: " + gameMode);
        for (int i = 0; i < lineLength - gameMode.length(); i++) {
            System.out.println(" ");
        }
        System.out.println("*");
        System.out.println("*                                               *");
        for (Score score : highscores) {
            System.out.println("* " + score.getPlayer() + "  "
                    + score.getTime() + "  " + score.getScoreValue() + " *");
        }
        System.out.println("*                                               *");
        System.out.println("*************************************************");
    }

}
