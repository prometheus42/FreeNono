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
package org.freenono.ui.highscore;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.freenono.controller.HighscoreManager;
import org.freenono.controller.Score;
import org.freenono.model.data.Nonogram;
import org.freenono.model.game_modes.GameModeType;

/**
 * Implements a table model for highscore table.
 * 
 * @author Christian Wichmann
 */
public class HighscoreTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 8693248331576638831L;

    private HighscoreManager manager = HighscoreManager.getInstance();
    private List<Score> scores;

    /**
     * Initializes a table model for providing high score data to high score
     * table view.
     * 
     * @param gameMode
     *            game mode for which to show high score table
     * @param pattern
     *            nonogram pattern to show high score table for
     */
    public HighscoreTableModel(final GameModeType gameMode,
            final Nonogram pattern) {

        super();

        scores = manager.getHighscoreListForNonogram(pattern.getHash(),
                gameMode);
    }

    @Override
    public final int getRowCount() {

        return scores.size();
    }

    @Override
    public final int getColumnCount() {

        return 3;
    }

    @Override
    public final Object getValueAt(final int rowIndex, final int columnIndex) {

        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IllegalArgumentException(
                    "Parameter rowIndex is out of bounds.");
        }
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            throw new IllegalArgumentException(
                    "Parameter columnIndex is out of bounds.");
        }

        Score x = scores.get(rowIndex);
        String value = "";

        switch (columnIndex) {
        case 0:
            value = x.getPlayer();
            break;
        case 1:
            value = new Date(x.getTime()).toString();
            break;
        case 2:
            value = Integer.toString(x.getScoreValue());
            break;
        default:
            assert false;
            break;
        }

        return value;
    }

    @Override
    public final String getColumnName(final int columnIndex) {

        String value = "";

        switch (columnIndex) {
        case 0:
            value = "Player";
            break;
        case 1:
            value = "Date";
            break;
        case 2:
            value = "Score";
            break;
        default:
            assert false;
            break;
        }

        return value;
    }

    @Override
    public final boolean isCellEditable(final int rowIndex,
            final int columnIndex) {

        // TODO make only newest entry edible!

        return false; // rowIndex == 2 && columnIndex == 0;
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex,
            final int columnIndex) {

        // Object oldValue = getValueAt(rowIndex, columnIndex);
    }

    @Override
    public final Class<?> getColumnClass(final int columnIndex) {

        return String.class;
    }
}
