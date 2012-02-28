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
package de.ichmann.markusw.java.apps.nonogram.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.nonogram.event.GameListener;
import de.ichmann.markusw.java.apps.nonogram.model.Game;
import de.ichmann.markusw.java.apps.nonogram.model.GameState;

public class BoardControl extends JComponent {

	private static final int BOARD_OCCUPIED_LEFT_ADJ = 2;
	private static final int BOARD_OCCUPIED_TOP_ADJ = 2;
	private static final int BOARD_OCCUPIED_WIDTH_ADJ = 3;
	private static final int BOARD_OCCUPIED_BOTTOM_ADJ = 3;

	private static final int BOARD_MARK_LEFT_ADJ = 4;
	private static final int BOARD_MARK_RIGHT_ADJ = 4;
	private static final int BOARD_MARK_TOP_ADJ = 4;
	private static final int BOARD_MARK_BOTTOM_ADJ = 4;

	private static final Color LARGE_LINE_COLOR = Color.BLACK; // @jve:decl-index=0:
	private static final Color SMALL_LINE_COLOR = Color.GRAY;
	private static final Color NUMBER_COLOR = Color.BLACK; // @jve:decl-index=0:
	private static final Color BOARD_OCCUPIED_COLOR = Color.BLACK; // @jve:decl-index=0:
	private static final Color BOARD_MARK_COLOR = Color.BLACK;
	private static final Color TIME_COLOR = Color.BLACK;

	private static final long serialVersionUID = 9008679908673187849L;

	private static Logger logger = Logger.getLogger(BoardControl.class);

	private BoardData data;
	private GameListener gameListener = new GameListener() {

		@Override
		public void Timer() {
		}

		@Override
		public void StateChanged(GameState oldState, GameState newState) {
		}

		@Override
		public void FieldOccupied(int x, int y) {
		}

		@Override
		public void FieldMarked(int x, int y) {
		}
	};

