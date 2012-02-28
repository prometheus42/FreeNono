package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;

import de.ichmann.markusw.java.apps.freenono.event.GameAdapter;
import de.ichmann.markusw.java.apps.freenono.event.GameEvent;
import de.ichmann.markusw.java.apps.freenono.event.GameEventHelper;
import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.Token;

/**
 * Builds a preview image of the running game represented by the Game object. At
 * changes on the board, the image is rebuild by calling the refreshPreview()
 * method. This class is Cloneable, so that it can be used in more than one gui
 * component at a time.
 * 
 * (More information on scaling of images: http://today.java.net/pub/a/today/2007
 * /04/03/perils-of-image-getscaledinstance.html)
 * 
 */
public class BoardPreview extends JComponent implements Cloneable {

	private static final long serialVersionUID = -7154680728413126386L;

	private Game game;
	private GameEventHelper eventHelper;

	private int boardWidth;
	private int boardHeight;

	private static final int previewWidth = 100;
	private static final int previewHeight = 100;

	private double newWidth;
	private double newHeight;

	private double offsetWidth;
	private double offsetHeight;

	private BufferedImage previewImage = null;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void FieldOccupied(GameEvent e) {
			refreshPreview();
		}

	};

	public BoardPreview(Game game) {

		this.game = game;
		this.boardWidth = game.width();
		this.boardHeight = game.height();

		createImage();

		Border border = new BevelBorder(BevelBorder.RAISED);
		this.setBorder(border);

	}

	public void refreshPreview() {
		createImage();
		calculateValues();
		repaint();
	}

	private void createImage() {

		byte pixelsAsByte[] = new byte[boardWidth * boardHeight];

		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				pixelsAsByte[(y * boardWidth) + x] = (byte) (game
						.getFieldValue(x, y) == Token.OCCUPIED ? 0 : 255);
			}
		}

		// get image object and fill it with the stored pixel values
		BufferedImage image = new BufferedImage(boardWidth, boardHeight,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = image.getRaster();
		raster.setDataElements(0, 0, boardWidth, boardHeight, pixelsAsByte);
		previewImage = image;

	}

	private void calculateValues() {

		if (boardWidth < boardHeight) {
			newHeight = previewHeight;
			newWidth = boardWidth * newHeight / boardHeight;
			offsetWidth = (newHeight - newWidth) / 2;
			offsetHeight = 0;
		} else {
			newWidth = previewWidth;
			newHeight = boardHeight * newWidth / boardWidth;
			offsetWidth = 0;
			offsetHeight = (newWidth - newHeight) / 2;
		}

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(previewImage, (int) offsetWidth, (int) offsetHeight,
				(int) newWidth, (int) newHeight, null);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(previewWidth, previewHeight);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(previewWidth, previewHeight);
	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	@Override
	protected void finalize() throws Throwable {
		eventHelper.removeGameListener(gameAdapter);
		super.finalize();
	}

	public BoardPreview clone() {
		Object theClone = null;
		try {
			theClone = super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return (BoardPreview) theClone;
	}
}
