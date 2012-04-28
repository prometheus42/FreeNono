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
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;
import org.freenono.controller.Settings;

public class BoardComponent extends JComponent {

	private static final long serialVersionUID = -2652246051248812529L;

	private Nonogram pattern;
	private GameEventHelper eventHelper;
	private Settings settings;

	private Dimension boardDimension;
	private Dimension tileDimension;
	private Dimension statusFieldDimension;

	private BoardTileSetPlayfield playfield;
	private BoardTileSetCaption columnCaptions;
	private BoardTileSetCaption rowCaptions;
	private StatusComponent statusField;
	private BoardPreview previewArea;

	private static final int MIN_TILESET_HEIGHT = 5;
	private static final int MIN_TILESET_WIDTH = 5;
	private static final int MAX_TILE_SIZE = 40;

	private GameAdapter gameAdapter = new GameAdapter() {

		// @Override
		// public void StateChanged(StateChangeEvent e) {
		// switch (e.getNewState()) {
		// case gameOver:
		// break;
		// case solved:
		// break;
		// default:
		// break;
		// }
		// }

	};

	public BoardComponent(Nonogram pattern, Settings settings,
			Dimension boardDimension) {
		super();

		// set own size to specified dimension
		this.boardDimension = boardDimension;
		this.setPreferredSize(boardDimension);
		this.setMinimumSize(new Dimension(500, 500));
		
		this.settings = settings;

		// initialize layout and add self to game Listener
		if (pattern != null) {

			this.pattern = pattern;
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

	public void removeEventHelper() {

		eventHelper.removeGameListener(gameAdapter);
		this.eventHelper = null;

		// remove eventHelper for children
		previewArea.removeEventHelper();
		columnCaptions.removeEventHelper();
		rowCaptions.removeEventHelper();
		playfield.removeEventHelper();
		statusField.removeEventHelper();
	}

	
	@Override
	protected void finalize() throws Throwable {

		removeEventHelper();
		super.finalize();
	}

	
	/**
	 * initializing data structures and layout, calculating sizes and dimensions
	 */
	private void initialize() {

		calculateSizes();

		// instantiate parts of BoardComponent
		playfield = new BoardTileSetPlayfield(pattern, settings.getHidePlayfield(),
				tileDimension);
		columnCaptions = new BoardTileSetCaption(pattern,
				BoardTileSetCaption.ORIENTATION_COLUMN, tileDimension);
		rowCaptions = new BoardTileSetCaption(pattern,
				BoardTileSetCaption.ORIENTATION_ROW, tileDimension);
		statusField = new StatusComponent(settings);// , startTime

		// set size of statusField
		// statusField.setPreferredSize(statusFieldDimension);

		// setup previewArea
		previewArea = new BoardPreview(pattern);
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
		// boardDimension = d;
		// calculateSizes();
		// statusField.setPreferredSize(statusFieldDimension);
		// playfield.handleResize(tileDimension);
		// columnCaptions.handleResize(tileDimension);
		// rowCaptions.handleResize(tileDimension);
	}

	public void focusPlayfield() {
		playfield.requestFocusInWindow();
	}

	/**
	 * calculating sizes for this component and its children
	 */
	private void calculateSizes() {

		// get count of tile necessary to paint the tilesets over the entire
		// available space
		// TODO: Replace 5 with minTileSetWidth and minTileSetHeight from the
		// TileSetCaption class. Ideally these values should be held together
		// with colors and so on in an options class.
		int tileCountWidth = pattern.width()
				+ Math.max(MIN_TILESET_WIDTH, pattern.getLineCaptionWidth())
				+ 5;
		int tileCountHeight = pattern.height()
				+ Math.max(MIN_TILESET_HEIGHT, pattern.getColumnCaptionHeight())
				+ 5;

		// maximum tile size to fit everything in BoardComponent limited by
		// MAX_TILE_SIZE
		int tileSize = (int) Math.min(boardDimension.getWidth()
				/ (tileCountWidth), boardDimension.getHeight()
				/ (tileCountHeight));
		if (tileSize > MAX_TILE_SIZE) {
			tileSize = MAX_TILE_SIZE;
		}
		tileDimension = new Dimension(tileSize, tileSize);

		// constant size of statusComponent
		statusFieldDimension = new Dimension(
				(int) (tileSize * (tileCountWidth / 2 + 2)),
				(int) (tileSize * (tileCountHeight / 2 + 2)));

	}

	public void solveGame() {
		// move this into event listener in this class and board preview!!!
		playfield.solveBoard();

	}

	public BoardPreview getPreviewArea() {
		return previewArea.clone();
	}

}