	/**
	 * This is the default constructor
	 */
	public BoardControl() {
		super();
		initialize();
		data = new BoardData();
		data.refresh(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 400);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(400, 400));
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					occupyField(e.getX(), e.getY());
					break;
				case MouseEvent.BUTTON3:
					markField(e.getX(), e.getY());
					break;
				default:
					break;
				}
			}
		});
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				handleResize();
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		if (data != null && data.boardArea != null) {
			int width = data.boardArea.x + data.boardArea.width
					+ data.getRightBorder();
			int height = data.boardArea.y + data.boardArea.height
					+ data.getBottomBorder();
			return new Dimension(width, height);
		}
		return super.getPreferredSize();
	}

	protected void occupyField(int x, int y) {

		if (logger.isDebugEnabled())
			logger.debug("occupyField(" + x + ", " + y + ")");		

		// TODO check implementation
		if (data == null) {
			// called during constructor, ignore
			if (logger.isInfoEnabled())
				logger.info("data is still null, occupyField was called during constructor");
			// TODO add user message here
			return;
		}
		if (data.boardArea == null) {
			// data not yet filled
			// TODO add log or user message here
			return;
		}
		if (data.boardArea.contains(x, y)) {

			int realX = (int) ((x - data.boardArea.x) / data.getFieldWidth());
			int realY = (int) ((y - data.boardArea.y) / data.getFieldHeight());

			// TODO add log message here
			//"left click inside of board aread at field (" + x+ ", " + y + ")"

			if (!data.getGame().canOccupy(realX, realY)) {
				// unable to occupy field, maybe it is already occupied
				
				// TODO add log message here
				// logger.info("can not occupy field (" + x + ", " + y + ")");
				
				// TODO add user message
				return;
			}
			if (!data.getGame().occupy(realX, realY)) {
				// failed to occupy field, there maybe some changes
				
				// TODO add log message here
				// logger.info("failed move on field (" + x + ", " + y + ")");
				
				// TODO add user message
			}
		} else {
			// TODO add log message here
			// logger.finer("left click outside of board aread");
		}

		data.refresh(false);
		this.repaint();
	}

	protected void markField(int x, int y) {
		// TODO check implementation
		if (data == null) {
			// called during constructor, ignore
			// TODO add log or user message here
			return;
		}
		if (data.boardArea == null) {
			// data not yet filled
			// TODO add log or user message here
			return;
		}
		if (data.boardArea.contains(x, y)) {

			int realX = (int) ((x - data.boardArea.x) / data.getFieldWidth());
			int realY = (int) ((y - data.boardArea.y) / data.getFieldHeight());

			if (!data.getGame().canMark(realX, realY)) {
				// unable to mark field, maybe it is already occupied
				// TODO add log or user message
				return;
			}
			if (!data.getGame().mark(realX, realY)) {
				// failed to mark field
				// TODO add log or user message
				return; // return, because there has been no change
			}
		} else {
			// TODO add log message ?
			// TODO I think a user message is not necessary
		}

		data.refresh(false);
		this.repaint();
	}

	public Image getPreviewImage() {
		return data.previewImage;
	}

	public Game getGame() {
		return data.getGame();
	}

	public void startGame(Game game) {

		stopGame();
		data.setGame(game);
		if (game != null) {
			game.addGameListener(gameListener);
			game.startGame();
		}
		data.refresh(true);
		this.repaint();
	}

	public void stopGame() {

		if (getGame() != null) {
			getGame().stopGame();
			getGame().removeGameListener(gameListener);
		}
		data.setGame(null);
		data.refresh(true);
		this.repaint();
	}

	public void pauseGame() {

		if (getGame() != null) {
			getGame().pauseGame();
		}

	}

	public void resumeGame() {

		if (getGame() != null) {
			getGame().resumeGame();
		}

	}

	private void handleResize() {
		if (data != null) {
			// TODO do we need this refresh, because the control/form should be
			// resized instead, when the board data changes
			data.refresh(false);
			repaint();
		}
	}

	public void refresh() {

		data.refresh(false);

	}

	public void refreshTime() {

		data.calculateTime();

	}

	@Override
	public void repaint() {
		super.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (data == null) {
			// TODO add log message here, but it should never occur
			return;
		}

		if (data.getGame() == null) {
			// TODO add log message here
			return;
		}

		paintRowNumbers(g);
		paintColumnNumbers(g);
		paintBoard(g);
		paintPreview(g);
		paintTime(g);
		paintFailCountLeft(g);
	}

	private void paintRowNumbers(Graphics g) {

		g.setColor(SMALL_LINE_COLOR);

		// draw horizontal lines
		for (int i = 0; i <= data.rowCount; i++) {
			if (i % 5 == 0) {
				g.setColor(LARGE_LINE_COLOR);
			}
			g.drawLine(data.rowNumberArea.x, data.rowNumberYCoords[i],
					data.rowNumberArea.x + data.rowNumberArea.width,
					data.rowNumberYCoords[i]);
			if (i % 5 == 0) {
				g.setColor(SMALL_LINE_COLOR);
			}
		}

		// TODO check number drawing, improve location of the strings (replace
		// +5)
		// draw numbers
		g.setColor(NUMBER_COLOR);
		for (int y = 0; y < data.rowCount; y++) {
			for (int x = 0; x < data.rowCaptionCount; x++) {
				g.drawString(data.rowNumbers[y][x],
						data.rowNumberXCoords[x] + 5,
						data.rowNumberYCoords[y + 1] - 5);
			}
		}
	}

	private void paintColumnNumbers(Graphics g) {

		g.setColor(SMALL_LINE_COLOR);

		// draw vertical lines
		for (int i = 0; i <= data.columnCount; i++) {
			if (i % 5 == 0) {
				g.setColor(LARGE_LINE_COLOR);
			}
			g.drawLine(data.columnNumberXCoords[i], data.columnNumberArea.y,
					data.columnNumberXCoords[i], data.columnNumberArea.y
							+ data.columnNumberArea.height);
			if (i % 5 == 0) {
				g.setColor(SMALL_LINE_COLOR);
			}
		}

		// TODO check number drawing, improve location of the strings (replace
		// +5)
		// draw numbers
		g.setColor(NUMBER_COLOR);
		for (int x = 0; x < data.columnCount; x++) {
			for (int y = 0; y < data.columnCaptionCount; y++) {
				g.drawString(data.columnNumbers[x][y],
						data.columnNumberXCoords[x] + 6,
						data.columnNumberYCoords[y + 1] - 5);
			}
		}
	}

	private void paintBoard(Graphics g) {
		g.setColor(SMALL_LINE_COLOR);

		// drawing horizontal lines
		for (int i = 0; i <= data.rowCount; i++) {
			if (i % 5 == 0) {
				g.setColor(LARGE_LINE_COLOR);
			}
			g.drawLine(data.boardXCoords[0], data.boardYCoords[i],
					data.boardXCoords[data.boardXCoords.length - 1],
					data.boardYCoords[i]);
			if (i % 5 == 0) {
				g.setColor(SMALL_LINE_COLOR);
			}
		}

		// drawing vertical lines
		for (int i = 0; i <= data.columnCount; i++) {
			if (i % 5 == 0) {
				g.setColor(LARGE_LINE_COLOR);
			}
			g.drawLine(data.boardXCoords[i], data.boardYCoords[0],
					data.boardXCoords[i],
					data.boardYCoords[data.boardYCoords.length - 1]);
			if (i % 5 == 0) {
				g.setColor(SMALL_LINE_COLOR);
			}
		}

		for (int y = 0; y < data.rowCount; y++) {
			for (int x = 0; x < data.columnCount; x++) {
				switch (data.boardValues[x][y]) {
				case FREE:
					// draw nothing
					break;
				case OCCUPIED:
					g.setColor(BOARD_OCCUPIED_COLOR);
					g.fillRect(data.boardXCoords[x] + BOARD_OCCUPIED_LEFT_ADJ,
							data.boardYCoords[y] + BOARD_OCCUPIED_TOP_ADJ, data
									.getFieldWidth()
									- BOARD_OCCUPIED_WIDTH_ADJ, data
									.getFieldHeight()
									- BOARD_OCCUPIED_BOTTOM_ADJ);
					break;
				case MARKED:
					g.setColor(BOARD_MARK_COLOR);
					g.drawLine(data.boardXCoords[x] + BOARD_MARK_LEFT_ADJ,
							data.boardYCoords[y] + BOARD_MARK_TOP_ADJ,
							data.boardXCoords[x + 1] - BOARD_MARK_RIGHT_ADJ,
							data.boardYCoords[y + 1] - BOARD_MARK_BOTTOM_ADJ);
					g.drawLine(data.boardXCoords[x] + BOARD_MARK_LEFT_ADJ,
							data.boardYCoords[y + 1] - BOARD_MARK_BOTTOM_ADJ,
							data.boardXCoords[x + 1] - BOARD_MARK_RIGHT_ADJ,
							data.boardYCoords[y] + BOARD_MARK_TOP_ADJ);
					break;
				}
			}
		}
		// TODO draw content
	}

	private void paintPreview(Graphics g) {
		g.setColor(LARGE_LINE_COLOR);

		// TODO limit zoom factor
		g.drawImage(data.previewImage, data.previewArea.x, data.previewArea.y,
				data.previewArea.width, data.previewArea.height, this);

		g.drawRect(data.previewArea.x, data.previewArea.y,
				data.previewArea.width, data.previewArea.height);
	}

	private void paintTime(Graphics g) {

		g.setColor(TIME_COLOR);

		g.drawString(data.timeLeft, data.timeArea.x, data.timeArea.y);
	}

	private void paintFailCountLeft(Graphics g) {

		g.setColor(TIME_COLOR);

		g.drawString(data.failCountLeft, data.failCountArea.x,
				data.failCountArea.y);
	}

}
