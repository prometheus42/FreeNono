package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;

import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.event.GameListener;
import de.ichmann.markusw.java.apps.freenono.model.GameState;

public class BoardComponent extends JComponent {

	private static final long serialVersionUID = -2652246051248812529L;

	private Game game;

	private Dimension boardDimension;
	private Dimension playfieldDimension;
	private Dimension columnCaptionDimension;
	private Dimension rowCaptionDimension;
	private Dimension tileDimension;
	private Dimension statusFieldDimension;

	public BoardTileSetPlayfield playfield;
	private BoardTileSetCaption columnCaptions;
	private BoardTileSetCaption rowCaptions;
	private StatusComponent statusField;
	private BoardPreview previewArea;

	private GameListener gameListener = new GameListener() {

		@Override
		public void Timer() {
		}

		@Override
		public void StateChanged(GameState oldState, GameState newState) {
			switch (newState) {
			case gameOver:
				break;
			case solved:
				break;
			default:
				break;
			}
		}

		@Override
		public void FieldOccupied(int x, int y) {
		}

		@Override
		public void FieldMarked(int x, int y) {
		}

		@Override
		public void ActiveFieldChanged(int x, int y) {
		}
	};
	
	public BoardComponent(Game game, Dimension boardDimension) {
		super();

		// set own size to specified dimension
		this.boardDimension = boardDimension;
		this.setPreferredSize(boardDimension);

		// initialize layout and add self to game Listener
		if (game != null) {

			this.game = game;
			
			initialize();
			
			game.getEventHelper().addGameListener(gameListener);

		}

		// add component Listener for handling the resize operation
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				handleResize();
			}
		});
	}

	/**
	 * initializing data structures and layout, calculating sizes and dimensions
	 */
	private void initialize() {

		calculateSizes();

		// instantiate parts of BoardComponent
		playfield = new BoardTileSetPlayfield(game, playfieldDimension,
				tileDimension);
		columnCaptions = new BoardTileSetCaption(game,
				BoardTileSetCaption.ORIENTATION_COLUMN, columnCaptionDimension,
				tileDimension);
		rowCaptions = new BoardTileSetCaption(game,
				BoardTileSetCaption.ORIENTATION_ROW, rowCaptionDimension,
				tileDimension);
		statusField = new StatusComponent(game);
		
		// setup previewArea
		previewArea = new BoardPreview(game);
		statusField.add(previewArea);
				
		// set start point to tile (0,0) for keyboard control
		playfield.setActive(0, 0);

		// set sizes of parts
		playfield.setPreferredSize(playfieldDimension);
		columnCaptions.setPreferredSize(columnCaptionDimension);
		rowCaptions.setPreferredSize(rowCaptionDimension);
		statusField.setPreferredSize(statusFieldDimension);
		
		// set layout manager and build BoardComponent
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		this.add(statusField, c);
		c.gridx = 1;
		c.gridy = 0;
		this.add(columnCaptions, c);
		c.gridx = 0;
		c.gridy = 1;
		this.add(rowCaptions, c);
		c.gridx = 1;
		c.gridy = 1;
		this.add(playfield, c);
		
	}

	private void handleResize() {
		// TODO handle resize correctly!
		// calculateSizes();
		// repaint();
	}
	
	public void focusPlayfield() {
		playfield.requestFocusInWindow();
	}

	/**
	 * calculating sizes for this component an all of its child's
	 */
	private void calculateSizes() {
		
		int nonogramWidth = game.width();
		int nonogramHeight = game.height();

		playfieldDimension = new Dimension(
				(int) (boardDimension.getWidth() * 0.6),
				(int) (boardDimension.getHeight() * 0.6));
		
		columnCaptionDimension = new Dimension(
				(int) (boardDimension.getWidth() * 0.6),
				(int) (boardDimension.getHeight() * 0.3));
		
		rowCaptionDimension = new Dimension(
				(int) (boardDimension.getWidth() * 0.3),
				(int) (boardDimension.getHeight() * 0.6));
		
		statusFieldDimension = new Dimension(
				(int) (boardDimension.getWidth() * 0.3),
				(int) (boardDimension.getHeight() * 0.3));

		tileDimension = new Dimension(playfieldDimension.width / nonogramWidth,
				playfieldDimension.height / nonogramHeight);

	}

	public void refresh() {
		if (game != null) {
			if (game.usesMaxFailCount()) {
				statusField.setFailCount(Integer.toString(game
						.getFailCountLeft()));
			} else {
				statusField.setFailCount("");
			}
		}
	}

	public void refreshTime() {
		if (statusField != null) {
			statusField.refreshTime();
		}
	}
	
	public void solveGame() {
		playfield.solveField();
		previewArea.refreshPreview();
	}

//	@Override
//	public Dimension getMinimumSize() {
//		return boardDimension;
//	}
//
//	@Override
//	public Dimension getPreferredSize() {
//		return boardDimension;
//	}

	public Game getGame() {
		return game;
	}

	public BoardPreview getPreviewArea() {
		return previewArea;
	}

	public void startGame() {
		stopGame();
		if (game != null) {
			game.addGameListener(gameListener);
			game.startGame();
		}
		refresh();
	}

	public void stopGame() {
		if (game != null) {
			game.stopGame();
			game.removeGameListener(gameListener);
		}
		refresh();
	}

	public void pauseGame() {
		if (getGame() != null) {
			getGame().pauseGame();
		}
	}

	public void resumeGame() {
		if (getGame() != null) {
			getGame().resumeGame();
		}
	}

}
