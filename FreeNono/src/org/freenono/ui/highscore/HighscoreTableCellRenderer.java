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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Implements a table cell renderer for highscore table.
 * 
 * @author Christian Wichmann
 */
public class HighscoreTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -2395257159689458562L;

    private Color bgColor = new Color(153, 255, 153);

    /**
     * Initializes a cell renderer for highscore table.
     */
    public HighscoreTableCellRenderer() {

        super();
    }

    @Override
    public final void setValue(final Object value) {

        System.out.println("fff");
        super.setValue(value);
    }

    @Override
    public final Component getTableCellRendererComponent(final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column) {

        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        // MyModel model = (MyModel) table.getModel();

        if (row % 2 == 0) {
            c.setBackground(bgColor.darker());
        } else {
            c.setBackground(bgColor.brighter());
        }

        return c;

        // setOpaque(true);
        // setBackground(row % 2 == 0 ? bgColor.darker() : bgColor.brighter());
        // System.out.println("eee");
        // if (isSelected) {
        // // selectedBorder is a solid border in the color
        // // table.getSelectionBackground().
        // // setBorder(selectedBorder);
        // } else {
        //
        // // unselectedBorder is a solid border in the color
        // // table.getBackground().
        // // setBorder(unselectedBorder);
        // }
        // setToolTipText("fff");
    }
}
