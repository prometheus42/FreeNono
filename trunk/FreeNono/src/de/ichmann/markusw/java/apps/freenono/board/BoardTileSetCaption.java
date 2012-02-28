package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;

import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.Nonogram;
import de.ichmann.markusw.java.apps.freenono.event.GameAdapter;
import de.ichmann.markusw.java.apps.freenono.event.GameListener;
import de.ichmann.markusw.java.apps.freenono.model.GameState;

public class BoardTileSetCaption extends BoardTileSet {

	private static final long serialVersionUID = -3593247761289294060L;

	public static final int ORIENTATION_COLUMN = 1;
	public static final int ORIENTATION_ROW = 2;
	private int orientation = 0;

	private int columnCaptionCount;
	private int rowCaptionCount;

	private GameAdapter gameAdapter = new GameAdapter() {

		public void ActiveFieldChanged(int x, int y) {
			if (orientation == ORIENTATION_COLUMN) {
				// if column caption...
				board[tileSetHeight - 1][activeFieldColumn]
						.setSelectionMarkerActive(false);
				activeFieldColumn = y;
				activeFieldRow = x;
				board[tileSetHeight - 1][activeFieldColumn]
						.setSelectionMarkerActive(true);
			} else if (orientation == ORIENTATION_ROW) {
				// ...else is row caption
				board[activeFieldRow][tileSetWidth - 1]
						.setSelectionMarkerActive(false);
				activeFieldColumn = y;
				activeFieldRow = x;
				board[activeFieldRow][tileSetWidth - 1]
						.setSelectionMarkerActive(true);
			}
		}
	};

	public BoardTileSetCaption(Game game, int orientation,
			Dimension tileSetDimension, Dimension tileDimension) {
		super(game, tileSetDimension, tileDimension);

		this.orientation = orientation;

		if (orientation == ORIENTATION_COLUMN) {
			tileSetWidth = game.width();
			tileSetHeight = game.height() / 2 + 2; 
			// +2 to make the array big enough for every possible nonogram
		} else if (orientation == ORIENTATION_ROW) {
			tileSetWidth = game.width() / 2 + 2;
			tileSetHeight = game.height();
		}

		// TODO: calculate number counts beforehand and make TileSetCaptions
		// only big enough to hold these numbers. (-> initialize())
		initialize();

		paintBorders();
		paintSelectionMarkers();
		paintNumbers();

		geh.addGameListener(gameAdapter);

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
