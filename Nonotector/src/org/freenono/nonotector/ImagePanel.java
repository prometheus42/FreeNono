/*****************************************************************************
 * Nonotector - Detector to import nonograms from scanned images
 * Copyright (c) 2013 Christian Wichmann
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
package org.freenono.nonotector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.xml.transform.OutputKeys;


/**
 * Paints an image on the panel and allows selection of parts to process. The
 * selected parts are stored and painted by the class SelectionRectangle.
 * 
 * Heavily inspired by John B. Matthews GraphPanel: 
 * https://sites.google.com/site/drjohnbmatthews/graphpanel
 * 
 * @author Christian Wichmann
 */
public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -7882855269000266277L;

	private File currentImageFile;
	private BufferedImage image;

	private static final int MAX_WIDTH = 800;
	private static final int MAX_HEIGHT = 800;
	private int width = MAX_WIDTH;
	private int height = MAX_HEIGHT;
	private double scaleFactor = 1;

	private Rectangle mouseRect = new Rectangle();
	private Point startPoint = null;
	private Point stopPoint = null;
	private List<SelectionRectangle> rectangles = new ArrayList<SelectionRectangle>();

	
	public ImagePanel(File imageFile) {

		currentImageFile = imageFile;

		try {
			image = ImageIO.read(currentImageFile);
		} catch (IOException e) {
			
			// TODO handle exception...
			e.printStackTrace();
		}

		setOpaque(true);
		
		addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
        
        calculateScaling();
		repaint();
	}

	@Override
    public Dimension getPreferredSize() {
		
        return new Dimension(width, height);
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		g.drawImage(image, 0, 0, width, height, null);
		
		// draw selection rectangle
		g.setColor(Color.RED);
		g.drawRect(mouseRect.x, mouseRect.y, mouseRect.width, mouseRect.height);
		
		// draw stored rectangles
		g.setColor(Color.RED);
		for (SelectionRectangle rectangle : rectangles) {
			
			rectangle.draw(g);	
		}
	}
	
	private void calculateScaling() {

		if (((double) image.getWidth() / MAX_WIDTH) >= 
				((double) image.getHeight() / MAX_HEIGHT)) {

			scaleFactor = (double) image.getWidth() / MAX_WIDTH;
			width = MAX_WIDTH;
			height = (int) Math.round(image.getHeight() / scaleFactor);
		}
		else {
			
			scaleFactor = (double) image.getHeight() / MAX_HEIGHT;
			height = MAX_HEIGHT;
			width = (int) Math.round(image.getWidth() / scaleFactor);
		}
	}
	
	public void convert() {
		
		// convert selections from image to nonograms
		for (SelectionRectangle rectagle : rectangles) {
			convertSelection(rectagle);
		}
	}
	
	
	private void convertSelection(SelectionRectangle rectangle) {
		
		boolean nonoData[][] = new boolean[rectangle.getNonogramHeight()]
				[rectangle.getNonogramWidth()];
		
		int baseX, baseY, value, cellWidth, cellHeight, cellSize, luminance;
		Color pixel;
		
		cellWidth = (int) (rectangle.getWidth() * scaleFactor / rectangle.getNonogramWidth());
		cellHeight = (int) (rectangle.getHeight() * scaleFactor / rectangle.getNonogramHeight());
		cellSize = (cellWidth + cellHeight) / 2;
		
		baseX = (int) (rectangle.getX() * scaleFactor + cellSize / 2);
		baseY = (int) (rectangle.getY() * scaleFactor + cellSize / 2);
		
		for (int i = 0; i < rectangle.getNonogramHeight(); i++) {
			
			for (int j = 0; j < rectangle.getNonogramWidth(); j++) {
				
				// get RGB value for pixel and calculate luminance
				// formula for luminance according to http://home.arcor.de/ulile/node54.html
				value = image
						.getRGB(baseX + cellSize * j, baseY + cellSize * i);
				pixel = new Color(value);
				luminance = (int) (0.3 * pixel.getRed() + 0.59
						* pixel.getGreen() + 0.11 * pixel.getBlue());
		
				if (luminance > 128) {
					nonoData[i][j] = false;
				}
				else {
					nonoData[i][j] = true;
				}
			}
		}
		
		NonogramStore.addNonogram(rectangle, nonoData);
	}
	
	public void rotateImage(int degrees) {
		
		AffineTransform transform = new AffineTransform();
		transform.rotate(Math.PI / 180 * degrees, image.getWidth() / 2,
				image.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_BICUBIC);
		image = op.filter(image, null);
	}
	
	public void deleteSelections() {
		
		rectangles.clear();
		mouseRect.setBounds(0, 0, 0, 0);
		repaint();
	}
	
	private void searchHotspot(Point p) {
				
		// scale point from window to point from image
		p.x *= scaleFactor;
		p.y *= scaleFactor;
		
		// search for nonogram surrounding clicked point and show it on screen
		NonogramSearcher searcher = new NonogramSearcher(image, p);
		Rectangle r = searcher.getResult();
		SelectionRectangle s = new SelectionRectangle(
				(int)(r.x / scaleFactor), 
				(int)(r.y	/ scaleFactor), 
				(int)(r.width / scaleFactor), 
				(int)(r.height / scaleFactor),
				"");
		rectangles.add(s);
		repaint();
		
		// ask user for information about new nonogram
		Nonotector.propertyDialog.setVisible(true);
		
		if (Nonotector.propertyDialog.getLastClicked() == 
				Nonotector.propertyDialog.OK_BUTTON) {
			
			s.setLabel(NonogramStore.getName());
			s.setNonogramHeight(NonogramStore.getHeight());
			s.setNonogramWidth(NonogramStore.getWidth());
			s.setDifficulty(NonogramStore.getDifficulty());
			s.setCreator(NonogramStore.getCreator());
			s.setLevel(NonogramStore.getLevel());
			s.setDescription(NonogramStore.getDescription());
		} else {
			rectangles.remove(s);
			repaint();
		}
	}

	
	
	/***** Mouse handling code *****/
	private class MouseHandler extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {

			if (!Nonotector.searchBounds) {
				
				stopPoint = e.getPoint();
				mouseRect.setBounds(startPoint.x, startPoint.y, stopPoint.x
						- startPoint.x, stopPoint.y - startPoint.y);

				// ask user for information and store rectangle
				Nonotector.propertyDialog.setVisible(true);

				SelectionRectangle s = new SelectionRectangle(mouseRect.x,
						mouseRect.y, mouseRect.width, mouseRect.height,
						NonogramStore.getName());
				s.setNonogramHeight(NonogramStore.getHeight());
				s.setNonogramWidth(NonogramStore.getWidth());
				s.setDifficulty(NonogramStore.getDifficulty());
				s.setCreator(NonogramStore.getCreator());
				s.setLevel(NonogramStore.getLevel());
				s.setDescription(NonogramStore.getDescription());

				rectangles.add(s);

				e.getComponent().repaint();
			}
			else {
				
				searchHotspot(e.getPoint());
			}
			
			// TODO add context menu for deleting rectangles
			// if (e.isPopupTrigger()) {
			// showPopup(e);
			// }
		}
        
        @Override
        public void mousePressed(MouseEvent e) {
        	
        	if (!Nonotector.searchBounds) {
        		
        		startPoint = e.getPoint();
            	stopPoint = e.getPoint();
        	}
        }
	}
	
	 private class MouseMotionHandler extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
        	
			if (!Nonotector.searchBounds) {

				stopPoint = e.getPoint();

				// calculate bounds however the selection is drawn
				mouseRect.setBounds(Math.min(startPoint.x, stopPoint.x),
						Math.min(startPoint.y, stopPoint.y),
						Math.abs(startPoint.x - stopPoint.x),
						Math.abs(startPoint.y - stopPoint.y));

				e.getComponent().repaint();
			}
  	    }
	 }
}
