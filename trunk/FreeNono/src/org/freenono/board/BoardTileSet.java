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

    private GameEventHelper eventHelper;

    private Nonogram pattern;
    private Settings settings;

    private static final int TILESET_WIDTH_DEFAULT = 10;
    private static final int TILESET_HEIGHT_DEFAULT = 10;

    private int tileSetWidth = TILESET_WIDTH_DEFAULT;
    private int tileSetHeight = TILESET_HEIGHT_DEFAULT;
    private Dimension tileDimension;

    private BoardTile[][] board = null;

    private boolean[][] isMarked;
    private boolean[][] isMarkedOld;
    private String[][] labels;
    private String[][] labelsOld;

    private int activeFieldColumn = 0;
    private int activeFieldRow = 0;

    /**
     * Constructor, that sets the event helper, pattern, settings and tile
     * dimension for later use.
     * 
     * @param eventHelper
     *            game event helper
     * @param pattern
     *            nonogram pattern
     * @param settings
     *            settings
     * @param tileDimension
     *            tile dimension
     */
    public BoardTileSet(final GameEventHelper eventHelper,
            final Nonogram pattern, final Settings settings,
            final Dimension tileDimension) {

        super();

        this.eventHelper = eventHelper;
        this.pattern = pattern;
        this.tileDimension = tileDimension;
        this.settings = settings;
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
    protected final int getTileSetWidth() {

        return tileSetWidth;
    }

    /**
     * Getter tileSetHeight.
     * @return tileSetHeight
     */
    protected final int getTileSetHeight() {

        return tileSetHeight;
    }

    /**
     * Sets width of tile set.
     * 
     * @param tileSetWidth
     *            width of tile set to set
     */
    protected final void setTileSetWidth(final int tileSetWidth) {

        this.tileSetWidth = tileSetWidth;
    }

    /**
     * Sets height of tile set.
     * 
     * @param tileSetHeight
     *            height of tile set to set
     */
    protected final void setTileSetHeight(final int tileSetHeight) {

        this.tileSetHeight = tileSetHeight;
    }

    /**
     * Gets column of active field.
     * 
     * @return column of active field
     */
    public final int getActiveFieldColumn() {

        return activeFieldColumn;
    }

    /**
     * Sets column of active field.
     * 
     * @param activeFieldColumn
     *            column of active field to be set
     */
    public final void setActiveFieldColumn(final int activeFieldColumn) {

        this.activeFieldColumn = activeFieldColumn;
    }

    /**
     * Gets row of active field.
     * 
     * @return row of active field
     */
    public final int getActiveFieldRow() {

        return activeFieldRow;
    }

    /**
     * Sets row of active field.
     * 
     * @param activeFieldRow
     *            row of active field to be set
     */
    public final void setActiveFieldRow(final int activeFieldRow) {

        this.activeFieldRow = activeFieldRow;
    }

    /**
     * Gets game event helper.
     * 
     * @return game event helper
     */
    protected final GameEventHelper getEventHelper() {

        return eventHelper;
    }

    /**
     * Gets array with board tiles that this TileSet consists of.
     * 
     * @return array of board tiles
     */
    protected final BoardTile[][] getBoard() {

        return board;
    }

    /**
     * Sets game event helper to fire events.
     * 
     * @param eventHelper
     *            game event helper to be set
     */
    protected final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
    }

    /**
     * Gets nonogram pattern from superclass.
     * 
     * @return nonogram pattern
     */
    public final Nonogram getPattern() {
        return pattern;
    }

    /**
     * Gets settings object stored in superclass.
     * 
     * @return settings object
     */
    public final Settings getSettings() {
        return settings;
    }

}
