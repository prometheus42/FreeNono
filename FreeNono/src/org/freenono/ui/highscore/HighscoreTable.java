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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Table showing the ten highest scores for a given game. The last played game
 * will be highlighted and editable.
 * 
 * @author Christian Wichmann
 */
public class HighscoreTable extends JTable {

    private static final long serialVersionUID = -2211752749921996800L;

    private Color bgColor = new Color(153, 255, 153);
    private Color gridColor = Color.WHITE;

    private int gapWidth = 10;
    private int gapHeight = 10;

    public HighscoreTable() {

        super(new HighscoreTableModel());

        setOpaque(false);
        setBackground(bgColor);
        setBorder(BorderFactory.createLoweredBevelBorder());
        setGridColor(gridColor);
        setIntercellSpacing(new Dimension(gapWidth, gapHeight));
        setRowHeight(getRowHeight() + gapHeight);
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        getTableHeader().setBackground(bgColor);

        setDefaultRenderer(String.class, new HighscoreTableCellRenderer());

        // TODO should L&F settings be used???
        // UIManager.put("Table.alternateRowColor", bgColor.darker());
        // UIManager.put("Table.background", bgColor.brighter());
    }

    public static void main(String[] args) {

        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
        }

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane sp = new JScrollPane(new HighscoreTable());
        sp.getViewport().setBackground(new Color(153, 255, 153));
        f.add(sp);
        f.pack();
        f.setVisible(true);
    }

}
