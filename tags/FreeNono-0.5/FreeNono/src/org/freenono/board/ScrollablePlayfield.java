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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;

public class ScrollablePlayfield extends JPanel implements Scrollable {

	private static final long serialVersionUID = -8124004468850971168L;

	private static Logger logger = Logger.getLogger(ScrollablePlayfield.class);

	private Settings settings = null;
	private Nonogram pattern;
	private Dimension tileDimension;
	private BoardTileSetPlayfield playfield;
	private GameEventHelper eventHelper;

	
	public ScrollablePlayfield(GameEventHelper eventHelper, Dimension d,
			Nonogram n, Settings settings) {

		this.eventHelper = eventHelper;
		this.pattern = n;
		this.tileDimension = d;
		this.settings = settings;
		
//		this.setPreferredSize(new Dimension(tileDimension.width * pattern.width(),
//				tileDimension.height * pattern.height()));
		
		initialize();
	}

	private void initialize() {
		
		playfield = new BoardTileSetPlayfield(eventHelper, pattern,
				settings, tileDimension);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(playfield);
		
		// setting this component not opaque prevents a bug which
		// causes faulty painting of ColumnHeaderView and RowHeaderView
		// when scrolling the board
		this.setOpaque(false);
		
		// enable synthetic drag events
		this.setAutoscrolls(true);
		this.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {

				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		        ((ScrollablePlayfield)e.getSource()).scrollRectToVisible(r);
				logger.debug("drag event");
			}
		});
	}

	public void removeEventHelper() {
		
		playfield.removeEventHelper();
	}

	public void focusPlayfield() {

		// TODO: fix focus problems??!
		playfield.requestFocusInWindow();
	}
	
	public void handleResize(Dimension tileDimension) {
		
		this.tileDimension = tileDimension;
		playfield.handleResize(tileDimension);
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
