/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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
package de.ichmann.markusw.java.apps.freenono.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.ichmann.markusw.java.apps.freenono.board.BoardPreview;
import de.ichmann.markusw.java.apps.freenono.model.Game;

import java.awt.event.KeyEvent;
import java.awt.Font;
import javax.swing.JButton;

public class GameOverUI extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private Game game = null;
	private boolean isSolved = false;
	
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JButton jButton = null;
	private JLabel jLabel1 = null;
	
	private BoardPreview boardPreview = null;
	
	/**
	 * This is the default constructor
	 */
	public GameOverUI() {
		super();
		initialize();
	}
	
	public GameOverUI(Game game, BoardPreview boardPreview, boolean isSolved) {
		super();
		
		this.game = game;
		this.boardPreview = boardPreview;
		this.isSolved = isSolved;
		
		initialize();
		jLabel.setText(game.getPattern().getName());
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("Game Over");
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setAlwaysOnTop(true);
		this.setVisible(false);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			if (isSolved) {
				jLabel1.setText("Congratulation, you've solved this...");
			}
			else {
				jLabel1.setText("Sorry, you've lost...");
			}
			jLabel1.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel = new JLabel();
			jLabel.setText("<NonogramName>");
			jLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			jLabel.setVerticalAlignment(SwingConstants.CENTER);
			jLabel.setFont(new Font("Dialog", Font.BOLD, 18));
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(jLabel, BorderLayout.CENTER);
			jContentPane.add(getJButton(), BorderLayout.SOUTH);
			jContentPane.add(jLabel1, BorderLayout.NORTH);
			jContentPane.add(boardPreview, BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Close");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
		}
		return jButton;
	}

}
