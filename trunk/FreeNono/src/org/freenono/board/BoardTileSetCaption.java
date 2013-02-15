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

import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.model.Nonogram;

public class BoardTileSetCaption extends BoardTileSet {

	private static final long serialVersionUID = -3593247761289294060L;

	public static final int ORIENTATION_COLUMN = 1;
	public static final int ORIENTATION_ROW = 2;
	private int orientation = 0;

	private static final int MIN_TILESET_HEIGHT = 5;
	private static final int MIN_TILESET_WIDTH = 5;

	private int columnCaptionCount;
	private int rowCaptionCount;

	private GameAdapter gameAdapter = new GameAdapter() {

		public void OptionsChanged(ProgramControlEvent e) {
			
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					
					board[i][j].setColorModel(settings.getColorModel());
					board[i][j].repaint();
				}
			}
		}
		
		public void ChangeActiveField(FieldControlEvent e) {
			if (orientation == ORIENTATION_COLUMN) {
				// if column caption...
				// XXX: The following if statements prevent OutOfBounds
				// Exceptions on all four board accesses. I have no idea WHY
				// these exceptions are thrown?
				if (activeFieldColumn < pattern.width()) {
					board[tileSetHeight - 1][activeFieldColumn]
							.setSelectionMarkerActive(false);
				}
				activeFieldColumn = e.getFieldColumn();
				activeFieldRow = e.getFieldRow();
				if (activeFieldColumn < pattern.width()) {
					board[tileSetHeight - 1][activeFieldColumn]
							.setSelectionMarkerActive(true);
				}
			} else if (orientation == ORIENTATION_ROW) {
				// ...else is row caption
				if (activeFieldRow < pattern.height()) {
					board[activeFieldRow][tileSetWidth - 1]
							.setSelectionMarkerActive(false);
				}
				activeFieldColumn = e.getFieldColumn();
				activeFieldRow = e.getFieldRow();
				if (activeFieldRow < pattern.height()) {
					board[activeFieldRow][tileSetWidth - 1]
							.setSelectionMarkerActive(true);
				}
			}
		}
	};

	public BoardTileSetCaption(GameEventHelper eventHelper, Nonogram pattern,
			Settings settings, int orientation, Dimension tileDimension) {
		
		super(eventHelper, pattern, settings, tileDimension);
		
		eventHelper.addGameListener(gameAdapter);

		this.orientation = orientation;

		// set tileSet height and width according to necessary numbers of tiles
		columnCaptionCount = pattern.getColumnCaptionHeight();
		rowCaptionCount = pattern.getLineCaptionWidth();
		if (orientation == ORIENTATION_COLUMN) {
			tileSetWidth = pattern.width();
			tileSetHeight = Math.max(columnCaptionCount + 1, MIN_TILESET_HEIGHT);
		} else if (orientation == ORIENTATION_ROW) {
			tileSetWidth = Math.max(rowCaptionCount + 1, MIN_TILESET_WIDTH);
			tileSetHeight = pattern.height();
		}

		initialize();
		
		// setting caption components opaque so background of mainUI can be seen
		this.setOpaque(true);

		paintBorders();
		paintSelectionMarkers();
		paintNumbers();
	}
	
	public void removeEventHelper() {
		
		if (eventHelper != null) {
			
			eventHelper.removeGameListener(gameAdapter);
			eventHelper = null;
		}
	}


	private void paintBorders() {

		if (orientation == ORIENTATION_COLUMN) {
			// column borders
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					board[i][j].setDrawBorderWest(true);
					if ((j + 1) % 5 == 0 || (j + 1) == tileSetWidth) {
						board[i][j].setDrawBorderEast(true);
					}
				}
			}
		} else if (orientation == ORIENTATION_ROW) {
			// row borders
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					board[i][j].setDrawBorderNorth(true);
					if ((i + 1) % 5 == 0 || (i + 1) == tileSetHeight) {
						board[i][j].setDrawBorderSouth(true);
					}
				}
			}
		}
	}

	private void paintSelectionMarkers() {
		
		if (orientation == ORIENTATION_COLUMN) {
			// column selection markers
			for (int i = 0; i < tileSetWidth; i++) {
				board[tileSetHeight - 1][i]
						.setSelectionMarker(BoardTile.SELECTION_MARKER_DOWN);
				if (i == activeFieldColumn) {
					board[tileSetHeight - 1][i].setSelectionMarkerActive(true);
				}
			}
		} else if (orientation == ORIENTATION_ROW) {
			// row selection markers
			for (int i = 0; i < tileSetHeight; i++) {
				board[i][tileSetWidth - 1]
						.setSelectionMarker(BoardTile.SELECTION_MARKER_RIGHT);
				if (i == activeFieldRow) {
					board[i][tileSetWidth - 1].setSelectionMarkerActive(true);
				}
			}
		}
	}

	private void paintNumbers() {

		// get number of numbers for captions
		columnCaptionCount = pattern.getColumnCaptionHeight();
		rowCaptionCount = pattern.getLineCaptionWidth();
		String labels[][] = new String[tileSetHeight + 2][tileSetWidth + 2];

		if (orientation == ORIENTATION_COLUMN) {
			// initialize column numbers
			for (int x = 0; x < tileSetWidth; x++) {
				int len = pattern.getColumnNumbersCount(x);
				for (int i = 0; i < columnCaptionCount; i++) {
					int number = pattern.getColumnNumber(x, i);
					int y = (i + columnCaptionCount - len) % columnCaptionCount
							+ Math.max(0, MIN_TILESET_HEIGHT - 1
									- columnCaptionCount);
					labels[y][x] = number >= 0 ? Integer.toString(number) : "";
				}
			}
		} else if (orientation == ORIENTATION_ROW) {
			// initialize row numbers
			for (int y = 0; y < tileSetHeight; y++) {
				int len = pattern.getLineNumberCount(y);
				for (int i = 0; i < rowCaptionCount; i++) {
					int number = pattern.getLineNumber(y, i);
					int x = (i + rowCaptionCount - len) % rowCaptionCount
							+ Math.max(0, MIN_TILESET_WIDTH - 1 - rowCaptionCount);
					labels[y][x] = number >= 0 ? Integer.toString(number) : "";
				}
			}
		}

		this.setLabels(labels);
	}

}
