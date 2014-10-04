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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.freenono.provider.CollectionTools;
import org.freenono.provider.CourseFromSeed;
import org.freenono.provider.CourseProvider;
import org.freenono.provider.NonogramFromSeed;
import org.freenono.provider.NonogramFromSeed.RandomTypes;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;
import org.freenono.ui.colormodel.ColorModel;
import org.freenono.ui.common.AskUserDialog;
import org.freenono.ui.common.FontFactory;

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

    private int buttonColumns = 6;

    private final ColorModel colorModel;

    /**
     * Asks user for input to generate a random nonogram by seed. This dialog
     * adds three buttons to the default <code>AskUserDialog</code> class: "Ok",
     * "Cancel", "Give Random Seed".
     * <p>
     * The actual generation of a random nonogram happens in a course provider.
     * 
     * @author Christian Wichmann
     */
    private class AskUserForSeed extends AskUserDialog {

        private static final long serialVersionUID = -7201316017600941387L;

        private static final String CHARACTERS_FOR_RANDOM_SEED =
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-.,_:;#+*!\"§$%&/()=?";
        private static final int RANDOM_SEED_LENGTH = 14;
        private final Random rng = new Random();

        /**
         * Initializes a new dialog to ask user for a seed string to generate a
         * random nonogram pattern.
         * 
         * @param question
         *            question to ask the user
         * @param defaultAnswer
         *            default answer that should be in the text field when
         *            showing dialog
         * @param foregroundColor
         *            foreground color to be used
         * @param backgroundColor
         *            background color to be used
         */
        public AskUserForSeed(final String question, final String defaultAnswer, final Color foregroundColor, final Color backgroundColor) {
            super(question, defaultAnswer, foregroundColor, backgroundColor);

            initialize();
        }

        /**
         * Initializes a dialog to ask user for a seed to generate a random
         * nonogram pattern.
         */
        private void initialize() {

            // ask user for seed
            JButton okButton = new JButton(Messages.getString("OK"));
            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    setVisible(false);
                }
            });
            setOkButton(okButton);
            JButton cancelButton = new JButton(Messages.getString("Cancel"));
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    setVisible(false);
                }
            });
            setCancelButton(cancelButton);
            JButton randomSeedButton = new JButton(Messages.getString("NonogramChooserUI.Random"));
            randomSeedButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    char[] text = new char[RANDOM_SEED_LENGTH];
                    for (int i = 0; i < RANDOM_SEED_LENGTH; i++) {
                        text[i] = CHARACTERS_FOR_RANDOM_SEED.charAt(rng.nextInt(CHARACTERS_FOR_RANDOM_SEED.length()));
                    }
                    setDefaultValue(new String(text));
                }
            });
            addExtraButton(randomSeedButton);
            setVisible(true);
        }
    }

    /**
     * Initializes a course view pane for a given course.
     * 
     * @param cp
     *            course for which this course view pane should be build, null
     *            is no a valid value
     * @param colorModel
     *            color model to be used for foreground and background colors
     * @param columnCount
     *            number of columns that should be shown
     */
    public CourseViewPane(final CourseProvider cp, final ColorModel colorModel, final int columnCount) {

        if (cp == null) {
            throw new IllegalArgumentException("Course provider for course view pane should not be null.");
        }
        if (colorModel == null) {
            throw new IllegalArgumentException("Argument colorModel for course view pane should not be null.");
        }
        this.courseProvider = cp;
        this.colorModel = colorModel;
        this.buttonColumns = columnCount;

        initialize();
    }

    /**
     * Initialize course view pane with title and scrollable button panel.
     */
    private void initialize() {

        final int borderMargin = 10;
        setBorder(BorderFactory.createEmptyBorder(borderMargin, borderMargin, borderMargin, borderMargin));

        // setMinimumSize(new Dimension(700, 700));
        // setPreferredSize(getMinimumSize());
        // setPreferredSize(new Dimension(1920, 1920));
        setOpaque(false);

        final int insets = 10;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(insets, insets, insets, insets);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        add(buildTitle(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(buildScrollPane(), c);

        buttonPane.requestFocusInWindow();
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
        titleLabel.setHorizontalTextPosition(JLabel.LEADING);
        titleLabel.setAlignmentX(JLabel.RIGHT);

        if (CollectionTools.countUnsolvedNonograms(courseProvider) == 0) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon/checkmark.png"));
            titleLabel.setIcon(icon);
        }

        return titleLabel;
    }

    /**
     * Builds scroll panel including the button panel.
     * 
     * @return Scroll pane with the button panel.
     */
    private JScrollPane buildScrollPane() {

        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(buildButtonPane());
        scrollPane.setOpaque(false);

        /*
         * TODO Make viewport (button pane) a scroll-savy component and
         * dynamically change the scroll pane's client's size (use
         * getPreferredScrollableViewportSize?)
         */
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
        scrollPane.getViewport().setOpaque(false);
        // use FlowLayout to move all nonogram buttons to the top of the page
        // scrollPane.getViewport().setLayout(new FlowLayout());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    /**
     * Builds the button panel with NonogramButtons for all nonograms in the
     * course.
     * 
     * @return Panel with all NonogramButtons.
     */
    private JPanel buildButtonPane() {

        logger.debug("Build course view for course " + courseProvider.getCourseName() + ".");

        int buttonCount = 0;

        buttonPane = new JPanel();
        buttonPane.setOpaque(false);

        buttonPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;

        List<String> nonogramList = courseProvider.getNonogramList();

        if (nonogramList != null) {
            for (NonogramProvider np : courseProvider.getNonogramProvider()) {
                // create new nonogram button and define what should happen when
                // its clicked...
                NonogramButton nb = new NonogramButton(np);
                nb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        NonogramButton nb = ((NonogramButton) e.getSource());
                        if (e.getSource() instanceof NonogramButton) {
                            chosenNonogram = nb.getNonogramProvider();

                            // call main user interface to switch from nonogram
                            // explorer to game board
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainUI) getTopLevelAncestor()).finishStart();
                                }
                            });
                        }
                    }
                });

                // ...and add it to the grid
                c.gridx = buttonCount % buttonColumns;
                c.gridy = buttonCount / buttonColumns;
                buttonPane.add(nb, c);
                buttonCount++;
            }

            if (courseProvider instanceof CourseFromSeed) {
                /*
                 * For all courses of random nonograms, ask user for seed and
                 * generate necessary random nonogram pattern.
                 */

                // create nonogram button for generating new random nonograms...
                final CourseFromSeed cfs = (CourseFromSeed) courseProvider;
                final NonogramButton nb = new NonogramButton(new NonogramFromSeed("", RandomTypes.RANDOM, cfs));
                nb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        AskUserForSeed aufs =
                                new AskUserForSeed(Messages.getString("NonogramChooserUI.SeedLabel"), "", colorModel.getBottomColor(),
                                        colorModel.getTopColor());

                        // generate nonogram from seed and set it as
                        // chosenNonogram if OK button was clicked
                        if (aufs.okButtonWasClicked()) {
                            String seed = aufs.getUserInput();
                            if (seed != null && !seed.isEmpty()) {
                                chosenNonogram = cfs.generateSeededNonogram(seed);
                            }
                        }

                        // call main user interface to switch from nonogram
                        // explorer to game board
                        // TODO Check if inbokeLater() is necessary.
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ((MainUI) getTopLevelAncestor()).finishStart();
                            }
                        });
                    }
                });

                // ...and add it to the grid
                c.gridx = buttonCount % buttonColumns;
                c.gridy = buttonCount / buttonColumns;
                buttonPane.add(nb, c);
                buttonCount++;
            }
        }

        buttonPane.validate();

        return buttonPane;
    }

    /**
     * Gets the NonogramProvider for the clicked button on this course panel.
     * 
     * @return NonogramProvider for the clicked button on this course panel or
     *         null if no button has been pressed
     */
    public final NonogramProvider getChosenNonogram() {

        return chosenNonogram;
    }

    /**
     * Returns the course provider of this view. This value is guaranteed to be
     * not null.
     * 
     * @return course provider of this view
     */
    public final CourseProvider getCourseProvider() {

        assert courseProvider != null;
        return courseProvider;
    }
}
