package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;

import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.event.GameListener;
import de.ichmann.markusw.java.apps.freenono.model.GameState;

public class BoardTileSetCaption extends BoardTileSet {

	private static final long serialVersionUID = -3593247761289294060L;

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

		@Override
		public void ActiveFieldChanged(int x, int y) {
			if (tileSetHeight < tileSetWidth) {
				// if column caption...
				board[tileSetHeight - 1][activeFieldColumn]
						.setSelectionMarkerActive(false);
				activeFieldColumn = y;
				activeFieldRow = x;
				board[tileSetHeight - 1][activeFieldColumn]
						.setSelectionMarkerActive(true);
			} else {
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

	// public BoardTileSetCaption(Nonogram n) {
	public BoardTileSetCaption(Game game, int tileSetWidth, int tileSetHeight,
			Dimension tileSetDimension, Dimension tileDimension) {
		super(game, tileSetWidth, tileSetHeight, tileSetDimension,
				tileDimension);

		game.getEventHelper().addGameListener(gameListener);

		activeFieldColumn = 0;
		activeFieldRow = 0;

		paintBorders();
		paintSelectionMarkers();
		paintNumbers();
	}

	private void paintBorders() {
		if (tileSetHeight < tileSetWidth) {
			// column borders
			for (int i = 0; i < tileSetHeight; i++) {
				for (int j = 0; j < tileSetWidth; j++) {
					board[i][j].setDrawBorderWest(true);
					if ((j + 1) % 5 == 0 || (j + 1) == tileSetWidth) {
						board[i][j].setDrawBorderEast(true);
					}
				}
			}
		} else {
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
		if (tileSetHeight < tileSetWidth) {
			// column selection markers
			for (int i = 0; i < tileSetWidth; i++) {
				board[tileSetHeight - 1][i]
						.setSelectionMarker(BoardTile.SELECTION_MARKER_DOWN);
				if (i == activeFieldColumn) {
					board[tileSetHeight - 1][i].setSelectionMarkerActive(true);
				}
			}
		} else {
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
//		String labels[][] = new String[tileSetHeight][tileSetWidth];
//
//		for (int i = 0; i < tileSetHeight; i++) {
//			for (int j = 0; j < tileSetWidth; j++) {
//				labels[i][j] = new String("8");
//			}
//		}
//		this.setLabels(labels);
	}

}
