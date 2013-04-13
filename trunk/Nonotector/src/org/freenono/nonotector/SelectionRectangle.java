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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import org.freenono.model.DifficultyLevel;


public class SelectionRectangle {
	
	private Color drawColor = Color.RED;
	private String label;
	private String creator;
	private int nonogramWidth = 15;
	private int nonogramHeight = 15;
	private int level = 1;
	private DifficultyLevel difficulty = DifficultyLevel.undefined;
	private Rectangle b = new Rectangle();
	private boolean selected = false;
	
	
	public SelectionRectangle(int x, int y, int width, int height, String label) {
		
		this.b = new Rectangle(x, y, width, height);
		this.label = label;
	}
	
	public SelectionRectangle(Rectangle r, String label) {
		
		this.b = r;
		this.label = label;
	}
	
	public void draw(Graphics g) {

		// paint selection
		g.setColor(Color.RED);
		if (selected) {
			g.setColor(Color.BLUE);
		}
		g.drawRect(b.x, b.y, b.width, b.height);

		// paint label for selection
		Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
		// g2d.setStroke(new BasicStroke(8,
		//    BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawString(label, b.x + 5, b.y + b.height - 5);
	}


	public boolean contains(Point p) {
		
        return b.contains(p);
    }
	
	public Color getDrawColor() {
		
		return drawColor;
	}

	public void setDrawColor(Color drawColor) {
		
		this.drawColor = drawColor;
	}


	public boolean isSelected() {
		
		return selected;
	}


	public void setSelected(boolean selected) {
		
		this.selected = selected;
	}

	public int getWidth() {

		return b.width;
	}

	public int getHeight() {

		return b.height;
	}

	public int getX() {

		return b.x;
	}

	public int getY() {

		return b.y;
	}

	public String getLabel() {
		
		return label;
	}

	public void setLabel(String label) {
		
		this.label = label;
	}

	public int getNonogramWidth() {
		
		return nonogramWidth;
	}

	public void setNonogramWidth(int nonogramWidth) {
		
		this.nonogramWidth = nonogramWidth;
	}

	public int getNonogramHeight() {
		
		return nonogramHeight;
	}

	public void setNonogramHeight(int nonogramHeight) {
		
		this.nonogramHeight = nonogramHeight;
	}

	public int getLevel() {
		
		return level;
	}

	public void setLevel(int level) {
		
		this.level = level;
	}

	public String getCreator() {
		
		return creator;
	}

	public void setCreator(String creator) {
		
		this.creator = creator;
	}

	public DifficultyLevel getDifficulty() {
		
		return difficulty;
	}

	public void setDifficulty(DifficultyLevel difficulty) {
		
		this.difficulty = difficulty;
	}
}
