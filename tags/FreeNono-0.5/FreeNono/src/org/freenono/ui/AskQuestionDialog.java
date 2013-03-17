/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Christian Wichmann
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
import java.awt.Label;
import java.awt.TextField;
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
import org.freenono.controller.Settings;
import org.freenono.quiz.Question;
import org.freenono.quiz.QuestionMultipleChoice;
import org.freenono.quiz.QuestionMultiplication;


public class AskQuestionDialog extends JDialog {

	private static final long serialVersionUID = 178708420782508382L;

	private static Logger logger = Logger.getLogger(AskQuestionDialog.class);

	private JTextField answer;
	private Question currentQuestion = null;
	private Settings settings;
	protected static String givenAnswer = "42";

	
	public AskQuestionDialog(Question question, Settings settings) 	{
		
		this.settings = settings;
		
		setTitle(Messages.getString("MainUI.QuestionDialogTitle"));
		setLocationRelativeTo(null);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBackground(settings.getColorModel().getTopColor());
		setForeground(settings.getColorModel().getBottomColor());
		setUndecorated(true);
		
		// initialize dialog
		logger.debug("Building AskQuestions Dialog...");
		
		if (question instanceof QuestionMultipleChoice)
		{
			this.currentQuestion = (QuestionMultipleChoice) question;	
			initializeMultipleChoice();
		}
		else if (question instanceof QuestionMultiplication)
		{
			this.currentQuestion = (QuestionMultiplication) question;
			initializeMultiplication();
		}
		
		pack();
		
		answer.requestFocus();
		
		setVisible(true);
	}


	private void initializeMultiplication() {
		
		JPanel dialogPanel = new JPanel();
		dialogPanel.setBackground(settings.getColorModel().getBaseColor());
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		// set layout
		c.insets = new Insets(20, 20, 20, 20);
		dialogPanel.setLayout(layout);

		// create question box
		JLabel buttonQuestion = new JLabel(
				currentQuestion.getQuestion());
		buttonQuestion.setFocusable(false);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		dialogPanel.add(buttonQuestion, c);

		// create text field for input
		answer = new JTextField(30);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		dialogPanel.add(answer, c);

		// create button for user input
		JButton giveAnswer = new JButton(
				Messages.getString("AskQuestionDialog.AnswerButton"));
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		dialogPanel.add(giveAnswer, c);
		giveAnswer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performClose();
			}
		});
		
		dialogPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke("ENTER"), "Close");
		dialogPanel.getActionMap().put("Close", new AbstractAction() {
			private static final long serialVersionUID = 1455344260422807492L;
			
			public void actionPerformed(ActionEvent e) {
				performClose();
			}
		});
		
		add(dialogPanel);
	}


	protected void performClose() {
		
		givenAnswer = answer.getText();
		this.setVisible(false);
	}


	private void initializeMultipleChoice() {
		
		JPanel dialogPanel = new JPanel();
		dialogPanel.setBackground(settings.getColorModel().getBaseColor());
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		// set layout
		//c.insets = new Insets(10, 0, 10, 0);
		dialogPanel.setLayout(layout);

		// create question box
		AskQuestionButton buttonQuestion = new AskQuestionButton(
				currentQuestion.getQuestion());
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		dialogPanel.add(buttonQuestion, c);

		// create buttons for possible answers
		String answers[] = ((QuestionMultipleChoice) currentQuestion).getAnswers();
		AskQuestionButton button1 = new AskQuestionButton(answers[0]);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		dialogPanel.add(button1, c);
		ActionListener al1 = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				givenAnswer = "1";
				AskQuestionDialog.this.setVisible(false);
			}
		};
		button1.addActionListener(al1);
		
		AskQuestionButton button2 = new AskQuestionButton(answers[1]);
		c.gridx = 1;
		c.gridy = 1;
		dialogPanel.add(button2, c);
		ActionListener al2 = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				givenAnswer = "2";
				AskQuestionDialog.this.setVisible(false);
			}
		};
		button2.addActionListener(al2);
		
		AskQuestionButton button3 = new AskQuestionButton(answers[2]);
		c.gridx = 0;
		c.gridy = 2;
		dialogPanel.add(button3, c);
		ActionListener al3 = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				givenAnswer = "3";
				AskQuestionDialog.this.setVisible(false);
			}
		};
		button3.addActionListener(al3);
		
		AskQuestionButton button4 = new AskQuestionButton(answers[3]);
		c.gridx = 1;
		c.gridy = 2;
		dialogPanel.add(button4, c);
		ActionListener al4 = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				givenAnswer = "4";
				AskQuestionDialog.this.setVisible(false);
			}
		};
		button4.addActionListener(al4);
		
		this.add(dialogPanel);
	}


	public String getAnswer() {
		
		return givenAnswer;
	}
}
