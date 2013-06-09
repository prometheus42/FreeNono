/*****************************************************************************
 * Nonotector - Detector to import nonograms from scanned images
 * Copyright (c) 2013 by FreeNono Development Team
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class NonogramSearcher {

	private int threshold = 200; 
	private int boundaryLeft;
	private int boundaryRight;
	private int boundaryUp;
	private int boundaryDown;
	
	private Rectangle result;
	private Point pointOfOrigin;
	private BufferedImage image;
	private Color pixel;
	
	
	public NonogramSearcher(BufferedImage image, Point pointOfOrigin) {
		
		this.pointOfOrigin = pointOfOrigin;
		this.image = image;
		
		result = new Rectangle();
		
		searchBounds();
	}
	
	private void searchBounds() {
		
		boundaryLeft = pointOfOrigin.x;
		boundaryRight = pointOfOrigin.x;
		boundaryUp = pointOfOrigin.y;
		boundaryDown = pointOfOrigin.y;

		if (luminance(pointOfOrigin) < threshold) {

			searchUpperLeft(pointOfOrigin);
			searchBelowRight(pointOfOrigin);

			result.setBounds(boundaryLeft, boundaryUp, boundaryRight
					- boundaryLeft, boundaryDown - boundaryUp);
		}
	}

	private void searchUpperLeft(Point p) {
		
		Point startPoint = new Point(p);
		
		// go up as far as possible
		p.y -= 1;
		while (regionLuminance(p) < threshold) {
		
			p.y -= 1;
		}
		p.y += 1;
		
		// go left as far as possible
		p.x -= 1;
		while (regionLuminance(p) < threshold) {
			
			p.x -= 1;
		}
		p.x += 1;
		
		// break recursion if point doesn't move anymore
		if (startPoint.equals(p)) {
			
			boundaryLeft = p.x;
			boundaryUp = p.y;
			return;
		}
		else {
		
			searchUpperLeft(p);
		}
	}
	
	private void searchBelowRight(Point p) {
		
		Point startPoint = new Point(p);
		
		// go down as far as possible
		p.y += 1;
		while (regionLuminance(p) < threshold) {
			
			p.y += 1;
		}
		p.y -= 1;
		
		// go right as far as possible
		p.x += 1;
		while (regionLuminance(p) < threshold) {
			
			p.x += 1;
		}
		p.x -= 1;
		
		// break recursion if point doesn't move anymore
		if (startPoint.equals(p)) {

			boundaryRight = p.x;
			boundaryDown = p.y;
			return;
		} 
		else {

			searchBelowRight(p);
		}
	}
	
	private int regionLuminance(Point p) {
		
		int average = 0;
		
		average += luminance(p) * 5;
		average += luminance(new Point(p.x+1, p.y));
		average += luminance(new Point(p.x+1, p.y+1));
		average += luminance(new Point(p.x, p.y+1));
		average += luminance(new Point(p.x-1, p.y+1));
		average += luminance(new Point(p.x-1, p.y));
		average += luminance(new Point(p.x-1, p.y-1));
		average += luminance(new Point(p.x, p.y-1));
		average += luminance(new Point(p.x+1, p.y-1));
		
		return (int) (average / 13);
	}
	
	private int luminance(Point p) {

		pixel = new Color(image.getRGB(p.x, p.y));
		return ((int) (0.3 * pixel.getRed() + 0.59 * pixel.getGreen() 
				+ 0.11 * pixel.getBlue()));
	}

	public Rectangle getResult() {
		
		return result;
	}
}
