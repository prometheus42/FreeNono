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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.freenono.controller.HighscoreManager;
import org.freenono.controller.Score;
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

    private static final DateFormat DATE_FORMAT = SimpleDateFormat
            .getDateInstance(SimpleDateFormat.SHORT);
    private final ImageIcon icon;

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

        /*
         * For all leafs in the tree that stand for a nonogram course a tool tip
         * is added with statistical information about this course.
         */

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof CourseProvider) {

            final Object userObject = node.getUserObject();

            if (leaf) {
                final StringBuilder tooltipText = new StringBuilder("<html>");
                final int unsolvedNonograms = countUnsolvedNonograms((CourseProvider) userObject);

                // construct tool tip text from number of solved nonograms...
                if (unsolvedNonograms == 0) {
                    setIcon(icon);
                    tooltipText
                            .append(Messages
                                    .getString("NonogramChooserUI.NonogramTreeCompleteCourse"));
                } else {
                    tooltipText.append(String.valueOf(unsolvedNonograms));
                    tooltipText.append(" ");
                    tooltipText
                            .append(Messages
                                    .getString("NonogramChooserUI.NonogramTreeUnsolvedNonograms"));
                }

                // ...and date when course was last played.
                final long dateLastPlayed = determineDateWhenLastPlayed((CourseProvider) userObject);
                if (dateLastPlayed != 0) {
                    tooltipText.append("<br>");
                    tooltipText.append("Last played:");
                    tooltipText.append(" ");
                    tooltipText.append(DATE_FORMAT.format(new Date(dateLastPlayed)));
                }
                tooltipText.append("</html>");
                setToolTipText(tooltipText.toString());

            } else {
                // turn off tool tip
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

    /**
     * Determines when the last nonogram from a given course was played. Playing
     * dates are stored in HighscoreManager and retrieved by nonogram hash.
     * 
     * @param cp
     *            course provider from nonogram tree to be checked
     * @return time when last nonogram from course was played or 0 when no
     *         nonogram was ever played.
     */
    private long determineDateWhenLastPlayed(final CourseProvider cp) {

        long dateWhenLastPlayed = 0;
        HighscoreManager hm = HighscoreManager.getInstance();

        for (NonogramProvider np : cp.getNonogramProvider()) {
            /*
             * Fetching all highscores for every nonogram in course and check
             * when the last one was played. This time is returned by the
             * method. Algorithm based on the assumption that scores are
             * returned by HighscoreManager sorted by time!
             */
            String hash = np.fetchNonogram().getHash();
            List<Score> list = hm.getHighscoreListForNonogram(hash);
            if (!list.isEmpty()) {
                final long currentScore = list.get(0).getTime();
                if (currentScore > dateWhenLastPlayed) {
                    dateWhenLastPlayed = currentScore;
                }
            }
        }

        return dateWhenLastPlayed;
    }
}
