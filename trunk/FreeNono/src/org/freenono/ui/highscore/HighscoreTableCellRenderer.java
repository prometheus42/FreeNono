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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.freenono.ui.colormodel.ColorModel;
import org.freenono.ui.common.FontFactory;

/**
 * Implements a table cell renderer for high score table.
 *
 * @author Christian Wichmann
 */
public class HighscoreTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -2395257159689458562L;

    private final ColorModel colorModel;

    /**
     * Initializes a cell renderer for high score table.
     *
     * @param colorModel
     *            color model defining colors to be used
     */
    public HighscoreTableCellRenderer(final ColorModel colorModel) {

        super();

        this.colorModel = colorModel;
    }

    @Override
    public final void setValue(final Object value) {

        super.setValue(value);
    }

    @Override
    public final Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {

        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setFont(FontFactory.createTextFont().deriveFont(14.0f));

        c.setBackground(colorModel.getTopColor());

        // TODO better cell rendering :-)

        // if (row % 2 == 0) {
        // c.setBackground(colorModel.getTopColor().darker());
        // } else {
        // c.setBackground(colorModel.getTopColor().brighter());
        // }

        return c;
    }
}
