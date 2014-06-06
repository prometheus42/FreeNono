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

import org.freenono.board.BoardTile.SelectionMarkerType;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.model.CaptionOrientation;
import org.freenono.model.data.Nonogram;

/**
 * Sets up captions around the playfield (BoardTileSetPlayfield). Dependent on
 * the orientation of the instance (ORIENTATION_COLUMN, ORIENTATION_ROW) the
 * captions are horizontally or vertically painted.
 * 
 * @author Christian Wichmann
 */
public class BoardTileSetCaption extends BoardTileSet {

    private static final long serialVersionUID = -3593247761289294060L;

    private CaptionOrientation orientation;

    private static final int MIN_TILESET_HEIGHT = 5;
    private static final int MIN_TILESET_WIDTH = 5;

    private int columnCaptionCount;
    private int rowCaptionCount;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void optionsChanged(final ProgramControlEvent e) {

            for (int i = 0; i < getTileSetHeight(); i++) {
                for (int j = 0; j < getTileSetWidth(); j++) {

                    getBoard()[i][j].setColorModel(getSettings().getColorModel());
                    getBoard()[i][j].repaint();
                }
            }
        }

        @Override
        public void changeActiveField(final FieldControlEvent e) {

            if (orientation == CaptionOrientation.ORIENTATION_COLUMN) {
                // if column caption...
                // XXX The following if statements prevent OutOfBounds
                // Exceptions on all four board accesses. I have no idea WHY
                // these exceptions are thrown?
                if (getActiveFieldColumn() < getPattern().width()) {
                    getBoard()[getTileSetHeight() - 1][getActiveFieldColumn()].setSelectionMarkerActive(false);
                }
                setActiveFieldColumn(e.getFieldColumn());
                setActiveFieldRow(e.getFieldRow());
                if (getActiveFieldColumn() < getPattern().width()) {
                    getBoard()[getTileSetHeight() - 1][getActiveFieldColumn()].setSelectionMarkerActive(true);
                }
            } else if (orientation == CaptionOrientation.ORIENTATION_ROW) {
                // ...else is row caption
                if (getActiveFieldRow() < getPattern().height()) {
                    getBoard()[getActiveFieldRow()][getTileSetWidth() - 1].setSelectionMarkerActive(false);
                }
                setActiveFieldColumn(e.getFieldColumn());
                setActiveFieldRow(e.getFieldRow());
                if (getActiveFieldRow() < getPattern().height()) {
                    getBoard()[getActiveFieldRow()][getTileSetWidth() - 1].setSelectionMarkerActive(true);
                }
            }
        }

