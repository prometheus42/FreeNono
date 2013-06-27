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
import java.awt.GridLayout;

import javax.swing.JComponent;

import org.freenono.controller.Settings;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Nonogram;

/**
 * The class BoardTileSet provides an two-dimensional array of BoardTile's to
 * use as a building block to create the column and row captions as well as the
 * playfield.
 * 
 * As with the BoardComponent this class has to be newly instantiated each time
 * a new nonogram should be drawn!
 * 
 * @author Christian Wichmann
 */
public class BoardTileSet extends JComponent {

    private static final long serialVersionUID = 3230262588929434548L;

    protected GameEventHelper eventHelper;
    protected Nonogram pattern;
    protected Settings settings;

    protected static final int TILESET_WIDTH_DEFAULT = 10;
    protected static final int TILESET_HEIGHT_DEFAULT = 10;

    protected int tileSetWidth = TILESET_WIDTH_DEFAULT;
    protected int tileSetHeight = TILESET_HEIGHT_DEFAULT;
    protected Dimension tileDimension;

    protected BoardTile[][] board = null;

    protected boolean[][] isMarked;
    protected boolean[][] isMarkedOld;
    protected String[][] labels;
    protected String[][] labelsOld;

    protected int activeFieldColumn = 0;
    protected int activeFieldRow = 0;

    /**
     * Constructor, that sets the event helper, pattern, settings and tile
     * dimension for later use.
     * @param eventHelper
     *            Event helper
     * @param pattern
     *            Pattern
     * @param settings
     *            Settings
     * @param tileDimension
     *            Tile dimension
     */
    public BoardTileSet(final GameEventHelper eventHelper,
            final Nonogram pattern, final Settings settings,
            final Dimension tileDimension) {

        super();

        this.settings = settings;
        this.eventHelper = eventHelper;
        this.pattern = pattern;
        this.tileDimension = tileDimension;
    }

    /**
     * Initialize the tile set.
     */
    protected final void initialize() {

        // get array for tile attributes
        isMarked = new boolean[tileSetHeight][tileSetWidth];
        labels = new String[tileSetHeight][tileSetWidth];

        // build gridLayout
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(tileSetHeight);
        gridLayout.setColumns(tileSetWidth);
        gridLayout.setHgap(0);
        gridLayout.setVgap(0);
        setLayout(gridLayout);

        // fill grid with tiles
        board = new BoardTile[tileSetHeight][tileSetWidth];
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j] = new BoardTile(eventHelper,
                        settings.getColorModel(), tileDimension, j, i);
                // board[i][j].setMinimumSize(tileDimension);
                // board[i][j].setPreferredSize(tileDimension);
                board[i][j].setColumn(j);
                board[i][j].setRow(i);
                this.add(board[i][j]);
                isMarked[i][j] = false;
            }
        }
    }

    /**
     * Get preferred size.
     * @return Preferred size
     */
    @Override
    public final Dimension getPreferredSize() {

        return new Dimension(tileSetWidth * tileDimension.width, tileSetHeight
                * tileDimension.height);
    }

    /**
     * Handle resizing of window.
     * @param tileDimension
     *            New tile dimension
     */
    public final void handleResize(final Dimension tileDimension) {

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j].handleResize(tileDimension);
            }
        }
    }

    /**
     * Getter isMarked.
     * @return isMarked
     */
    public final boolean[][] getIsMarked() {
        return isMarked;
    }

    /**
     * Setter isMarked.
     * @param isMarked
     *            Is marked
     */
    public final void setIsMarked(final boolean[][] isMarked) {
        isMarkedOld = this.isMarked;
        this.isMarked = isMarked;

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                if (this.isMarked[i][j] != isMarkedOld[i][j]) {
                    board[i][j].setMarked(this.isMarked[i][j]);
                }
            }
        }
    }

    /**
     * Getter labels.
     * @return Labels
     */
    public final String[][] getLabels() {
        return labels;
    }

    /**
     * Setter labels.
     * @param labels
     *            Labels
     */
    public final void setLabels(final String[][] labels) {
        labelsOld = this.labels;
        this.labels = labels;

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                if (this.labels[i][j] != labelsOld[i][j]) {
                    board[i][j].setLabel(this.labels[i][j]);
                }
            }
        }
    }

    /**
     * Getter tileSetWidth.
     * @return tileSetWidth
     */
    public final int getTileSetWidth() {
        return tileSetWidth;
    }

    /**
     * Getter tileSetHeight.
     * @return tileSetHeight
     */
    public final int getTileSetHeight() {
        return tileSetHeight;
    }

}
