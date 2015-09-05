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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Asks user for an input.
 * <p>
 * A displayed dialog will not set itself invisible. Dialog has always be closed through the caller.
 * <p>
 * If "Enter" or "Escape" key are pressed, the dialog searches for buttons that resembles "OK" or
 * "CANCEL" and hits them.
 *
 * @author Christian Wichmann
 */
public class AskUserDialog extends JDialog {

    /*
     * TODO Add icon for dialog (like JDKs JOPtionsPane dialogs.)
     */

    private static final long serialVersionUID = -3773835647632316585L;

    private static final int DEFAULT_ANSWER_FIELD_LENGTH = 25;

    private String question = "";
    private String defaultAnswer = "";
    private String askAgainQuestion = "";

    private JButton okButton = null;
    private JButton cancelButton = null;
    private final List<JButton> extraButtons = new ArrayList<JButton>();
    private boolean okButtonWasClicked = false;

    private final Color foregroundColor;
    private final Color backgroundColor;

    private final JPanel buttonPanel = new JPanel();

    private JTextField askQuestionAnswerField;

    private JCheckBox shouldAskCheckBox;

    /**
     * Initializes a new dialog to ask user for input. Lets the user choose if dialog should be
     * shown again.
     *
     * @param question
     *            question to ask the user
     * @param defaultAnswer
     *            default answer that should be in the text field when showing dialog
     * @param askAgainQuestion
     *            question text for asking if this dialog should be showed again
     * @param foregroundColor
     *            foreground color to be used
     * @param backgroundColor
     *            background color to be used
     */
    public AskUserDialog(final String question, final String defaultAnswer, final String askAgainQuestion, final Color foregroundColor,
            final Color backgroundColor) {

        super();

        // TODO add reference to parent frame/window?

        if (question == null) {
            throw new IllegalArgumentException("Value of parameter question is not valid.");
        }
        if (defaultAnswer == null) {
            throw new IllegalArgumentException("Value of parameter defaultAnswer is not valid.");
        }
        if (askAgainQuestion == null) {
            throw new IllegalArgumentException("Value of parameter askAgainQuestion is not valid.");
        }
        if (foregroundColor == null) {
            throw new IllegalArgumentException("Value of parameter foregroundColor is not valid.");
        }
        if (backgroundColor == null) {
            throw new IllegalArgumentException("Value of parameter backgroundColor is not valid.");
        }

        this.question = question;
        this.defaultAnswer = defaultAnswer;
        this.askAgainQuestion = askAgainQuestion;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;

        assert this.question != null;
        assert this.defaultAnswer != null;
        assert this.askAgainQuestion != null;
        assert this.foregroundColor != null;
        assert this.backgroundColor != null;

        initialize();
    }

    /**
     * Initializes a new dialog to ask user for input.
     *
     * @param question
     *            question to ask the user
     * @param defaultAnswer
     *            default answer that should be in the text field when showing dialog
     * @param foregroundColor
     *            foreground color to be used
     * @param backgroundColor
     *            background color to be used
     */
    public AskUserDialog(final String question, final String defaultAnswer, final Color foregroundColor, final Color backgroundColor) {

        this(question, defaultAnswer, "", foregroundColor, backgroundColor);
    }

    /**
     * Initializes all UI components for this dialog. Check box for repeated displaying of dialog is
     * only shown if <code>askAgainQuestion</code> was given when calling constructor.
     */
    private void initialize() {

        // building dialog
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setTitle("");
        getContentPane().setBackground(backgroundColor);
        getContentPane().setForeground(foregroundColor);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEtchedBorder());

        final GridBagLayout layout = new GridBagLayout();
        getContentPane().setLayout(layout);
        final GridBagConstraints c = new GridBagConstraints();
        final int inset = 10;
        c.insets = new Insets(inset, inset, inset, inset);

        final JLabel askQuestionLabel = new JLabel(question);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(askQuestionLabel, c);

        askQuestionAnswerField = new JTextField(defaultAnswer, DEFAULT_ANSWER_FIELD_LENGTH);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(askQuestionAnswerField, c);

        // adding check box for repeated displaying of dialog only when string
        // was given
        if (!"".equals(askAgainQuestion)) {
            shouldAskCheckBox = new JCheckBox(askAgainQuestion);
            c.gridx = 0;
            c.gridy = 2;
            c.gridheight = 1;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(shouldAskCheckBox, c);
        }

        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setForeground(foregroundColor);
        add(buttonPanel, c);

        // handling key events
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "QuitAskUserDialog");
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "QuitAskUserDialog");
        getRootPane().getActionMap().put("QuitAskUserDialog", new AbstractAction() {
            private static final long serialVersionUID = 4941805525864237285L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                okButton.doClick();
            }
        });
    }

    /**
     * Sets button component as OK button.
     *
     * @param button
     *            button component as OK button, <b>not null</b>
     */
    public final void setOkButton(final JButton button) {

        if (button == null) {
            throw new IllegalArgumentException("Value of parameter button is not valid.");
        }
        okButton = button;
        buttonPanel.add(okButton);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                okButtonWasClicked = true;
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Sets button component as CANCEL button.
     *
     * @param button
     *            button component as CANCEL button, <b>not null</b>
     */
    public final void setCancelButton(final JButton button) {

        if (button == null) {
            throw new IllegalArgumentException("Value of parameter button is not valid.");
        }
        cancelButton = button;
        buttonPanel.add(cancelButton);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Adds one more button to this dialog. Buttons can <i>never</i> be removed!
     *
     * @param button
     *            button component to be added to this dialog, <b>not null</b>
     */
    public final void addExtraButton(final JButton button) {

        if (button == null) {
            throw new IllegalArgumentException("Value of parameter button is not valid.");
        }
        extraButtons.add(button);
        buttonPanel.add(button);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Returns input given into the text field by user.
     *
     * @return input by the user
     */
    public final String getUserInput() {

        return askQuestionAnswerField.getText();
    }

    /**
     * Sets default value for the text field.
     *
     * @param value
     *            value to set text field to, <b>not null</b>
     */
    public final void setDefaultValue(final String value) {

        if (value == null) {
            throw new IllegalArgumentException("Value of parameter value is not valid.");
        }
        askQuestionAnswerField.setText(value);
    }

    /**
     * Returns whether this dialog should be displayed again.
     *
     * @return true, if dialog should pop up again
     */
    public final boolean isShouldAskAgain() {

        return shouldAskCheckBox.isSelected();
    }

    /**
     * Returns whether the OK button was clicked by user.
     *
     * @return true, if OK button was clicked
     */
    public final boolean okButtonWasClicked() {

        return okButtonWasClicked;
    }
}
