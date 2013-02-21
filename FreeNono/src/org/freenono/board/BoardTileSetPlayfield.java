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
package org.freenono.board;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Random;

import org.apache.log4j.Logger;
import org.freenono.controller.ControlSettings.Control;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.QuizEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.GameState;
import org.freenono.model.Nonogram;
import org.freenono.model.Token;

public class BoardTileSetPlayfield extends BoardTileSet {

	private static final long serialVersionUID = 723055953042228828L;

	private boolean gameRunning = false;
	
	private Token[][] oldBoard = null;

	private static Logger logger = Logger
			.getLogger(BoardTileSetPlayfield.class);

	private GameAdapter gameAdapter = new GameAdapter() {
		
		public void OptionsChanged(ProgramControlEvent e) {
			
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					
					board[i][j].setColorModel(settings.getColorModel());
					board[i][j].repaint();
				}
			}
		}
		
		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				board[activeFieldRow][activeFieldColumn].releaseMouseButton();
				board[activeFieldRow][activeFieldColumn].setActive(false);
				gameRunning = false;
				break;

			case solved:
				board[activeFieldRow][activeFieldColumn].releaseMouseButton();
				board[activeFieldRow][activeFieldColumn].setActive(false);
				gameRunning = false;
				solveBoard();
				break;
				
			case userStop:
				break;

			case paused:
				// clear board during pause
				if (settings.getHidePlayfield()) {
					
					clearBoard();
				}
				break;

			case running:
				gameRunning = true;
				if (e.getOldState() == GameState.paused) {
					
					// restore board after pause
					if (settings.getHidePlayfield()) {
						restoreBoard();
					}
				}
				break;

			default:
				break;
			}

		}

		public void FieldOccupied(FieldControlEvent e) {
			if (gameRunning)
				board[e.getFieldRow()][e.getFieldColumn()].setMarked(true);
		}

		public void FieldMarked(FieldControlEvent e) {
			if (gameRunning)
				board[e.getFieldRow()][e.getFieldColumn()].setCrossed(true);
		}

		public void FieldUnmarked(FieldControlEvent e) {
			if (gameRunning)
				board[e.getFieldRow()][e.getFieldColumn()].setCrossed(false);
		}
		
		public void ChangeActiveField(FieldControlEvent e) {
			if (gameRunning)
			{
				board[activeFieldRow][activeFieldColumn].setActive(false);
				activeFieldColumn = e.getFieldColumn();
				activeFieldRow = e.getFieldRow();
				board[activeFieldRow][activeFieldColumn].setActive(true);
			}
		}
		
		public void AskQuestion(QuizEvent e) {
			
			// Resets internal variables of currently active board tile to
			// prevent bug where mouse button stays 'active' after user is
			// asked a question (in GameModeQuestions).
			board[activeFieldRow][activeFieldColumn].releaseMouseButton();
		}

	};

	public BoardTileSetPlayfield(GameEventHelper eventHelper, Nonogram pattern,
			Settings settings, Dimension tileDimension) {
		
		super(eventHelper, pattern, settings, tileDimension);
		
		eventHelper.addGameListener(gameAdapter);
		
		tileSetWidth = pattern.width();
		tileSetHeight = pattern.height();
		oldBoard = new Token[tileSetHeight][tileSetWidth];

		initialize();
		
		paintBorders();
		
		addListeners();
		
		// set all board tiles interactive to activate their mouse listener
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setInteractive(true);
			}
		}
		
		board[0][0].setActive(true);
	}
	
	public void removeEventHelper() {
		
		if (eventHelper != null) {
			
			eventHelper.removeGameListener(gameAdapter);
			eventHelper = null;
		}
	}

	
	/**
	 * Adding Listeners for key and mouse events on the nonogram board.
	 */
	private void addListeners() {
		
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			
			public void keyPressed(KeyEvent evt) {
				
				int keyCode = evt.getKeyCode();
				
				if (keyCode == settings.getKeyCodeForControl(Control.moveLeft)) {
					moveActiveLeft();
				} else if (keyCode == settings.getKeyCodeForControl(Control.moveRight)) {
					moveActiveRight();
				} else if (keyCode == settings.getKeyCodeForControl(Control.moveUp)) {
					moveActiveUp();
				} else if (keyCode == settings.getKeyCodeForControl(Control.moveDown)) {
					moveActiveDown();
				} else if (keyCode == settings.getKeyCodeForControl(Control.markField)) {
					markActiveField();
				} else if (keyCode == settings.getKeyCodeForControl(Control.occupyField)) {
					occupyActiveField();
				} else if (keyCode == KeyEvent.VK_H) {
					giveHint();
				}
			}
		});
	}


	private void paintBorders() {
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setDrawBorderWest(true);
				if ((j + 1) % 5 == 0 || (j + 1) == tileSetWidth) {
					board[i][j].setDrawBorderEast(true);
				}
				board[i][j].setDrawBorderNorth(true);
				if ((i + 1) % 5 == 0 || (i + 1) == tileSetHeight) {
					board[i][j].setDrawBorderSouth(true);
				}
			}
		}
	}

	public void occupyActiveField() {

		eventHelper.fireOccupyFieldEvent(new FieldControlEvent(this,
				activeFieldColumn, activeFieldRow));

	}

	public void markActiveField() {

		eventHelper.fireMarkFieldEvent(new FieldControlEvent(this,
				activeFieldColumn, activeFieldRow));

	}

	public void setActive(int column, int row) {
		if (column >= 0 && column < tileSetWidth && row >= 0
				&& row < tileSetHeight) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldColumn = column;
			activeFieldRow = row;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
	}

	public void moveActiveLeft() {
		if (activeFieldColumn > 0) {
			//board[activeFieldRow][activeFieldColumn].setActive(false);
			//activeFieldColumn -= 1;
			eventHelper.fireChangeActiveFieldEvent(new FieldControlEvent(this,
					activeFieldColumn - 1, activeFieldRow));
			//board[activeFieldRow][activeFieldColumn].setActive(true);
		}
	}

	public void moveActiveRight() {
		if (activeFieldColumn < tileSetWidth - 1) {
			//board[activeFieldRow][activeFieldColumn].setActive(false);
			//activeFieldColumn += 1;
			eventHelper.fireChangeActiveFieldEvent(new FieldControlEvent(this,
					activeFieldColumn + 1, activeFieldRow));
			//board[activeFieldRow][activeFieldColumn].setActive(true);
		}
	}

	public void moveActiveUp() {
		if (activeFieldRow > 0) {
			//board[activeFieldRow][activeFieldColumn].setActive(false);
			//activeFieldRow -= 1;
			eventHelper.fireChangeActiveFieldEvent(new FieldControlEvent(this,
					activeFieldColumn, activeFieldRow - 1));
			//board[activeFieldRow][activeFieldColumn].setActive(true);
		}
	}

	public void moveActiveDown() {
		if (activeFieldRow < tileSetHeight - 1) {
			//board[activeFieldRow][activeFieldColumn].setActive(false);
			//activeFieldRow += 1;
			eventHelper.fireChangeActiveFieldEvent(new FieldControlEvent(this,
					activeFieldColumn, activeFieldRow + 1));
			//board[activeFieldRow][activeFieldColumn].setActive(true);
		}
	}

	
	public void clearBoard() {
		
		for (int i = 0; i < tileSetHeight; i++) {
			
			for (int j = 0; j < tileSetWidth; j++) {
				
				oldBoard[i][j] = Token.FREE;
				
				if (board[i][j].isMarked())
					oldBoard[i][j] = Token.OCCUPIED;
				
				if (board[i][j].isCrossed())
					oldBoard[i][j] = Token.MARKED;
				
				board[i][j].setMarked(false);
				board[i][j].setCrossed(false);
			}
		}
	}

	public void restoreBoard() {
		
		for (int i = 0; i < tileSetHeight; i++) {
			
			for (int j = 0; j < tileSetWidth; j++) {
				
				if (oldBoard[i][j] == Token.MARKED)
					board[i][j].setCrossed(true);
				
				if (oldBoard[i][j] == Token.OCCUPIED)
					board[i][j].setMarked(true);
			}
		}
	}

	public void solveBoard() {
		
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setCrossed(false);
				if (pattern.getFieldValue(j, i)) {
					board[i][j].setMarked(true);
				} else {
					board[i][j].setMarked(false);
				}
			}
		}
	}

	
	public void giveHint() {

		Random rnd = new Random();
		int y = rnd.nextInt(tileSetHeight);
		int x = rnd.nextInt(tileSetWidth);

		for (int i = 0; i < tileSetHeight; i++) {
			setActive(x, i);
			if (pattern.getFieldValue(x, i)) {
				occupyActiveField();
			} else {
				if (!(board[i][x].isCrossed()))
					markActiveField();
			}
		}
		for (int i = 0; i < tileSetWidth; i++) {
			setActive(i, y);
			if (pattern.getFieldValue(i, y)) {
				occupyActiveField();
			} else {
				if (!(board[y][i].isCrossed()))
					markActiveField();
			}
		}
	}

}
