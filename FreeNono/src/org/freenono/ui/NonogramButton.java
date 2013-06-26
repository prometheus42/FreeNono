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
import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.DifficultyLevel;

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

        this.nonogram = n;

        initialize();
    }

    /**
     * Set size and background color for this button.
     */
    private void initialize() {

        final int size = 90;
        final Color easiestColor = new Color(122, 255, 123);
        final Color easyColor = new Color(123, 152, 255);
        final Color normalColor = new Color(255, 246, 117);
        final Color hardColor = new Color(255, 187, 113);
        final Color hardestColor = new Color(255, 113, 113);
        final Color undefinedColor = new Color(128, 128, 128);

        setPreferredSize(new Dimension(size, size));
        setFocusable(true);
        setBorderPainted(false);

        // show difficulty of nonograms by color
        if (nonogram.getDifficulty() == DifficultyLevel.easiest) {

            setBackground(easiestColor); // green
        }
        if (nonogram.getDifficulty() == DifficultyLevel.easy) {

            setBackground(easyColor); // blue

        } else if (nonogram.getDifficulty() == DifficultyLevel.normal) {

            setBackground(normalColor); // yellow

        } else if (nonogram.getDifficulty() == DifficultyLevel.hard) {

            setBackground(hardColor); // orange

        } else if (nonogram.getDifficulty() == DifficultyLevel.hardest) {

            setBackground(hardestColor); // red

        } else if (nonogram.getDifficulty() == DifficultyLevel.undefined) {

            setBackground(undefinedColor); // gray
        }

        File thumb = new File(MainUI.DEFAULT_THUMBNAILS_PATH, nonogram
                .fetchNonogram().getHash());

        if (thumb.exists()) {

            // Toolkit.getDefaultToolkit().getImage(thumb);
            try {
                this.setIcon(new ImageIcon(thumb.toURI().toURL()));
            } catch (MalformedURLException e) {
                logger.warn("Could not load existing thumbnail!");
            }
            this.setToolTipText(nonogram.getName());
        } else {
            this.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/courseViewEmpty.png")));
        }
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
