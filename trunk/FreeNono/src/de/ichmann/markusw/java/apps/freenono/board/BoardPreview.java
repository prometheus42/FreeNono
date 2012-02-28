package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;

import de.ichmann.markusw.java.apps.freenono.event.GameListener;
import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.GameState;
import de.ichmann.markusw.java.apps.freenono.model.Token;

public class BoardPreview extends JComponent {

	private static final long serialVersionUID = -7154680728413126386L;

	private Game game;
	private int boardWidth;
	private int boardHeight;
	private Image previewImage = null;
	
	private GameListener gameListener = new GameListener() {

		@Override
		public void Timer() {
		}

		@Override
		public void StateChanged(GameState oldState, GameState newState) {
		}

		@Override
		public void FieldOccupied(int x, int y) {
			refreshPreview();
		}

		@Override
		public void FieldMarked(int x, int y) {
		}

		@Override
		public void ActiveFieldChanged(int x, int y) {
		}
	};

	public BoardPreview(Game game) {

		this.game = game;
		this.boardWidth = game.width();
		this.boardHeight = game.height();

		createImage();

		Border border = new BevelBorder(BevelBorder.RAISED);
		this.setBorder(border);
		
		game.getEventHelper().addGameListener(gameListener);
		
	}

	private void createImage() {

		byte pixelsAsByte[] = new byte[boardWidth * boardHeight];

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				pixelsAsByte[(y * boardWidth) + x] = (byte) 
						(game.getFieldValue(x, y) == Token.OCCUPIED ? 0 : 255);
			}
		}

		BufferedImage image = new BufferedImage(boardHeight, boardWidth,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = image.getRaster();
		raster.setDataElements(0, 0, boardHeight, boardWidth, pixelsAsByte);

		previewImage = image;
	}

	public void refreshPreview() {
		createImage();
		repaint();
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(50, 50);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(102, 102);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(previewImage, 2, 2, 100, 100, this);
	}
}
