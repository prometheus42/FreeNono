package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;

import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.Nonogram;
import de.ichmann.markusw.java.apps.freenono.event.GameListener;
import de.ichmann.markusw.java.apps.freenono.model.GameState;

public class BoardComponent extends JComponent {

	private static final long serialVersionUID = -2652246051248812529L;
	
	private Game game;

	private int nonogramWidth;
	private int nonogramHeight;
	private boolean[][] board = null;
	private int columnCaptionCount;
	private int rowCaptionCount;

	private Dimension boardComponentDimension;
	private Dimension tileSetDimension;
	private Dimension tileDimension;

	private BoardTileSetPlayfield nonogram;
	private BoardTileSetCaption columnNumbers;
	private BoardTileSetCaption rowNumbers;
	private StatusComponent statusComponent;
	private BoardPreview previewArea;

	// private Game game;
	private GameListener gameListener = new GameListener() {

		@Override
		public void Timer() {
		}

		@Override
		public void StateChanged(GameState oldState, GameState newState) {
		}

		@Override
		public void FieldOccupied(int x, int y) {
			board[x][y] = true;
			previewArea.refresh(board);
		}

		@Override
		public void FieldMarked(int x, int y) {
		}
		
		@Override
		public void ActiveFieldChanged(int x, int y) {
		}
	};

	public BoardComponent(Game game) {
		super();

		this.nonogramWidth = game.width();
		this.nonogramHeight = game.height();;

		Nonogram n = game.getPattern();
		
		columnCaptionCount = n.getColumnCaptionHeight();
		rowCaptionCount = n.getLineCaptionWidth();
		
		initialize();
		
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				handleResize();
			}
		});

		game.getEventHelper().addGameListener(gameListener);
	}

	/**
	 * initializing data structures and layout, calculating sizes and dimensions
	 */
	private void initialize() {
		// building internal data structure for board data
		board = new boolean[nonogramHeight][nonogramWidth];
		for (int i = 0; i < nonogramHeight; i++) {
			for (int j = 0; j < nonogramWidth; j++) {
				board[i][j] = false;
			}
		}

		calculateSizes();

		// instantiate parts of BoardComponent
		nonogram = new BoardTileSetPlayfield(game, nonogramWidth, nonogramHeight,
				tileSetDimension, tileDimension);
		columnNumbers = new BoardTileSetCaption(game, nonogramWidth,
				nonogramHeight / 2, tileSetDimension, tileDimension);
		rowNumbers = new BoardTileSetCaption(game, nonogramWidth / 2, nonogramHeight,
				tileSetDimension, tileDimension);
		statusComponent = new StatusComponent(game);

		// set start point to tile (0,0) for keyboard control
		nonogram.setActive(0, 0);

		// set sizes of parts
		nonogram.setPreferredSize(tileSetDimension);
		columnNumbers.setPreferredSize(new Dimension(tileSetDimension.width,
				(int) (tileSetDimension.height / 2)));
		rowNumbers.setPreferredSize(new Dimension(
				(int) (tileSetDimension.width / 2), tileSetDimension.height));
		statusComponent.setPreferredSize(new Dimension(
				(int) (tileSetDimension.width / 2),
				(int) (tileSetDimension.height / 2)));

		// set layout manager and build BoardComponent
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		this.add(statusComponent, c);
		c.gridx = 1;
		c.gridy = 0;
		this.add(columnNumbers, c);
		c.gridx = 0;
		c.gridy = 1;
		this.add(rowNumbers, c);
		c.gridx = 1;
		c.gridy = 1;
		this.add(nonogram, c);
		
		previewArea = new BoardPreview(nonogramWidth, nonogramHeight, board);
		statusComponent.add(previewArea);
	}

	private void handleResize() {
		// TODO Auto-generated method stub
		//refresh();
		//repaint();
	}
	
	/**
	 * calculating sizes for this component an all of its childs
	 */
	private void calculateSizes() {
		boardComponentDimension = new Dimension(800, 800);
		this.setSize(boardComponentDimension);
		this.setPreferredSize(boardComponentDimension);

		tileSetDimension = new Dimension((int) (800 * 0.6), (int) (800 * 0.6));

		tileDimension = new Dimension(tileSetDimension.width / nonogramWidth,
				tileSetDimension.height / nonogramHeight);
	}

//	public void refresh() {
//		calculateSizes();
//		if (game.usesMaxFailCount()) {
//			failCountLeft = Integer.toString(game.getFailCountLeft());
//		} else {
//			failCountLeft = "";
//		}
//	}
	
	@Override
	public Dimension getMinimumSize() {
		return boardComponentDimension;
	}

	@Override
	public Dimension getPreferredSize() {
		return boardComponentDimension;
	}

//	 public Game getGame() {
//	 return game;
//	 }
//	
//	 public void setGame(Game game) {
//	 this.game = game;
//	 }

//	 protected void occupyField(int x, int y) {
//	 game.canOccupy(realX, realY)
//	 game.occupy(realX, realY)
//	 }
//	
//	 protected void markField(int x, int y) {
//	 game.canMark(realX, realY)
//	 game.mark(realX, realY)
//	 }
	
//	public void startGame(Game game) {
//		stopGame();
//		setGame(game);
//		if (game != null) {
//			game.addGameListener(gameListener);
//			game.startGame();
//		}
//		refresh();
//		this.repaint();
//	}

//	public void stopGame() {
//		if (getGame() != null) {
//			getGame().stopGame();
//			getGame().removeGameListener(gameListener);
//		}
//		setGame(null);
//		refresh();
//		this.repaint();
//	}

//	public void pauseGame() {
//		if (getGame() != null) {
//			getGame().pauseGame();
//		}
//	}

//	public void resumeGame() {
//		if (getGame() != null) {
//			getGame().resumeGame();
//		}
//	}

}
