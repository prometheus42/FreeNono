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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;

/**
 * Builds a preview image of the running game represented by the Game object. At
 * changes on the board, the image is rebuild by calling the refreshPreview()
 * method. This class is Cloneable, so that it can be used in more than one gui
 * component at a time.
 * 
 * (More information on scaling of images:
 * http://today.java.net/pub/a/today/2007
 * /04/03/perils-of-image-getscaledinstance.html)
 * 
 */
public class BoardPreview extends JComponent implements Cloneable {

	private static final long serialVersionUID = -7154680728413126386L;

	private Nonogram pattern;
	private GameEventHelper eventHelper;

	private int boardWidth;
	private int boardHeight;

	private static final int previewWidth = 75;
	private static final int previewHeight = 75;

	private double newWidth;
	private double newHeight;

	private double offsetWidth;
	private double offsetHeight;

	private BufferedImage previewImage = null;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void OccupyField(FieldControlEvent e) {
			refreshPreview();
		}

	};

	public BoardPreview(Nonogram pattern) {

		this.pattern = pattern;
		this.boardWidth = pattern.width();
		this.boardHeight = pattern.height();

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

		// TODO: change this method to listen on event for painting the preview
//		for (int y = 0; y < boardHeight; y++) {
//			for (int x = 0; x < boardWidth; x++) {
//				pixelsAsByte[(y * boardWidth) + x] = (byte) (game
//						.getFieldValue(x, y) == Token.OCCUPIED ? 0 : 255);
//			}
//		}

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
