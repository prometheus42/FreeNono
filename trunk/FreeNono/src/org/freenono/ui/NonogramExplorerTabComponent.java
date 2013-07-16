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
package org.freenono.ui;

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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.provider.CourseProvider;
import org.freenono.ui.common.FontFactory;

/**
 * Shows all information about a course inside its tab. The informations are
 * course name, number of nonograms in course, icon representing the source of
 * this course and a indicator of the courses nonograms difficulties.
 * 
 * As sources there are different providers like from file system, random
 * generated courses or courses form a NonoServer.
 * 
 * @author Christian Wichmann
 */
public class NonogramExplorerTabComponent extends JPanel {

    private static final long serialVersionUID = 6035509805491207825L;

    private static Logger logger = Logger
            .getLogger(NonogramExplorerTabComponent.class);

    private final ImageIcon labelIcon;
    private final CourseProvider labelCourse;

    /**
     * Initializes a new tab component for NonogramExplorer.
     * 
     * @param course
     *            course which is represented by this tab
     * @param icon
     *            icon indicating origin of this course
     */
    public NonogramExplorerTabComponent(final CourseProvider course,
            final ImageIcon icon) {

        labelCourse = course;
        labelIcon = icon;

        initializes();
    }

    /**
     * Initializes the tab with its text label and icon. Also the number of
     * nonograms is given as well as their difficulty.
     */
    private void initializes() {

        setOpaque(false);
        
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
        JLabel label = new JLabel(labelCourse.getCourseName());
        label.setOpaque(false);
        add(label, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        JLabel numberLabel = new JLabel("(" + labelCourse.getNumberOfNonograms() + ")");
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

}
