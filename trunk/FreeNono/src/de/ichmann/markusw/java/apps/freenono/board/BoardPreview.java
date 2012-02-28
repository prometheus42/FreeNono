package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;

public class BoardPreview extends JComponent {

	private static final long serialVersionUID = -7154680728413126386L;

	private int boardWidth;
	private int boardHeight;
	private boolean[][] board = null;
	private Image previewImage = null;

	public BoardPreview(int boardWidth, int boardHeight, boolean[][] board) {
		this.board = board;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		createImage();
		
		Border border = new BevelBorder(BevelBorder.RAISED);
		this.setBorder(border);
	}

	private void createImage() {
		
		byte pixelsAsByte[] = new byte[boardWidth * boardHeight];

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				pixelsAsByte[(y * boardHeight) + x] = (byte) 
						(board[y][x] == true ? 0 : 255);
				// (game.getFieldValue(x, y) == Token.OCCUPIED ? 0 : 255);
			}
		}

		BufferedImage image = new BufferedImage(boardHeight, boardWidth,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = image.getRaster();
		raster.setDataElements(0, 0, boardHeight, boardWidth, pixelsAsByte);

		previewImage = image;
	}
	
	public void refresh(boolean[][] board) {
		this.board = board;
		createImage();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(102,102);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(previewImage, 2, 2, 100, 100, this);
	}
}
