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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.freenono.controller.Manager;
import org.freenono.controller.SimpleStatistics;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.game_modes.GameTime;
import org.freenono.provider.NonogramFromSeed;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.Messages;

/**
 * Shows a button with a preview of the nonogram if user has finished it already.
 *
 * @author Christian Wichmann
 */
public class NonogramButton extends JButton {

    private static final long serialVersionUID = 6516455428864083473L;

    private final NonogramProvider nonogram;

    /**
     * Initializes a new button to represent a nonogram.
     *
     * @param n
     *            NonogramProvider providing the nonogram for this button.
     */
    public NonogramButton(final NonogramProvider n) {

        if (n == null) {
            throw new NullPointerException("Nonogram provider should not be null.");
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

        final boolean nonogramSolved = setThumbnailIcon();

        setTooltipInformation(nonogramSolved);
    }

    /**
     * Sets button color according to difficulty of nonogram. Colors are declared by
     * <code>NonogramButton</code> as static fields.
     */
    private void setButtonColor() {

        final DifficultyLevel difficultyOfNonogram = nonogram.getDifficulty();

        if (difficultyOfNonogram == DifficultyLevel.EASIEST) {
            setBackground(NonogramExplorer.EASIEST_COLOR);
        }
        if (difficultyOfNonogram == DifficultyLevel.EASY) {
            setBackground(NonogramExplorer.EASY_COLOR);

        } else if (difficultyOfNonogram == DifficultyLevel.NORMAL) {
            setBackground(NonogramExplorer.NORMAL_COLOR);

        } else if (difficultyOfNonogram == DifficultyLevel.HARD) {
            setBackground(NonogramExplorer.HARD_COLOR);

        } else if (difficultyOfNonogram == DifficultyLevel.HARDEST) {
            setBackground(NonogramExplorer.HARDEST_COLOR);

        } else if (difficultyOfNonogram == DifficultyLevel.UNDEFINED) {
            setBackground(NonogramExplorer.UNDEFINED_COLOR);
        }
    }

    /**
     * Sets thumbnail for this button from file. Thumbnail is only be set, if nonogram was
     * previously ever solved and an image file exists.
     *
     * @return true, if thumbnail exists, nonogram was previously solved
     */
    private boolean setThumbnailIcon() {

        boolean nonogramSolved = false;

        final Path thumb = Paths.get(Manager.DEFAULT_THUMBNAILS_PATH, nonogram.fetchNonogram().getHash());

        if (Files.exists(thumb)) {
            setIcon(new ImageIcon(thumb.toString()));
            nonogramSolved = true;

        } else if (nonogram instanceof NonogramFromSeed && "".equals(nonogram.getName())) {
            setIcon(new ImageIcon(getClass().getResource("/resources/icon/courseViewNewNonogram.png")));

        } else {
            setIcon(new ImageIcon(getClass().getResource("/resources/icon/courseViewEmpty.png")));
        }

        return nonogramSolved;
    }

    /**
     * Sets tooltip information for this button. Nonogram name is only shown when it was previously
     * solved.
     *
     * @param nonogramSolved
     *            If nonogram was previously solved
     */
    private void setTooltipInformation(final boolean nonogramSolved) {

        final StringBuilder sb = new StringBuilder("<html>");

        if (nonogram instanceof NonogramFromSeed && "".equals(nonogram.getName())) {
            sb.append(Messages.getString("NonogramChooserUI.GenerateNewRandomNonogram"));

        } else {
            if (nonogramSolved) {
                sb.append(Messages.getString("NonogramButton.Name"));
                sb.append(nonogram.getName());
                sb.append("<br>");
            }

            // get statistical values that have been stored
            final String played = (String) SimpleStatistics.getInstance().getValue("played_" + nonogram.fetchNonogram().getHash());
            final String won = (String) SimpleStatistics.getInstance().getValue("won_" + nonogram.fetchNonogram().getHash());
            final String[] tooltipText =
                    {Messages.getString("NonogramButton.Author"), nonogram.getAuthor(), "<br>",
                            Messages.getString("NonogramButton.Duration"), new GameTime(nonogram.getDuration()).toString(), "<br>",
                            Messages.getString("NonogramButton.Played"), played, "<br>", Messages.getString("NonogramButton.Solved"), won};

            for (final String string : tooltipText) {
                sb.append(string);
            }
        }

        sb.append("</html>");
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
