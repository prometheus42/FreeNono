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
package org.freenono.ui.explorer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.freenono.controller.SimpleStatistics;
import org.freenono.provider.CourseProvider;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.Messages;

/**
 * Renders all cells in the nonogram tree in NonogramChooserUI.
 * 
 * @author Christian Wichmann
 */
public class NonogramTreeRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -1903332761908135884L;
    private ImageIcon icon;

    /**
     * Initializes an own tree cell renderer for the nonogram tree in
     * NonogramChooserUI.
     */
    public NonogramTreeRenderer() {

        super();

        icon = new ImageIcon(getClass().getResource(
                "/resources/icon/checkmark.png"));
    }

    @Override
    public final Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean sel, final boolean expanded,
            final boolean leaf, final int row, final boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof CourseProvider) {

            int unsolvedNonograms = countUnsolvedNonograms((CourseProvider) (node
                    .getUserObject()));

            if (leaf) {
                if (unsolvedNonograms == 0) {
                    setIcon(icon);
                    setToolTipText(Messages
                            .getString("NonogramChooserUI.NonogramTreeCompleteCourse"));
                } else {
                    setToolTipText(String.valueOf(unsolvedNonograms)
                            + " "
                            + Messages
                                    .getString("NonogramChooserUI.NonogramTreeUnsolvedNonograms"));
                }
            } else {
                setToolTipText(null);
            }
        }
        return this;
    }

    /**
     * Checks how much nonograms of a given course are solved and whether the
     * course is completed.
     * 
     * @param cp
     *            course provider from nonogram tree to be checked
     * @return number of unsolved nonograms or zero if course is complete
     */
    private int countUnsolvedNonograms(final CourseProvider cp) {

        int unsolvedNonogramsInCourse = 0;

        for (NonogramProvider np : cp.getNonogramProvider()) {
            String hash = np.fetchNonogram().getHash();
            String won = SimpleStatistics.getInstance().getValue("won_" + hash);
            if ("0".equals(won)) {
                unsolvedNonogramsInCourse++;
            }
        }

        return unsolvedNonogramsInCourse;
    }
}
