/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;


public class BoardPanel extends JPanel {

	private static final long serialVersionUID = -1990300290056624573L;

	private static Logger logger = Logger.getLogger(BoardPanel.class);

	private Dimension boardDimension;
	private Dimension tileDimension;

	private Nonogram pattern;
	private Settings settings;
	private GameEventHelper eventHelper;

	private JScrollPane boardScrollPane;
	private ScrollablePlayfield board;
	private BoardTileSetCaption columnView;
	private BoardTileSetCaption rowView;
	private BoardPreview previewArea;

	private static final int MIN_TILESET_HEIGHT = 5;
	private static final int MIN_TILESET_WIDTH = 5;
	private static final int MAX_TILE_SIZE = 60;
	private static final int MIN_TILE_SIZE = 28;

	
	public BoardPanel(GameEventHelper eventHelper, Nonogram currentNonogram,
			Settings settings, Dimension boardDimension) {

		this.eventHelper = eventHelper;
		this.boardDimension = boardDimension;
		this.settings = settings;
		this.pattern = currentNonogram;

		calculateSizes();

		initialize();
	}

	
	private void initialize() {
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.add(getBoardScrollPane(), BorderLayout.NORTH);
	}


	private JScrollPane getBoardScrollPane() {
		
		// Set up the scroll pane.
		boardScrollPane = new JScrollPane();
		boardScrollPane.setBorder(BorderFactory.createEmptyBorder());
		boardScrollPane.setPreferredSize(boardDimension);
		board = new ScrollablePlayfield(eventHelper, tileDimension, pattern,
				settings);
		boardScrollPane.setViewportView(board);

		// Set method of scrolling
		boardScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		//boardScrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		//boardScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		
		
		// Set up the header for columns and rows
		columnView = new BoardTileSetCaption(eventHelper, pattern, settings,
				BoardTileSetCaption.ORIENTATION_COLUMN, tileDimension);

		rowView = new BoardTileSetCaption(eventHelper, pattern, settings, 
				BoardTileSetCaption.ORIENTATION_ROW, tileDimension);
 
		// JPanel xyz = new JPanel();
		// xyz.setLayout(new BorderLayout());
		// xyz.add(columnView, BorderLayout.WEST);
		// xyz.setOpaque(true);
		boardScrollPane.setColumnHeaderView(columnView);
		boardScrollPane.setRowHeaderView(rowView);

		
		// Set up the preview in the upper left corner
		previewArea = new BoardPreview(pattern);
		JPanel tmpPane = new JPanel();
		tmpPane.setOpaque(false);
		tmpPane.setLayout(new BoxLayout(tmpPane, BoxLayout.PAGE_AXIS));
		tmpPane.add(Box.createVerticalGlue());
		tmpPane.add(previewArea);
		tmpPane.add(Box.createVerticalGlue());
		boardScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, tmpPane);
		
		return boardScrollPane;
	}
	

	/**
	 * Calculates sizes for the tiles of its children (tile sets for play field
	 * and header) and the whole scroll pane. It uses the provided parameter
	 * boardDimension which is calculated by the parent UI. The size of the
	 * tiles depends on the available space and the actual number of tiles.
	 */
	private void calculateSizes() {

		logger.debug("Available size for board panel: " + boardDimension);
		
		// get number of tiles necessary to paint the tile sets
		int tileCountWidth = pattern.width()
				+ Math.max(MIN_TILESET_WIDTH, pattern.getLineCaptionWidth());
		int tileCountHeight = pattern.height()
				+ Math.max(MIN_TILESET_HEIGHT, pattern.getColumnCaptionHeight());
		logger.debug("Tile sets size: " + tileCountWidth + " x " + tileCountHeight);

		// calculate minimal and maximal sizes of board
		Dimension maxSize = new Dimension(tileCountWidth * MAX_TILE_SIZE
				+ tileCountWidth, tileCountHeight * MAX_TILE_SIZE
				+ tileCountHeight);
		Dimension minSize = new Dimension(tileCountWidth * MIN_TILE_SIZE
				+ tileCountWidth, tileCountHeight * MIN_TILE_SIZE
				+ tileCountHeight);

		if (maxSize.getHeight() < boardDimension.getHeight()
				&& maxSize.getWidth() < boardDimension.getWidth()) {

			tileDimension = new Dimension(MAX_TILE_SIZE, MAX_TILE_SIZE);
			boardDimension = new Dimension(MAX_TILE_SIZE * tileCountWidth + 10, 
					MAX_TILE_SIZE * tileCountHeight + 10);
			
		} else if (minSize.getHeight() < boardDimension.getHeight()
				&& minSize.getWidth() < boardDimension.getWidth()) {
		
			// calculate maximum tile size to fit everything in BoardPanel
			int tileSize = (int) Math.floor(Math.min(boardDimension.getWidth()
					/ (double)(tileCountWidth), boardDimension.getHeight()
					/ (double)(tileCountHeight)));
			
			// post processing of tile size
			// while (tileSize % 4 != 0)
			// tileSize = tileSize - 1;
			
			tileDimension = new Dimension(tileSize, tileSize);
			boardDimension = new Dimension(tileSize * tileCountWidth + 10, tileSize
					* tileCountHeight + 10);
			
		}
		else {
			
			tileDimension = new Dimension(MIN_TILE_SIZE, MIN_TILE_SIZE);
		}

		logger.debug("Tile size set to: " + tileDimension);
		logger.debug("Board size set to: " + boardDimension);
	}
	
	
	public void handleResize(Dimension boardDimension) {
		
		this.boardDimension = boardDimension;
		
		calculateSizes();
		
		// set all children to correct size and repaint...
		columnView.handleResize(tileDimension);
		rowView.handleResize(tileDimension);
		board.handleResize(tileDimension);
		boardScrollPane.setPreferredSize(boardDimension);
	}

	public void setEventHelper(GameEventHelper eventHelper) {

		this.eventHelper = eventHelper;

		// set eventHelper for children
		previewArea.setEventHelper(eventHelper);
	}

	public void removeEventHelper() {

		this.eventHelper = null;

		// remove eventHelper for children
		previewArea.removeEventHelper();
		board.removeEventHelper();
		columnView.removeEventHelper();
		rowView.removeEventHelper();
	}

	public void focusPlayfield() {

		board.focusPlayfield();
	}

	public BoardPreview getPreviewArea() {

		return previewArea.clone();
	}
}