        @Override
        public void crossOutCaption(final FieldControlEvent e) {

            if (getSettings().getCrossCaptions()) {

                if (e.getOrientation() == orientation) {

                    if (orientation == CaptionOrientation.ORIENTATION_COLUMN) {
                        getBoard()[getTileSetHeight() - 2 - getPattern().getColumnNumbersCount(e.getFieldColumn()) + e.getCaption()]
                                [e.getFieldColumn()].setCrossedSingleLine(true);

                    } else if (orientation == CaptionOrientation.ORIENTATION_ROW) {
                        getBoard()[e.getFieldRow()][getTileSetWidth() - 2 - getPattern().getLineNumberCount(e.getFieldRow())
                                + e.getCaption()].setCrossedSingleLine(true);

                    }
                }
            }
        }
    };

    /**
     * Constructor for BoardTileSetCaption. Initializes everything and paints
     * the component.
     * 
     * @param eventHelper
     *            game event helper
     * @param pattern
     *            nonogram pattern
     * @param settings
     *            settings object
     * @param orientation
     *            sets if BoardTileSet should be used for columns or for rows
     * @param tileDimension
     *            tile dimension
     */
    public BoardTileSetCaption(final GameEventHelper eventHelper, final Nonogram pattern, final Settings settings,
            final CaptionOrientation orientation, final Dimension tileDimension) {

        super(eventHelper, pattern, settings, tileDimension);

        eventHelper.addGameListener(gameAdapter);

        this.orientation = orientation;

        // set tileSet height and width according to necessary numbers of tiles
        columnCaptionCount = pattern.getColumnCaptionHeight();
        rowCaptionCount = pattern.getLineCaptionWidth();
        if (orientation == CaptionOrientation.ORIENTATION_COLUMN) {
            setTileSetWidth(pattern.width());
            setTileSetHeight(Math.max(columnCaptionCount + 1, MIN_TILESET_HEIGHT));
        } else if (orientation == CaptionOrientation.ORIENTATION_ROW) {
            setTileSetWidth(Math.max(rowCaptionCount + 1, MIN_TILESET_WIDTH));
            setTileSetHeight(pattern.height());
        }

        buildBoardGrid();

        // setting caption components opaque so background of mainUI can be seen
        setOpaque(true);

        paintBorders();
        paintSelectionMarkers();
        paintNumbers();
    }

    /**
     * Remove event helper.
     */
    public final void removeEventHelper() {

        if (getEventHelper() != null) {

            getEventHelper().removeGameListener(gameAdapter);
            setEventHelper(null);
        }
    }

    /**
     * Paint the borders of the TileSet.
     */
    private void paintBorders() {

        if (orientation == CaptionOrientation.ORIENTATION_COLUMN) {
            // column borders
            for (int i = 0; i < getTileSetHeight(); i++) {
                for (int j = 0; j < getTileSetWidth(); j++) {
                    getBoard()[i][j].setDrawBorderWest(true);
                    if ((j + 1) % MIN_TILESET_WIDTH == 0 || (j + 1) == getTileSetWidth()) {
                        getBoard()[i][j].setDrawBorderEast(true);
                    }
                }
            }
        } else if (orientation == CaptionOrientation.ORIENTATION_ROW) {
            // row borders
            for (int i = 0; i < getTileSetHeight(); i++) {
                for (int j = 0; j < getTileSetWidth(); j++) {
                    getBoard()[i][j].setDrawBorderNorth(true);
                    if ((i + 1) % MIN_TILESET_HEIGHT == 0 || (i + 1) == getTileSetHeight()) {
                        getBoard()[i][j].setDrawBorderSouth(true);
                    }
                }
            }
        }
    }

    /**
     * Paint the selection markers.
     */
    private void paintSelectionMarkers() {

        if (orientation == CaptionOrientation.ORIENTATION_COLUMN) {
            // column selection markers
            for (int i = 0; i < getTileSetWidth(); i++) {
                getBoard()[getTileSetHeight() - 1][i].setSelectionMarker(SelectionMarkerType.SELECTION_MARKER_DOWN);
                if (i == getActiveFieldColumn()) {
                    getBoard()[getTileSetHeight() - 1][i].setSelectionMarkerActive(true);
                }
            }
        } else if (orientation == CaptionOrientation.ORIENTATION_ROW) {
            // row selection markers
            for (int i = 0; i < getTileSetHeight(); i++) {
                getBoard()[i][getTileSetWidth() - 1].setSelectionMarker(SelectionMarkerType.SELECTION_MARKER_RIGHT);
                if (i == getActiveFieldRow()) {
                    getBoard()[i][getTileSetWidth() - 1].setSelectionMarkerActive(true);
                }
            }
        }
    }

    /**
     * Paint the numbers.
     */
    private void paintNumbers() {

        // get number of numbers for captions
        columnCaptionCount = getPattern().getColumnCaptionHeight();
        rowCaptionCount = getPattern().getLineCaptionWidth();
        final String[][] labels = new String[getTileSetHeight() + 2][getTileSetWidth() + 2];

        if (orientation == CaptionOrientation.ORIENTATION_COLUMN) {
            // initialize column numbers
            for (int x = 0; x < getTileSetWidth(); x++) {
                int len = getPattern().getColumnNumbersCount(x);
                for (int i = 0; i < columnCaptionCount; i++) {
                    int number = getPattern().getColumnNumber(x, i);
                    int y = (i + columnCaptionCount - len) % columnCaptionCount + Math.max(0, MIN_TILESET_HEIGHT - 1 - columnCaptionCount);
                    labels[y][x] = number >= 0 ? Integer.toString(number) : "";
                }
            }
        } else if (orientation == CaptionOrientation.ORIENTATION_ROW) {
            // initialize row numbers
            for (int y = 0; y < getTileSetHeight(); y++) {
                int len = getPattern().getLineNumberCount(y);
                for (int i = 0; i < rowCaptionCount; i++) {
                    int number = getPattern().getLineNumber(y, i);
                    int x = (i + rowCaptionCount - len) % rowCaptionCount + Math.max(0, MIN_TILESET_WIDTH - 1 - rowCaptionCount);
                    labels[y][x] = number >= 0 ? Integer.toString(number) : "";
                }
            }
        }

        setLabels(labels);
    }
}
