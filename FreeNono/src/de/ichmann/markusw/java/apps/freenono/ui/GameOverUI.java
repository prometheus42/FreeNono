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

import java.awt.FlowLayout;

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
	private JLabel nonogramNameLabel = null;
	private JButton closeButton = null;
	private JLabel messageLabel = null;
	
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
		nonogramNameLabel.setText(game.getPattern().getName());
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle(Messages.getString("GameOverUI.Title"));
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
			messageLabel = new JLabel();
			if (isSolved) {
				messageLabel.setText(Messages.getString("GameOverUI.WinningText"));
			}
			else {
				messageLabel.setText(Messages.getString("GameOverUI.LosingText"));
			}
			messageLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			nonogramNameLabel = new JLabel();
			nonogramNameLabel.setText(game.getPattern().getName());
			nonogramNameLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			nonogramNameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			nonogramNameLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			nonogramNameLabel.setVerticalAlignment(SwingConstants.CENTER);
			nonogramNameLabel.setFont(new Font("Dialog", Font.BOLD, 18));
			nonogramNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jContentPane = new JPanel();
			FlowLayout layout = new FlowLayout();
			layout.setHgap(100);
			layout.setVgap(20);
			jContentPane.setLayout(layout);
			jContentPane.add(messageLabel);
			jContentPane.add(nonogramNameLabel);
			jContentPane.add(boardPreview);
			jContentPane.add(getJButton());
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText(Messages.getString("GameOverUI.CloseButton"));
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
		}
		return closeButton;
	}

}
