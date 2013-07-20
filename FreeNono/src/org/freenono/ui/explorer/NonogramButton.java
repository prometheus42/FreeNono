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

import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.log4j.Logger;
import org.freenono.controller.Manager;
import org.freenono.model.data.DifficultyLevel;
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

    /**
     * Initializes a new button to represent a nonogram.
     * 
     * @param n
     *            NonogramProvider providing the nonogram for this button.
     */
    public NonogramButton(final NonogramProvider n) {

        if (n == null) {
            throw new NullPointerException(
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

        if (nonogram.getDifficulty() == DifficultyLevel.EASIEST) {

            setBackground(NonogramExplorer.EASIEST_COLOR);
        }
        if (nonogram.getDifficulty() == DifficultyLevel.EASY) {

            setBackground(NonogramExplorer.EASY_COLOR);

        } else if (nonogram.getDifficulty() == DifficultyLevel.NORMAL) {

            setBackground(NonogramExplorer.NORMAL_COLOR);

        } else if (nonogram.getDifficulty() == DifficultyLevel.HARD) {

            setBackground(NonogramExplorer.HARD_COLOR);

        } else if (nonogram.getDifficulty() == DifficultyLevel.HARDEST) {

            setBackground(NonogramExplorer.HARDEST_COLOR);

        } else if (nonogram.getDifficulty() == DifficultyLevel.UNDEFINED) {

            setBackground(NonogramExplorer.UNDEFINED_COLOR);
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

        File thumb = new File(Manager.DEFAULT_THUMBNAILS_PATH, nonogram
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
