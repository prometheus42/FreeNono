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
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.log4j.Logger;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.game_modes.GameTime;
import org.freenono.provider.NonogramProvider;

/**
 * Shows a button with a preview of the nonogram if user has finished it
 * already.
 * 
 * @author Christian Wichmann
 */
public class NonogramButton extends JButton {

    private static final long serialVersionUID = 6516455428864083473L;

    private static Logger logger = Logger.getLogger(NonogramButton.class);

    private final NonogramProvider nonogram;

    private static final Color EASIEST_COLOR = new Color(122, 255, 123);
    private static final Color EASY_COLOR = new Color(123, 152, 255);
    private static final Color NORMAL_COLOR = new Color(255, 246, 117);
    private static final Color HARD_COLOR = new Color(255, 187, 113);
    private static final Color HARDEST_COLOR = new Color(255, 113, 113);
    private static final Color UNDEFINED_COLOR = new Color(128, 128, 128);

    /**
     * Initializes a new button to represent a nonogram.
     * 
     * @param n
     *            NonogramProvider providing the nonogram for this button.
     */
    public NonogramButton(final NonogramProvider n) {

        if (n == null) {
            throw new IllegalArgumentException(
                    "Nonogram provider should not be null.");
        }

        this.nonogram = n;

        initialize();
    }

    /**
     * Set size and background color for this button.
     */
    private void initialize() {

        final int size = 90;

        setPreferredSize(new Dimension(size, size));
        setFocusable(true);
        setBorderPainted(false);

        setButtonColor();

        boolean nonogramSolved = setThumbnailIcon();

        setTooltipInformation(nonogramSolved);
    }

    /**
     * Sets button color according to difficulty of nonogram. Colors are
     * declared by <code>NonogramButton</code> as static fields.
     */
    private void setButtonColor() {

        if (nonogram.getDifficulty() == DifficultyLevel.easiest) {

            setBackground(EASIEST_COLOR); // green
        }
        if (nonogram.getDifficulty() == DifficultyLevel.easy) {

            setBackground(EASY_COLOR); // blue

        } else if (nonogram.getDifficulty() == DifficultyLevel.normal) {

            setBackground(NORMAL_COLOR); // yellow

        } else if (nonogram.getDifficulty() == DifficultyLevel.hard) {

            setBackground(HARD_COLOR); // orange

        } else if (nonogram.getDifficulty() == DifficultyLevel.hardest) {

            setBackground(HARDEST_COLOR); // red

        } else if (nonogram.getDifficulty() == DifficultyLevel.undefined) {

            setBackground(UNDEFINED_COLOR); // gray
        }
    }

    /**
     * Sets thumbnail for this button from file. Thumbnail is only be set, if
     * nonogram was previously ever solved and an image file exists.
     * 
     * @return true, if thumbnail exists, nonogram was previously solved
     */
    private boolean setThumbnailIcon() {

        boolean nonogramSolved = false;

        File thumb = new File(MainUI.DEFAULT_THUMBNAILS_PATH, nonogram
                .fetchNonogram().getHash());

        if (thumb.exists()) {

            // Toolkit.getDefaultToolkit().getImage(thumb);
            try {
                setIcon(new ImageIcon(thumb.toURI().toURL()));
            } catch (MalformedURLException e) {
                logger.warn("Could not load existing thumbnail!");
            }

            nonogramSolved = true;

        } else {
            setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/courseViewEmpty.png")));
        }

        return nonogramSolved;
    }

    /**
     * Sets tooltip information for this button. Nonogram name is only shown
     * when it was previously solved.
     * 
     * @param nonogramSolved
     *            If nonogram was previously solved
     */
    private void setTooltipInformation(final boolean nonogramSolved) {

        StringBuilder sb = new StringBuilder("<html>");
        if (nonogramSolved) {
            sb.append("Name: ");
            sb.append(nonogram.getName());
            sb.append("<br>");
        }
        // TODO Add real values for "played" and "solved" from HighscoreManager! 
        String[] tooltipText = {"Author: ", nonogram.getAuthor(), "<br>",
                "Duration: ", new GameTime(nonogram.getDuration()).toString(),
                "<br>", "Played: 42", "<br>", "Solved: 17", "</html>"};
        for (String string : tooltipText) {
            sb.append(string);
        }
        setToolTipText(sb.toString());
    }

    /**
     * Gets the NonogramProvider for the nonogram of this button.
     * 
     * @return NonogramProvider for the nonogram of this button.
     */
    public final NonogramProvider getNonogramProvider() {

        return nonogram;
    }
}
