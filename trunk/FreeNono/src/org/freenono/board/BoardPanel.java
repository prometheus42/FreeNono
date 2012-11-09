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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.freenono.controller.Settings;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;


public class BoardPanel extends JPanel {

	private static final long serialVersionUID = -1990300290056624573L;

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
	private StatusComponent statusField;

	private static final int MIN_TILESET_HEIGHT = 5;
	private static final int MIN_TILESET_WIDTH = 5;
	private static final int MAX_TILE_SIZE = 75;
	private static final int MIN_TILE_SIZE = 30;

	
	public BoardPanel(Nonogram currentNonogram, Settings settings,
			Dimension dimension) {

		this.boardDimension = dimension;
		this.settings = settings;
		this.pattern = currentNonogram;

		calculateSizes();

		initialize();
	}

	
	private void initialize() {

		// this.setSize(boardDimension);
		this.setPreferredSize(boardDimension);
		this.setOpaque(false); // content panes must be opaque
		
		// TODO: remove borders on all component in MainUI!
		this.setBorder(BorderFactory.createEmptyBorder());

		add(getStatusField());
		add(getBoardScrollPane());
	}

	private StatusComponent getStatusField() {
		
		statusField = new StatusComponent(settings);
		return statusField;
	}

	private JScrollPane getBoardScrollPane() {
		
		// Set up the scroll pane.
		board = new ScrollablePlayfield(tileDimension, pattern,
				settings.getHidePlayfield());
		boardScrollPane = new JScrollPane(board);
		boardScrollPane.setPreferredSize(boardDimension);

		// Set up the header for columns and rows
		columnView = new BoardTileSetCaption(pattern,
				BoardTileSetCaption.ORIENTATION_COLUMN, tileDimension);

		rowView = new BoardTileSetCaption(pattern,
				BoardTileSetCaption.ORIENTATION_ROW, tileDimension);

		boardScrollPane.setColumnHeaderView(columnView);
		boardScrollPane.setRowHeaderView(rowView);

		// Set the preview in the upper left corner
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
	 * calculating sizes for this component and its children
	 */
	private void calculateSizes() {

		// get number of tiles necessary to paint the tilesets over the entire
		// available space
		int tileCountWidth = pattern.width()
				+ Math.max(MIN_TILESET_WIDTH, pattern.getLineCaptionWidth());
		int tileCountHeight = pattern.height()
				+ Math.max(MIN_TILESET_HEIGHT, pattern.getColumnCaptionHeight());

		// maximum tile size to fit everything in BoardPanel limited by
		// MAX_TILE_SIZE
		int tileSize = (int) Math.min(boardDimension.getWidth()
				/ (tileCountWidth), boardDimension.getHeight()
				/ (tileCountHeight));
		if (tileSize > MAX_TILE_SIZE) {
			tileSize = MAX_TILE_SIZE;
		} else if (tileSize < MIN_TILE_SIZE) {
			tileSize = MIN_TILE_SIZE;
		}
		tileDimension = new Dimension(tileSize, tileSize);

	}

	public void setEventHelper(GameEventHelper eventHelper) {

		this.eventHelper = eventHelper;

		// set eventHelper for children
		previewArea.setEventHelper(eventHelper);
		board.setEventHelper(eventHelper);
		columnView.setEventHelper(eventHelper);
		rowView.setEventHelper(eventHelper);
		statusField.setEventHelper(eventHelper);
	}

	public void removeEventHelper() {

		this.eventHelper = null;

		// remove eventHelper for children
		previewArea.removeEventHelper();
		board.removeEventHelper();
		columnView.removeEventHelper();
		rowView.removeEventHelper();
		statusField.removeEventHelper();
	}

	// protected void paintComponent(Graphics g) {
	// Graphics2D g2 = (Graphics2D) g;
	// BufferedImage cache = null;
	// if (cache == null || cache.getHeight() != getHeight()) {
	// cache = new BufferedImage(2, getHeight(),
	// BufferedImage.TYPE_INT_RGB);
	// Graphics2D g2d = cache.createGraphics();
	//
	// GradientPaint paint = new GradientPaint(0, 0, new Color(143, 231,
	// 200), 0, getHeight(), Color.WHITE);
	// g2d.setPaint(paint);
	// g2d.fillRect(0, 0, 2, getHeight());
	// g2d.dispose();
	// }
	// g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
	// }

	public void focusPlayfield() {

		board.focusPlayfield();
	}

	public BoardPreview getPreviewArea() {

		return previewArea.clone();
	}
}
