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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.freenono.controller.Control;
import org.freenono.controller.Settings;

/**
 * Shows a dialog to enter a new key for given control.
 * 
 * @author Martin Wichmann, Christian Wichmann
 */
public class NewKeyAssignmentDialog extends JDialog {

    private static final long serialVersionUID = 8423411694004619728L;

    private static Logger logger = Logger
            .getLogger(NewKeyAssignmentDialog.class);

    private JLabel warningText;

    private int newKeyCode;

    private Settings settings;
    private Control control;

    /**
     * Initializes a dialog to assign a key to a control.
     * 
     * @param settings
     *            ControlSettings object containing old key code for control.
     * @param control
     *            Control for which to set new key.
     */
    public NewKeyAssignmentDialog(final Settings settings, final Control control) {

        this.settings = settings;
        this.control = control;

        newKeyCode = settings.getKeyCodeForControl(control);

        initialize();

        addListener();

        setVisible(true);
    }

    /**
     * Initializes the layout and components for this dialog.
     */
    private void initialize() {

        final int inset = 20;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle(Messages.getString("OptionsUI.UserKeyPromptTitle"));

        // set layout manager
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(inset, inset, inset, inset);

        // add labels for user message and warning
        JLabel hint = new JLabel(Messages.getString("OptionsUI.UserKeyPrompt"),
                JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(hint, c);

        warningText = new JLabel("", JLabel.CENTER);
        warningText.setForeground(Color.RED);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(warningText, c);

        pack();
    }

    /**
     * Adds key listener to get key code for pressed key.
     */
    private void addListener() {

        addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(final KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

                    dispose();
                    return;
                }

                if (!keycodeAlreadyUsed(e.getKeyCode())
                        && !isReservedKey(e.getKeyCode())) {

                    newKeyCode = e.getKeyCode();
                    logger.debug("New key code for control " + control + ": "
                            + e.getKeyCode());

                    dispose();
                }

                warningText.setText(Messages
                        .getString("OptionsUI.WarningAssignKey"));
            }

            @Override
            public void keyTyped(final KeyEvent e) {
            }

            @Override
            public void keyPressed(final KeyEvent e) {
            }
        });
    }

    /**
     * Checks whether key code is already in use.
     * 
     * @param enteredKeyCode
     *            Pressed key that should be assigned to Control.
     * @return True, if key code is already used.
     */
    private boolean keycodeAlreadyUsed(final int enteredKeyCode) {

        if (enteredKeyCode != newKeyCode) {

            if (enteredKeyCode == settings
                    .getKeyCodeForControl(Control.MOVE_LEFT)) {
                return true;
            }
            if (enteredKeyCode == settings
                    .getKeyCodeForControl(Control.MOVE_RIGHT)) {
                return true;
            }
            if (enteredKeyCode == settings
                    .getKeyCodeForControl(Control.MOVE_UP)) {
                return true;
            }
            if (enteredKeyCode == settings
                    .getKeyCodeForControl(Control.MOVE_DOWN)) {
                return true;
            }
            if (enteredKeyCode == settings
                    .getKeyCodeForControl(Control.MARK_FIELD)) {
                return true;
            }
            if (enteredKeyCode == settings
                    .getKeyCodeForControl(Control.OCCUPY_FIELD)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether key code is a reserved code that should not be used.
     * 
     * @param enteredKeyCode
     *            pressed key that should be assigned to Control.
     * @return true, if key code is reserved and should not be used.
     */
    private boolean isReservedKey(final int enteredKeyCode) {

        return enteredKeyCode == KeyEvent.VK_F1
                || enteredKeyCode == KeyEvent.VK_F2
                || enteredKeyCode == KeyEvent.VK_F3
                || enteredKeyCode == KeyEvent.VK_F4
                || enteredKeyCode == KeyEvent.VK_F5
                || enteredKeyCode == KeyEvent.VK_F6
                || enteredKeyCode == KeyEvent.VK_F7
                || enteredKeyCode == KeyEvent.VK_F8
                || enteredKeyCode == KeyEvent.VK_F9
                || enteredKeyCode == KeyEvent.VK_F10
                || enteredKeyCode == KeyEvent.VK_F11
                || enteredKeyCode == KeyEvent.VK_F12;
    }

    /**
     * Gets new assigned key code for Control.
     * 
     * @return New key code for Control.
     */
    public final int getNewKeyCode() {

        return newKeyCode;
    }
}
