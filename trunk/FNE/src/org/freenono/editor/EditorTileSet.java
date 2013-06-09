/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
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
package org.freenono.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.freenono.model.Nonogram;

public class EditorTileSet extends JComponent {

	private static Logger logger = Logger.getLogger(EditorTileSet.class);

	private static final long serialVersionUID = 7925588477738889049L;

	protected Nonogram pattern;

	protected static final int TILESET_WIDTH_DEFAULT = 10;
	protected static final int TILESET_HEIGHT_DEFAULT = 10;

	protected int tileSetWidth = TILESET_WIDTH_DEFAULT;
	protected int tileSetHeight = TILESET_HEIGHT_DEFAULT;
	protected Dimension tileDimension;
	
	protected BoardTile[][] board = null;

	protected boolean isMarked[][];
	protected boolean isMarkedOld[][];
	protected String labels[][];
	protected String labelsOld[][];
	
	protected int activeFieldColumn = 0;
	protected int activeFieldRow = 0;
	
	
	public EditorTileSet(Nonogram pattern, Dimension tileDimension) {
		
		this.pattern = pattern;
		this.tileDimension = tileDimension;
		
		this.tileSetWidth = pattern.width();
		this.tileSetHeight = pattern.height();

		initialize();

		addListeners();
		paintBorders();
		
		solveBoard();
		
	}

	protected void initialize() {
		// get array for tile attributes
		isMarked = new boolean[tileSetHeight][tileSetWidth];
		labels = new String[tileSetHeight][tileSetWidth];
		
		// build gridLayout
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(tileSetHeight);
		gridLayout.setColumns(tileSetWidth);
		this.setLayout(gridLayout);

		// fill grid with tiles
		board = new BoardTile[tileSetHeight][tileSetWidth];
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j] = new BoardTile(tileDimension, j, i);
				board[i][j].setMinimumSize(tileDimension);
				board[i][j].setPreferredSize(tileDimension);
				board[i][j].setColumn(j);
				board[i][j].setRow(i);
				this.add(board[i][j]);
				isMarked[i][j] = false;
			}
		} 
	}
	
	public void handleResize(Dimension tileDimension) {
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				//board[i][j].setMinimumSize(tileDimension);
				//board[i][j].setPreferredSize(tileDimension);
				//board[i][j].setTileSize(tileDimension);
				board[i][j].repaint();
			}
		}
	}

	public boolean[][] getIsMarked() {
		return isMarked;
	}

	public void setIsMarked(boolean[][] isMarked) {
		isMarkedOld = this.isMarked;
		this.isMarked = isMarked;

		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				if (this.isMarked[i][j] != isMarkedOld[i][j]) {
					board[i][j].setMarked(this.isMarked[i][j]);
				}
			}
		}
	}

	public String[][] getLabels() {
		return labels;
	}

	public void setLabels(String labels[][]) {
		labelsOld = this.labels;
		this.labels = labels;

		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				if (this.labels[i][j] != labelsOld[i][j]) {
					board[i][j].setLabel(this.labels[i][j]);
				}
			}
		}
	}

	public int getTileSetWidth() {
		return tileSetWidth;
	}

	public int getTileSetHeight() {
		return tileSetHeight;
	}

	/**
	 * Adding Listeners for key and mouse events on the nonogram board.
	 */
	private void addListeners() {
		// set this Component focusable to capture key events
		this.setFocusable(true);
		this.grabFocus();

		// add Listener for mouse and keyboard usage
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Since the user clicked on us, let us get focus!
				requestFocusInWindow();
			}

			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					handleClick(p);
					changeActiveField();
					break;
				case MouseEvent.BUTTON3:
					break;
				default:
					break;
				}
			}
		});
		this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				handleMouseMovement(p);
			}
		});
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int keyCode = evt.getKeyCode();
				if (keyCode == KeyEvent.VK_LEFT) {
					moveActiveLeft();
				} else if (keyCode == KeyEvent.VK_RIGHT) {
					moveActiveRight();
				} else if (keyCode == KeyEvent.VK_UP) {
					moveActiveUp();
				} else if (keyCode == KeyEvent.VK_DOWN) {
					moveActiveDown();
				} else if (keyCode == KeyEvent.VK_PERIOD) {
					
				} else if (keyCode == KeyEvent.VK_COMMA) {
					changeActiveField();
				} else if (keyCode == KeyEvent.VK_SPACE) {
					changeActiveField();
				} else if (keyCode == KeyEvent.VK_HOME) {
					setActive(0, activeFieldRow); 
				} else if (keyCode == KeyEvent.VK_END) {
					setActive(tileSetWidth-1, activeFieldRow);
				} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
					setActive(activeFieldColumn, tileSetHeight-1); 
				} else if (keyCode == KeyEvent.VK_PAGE_UP) {
					setActive(activeFieldColumn, 0); 
				} else if (keyCode == KeyEvent.VK_F1) {
					
				} else if (keyCode == KeyEvent.VK_F2) {
					
				} else if (keyCode == KeyEvent.VK_F3) {
					
				} else if (keyCode == KeyEvent.VK_F4) {
					
				} else if (keyCode == KeyEvent.VK_F5) {
					
				} else if (keyCode == KeyEvent.VK_H) {
					
				}
			}
		});
	}

	protected void handleMouseMovement(Point p) {
		Component c = this.findComponentAt(p);
		if (c instanceof BoardTile) {
			// deactivate old tile...
			board[activeFieldRow][activeFieldColumn].setActive(false);
			// ...find coordinates for clicked tile by searching the board...
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					if (((BoardTile) c).equals(board[i][j])) {
						activeFieldRow = i;
						activeFieldColumn = j;
					}
				}
			}
			// ...and set it as active tile.
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}

	}

	protected void handleClick(Point p) {
		Component c = this.findComponentAt(p);
		if (c instanceof BoardTile) {
			// find coordinates for clicked tile...
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					if (((BoardTile) c).equals(board[i][j])) {
						activeFieldRow = i;
						activeFieldColumn = j;
					}
				}
			}
		}

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

	public void changeActiveField() {

		if (board[activeFieldRow][activeFieldColumn].isMarked()) {
			board[activeFieldRow][activeFieldColumn].setMarked(false);
			pattern.setFieldValue(false, activeFieldColumn, activeFieldRow);
		} else {
			board[activeFieldRow][activeFieldColumn].setMarked(true);
			pattern.setFieldValue(true, activeFieldColumn, activeFieldRow);
		}

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
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldColumn -= 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}

	}

	public void moveActiveRight() {
		if (activeFieldColumn < tileSetWidth - 1) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldColumn += 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}

	}

	public void moveActiveUp() {
		if (activeFieldRow > 0) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldRow -= 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		
	}

	public void moveActiveDown() {
		if (activeFieldRow < tileSetHeight - 1) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldRow += 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		
	}

	public void clearBoard() {
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setMarked(false);
				board[i][j].setCrossed(false);
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

	public Nonogram getPattern() {
		
		return pattern;
		
	}

	public void setPattern(Nonogram pattern) {
		
		this.pattern = pattern;
		solveBoard();
		
	}

}
