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
package org.freenono.ui.common;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Dialog super class that should be used for all dialogs in FreeNono to display
 * a consistent look-and-feel. Also a location-policy is implemented so that all
 * dialogs are displayed on the same screen in a multi-screen-setup.
 * 
 * @author Christian Wichmann
 */
public class FreeNonoDialog extends JDialog {

    private static final long serialVersionUID = -1200594163584925416L;

    private Color backgroundColor;
    private Color foregroundColor;

    /**
     * Initialize a dialog that is consistently designed for FreeNono
     * application.
     * 
     * @param owner
     *            parent components of this dialog
     * @param foregroundColor
     *            foreground color to be used
     * @param backgroundColor
     *            background color to be used
     */
    public FreeNonoDialog(final Frame owner, final Color foregroundColor,
            final Color backgroundColor) {

        super(owner);

        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;

        decorateDialog();
    }

    /**
     * Decorate this dialog for consistent look-and-feel.
     */
    private void decorateDialog() {

        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        getContentPane().setBackground(backgroundColor);
        getContentPane().setForeground(foregroundColor);
        ((JPanel) getContentPane()).setBorder(BorderFactory
                .createEtchedBorder());
    }

    /**
     * Finds screen on which owner of this dialog is shown.
     * 
     * @return bounds of main screen or null if no screen could be found
     */
    private Rectangle findMainScreen() {

        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (GraphicsDevice screen : gs) {
            Point centerPoint = new Point((int) getOwner().getBounds()
                    .getCenterX(), (int) getOwner().getBounds().getCenterY());
            if (screen.getDefaultConfiguration().getBounds()
                    .contains(centerPoint)) {
                return screen.getDefaultConfiguration().getBounds();
            }
        }
        return null;
    }

    /**
     * Moves a window (e.g. a dialog or a frame) to the main screen. Main screen
     * is defined as the screen where the main window is placed.
     * 
     * @param dx
     *            distance that window should be moved in horizontal direction
     * @param dy
     *            distance that window should be moved in vertical direction
     */
    public final void centerWindowOnMainScreen(final int dx, final int dy) {

        Rectangle mainScreenBounds = findMainScreen();

        if (mainScreenBounds != null) {
            int newX = mainScreenBounds.x + mainScreenBounds.width / 2;
            int newY = mainScreenBounds.y + mainScreenBounds.height / 2;

            newX -= getWidth() / 2;
            newY -= getHeight() / 2;

            newX += dx;
            newY += dy;

            setLocation(newX, newY);
        }
    }

    @Override
    public final void pack() {

        super.pack();

        // move to correct screen
        centerWindowOnMainScreen(0, 0);
    }
}
