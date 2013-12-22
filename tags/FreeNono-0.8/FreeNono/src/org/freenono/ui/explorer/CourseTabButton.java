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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.provider.CourseProvider;
import org.freenono.ui.common.FontFactory;

/**
 * Displays all information about a course in one component and lets user click
 * this component to display course's nonograms in ui.
 * 
 * @author Christian Wichmann
 */
public class CourseTabButton extends JPanel {

    private static final long serialVersionUID = -6738442654055804246L;

    private static Logger logger = Logger.getLogger(CourseTabButton.class);

    private static CourseTabButton currentlySelectedTab = null;
    private static List<CourseTabButton> courseTabList = new ArrayList<CourseTabButton>();
    private static EventListenerList listeners = new EventListenerList();

    private static final int TAB_WIDTH_DEFAULT = 300;
    private static int tabWidth = TAB_WIDTH_DEFAULT;
    private static final int TAB_HEIGHT_DEFAULT = 100;
    private static int tabHeight = TAB_HEIGHT_DEFAULT;

    private final ImageIcon labelIcon;
    private final CourseProvider labelCourse;

    /**
     * Initializes a course tab button.
     * 
     * @param course
     *            course which is represented by this tab
     * @param icon
     *            icon indicating origin of this course
     */
    public CourseTabButton(final CourseProvider course, final ImageIcon icon) {

        labelCourse = course;
        labelIcon = icon;

        initialize();

        // if no tab button was selected, select this first one...
        if (currentlySelectedTab == null) {
            currentlySelectedTab = this;
        }
        courseTabList.add(this);

        addListeners();
    }

    /**
     * Initializes the tab with its text label and icon. Also the number of
     * nonograms is given as well as their difficulty.
     */
    private void initialize() {

        final int border = 5;
        setBorder(BorderFactory.createEmptyBorder(0, border, 0, border));
        setOpaque(false);
        setSize(new Dimension(tabWidth, tabHeight));

        // set layout manager
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        final int borderVertical = 2;
        final int borderHorizontal = 4;
        c.insets = new Insets(borderVertical, borderHorizontal, borderVertical,
                borderHorizontal);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel label = new JLabel(shortenString(labelCourse.getCourseName()));
        label.setOpaque(false);
        add(label, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        JLabel numberLabel = new JLabel("("
                + labelCourse.getNumberOfNonograms() + ")");
        numberLabel.setFont(FontFactory.createTextFont().deriveFont(10.0f));
        add(numberLabel, c);

        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        add(new JLabel(labelIcon), c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.NONE;
        add(buildDifficultyIndicator(), c);
    }

    /**
     * Adds listeners to check for mouse clicks on this component and set the
     * selected course tab button accordingly.
     */
    private void addListeners() {

        addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(final MouseEvent e) {
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseExited(final MouseEvent e) {
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                setThisAsSelected();
            }
        });
    }

    /**
     * Builds a difficulty indicator for course represented by this tab.
     * 
     * @return difficulty indicator
     */
    private Component buildDifficultyIndicator() {

        JPanel difficultyIndicator = new JPanel() {

            private static final long serialVersionUID = -6580237100283008535L;

            private final Color leftColor = getMinimumColor();
            private final Color rightColor = getMaximumColor();

            @Override
            protected void paintComponent(final Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                Paint paint = new GradientPaint(0, 0, leftColor, getWidth(), 0,
                        rightColor);
                g2.setPaint(paint);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            private Color getMinimumColor() {

                return getDifficultyColor(labelCourse.fetchCourse()
                        .getMinimumDifficulty());
            }

            private Color getMaximumColor() {

                return getDifficultyColor(labelCourse.fetchCourse()
                        .getMaximumDifficulty());
            }

            private Color getDifficultyColor(final DifficultyLevel difficulty) {

                Color difficultyColor = NonogramExplorer.UNDEFINED_COLOR;
                switch (difficulty) {
                case EASIEST:
                    difficultyColor = NonogramExplorer.EASIEST_COLOR;
                    break;
                case EASY:
                    difficultyColor = NonogramExplorer.EASY_COLOR;
                    break;
                case HARD:
                    difficultyColor = NonogramExplorer.HARD_COLOR;
                    break;
                case HARDEST:
                    difficultyColor = NonogramExplorer.HARDEST_COLOR;
                    break;
                case NORMAL:
                    difficultyColor = NonogramExplorer.NORMAL_COLOR;
                    break;
                case UNDEFINED:
                    difficultyColor = NonogramExplorer.UNDEFINED_COLOR;
                    break;
                default:
                    assert false : difficulty;
                    break;
                }
                return difficultyColor;
            }
        };

        // set initial size
        final int width = 75;
        final int height = 10;
        difficultyIndicator.setSize(new Dimension(width, height));
        difficultyIndicator.setPreferredSize(new Dimension(width, height));

        return difficultyIndicator;
    }

    /**
     * Shortens a given string to a maximum given length.
     * 
     * @param string
     *            given string to be shortened
     * @return shortened string
     */
    private String shortenString(final String string) {

        final int maxLength = 25;
        final int subLength = (maxLength - 3) / 2;
        if (string.length() > maxLength) {
            String s = string.substring(0, subLength)
                    + "..."
                    + string.substring(string.length() - subLength,
                            string.length());
            return s;
        } else {
            return string;
        }
    }

    /**
     * Sets this course tab button as the currently selected tab.
     */
    private void setThisAsSelected() {

        if (!currentlySelectedTab.equals(this)) {
            currentlySelectedTab = this;
            fireCourseTabChanged();
        }
    }

    /**
     * Returns the currently selected tab button.
     * 
     * @return currently selected tab button
     */
    public static CourseProvider getSelected() {

        return currentlySelectedTab.labelCourse;
    }

    /*
     * ===== Methods to communicate course tab changes =====
     */

    /**
     * Adds a new course tab listener to a static list for all course tab
     * buttons.
     * 
     * @param l
     *            listener to be added
     */
    public static void addCourseTabListener(final CourseTabListener l) {

        listeners.add(CourseTabListener.class, l);
    }

    /**
     * Removes a course tab listener from the static list for all course tab
     * buttons.
     * 
     * @param l
     *            listener to be removed
     */
    public static void removeCourseTabListener(final CourseTabListener l) {

        listeners.remove(CourseTabListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for notification on
     * course tab changes.
     */
    private static void fireCourseTabChanged() {

        logger.debug("Firing course tab changed event.");

        CourseTabListener[] list = listeners
                .getListeners(CourseTabListener.class);

        for (CourseTabListener listener : list) {
            listener.courseTabChanged();
        }
    }
}
