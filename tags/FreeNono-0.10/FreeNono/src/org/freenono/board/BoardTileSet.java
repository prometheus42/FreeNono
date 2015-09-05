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
import org.freenono.model.data.Nonogram;

/**
 * The class BoardTileSet provides an two-dimensional array of BoardTile's to use as a building
 * block to create the column and row captions as well as the playfield.
 *
 * As with the BoardComponent this class has to be newly instantiated each time a new nonogram
 * should be drawn!
 *
 * @author Christian Wichmann
 */
public class BoardTileSet extends JComponent {

    private static final long serialVersionUID = 3230262588929434548L;

    private GameEventHelper eventHelper;

    private final Nonogram pattern;
    private final Settings settings;

    private static final int TILESET_WIDTH_DEFAULT = 10;
    private static final int TILESET_HEIGHT_DEFAULT = 10;

    private int tileSetWidth = TILESET_WIDTH_DEFAULT;
    private int tileSetHeight = TILESET_HEIGHT_DEFAULT;
    private Dimension tileDimension;

    private BoardTile[][] board = null;
    private String[][] labels;
    private String[][] labelsOld;

    private int activeFieldColumn = 0;
    private int activeFieldRow = 0;

    /**
     * Constructor, that sets the event helper, pattern, settings and tile dimension for later use.
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
    public BoardTileSet(final GameEventHelper eventHelper, final Nonogram pattern, final Settings settings, final Dimension tileDimension) {

        super();

        this.eventHelper = eventHelper;
        this.pattern = pattern;
        this.tileDimension = tileDimension;
        this.settings = settings;
    }

    /**
     * Initialize the tile set.
     */
    protected final void buildBoardGrid() {

        // get array for tile attributes
        labels = new String[tileSetHeight][tileSetWidth];

        // build gridLayout
        final GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(tileSetHeight);
        gridLayout.setColumns(tileSetWidth);
        gridLayout.setHgap(0);
        gridLayout.setVgap(0);
        setLayout(gridLayout);

        // fill grid with tiles
        board = new BoardTile[tileSetHeight][tileSetWidth];
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j] = new BoardTile(eventHelper, settings.getColorModel(), settings.getTextColor(), tileDimension, j, i);
                // board[i][j].setMinimumSize(tileDimension);
                // board[i][j].setPreferredSize(tileDimension);
                board[i][j].setColumn(j);
                board[i][j].setRow(i);
                this.add(board[i][j]);
            }
        }
    }

    /**
     * Get preferred size.
     * @return Preferred size
     */
    @Override
    public final Dimension getPreferredSize() {

        return new Dimension(tileSetWidth * tileDimension.width, tileSetHeight * tileDimension.height);
    }

    /**
     * Handle resizing of window.
     * @param tileDimension
     *            New tile dimension
     */
    public final void handleResize(final Dimension tileDimension) {

        this.tileDimension = tileDimension;

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j].handleResize(tileDimension);
            }
        }
    }

    /**
     * Sets all labels for this tile set. All labels will be set to given label and store it
     * internally.
     *
     * @param newLabels
     *            two-dimensional array of labels
     */
    protected final void setLabels(final String[][] newLabels) {

        labelsOld = labels;
        labels = newLabels;

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                if (labels[i][j] != null && !(labels[i][j].equals(labelsOld[i][j]))) {
                    board[i][j].setLabel(labels[i][j]);
                }
            }
        }
    }

    /**
     * Gets width of tile set.
     *
     * @return tile set width
     */
    protected final int getTileSetWidth() {

        return tileSetWidth;
    }

    /**
     * Gets height of tile set.
     *
     * @return tile set height
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
     * Gets dimension of one tile in this tile set.
     *
     * @return dimension of tile
     */
    protected final Dimension getTileDimension() {

        return tileDimension;
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
