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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.provider.CollectionTools;
import org.freenono.provider.CourseProvider;
import org.freenono.ui.Messages;
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

    private static final int TAB_WIDTH_DEFAULT = 250;
    private static int tabWidth = TAB_WIDTH_DEFAULT;
    private static final int TAB_HEIGHT_DEFAULT = 40;
    private static int tabHeight = TAB_HEIGHT_DEFAULT;

    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);

    private static final boolean SHOW_SOURCE_ICONS = false;
    private static final ImageIcon COURSE_COMPLETED_ICON =
            new ImageIcon(CourseTabButton.class.getResource("/resources/icon/checkmark.png"));
    private final ImageIcon labelIcon;

    private final CourseProvider labelCourse;

    private JLabel courseNameLabel;
    private JLabel numberLabel;

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

        loadCourseDataIntoLabels();

        addTooltip();

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

        // set border and other parameters of this tab button
        final int border = 5;
        setBorder(BorderFactory.createEmptyBorder(0, border, 0, border));
        setOpaque(false);
        setPreferredSize(new Dimension(tabWidth, tabHeight));
        setMinimumSize(new Dimension(tabWidth, tabHeight));

        // set layout manager
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        final int borderVertical = 2;
        final int borderHorizontal = 4;
        c.insets = new Insets(borderVertical, borderHorizontal, borderVertical, borderHorizontal);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0.8;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        courseNameLabel = new JLabel(shortenString(labelCourse.getCourseName()));
        courseNameLabel.setOpaque(false);
        add(courseNameLabel, c);

        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        numberLabel = new JLabel();
        numberLabel.setFont(FontFactory.createTextFont().deriveFont(10.0f));
        add(numberLabel, c);

        if (SHOW_SOURCE_ICONS) {
            c.gridx = 2;
            c.gridy = 0;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.weightx = 0.2;
            c.weighty = 0.2;
            c.anchor = GridBagConstraints.NORTHEAST;
            c.fill = GridBagConstraints.NONE;
            add(new JLabel(labelIcon), c);
        }

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(buildDifficultyIndicator(), c);
    }

    /**
     * Loads data about how many nonograms have been solved into the labels in
     * this button.
     */
    private void loadCourseDataIntoLabels() {

        // get number of unsolved nonograms in this course
        final int numberUnsolvedNonograms = CollectionTools.countUnsolvedNonograms(labelCourse);
        final int numberNonograms = labelCourse.getNumberOfNonograms();
        final int numberSolvedNonograms = labelCourse.getNumberOfNonograms() - numberUnsolvedNonograms;

        // set couse completed icon if all nonograms have been solved
        if (numberUnsolvedNonograms == 0) {
            courseNameLabel.setIcon(COURSE_COMPLETED_ICON);
        }

        // set information about how many nonograms have been solved
        numberLabel.setText(numberSolvedNonograms + " / " + numberNonograms);
    }

    /**
     * Add tooltip to this course tab button with all information.
     */
    public final void addTooltip() {

        final StringBuilder tooltipText = new StringBuilder("<html>");
        final int unsolvedNonograms = CollectionTools.countUnsolvedNonograms(labelCourse);

        // construct tool tip text from number of solved nonograms...
        if (unsolvedNonograms == 0) {
            tooltipText.append(Messages.getString("NonogramChooserUI.NonogramTreeCompleteCourse"));
        } else {
            tooltipText.append(String.valueOf(unsolvedNonograms));
            tooltipText.append(" ");
            tooltipText.append(Messages.getString("NonogramChooserUI.NonogramTreeUnsolvedNonograms"));
        }

        // ...and date when course was last played.
        final long dateLastPlayed = CollectionTools.determineDateWhenLastPlayed(labelCourse);
        if (dateLastPlayed != 0) {
            tooltipText.append("<br>");
            tooltipText.append(Messages.getString("NonogramChooserUI.LastPlayedTooltip"));
            tooltipText.append(" ");
            tooltipText.append(DATE_FORMAT.format(new Date(dateLastPlayed)));
        }
        tooltipText.append("</html>");
        setToolTipText(tooltipText.toString());
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
                Paint paint = new GradientPaint(0, 0, leftColor, getWidth(), 0, rightColor);
                g2.setPaint(paint);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            private Color getMinimumColor() {

                return getDifficultyColor(labelCourse.fetchCourse().getMinimumDifficulty());
            }

            private Color getMaximumColor() {

                return getDifficultyColor(labelCourse.fetchCourse().getMaximumDifficulty());
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

        final int indicatorHeight = 10;
        difficultyIndicator.setPreferredSize(new Dimension(tabWidth - 25, indicatorHeight));

        return difficultyIndicator;
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
     * Updates course data in all components of this tab button. It changes the
     * tool tip and the information about how many of the nonograms have been
     * solved already.
     */
    public final void updateCourseData() {

        loadCourseDataIntoLabels();

        addTooltip();
    }

    /**
     * Shortens a given string to a maximum given length.
     * 
     * @param string
     *            given string to be shortened
     * @return shortened string
     */
    private String shortenString(final String string) {

        final int maxLength = 30;
        final int subLength = (maxLength - 3) / 2;
        if (string.length() > maxLength) {
            String s = string.substring(0, subLength) + "..." + string.substring(string.length() - subLength, string.length());
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

        CourseTabListener[] list = listeners.getListeners(CourseTabListener.class);

        for (CourseTabListener listener : list) {
            listener.courseTabChanged();
        }
    }
}
