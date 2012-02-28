package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.freenono.event.GameEventHelper;
import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.Token;

public class BoardTileSetPlayfield extends BoardTileSet {

	private static final long serialVersionUID = 723055953042228828L;

	private static Logger logger = Logger
			.getLogger(BoardTileSetPlayfield.class);

	public BoardTileSetPlayfield(Game game, Dimension tileDimension) {
		super(game, tileDimension);

		tileSetWidth = game.width();
		tileSetHeight = game.height();
		
		initialize();

		addListeners();
		paintBorders();
		
	}
	
	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
		//eventHelper.addGameListener(gameAdapter);
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
			    //Since the user clicked on us, let us get focus!
			    requestFocusInWindow();
			}
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					handleClick(p);
					occupyActiveField();
					break;
				case MouseEvent.BUTTON3:
					handleClick(p);
					markActiveField();
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
				} else if (keyCode == KeyEvent.VK_ENTER) {
					markActiveField();
				} else if (keyCode == KeyEvent.VK_SPACE) {
					occupyActiveField();
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
		eventHelper.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
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

	public void occupyActiveField() {

		if (!game.canOccupy(activeFieldColumn, activeFieldRow)) {
			// unable to occupy field, maybe it is already occupied
			logger.debug("can not occupy field (" + activeFieldColumn + ", "
					+ activeFieldRow + ")");
			// TODO add user message
			return;
		}
		if (!game.occupy(activeFieldColumn, activeFieldRow)) {
			// failed to occupy field, there maybe some changes
			board[activeFieldRow][activeFieldColumn].setCrossed(true);
			logger.debug("failed move on field (" + activeFieldColumn + ", "
					+ activeFieldRow + ")");
			// TODO add user message
			return;
		} else {
			logger.debug("field (" + activeFieldColumn + ", " + activeFieldRow
					+ ") occupied");
		}

		board[activeFieldRow][activeFieldColumn].setMarked(true);
		eventHelper.fireFieldOccupiedEvent(activeFieldRow, activeFieldColumn);

	}

	public void markActiveField() {

		if (!game.canMark(activeFieldColumn, activeFieldRow)) {
			// unable to mark field, maybe it is already occupied
			logger.debug("can not mark field (" + activeFieldColumn + ", "
					+ activeFieldRow + ")");
			// TODO add user message
			return;
		}
		if (!game.mark(activeFieldColumn, activeFieldRow)) {
			// failed to mark field
			logger.debug("failed to mark field (" + activeFieldColumn + ", "
					+ activeFieldRow + ")");
			// TODO add user message
			return; // return, because there has been no change

		} else {
			if (game.getFieldValue(activeFieldColumn, activeFieldRow) == Token.MARKED) {
				board[activeFieldRow][activeFieldColumn].setCrossed(true);
				eventHelper.fireFieldMarkedEvent(activeFieldRow, activeFieldColumn);
			} else {
				board[activeFieldRow][activeFieldColumn].setCrossed(false);
			}
			logger.debug("field (" + activeFieldColumn + ", " + activeFieldRow
					+ ") marked");
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
		eventHelper.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void moveActiveRight() {
		if (activeFieldColumn < tileSetWidth - 1) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldColumn += 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		eventHelper.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void moveActiveUp() {
		if (activeFieldRow > 0) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldRow -= 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		eventHelper.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void moveActiveDown() {
		if (activeFieldRow < tileSetHeight - 1) {
			board[activeFieldRow][activeFieldColumn].setActive(false);
			activeFieldRow += 1;
			board[activeFieldRow][activeFieldColumn].setActive(true);
		}
		eventHelper.fireActiveFieldChangedEvent(activeFieldRow, activeFieldColumn);
	}

	public void clearField() {
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setMarked(false);
				board[i][j].setCrossed(false);
			}
		}
	}

	public void solveField() {
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setCrossed(false);
				if (game.getPattern().getFieldValue(j, i)) {
					board[i][j].setMarked(true);
				}
				else {
					board[i][j].setMarked(false);
				}
			}
		}
	}

}
