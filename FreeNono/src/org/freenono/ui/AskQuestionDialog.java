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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.freenono.quiz.Question;
import org.freenono.quiz.QuestionMultipleChoice;
import org.freenono.quiz.QuestionMultiplication;
import org.freenono.ui.colormodel.ColorModel;

/**
 * Shows a dialog asking the user a question.
 *
 * @author Christian Wichmann
 */
public class AskQuestionDialog extends JDialog {

    private static final long serialVersionUID = 178708420782508382L;

    private static Logger logger = Logger.getLogger(AskQuestionDialog.class);

    private JTextField answer;
    private Question currentQuestion = null;
    private final ColorModel colorModel;
    private static String givenAnswer = "";

    /**
     * Initializes a dialog to ask user a question.
     *
     * @param owner
     *            Parent of this dialog.
     * @param question
     *            question to be asked
     * @param colorModel
     *            colorModel describing colors to be used for UI elements
     */
    public AskQuestionDialog(final Frame owner, final Question question, final ColorModel colorModel) {

        super(owner);

        this.colorModel = colorModel;

        setTitle(Messages.getString("MainUI.QuestionDialogTitle"));
        setLocationRelativeTo(null);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBackground(colorModel.getTopColor());
        setForeground(colorModel.getBottomColor());
        setUndecorated(true);

        logger.debug("Building AskQuestions Dialog...");

        if (question instanceof QuestionMultipleChoice) {
            currentQuestion = question;
            initializeMultipleChoice();
        } else if (question instanceof QuestionMultiplication) {
            currentQuestion = question;
            initializeMultiplication();
        }

        pack();
        answer.requestFocus();
    }

    /**
     * Initializes the dialog for showing multiplication questions.
     */
    private void initializeMultiplication() {

        final JPanel dialogPanel = new JPanel();
        dialogPanel.setBackground(colorModel.getBaseColor());

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();

        // set layout
        final int inset = 20;
        c.insets = new Insets(inset, inset, inset, inset);
        dialogPanel.setLayout(layout);

        // create question box
        final JLabel buttonQuestion = new JLabel(currentQuestion.getQuestion());
        buttonQuestion.setFocusable(false);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        dialogPanel.add(buttonQuestion, c);

        // create text field for input
        final int answerFieldLength = 30;
        answer = new JTextField(answerFieldLength);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        dialogPanel.add(answer, c);

        // create button for user input
        final JButton giveAnswer = new JButton(Messages.getString("AskQuestionDialog.AnswerButton"));
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        dialogPanel.add(giveAnswer, c);
        giveAnswer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                performClose();
            }
        });

        dialogPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "Close");
        dialogPanel.getActionMap().put("Close", new AbstractAction() {
            private static final long serialVersionUID = 1455344260422807492L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                performClose();
            }
        });

        add(dialogPanel);
    }

    /**
     * Closes this dialog.
     */
    protected final void performClose() {

        givenAnswer = answer.getText();
        setVisible(false);
    }

    /**
     * Initializes this dialog for showing multiple choice questions.
     */
    private void initializeMultipleChoice() {

        final JPanel dialogPanel = new JPanel();
        dialogPanel.setBackground(colorModel.getBaseColor());

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();

        // set layout
        // c.insets = new Insets(10, 0, 10, 0);
        dialogPanel.setLayout(layout);

        // create question box
        final AskQuestionButton buttonQuestion = new AskQuestionButton(currentQuestion.getQuestion());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        dialogPanel.add(buttonQuestion, c);

        // create buttons for possible answers
        final String[] answers = ((QuestionMultipleChoice) currentQuestion).getAnswers();
        final AskQuestionButton button1 = new AskQuestionButton(answers[0]);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        dialogPanel.add(button1, c);
        final ActionListener al1 = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                givenAnswer = "1";
                AskQuestionDialog.this.setVisible(false);
            }
        };
        button1.addActionListener(al1);

        final AskQuestionButton button2 = new AskQuestionButton(answers[1]);
        c.gridx = 1;
        c.gridy = 1;
        dialogPanel.add(button2, c);
        final ActionListener al2 = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                givenAnswer = "2";
                AskQuestionDialog.this.setVisible(false);
            }
        };
        button2.addActionListener(al2);

        final AskQuestionButton button3 = new AskQuestionButton(answers[2]);
        c.gridx = 0;
        c.gridy = 2;
        dialogPanel.add(button3, c);
        final ActionListener al3 = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                givenAnswer = "3";
                AskQuestionDialog.this.setVisible(false);
            }
        };
        button3.addActionListener(al3);

        final AskQuestionButton button4 = new AskQuestionButton(answers[3]);
        c.gridx = 1;
        c.gridy = 2;
        dialogPanel.add(button4, c);
        final ActionListener al4 = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                givenAnswer = "4";
                AskQuestionDialog.this.setVisible(false);
            }
        };
        button4.addActionListener(al4);

        this.add(dialogPanel);
    }

    /**
     * Returns given answer by the user.
     *
     * @return given answer
     */
    public final String getAnswer() {

        return givenAnswer;
    }
}
