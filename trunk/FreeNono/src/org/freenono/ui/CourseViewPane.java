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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CourseProvider;
import org.freenono.interfaces.NonogramProvider;

/**
 * Shows a panel containing NonogramButton instances for every nonogram in given
 * course.
 * 
 * @author Christian Wichmann
 */
public class CourseViewPane extends JPanel {

    private static final long serialVersionUID = 1160970301029289041L;

    private static Logger logger = Logger.getLogger(CourseViewPane.class);

    private CourseProvider courseProvider = null;
    private NonogramProvider chosenNonogram = null;

    private JScrollPane scrollPane = null;
    private JPanel buttonPane = null;
    private JLabel titleLabel = null;

    /**
     * Initializes a course view pane for a given course.
     * 
     * @param cp
     *            Course for which this course view pane should be build.
     */
    public CourseViewPane(final CourseProvider cp) {

        this.courseProvider = cp;

        initialize();
    }

    /**
     * Initialize course view pane with title and scrollable button panel.
     */
    private void initialize() {

        final int borderMargin = 10;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(buildTitle());
        add(buildScrollPane());
        setBorder(BorderFactory.createEmptyBorder(borderMargin, borderMargin,
                borderMargin, borderMargin));
        validate();

        buttonPane.requestFocusInWindow();
    }

    /**
     * Builds scroll panel including the button panel.
     * 
     * @return Scroll pane with the button panel.
     */
    private JScrollPane buildScrollPane() {

        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(buildButtonPane());

        // TODO Dynamically Change the scroll pane's client's size
        // (use getPreferredScrollableViewportSize?)
        scrollPane.setPreferredSize(new Dimension(650, 450));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    /**
     * Builds the course title.
     * 
     * @return Label containing course title.
     */
    private JLabel buildTitle() {

        final int fontSize = 24;

        titleLabel = new JLabel(courseProvider.getCourseName());
        titleLabel.setFont(FontFactory.createLcdFont().deriveFont(fontSize));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return titleLabel;
    }

    /**
     * Builds the button panel with NonogramButtons for all nonograms in the
     * course.
     * 
     * @return Panel with all NonogramButtons.
     */
    private JPanel buildButtonPane() {

        logger.debug("Build course view for course "
                + courseProvider.getCourseName() + ".");

        buttonPane = new JPanel();

        buttonPane.setLayout(new FlowLayout());

        List<String> nonogramList = courseProvider.getNonogramList();

        if (nonogramList != null) {

            // TODO Eliminate constants (100) for size calculation!
            buttonPane.setPreferredSize(new Dimension(600,
                    (int) (100 * (nonogramList.size() / 6.))));

            for (NonogramProvider np : courseProvider.getNonogramProvider()) {

                NonogramButton nb = new NonogramButton(np);
                buttonPane.add(nb);
                nb.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {

                        NonogramButton nb = ((NonogramButton) e.getSource());
                        if (e.getSource() instanceof NonogramButton) {

                            chosenNonogram = nb.getNonogramProvider();
                            ((JDialog) getTopLevelAncestor()).dispose();
                        }
                    }
                });
            }
        }

        return buttonPane;
    }

    /**
     * Gets the NonogramProvider for the clicked button on this course panel.
     * 
     * @return NonogramProvider for the clicked button on this course panel.
     */
    public final NonogramProvider getChosenNonogram() {

        return chosenNonogram;
    }
}
