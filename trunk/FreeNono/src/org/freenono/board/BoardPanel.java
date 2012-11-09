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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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

	private ScrollablePlayfield board;
	private BoardTileSetCaption columnView;
	private BoardTileSetCaption rowView;
	private BoardPreview previewArea;

	private static final int MIN_TILESET_HEIGHT = 5;
	private static final int MIN_TILESET_WIDTH = 5;
	private static final int MAX_TILE_SIZE = 100;
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
		this.setOpaque(true); // content panes must be opaque

		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		// Set up the scroll pane.
		board = new ScrollablePlayfield(tileDimension, pattern,
				settings.getHidePlayfield());
		JScrollPane boardScrollPane = new JScrollPane(board);
		boardScrollPane.setPreferredSize(boardDimension);
		// boardScrollPane.setViewportBorder(BorderFactory
		// .createLineBorder(Color.black));

		// Set up the header for columns and rows
		columnView = new BoardTileSetCaption(pattern,
				BoardTileSetCaption.ORIENTATION_COLUMN, tileDimension);

		rowView = new BoardTileSetCaption(pattern,
				BoardTileSetCaption.ORIENTATION_ROW, tileDimension);

		boardScrollPane.setColumnHeaderView(columnView);
		boardScrollPane.setRowHeaderView(rowView);

		// Set the corners
		previewArea = new BoardPreview(pattern);
		boardScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, previewArea);

		// Put it in this panel.
		add(boardScrollPane);
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
	}

	public void removeEventHelper() {

		this.eventHelper = null;

		// remove eventHelper for children
		previewArea.removeEventHelper();
		board.removeEventHelper();
		columnView.removeEventHelper();
		rowView.removeEventHelper();
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		BufferedImage cache = null;
		if (cache == null || cache.getHeight() != getHeight()) {
			cache = new BufferedImage(2, getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = cache.createGraphics();

			GradientPaint paint = new GradientPaint(0, 0, new Color(143, 231,
					200), 0, getHeight(), Color.WHITE);
			g2d.setPaint(paint);
			g2d.fillRect(0, 0, 2, getHeight());
			g2d.dispose();
		}
		g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
	}

	public void focusPlayfield() {

		board.focusPlayfield();
	}

	public BoardPreview getPreviewArea() {

		return previewArea.clone();
	}
}
