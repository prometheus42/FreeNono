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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;

import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Game;


public class BoardComponent extends JComponent {

	private static final long serialVersionUID = -2652246051248812529L;

	private Game game;
	private GameEventHelper eventHelper;

	private Dimension boardDimension;
	private Dimension tileDimension;
	private Dimension statusFieldDimension;

	public BoardTileSetPlayfield playfield;
	private BoardTileSetCaption columnCaptions;
	private BoardTileSetCaption rowCaptions;
	private StatusComponent statusField;
	private BoardPreview previewArea;

	private boolean hidePlayfield;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void StateChanged(GameEvent e) {
			switch (e.getNewState()) {
			case gameOver:
				break;
			case solved:
				break;
			default:
				break;
			}
		}

	};

	public BoardComponent(Game game, boolean hidePlayfield,
			Dimension boardDimension) {
		super();

		// set own size to specified dimension
		this.boardDimension = boardDimension;
		this.setPreferredSize(boardDimension);
		this.setMinimumSize(new Dimension(500, 500));

		this.hidePlayfield = hidePlayfield;

		// initialize layout and add self to game Listener
		if (game != null) {

			this.game = game;

			initialize();

		}

	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);

		// set eventHelper for children
		previewArea.setEventHelper(eventHelper);
		columnCaptions.setEventHelper(eventHelper);
		rowCaptions.setEventHelper(eventHelper);
		playfield.setEventHelper(eventHelper);
		statusField.setEventHelper(eventHelper);
	}

	@Override
	protected void finalize() throws Throwable {
		eventHelper.removeGameListener(gameAdapter);
		super.finalize();
	}

	/**
	 * initializing data structures and layout, calculating sizes and dimensions
	 */
	private void initialize() {

		calculateSizes();

		// instantiate parts of BoardComponent
		playfield = new BoardTileSetPlayfield(game, hidePlayfield,
				tileDimension);
		columnCaptions = new BoardTileSetCaption(game,
				BoardTileSetCaption.ORIENTATION_COLUMN, tileDimension);
		rowCaptions = new BoardTileSetCaption(game,
				BoardTileSetCaption.ORIENTATION_ROW, tileDimension);
		statusField = new StatusComponent(game);

		// set size of statusField
		//statusField.setPreferredSize(statusFieldDimension);

		// setup previewArea
		previewArea = new BoardPreview(game);
		statusField.addPreviewArea(previewArea);

		// set layout manager and build BoardComponent
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(statusField, c);
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		this.add(columnCaptions, c);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		this.add(rowCaptions, c);
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		this.add(playfield, c);

		// set start point to tile (0,0) for keyboard control
		playfield.setActive(0, 0);

	}

	public void handleResize(Dimension d) {
		// TODO handle resize correctly!
//		boardDimension = d;
//		calculateSizes();
//		statusField.setPreferredSize(statusFieldDimension);
//		playfield.handleResize(tileDimension);
//		columnCaptions.handleResize(tileDimension);
//		rowCaptions.handleResize(tileDimension);
	}

	public void focusPlayfield() {
		playfield.requestFocusInWindow();
	}

	/**
	 * calculating sizes for this component and its children
	 */
	private void calculateSizes() {

		int nonogramWidth = game.width();
		int nonogramHeight = game.height();

		// maximum tile size to fit everything in BoardComponent
		int tileSize = (int) Math.min(boardDimension.getWidth()
				/ (nonogramWidth * 1.5 + 3), boardDimension.getHeight()
				/ (nonogramHeight * 1.5 + 3));
		tileDimension = new Dimension(tileSize, tileSize);

		// constant size of statusComponent
		statusFieldDimension = new Dimension(
				(int) (tileSize * (nonogramWidth / 2 + 2)),
				(int) (tileSize * (nonogramHeight / 2 + 2)));

	}

	public void solveGame() {
		playfield.solveBoard();
		previewArea.refreshPreview();
	}

	// @Override
	// public Dimension getMinimumSize() {
	// return boardDimension;
	// }
	//
	// @Override
	// public Dimension getPreferredSize() {
	// return boardDimension;
	// }

	public Game getGame() {
		return game;
	}

	public BoardPreview getPreviewArea() {
		return previewArea.clone();
	}

	public void startGame() {
		stopGame();
		if (getGame() != null) {
			getGame().startGame();
		}
	}

	public void stopGame() {
		if (getGame() != null) {
			getGame().stopGame();
		}
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

}
