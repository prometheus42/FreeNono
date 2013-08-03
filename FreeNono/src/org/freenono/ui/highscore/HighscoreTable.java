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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JTable;

import org.freenono.controller.Settings;
import org.freenono.model.data.Nonogram;

/**
 * Table showing the ten highest scores for a given game. The last played game
 * will be highlighted and editable.
 * 
 * @author Christian Wichmann
 */
public class HighscoreTable extends JTable {

    private static final long serialVersionUID = -2211752749921996800L;

    private int gapWidth = 10;
    private int gapHeight = 10;

    /**
     * Initializes a highscore table.
     * 
     * @param settings
     *            settings object for getting current game mode
     * @param pattern
     *            nonogram pattern to show high score for
     */
    public HighscoreTable(final Settings settings, final Nonogram pattern) {

        super(new HighscoreTableModel(settings.getGameMode(), pattern));

        setOpaque(true);
        setBackground(settings.getColorModel().getTopColor());
        setBorder(BorderFactory.createLoweredBevelBorder());
        setGridColor(settings.getColorModel().getStrangeColor());
        setIntercellSpacing(new Dimension(gapWidth, gapHeight));
        setRowHeight(getRowHeight() + gapHeight);
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        getTableHeader().setBackground(settings.getColorModel().getTopColor());

        setDefaultRenderer(String.class, new HighscoreTableCellRenderer(
                settings.getColorModel()));

        // TODO should L&F settings be used?
        // UIManager.put("Table.alternateRowColor", bgColor.darker());
        // UIManager.put("Table.background", bgColor.brighter());
    }
}
