package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.ichmann.markusw.java.apps.freenono.model.Game;

public class BoardTileSetPlayfield extends BoardTileSet {

	private static final long serialVersionUID = 723055953042228828L;

	// public BoardTileSetCaption(Nonogram n) {
	public BoardTileSetPlayfield(Game game, int tileSetWidth,
			int tileSetHeight, Dimension tileSetDimension,
			Dimension tileDimension) {
		super(game, tileSetWidth, tileSetHeight, tileSetDimension,
				tileDimension);

		addListeners();
		paintBorders();
	}

	/**
	 * Adding Listeners for key and mouse events on the nonogram board.
	 */
	private void addListeners() {
		// set this Component focusable to capture key events
		this.setFocusable(true);

		// add Listener for mouse and keyboard usage
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					handleLeftClick(p);
					break;
				case MouseEvent.BUTTON3:
					handleRightClick(p);
					break;
				default:
					break;
				}
			}
		});
		this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				// System.out.println("Mouse moved");
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
				} else if (keyCode == KeyEvent.VK_ENTER) {
					crossActiveField();
				} else if (keyCode == KeyEvent.VK_SPACE) {
					markActiveField();
				}
			}
		});
	}

	protected void handleMouseMovement(Point p) {
		Component c = this.findComponentAt(p);
		if (c instanceof BoardTile) {
			// deactivate old tile...
			board[activeFieldRow][activeFieldColumn].setActive(false);
			// ...find coordinates for clicked tile...
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
		geh.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	protected void handleLeftClick(Point p) {
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
		if (!board[activeFieldRow][activeFieldColumn].isCrossed()) {
			// ...and mark tile
			board[activeFieldRow][activeFieldColumn].setMarked(true);
			geh.fireFieldOccupiedEvent(activeFieldRow, activeFieldColumn);
		}
	}

	protected void handleRightClick(Point p) {
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
		if (!board[activeFieldRow][activeFieldColumn].isMarked()) {
			// ...and mark tile
			board[activeFieldRow][activeFieldColumn].setCrossed(true);
			geh.fireFieldMarkedEvent(activeFieldRow, activeFieldColumn);
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

	public void markActiveField() {
		if (!board[activeFieldRow][activeFieldColumn].isCrossed()) {
			board[activeFieldRow][activeFieldColumn].setMarked(true);
			geh.fireFieldOccupiedEvent(activeFieldRow, activeFieldColumn);
		}
	}

	public void crossActiveField() {
		if (!board[activeFieldRow][activeFieldColumn].isMarked()) {
			board[activeFieldRow][activeFieldColumn].setCrossed(true);
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
		geh.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void moveActiveRight() {
		if (activeFieldColumn < tileSetWidth - 1) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldColumn += 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		geh.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void moveActiveUp() {
		if (activeFieldRow > 0) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldRow -= 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		geh.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void moveActiveDown() {
		if (activeFieldRow < tileSetHeight - 1) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldRow += 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		geh.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

}
