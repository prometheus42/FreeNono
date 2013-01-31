/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;

public class ScrollablePlayfield extends JPanel implements Scrollable {

	private static final long serialVersionUID = -8124004468850971168L;

	private Nonogram pattern;
	private Dimension tileDimension;
	private BoardTileSetPlayfield playfield;
	private boolean hidePlayfield;
	private GameEventHelper eventHelper;

	
	public ScrollablePlayfield(GameEventHelper eventHelper, Dimension d,
			Nonogram n, boolean hidePlayfield) {

		this.eventHelper = eventHelper;
		this.pattern = n;
		this.tileDimension = d;
		this.hidePlayfield = hidePlayfield;
		
		initialize();
	}

	private void initialize() {
		
		playfield = new BoardTileSetPlayfield(eventHelper, pattern,
				hidePlayfield, tileDimension);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(playfield);
		
		this.setOpaque(false);
		//setAutoscrolls(true); // enable synthetic drag events
	}
	
	public Dimension getPreferredSize() {

		return new Dimension(tileDimension.width * pattern.width() + 5,
				tileDimension.height * pattern.height() + 5);
	}
	
	public void focusPlayfield() {

		// TODO: fix focus problems??!
		//playfield.requestFocusInWindow();
	}

	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {

		if (orientation == SwingConstants.VERTICAL)
			return tileDimension.height;
		else if (orientation == SwingConstants.HORIZONTAL)
			return tileDimension.width;
		else
			return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {

		// Do not force the height of this Scrollable to match the height of the
		// viewport!
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {

		// Do not force the width of this Scrollable to match the width of the
		// viewport!
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		
		if (orientation == SwingConstants.VERTICAL)
			return tileDimension.height;
		else if (orientation == SwingConstants.HORIZONTAL)
			return tileDimension.width;
		else
			return 0;
	}

}
