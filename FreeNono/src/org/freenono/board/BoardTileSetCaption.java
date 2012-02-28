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

import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.event.GameListener;
import org.freenono.model.Game;
import org.freenono.model.GameState;
import org.freenono.model.Nonogram;

public class BoardTileSetCaption extends BoardTileSet {

	private static final long serialVersionUID = -3593247761289294060L;

	public static final int ORIENTATION_COLUMN = 1;
	public static final int ORIENTATION_ROW = 2;
	private int orientation = 0;

	private int columnCaptionCount;
	private int rowCaptionCount;

	private GameAdapter gameAdapter = new GameAdapter() {

		public void ActiveFieldChanged(GameEvent e) {
			if (orientation == ORIENTATION_COLUMN) {
				// if column caption...
				// XXX: The following if statements prevent OutOfBounds
				// Exceptions on all four board accesses. I have no idea WHY
				// these exceptions are thrown?
				if (activeFieldColumn < game.width()) {
					board[tileSetHeight - 1][activeFieldColumn]
							.setSelectionMarkerActive(false);
				}
				activeFieldColumn = e.getFieldColumn();
				activeFieldRow = e.getFieldRow();
				if (activeFieldColumn < game.width()) {
					board[tileSetHeight - 1][activeFieldColumn]
							.setSelectionMarkerActive(true);
				}
			} else if (orientation == ORIENTATION_ROW) {
				// ...else is row caption
				if (activeFieldRow < game.height()) {
					board[activeFieldRow][tileSetWidth - 1]
							.setSelectionMarkerActive(false);
				}
				activeFieldColumn = e.getFieldColumn();
				activeFieldRow = e.getFieldRow();
				if (activeFieldRow < game.height()) {
					board[activeFieldRow][tileSetWidth - 1]
							.setSelectionMarkerActive(true);
				}
			}
		}
	};

	public BoardTileSetCaption(Game game, int orientation,
			Dimension tileDimension) {
		super(game, tileDimension);

		this.orientation = orientation;

		// set tileSet height and width according to necessary numbers of tiles
		if (orientation == ORIENTATION_COLUMN) {
			tileSetWidth = game.width();
			tileSetHeight = game.getPattern().getColumnCaptionHeight() + 1;
		} else if (orientation == ORIENTATION_ROW) {
			tileSetWidth = game.getPattern().getLineCaptionWidth() + 1;
			tileSetHeight = game.height();
		}

		initialize();

		paintBorders();
		paintSelectionMarkers();
		paintNumbers();

	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	private void paintBorders() {
		// TODO: switch for and if!
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
		Nonogram n = game.getPattern();
		columnCaptionCount = n.getColumnCaptionHeight();
		rowCaptionCount = n.getLineCaptionWidth();
		String labels[][] = new String[tileSetHeight + 2][tileSetWidth + 2];

		if (orientation == ORIENTATION_COLUMN) {
			// initialize column numbers
			for (int x = 0; x < tileSetWidth; x++) {
				int len = n.getColumnNumbersCount(x);
				for (int i = 0; i < columnCaptionCount; i++) {
					int number = n.getColumnNumber(x, i);
					labels[(i + columnCaptionCount - len) % columnCaptionCount][x] = number >= 0 ? Integer
							.toString(number) : "";
					// columnNumbers[x][columnCaptionCount - i - 1]
				}
			}
		} else if (orientation == ORIENTATION_ROW) {
			// initialize row numbers
			for (int y = 0; y < tileSetHeight; y++) {
				int len = n.getLineNumberCount(y);
				for (int i = 0; i < rowCaptionCount; i++) {
					int number = n.getLineNumber(y, i);
					labels[y][(i + rowCaptionCount - len) % rowCaptionCount] = number >= 0 ? Integer
							.toString(number) : "";
					// rowNumbers[y][rowCaptionCount - i - 1]
				}
			}
		}

		this.setLabels(labels);

	}
}
