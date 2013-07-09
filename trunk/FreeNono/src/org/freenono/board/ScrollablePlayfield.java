/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono.board;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.freenono.controller.Settings;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;

/**
 * Implements a scrollable playingfield.
 * 
 * @author Christian Wichmann
 */
public class ScrollablePlayfield extends JPanel implements Scrollable {

    private static final long serialVersionUID = -8124004468850971168L;

    // private static Logger logger =
    // Logger.getLogger(ScrollablePlayfield.class);

    private Nonogram pattern;
    private Dimension tileDimension;
    private BoardTileSetPlayfield playfield;
    private GameEventHelper eventHelper;

    /**
     * Constructor that sets some attributes.
     * @param eventHelper
     *            Event helper
     * @param tileDimension
     *            Dimension
     * @param pattern
     *            nonogram
     * @param settings
     *            Settings
     */
    public ScrollablePlayfield(final GameEventHelper eventHelper,
            final Dimension tileDimension, final Nonogram pattern,
            final Settings settings) {

        this.eventHelper = eventHelper;
        this.pattern = pattern;
        this.tileDimension = tileDimension;

        // this.setPreferredSize(new Dimension(tileDimension.width *
        // pattern.width(),
        // tileDimension.height * pattern.height()));

        initialize(settings);
    }

    /**
     * Initialize the play field (BoardTileSetPlayfield) and set layout.
     * 
     * @param settings
     *            settings object to pass to <code>BoardTileSetPlayfield</code>.
     */
    private void initialize(final Settings settings) {

        playfield = new BoardTileSetPlayfield(eventHelper, pattern, settings,
                tileDimension);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(playfield);

        // setting this component not opaque prevents a bug which
        // causes faulty painting of ColumnHeaderView and RowHeaderView
        // when scrolling the board
        setOpaque(false);
    }

    /**
     * Remove event helper.
     */
    public final void removeEventHelper() {

        playfield.removeEventHelper();
    }

    /**
     * Focus the playingfield.
     */
    public final void focusPlayfield() {

        // TODO fix focus problems??!
        playfield.requestFocusInWindow();
    }

    /**
     * Handle resizing of the window.
     * @param tileDimension
     *            New tile dimension
     */
    public final void handleResize(final Dimension tileDimension) {

        this.tileDimension = tileDimension;
        playfield.handleResize(tileDimension);
    }

    @Override
    public final Dimension getPreferredScrollableViewportSize() {

        return getPreferredSize();
    }

    @Override
    public final int getScrollableBlockIncrement(final Rectangle visibleRect,
            final int orientation, final int direction) {

        if (orientation == SwingConstants.VERTICAL) {
            return tileDimension.height;
        } else if (orientation == SwingConstants.HORIZONTAL) {
            return tileDimension.width;
        } else {
            return 0;
        }
    }

    @Override
    public final int getScrollableUnitIncrement(final Rectangle visibleRect,
            final int orientation, final int direction) {

        if (orientation == SwingConstants.VERTICAL) {
            return tileDimension.height;
        } else if (orientation == SwingConstants.HORIZONTAL) {
            return tileDimension.width;
        } else {
            return 0;
        }
    }

    @Override
    public final boolean getScrollableTracksViewportHeight() {

        // Do not force the height of this ScrollablePlayfield to match the
        // height of the viewport!
        return false;
    }

    @Override
    public final boolean getScrollableTracksViewportWidth() {

        // Do not force the width of this ScrollablePlayfield to match the width
        // of the viewport!
        return false;
    }

}
