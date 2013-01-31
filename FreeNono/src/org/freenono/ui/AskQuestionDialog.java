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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.freenono.quiz.Question;
import org.freenono.quiz.QuestionMultipleChoice;
import org.freenono.quiz.QuestionMultiplication;


public class AskQuestionDialog extends JDialog {

	private static final long serialVersionUID = 178708420782508382L;

	private static Logger logger = Logger.getLogger(AskQuestionDialog.class);

	private Question currentQuestion = null;
	private String givenAnswer = "42";

	
	public AskQuestionDialog(Question question)
	{
		this.setTitle(Messages.getString("MainUI.QuestionDialogTitle"));
		this.setSize(400, 250);
		this.setLocationRelativeTo(null);
		this.setBackground(Color.getColor("#03267C"));
		this.setForeground(Color.getColor("#56B233"));
		
		// initialize dialog
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
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}


	private void initializeMultiplication() {
		
		// TODO create dialog for multiplication questions???
	}


	private void initializeMultipleChoice() {
		
		logger.debug("Building AskQuestions Dialog...");
		
		JPanel dialogPanel = new JPanel();
		dialogPanel.setBackground(Color.getColor("#03267C"));
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		// set layout
		//c.insets = new Insets(10, 0, 10, 0);
		dialogPanel.setLayout(layout);

		// create question box
		JButton buttonQuestion = new JButton(currentQuestion.getQuestion());
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		dialogPanel.add(buttonQuestion, c);

		// create buttons for possible answers
		String answers[] = ((QuestionMultipleChoice) currentQuestion).getAnswers();
		JButton button1 = new JButton(answers[0]);
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
		
		JButton button2 = new JButton(answers[1]);
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
		
		JButton button3 = new JButton(answers[2]);
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
		
		JButton button4 = new JButton(answers[3]);
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
