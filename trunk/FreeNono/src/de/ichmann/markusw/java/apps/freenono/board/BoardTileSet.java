package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;

import de.ichmann.markusw.java.apps.freenono.event.GameEventHelper;
import de.ichmann.markusw.java.apps.freenono.model.Game;

public class BoardTileSet extends JComponent {

	private static final long serialVersionUID = 3230262588929434548L;
	
	protected GameEventHelper eventHelper;
	protected Game game;

	protected static final int TILESET_WIDTH_DEFAULT = 10;
	protected static final int TILESET_HEIGHT_DEFAULT = 10;

	protected int tileSetWidth = TILESET_WIDTH_DEFAULT;
	protected int tileSetHeight = TILESET_HEIGHT_DEFAULT;
	protected Dimension tileDimension;
	
	protected BoardTile[][] board = null;

	protected boolean isMarked[][];
	protected boolean isMarkedOld[][];
	protected String labels[][];
	protected String labelsOld[][];
	
	protected int activeFieldColumn = 0;
	protected int activeFieldRow = 0;
	

	public BoardTileSet(Game game, Dimension tileDimension) {
		super();
		
		this.game = game;
		this.tileDimension = tileDimension;
		
	}

	protected void initialize() {
		// get array for tile attributes
		isMarked = new boolean[tileSetHeight][tileSetWidth];
		labels = new String[tileSetHeight][tileSetWidth];
		
		// build gridLayout
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(tileSetHeight);
		gridLayout.setColumns(tileSetWidth);
		this.setLayout(gridLayout);

		// fill grid with tiles
		board = new BoardTile[tileSetHeight][tileSetWidth];
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j] = new BoardTile(tileDimension);
				board[i][j].setMinimumSize(tileDimension);
				board[i][j].setPreferredSize(tileDimension);
				board[i][j].setColumn(j);
				board[i][j].setRow(i);
				this.add(board[i][j]);
				isMarked[i][j] = false;
			}
		}
	}
	
	public void handleResize(Dimension tileDimension) {
		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				board[i][j].setMinimumSize(tileDimension);
				board[i][j].setPreferredSize(tileDimension);
			}
		}
	}

	public boolean[][] getIsMarked() {
		return isMarked;
	}

	public void setIsMarked(boolean[][] isMarked) {
		isMarkedOld = this.isMarked;
		this.isMarked = isMarked;

		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				if (this.isMarked[i][j] != isMarkedOld[i][j]) {
					board[i][j].setMarked(this.isMarked[i][j]);
				}
			}
		}
	}

	public String[][] getLabels() {
		return labels;
	}

	public void setLabels(String labels[][]) {
		labelsOld = this.labels;
		this.labels = labels;

		for (int i = 0; i < tileSetHeight; i++) {
			for (int j = 0; j < tileSetWidth; j++) {
				if (this.labels[i][j] != labelsOld[i][j]) {
					board[i][j].setLabel(this.labels[i][j]);
				}
			}
		}
	}

	public int getTileSetWidth() {
		return tileSetWidth;
	}

	public int getTileSetHeight() {
		return tileSetHeight;
	}

}
