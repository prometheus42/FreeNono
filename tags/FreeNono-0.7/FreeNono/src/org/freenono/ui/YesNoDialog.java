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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Shows a dialog to ask user a simple yes/no question like if the program
 * should really be exited or restarted.
 * 
 * @author Christian Wichmann
 */
public class YesNoDialog extends JDialog {

    /*
     * TODO Move this class to lib_ui and create a resource file to hold "yes"
     * and "no" strings.
     */

    private static final long serialVersionUID = -3791896670433960168L;

    private boolean exit = false;
    private static final int BORDER_WIDTH = 20;

    private String dialogTitle;
    private Color backgroundColor;
    private Color forgroundColor;
    private String dialogText;

    /**
     * Initializes a dialog to ask user if program should really be exited.
     * 
     * @param dialogTitle
     *            title of the dialog
     * @param backgroundColor
     *            background color for this dialog
     * @param forgroundColor
     *            foreground color for this dialog
     * @param dialogText
     *            text that should be show as question for the user
     */
    public YesNoDialog(final String dialogTitle, final Color backgroundColor,
            final Color forgroundColor, final String dialogText) {

        this.dialogTitle = dialogTitle;
        this.backgroundColor = backgroundColor;
        this.forgroundColor = forgroundColor;
        this.dialogText = dialogText;

        initialize();
    }

    /**
     * Initializes a dialog to ask user if program should really be exited.
     */
    private void initialize() {

        setTitle(dialogTitle);
        getContentPane().setBackground(backgroundColor);
        getContentPane().setForeground(forgroundColor);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        add(buildContentPane());

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Builds a panel including the localized question for the user.
     * 
     * @return panel with text
     */
    private JPanel buildContentPane() {

        JPanel content = new JPanel();

        content.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEtchedBorder(), BorderFactory.createEmptyBorder(
                BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH)));
        content.setOpaque(false);

        content.add(new JLabel("<html>" + dialogText + "</html>"));
        content.add(buildButtonPane());

        return content;
    }

    /**
     * This method builds the panel which includes two buttons to chose whether
     * to exit program or not. By clicking a button the field <code>exit</code>
     * will be set.
     * 
     * @return button panel
     */
    private JPanel buildButtonPane() {

        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, BORDER_WIDTH,
                0, 0));

        JButton yesButton = new JButton(Messages.getString("Yes"));
        yesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                exit = true;
                dispose();
            }
        });
        yesButton.setActionCommand("Yes");
        buttonPane.add(yesButton);

        JButton noButton = new JButton(Messages.getString("No"));
        noButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                exit = false;
                dispose();
            }
        });
        noButton.setActionCommand("No");
        buttonPane.add(noButton);

        return buttonPane;
    }

    /**
     * Returns whether the user chose yes.
     * 
     * @return true, if user chose yes
     */
    public final boolean userChoseYes() {

        return exit;
    }
}
