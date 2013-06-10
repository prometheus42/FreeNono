/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono.board;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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

	private Dimension panelDimension = new Dimension(400,400);
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

	private static final int MIN_CAPTION_HEIGHT = 5;
	private static final int MIN_CAPTION_WIDTH = 5;
	private static final int MAX_TILE_SIZE = 60;
	private static final int MIN_TILE_SIZE = 24;

	
	public BoardPanel(GameEventHelper eventHelper, Nonogram currentNonogram,
			Settings settings) {

		this.eventHelper = eventHelper;
		this.settings = settings;
		this.pattern = currentNonogram;
	}


	public void layoutBoard() {

		calculateSizes();

		initialize();
		
		addListeners();
	}
	
	private void addListeners() {
		
		addComponentListener(new ComponentListener() {
			
			@Override
		    public void componentResized(ComponentEvent e) {       
		        
				//handleResize();
		    }

			@Override
			public void componentMoved(ComponentEvent e) {
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				
			}
		});
	}

	private void initialize() {
		
		setOpaque(false);
		
		add(getBoardScrollPane());
	}


	private JScrollPane getBoardScrollPane() {
		
		// Set up the scroll pane.
		boardScrollPane = new JScrollPane();
		boardScrollPane.setBorder(BorderFactory.createEmptyBorder());
		boardScrollPane.setPreferredSize(panelDimension);
		board = new ScrollablePlayfield(eventHelper, tileDimension, pattern,
				settings);
		boardScrollPane.setViewportView(board);
		
		// enable synthetic drag events
		board.setAutoscrolls(true);
		boardScrollPane.getViewport().addMouseMotionListener(
			new MouseMotionAdapter() {

				public void mouseDragged(MouseEvent e) {

					Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
					((ScrollablePlayfield)e.getSource()).scrollRectToVisible(r);
					logger.debug("drag event");
					//scrollRectToVisible(r);
				}
		});

		// Set method of scrolling
		boardScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		//boardScrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		//boardScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		
		
		// Set up the header for columns and rows
		columnView = new BoardTileSetCaption(eventHelper, pattern, settings,
				BoardTileSetCaption.ORIENTATION_COLUMN, tileDimension);

		rowView = new BoardTileSetCaption(eventHelper, pattern, settings, 
				BoardTileSetCaption.ORIENTATION_ROW, tileDimension);
 
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
	 * panelDimension which is calculated by the parent UI. The size of the
	 * tiles depends on the available space and the actual number of tiles.
	 */
	private void calculateSizes() {
		
		logger.debug("Size given by layout manager: " + this.getSize());

		panelDimension = this.getSize();
		panelDimension.height -= 15;
		panelDimension.width -= 15;
		
		// set board size to panel size and subtract some margin width
		boardDimension = new Dimension(panelDimension);
		
		// get number of tiles necessary to paint the tile sets
		int tileCountWidth = pattern.width()
				+ Math.max(MIN_CAPTION_WIDTH, pattern.getLineCaptionWidth() + 1);
		int tileCountHeight = pattern.height()
				+ Math.max(MIN_CAPTION_HEIGHT, pattern.getColumnCaptionHeight() + 1);
		logger.debug("Tile sets size: " + tileCountWidth + " x " + tileCountHeight);

		// calculate minimal and maximal sizes of board
		Dimension maxSize = new Dimension(tileCountWidth * MAX_TILE_SIZE
				+ tileCountWidth, tileCountHeight * MAX_TILE_SIZE
				+ tileCountHeight);
		Dimension minSize = new Dimension(tileCountWidth * MIN_TILE_SIZE
				+ tileCountWidth, tileCountHeight * MIN_TILE_SIZE
				+ tileCountHeight);

		// if nonogram has very few rows and columns, use maximum tile size
		if (maxSize.getHeight() < boardDimension.getHeight()
				&& maxSize.getWidth() < boardDimension.getWidth()) {

			tileDimension = new Dimension(MAX_TILE_SIZE, MAX_TILE_SIZE);
			boardDimension = new Dimension(MAX_TILE_SIZE * tileCountWidth, 
					MAX_TILE_SIZE * tileCountHeight);
			panelDimension = boardDimension;
			
		// if nonogram is medium large, calculate a tile size between minimum and maximum
		} else if (minSize.getHeight() < boardDimension.getHeight()
				&& minSize.getWidth() < boardDimension.getWidth()) {
		
			// calculate maximum tile size to fit everything in BoardPanel
			int tileSize = (int) Math.floor(Math.min(boardDimension.getWidth()
					/ (double)(tileCountWidth), boardDimension.getHeight()
					/ (double)(tileCountHeight)));
			
			tileDimension = new Dimension(tileSize, tileSize);
			boardDimension = new Dimension(tileSize * tileCountWidth, tileSize
					* tileCountHeight);
			panelDimension = boardDimension;
		}
		
		// else use minimum tile size and set panel size accordingly
		else {
			
			tileDimension = new Dimension(MIN_TILE_SIZE, MIN_TILE_SIZE);
			boardDimension = new Dimension(MIN_TILE_SIZE * tileCountWidth, 
					MIN_TILE_SIZE * tileCountHeight);
			
			// set size of panel dependent on needed size of board
			int testWidth = MIN_TILE_SIZE * tileCountWidth;
			int testHeight = MIN_TILE_SIZE * tileCountHeight;
			
			if (panelDimension.width < MIN_TILE_SIZE * tileCountWidth) {
			
				testHeight += 15;
			}
			if (panelDimension.height < MIN_TILE_SIZE * tileCountHeight) {
				
				testWidth += 15;
			}
			if (panelDimension.width > testWidth) {
				
				panelDimension.width = testWidth;
			}
			if (panelDimension.height > testHeight) {
				
				panelDimension.height = testHeight;
			}
		}

		logger.debug("Tile size set to: " + tileDimension);
		logger.debug("Board size set to: " + boardDimension);
		logger.debug("Panel size set to: " + panelDimension);
	}
	
	
	public Dimension getPreferredSize() {
	
		return panelDimension;
	}
	
	public void handleResize() {
		
		logger.debug("resizing board panel...");
		
    	calculateSizes();
    	
    	boardScrollPane.setPreferredSize(panelDimension);
    	
    	columnView.handleResize(tileDimension);
		rowView.handleResize(tileDimension);
		board.handleResize(tileDimension);
		
		columnView.validate();
		rowView.validate();
		board.validate();
		boardScrollPane.validate();
		validate();
		
    	boardScrollPane.repaint();
		columnView.repaint();
		rowView.repaint();
		board.repaint();
		repaint();
	}

	public void setEventHelper(GameEventHelper eventHelper) {

		this.eventHelper = eventHelper;

		// set eventHelper for children
		if (previewArea != null) {
			
			previewArea.setEventHelper(eventHelper);
		}
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
