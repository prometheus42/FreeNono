/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
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
package org.freenono.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;

import org.freenono.ui.common.FontFactory;

/**
 * Paints a single tile.
 *
 * @author Christian Wichmann
 */
public class BoardTile extends JComponent {

    private static final long serialVersionUID = -8166203161723979426L;

    private static boolean leftMouseButtonWasPressedToOccupy = false;
    private static boolean leftMouseButtonWasPressedToUnoccupy = false;

    private static final int TILE_DEFAULT_SIZE = 20;
    private static int tileWidth = TILE_DEFAULT_SIZE;
    private static int tileHeight = TILE_DEFAULT_SIZE;
    private static int tileWidthHalf = TILE_DEFAULT_SIZE / 2;
    private static int tileHeightHalf = TILE_DEFAULT_SIZE / 2;
    private static int tileWidthQuarter = TILE_DEFAULT_SIZE / 4;
    private static int tileHeightQuarter = TILE_DEFAULT_SIZE / 4;

    private final EditorTileSet editorTileSet;
    private int column = 0;
    private int row = 0;

    private static final Color FOREGROUND_COLOR = new Color(100, 100, 100);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color FOREGROUND_DORMANT_COLOR = new Color(110, 110, 110);
    private static final Color BACKGROUND_DORMANT_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static Color markerColor = new Color(0, 0, 0);
    private static Color activecolor = new Color(0, 0, 0);
    private static Color markerBackgroundColor = new Color(0, 0, 0);
    private static Color crossedSingleLineColor = Color.BLACK;

    private static Polygon polygonSelectionMarkerRight;
    private static Polygon polygonSelectionMarkerDown;

    private boolean marked = false;
    private boolean crossed = false;
    private boolean active = false;
    private boolean dormant = false;
    private boolean transparent = true;
    private boolean crossedSingleLine = false;
    private boolean drawBorderNorth = false;
    private boolean drawBorderSouth = false;
    private boolean drawBorderWest = false;
    private boolean drawBorderEast = false;

    /**
     * Indicates which kind of selection marker should be painted. Currently only
     * <code>SELECTION_MARKER_RIGHT</code> and <code>SELECTION_MARKER_DOWN</code> are implemented.
     *
     * @author Christian Wichmann
     */
    public enum SelectionMarkerType {
        SELECTION_MARKER_RIGHT, SELECTION_MARKER_LEFT, SELECTION_MARKER_DOWN, SELECTION_MARKER_UP, NO_SELECTION_MARKER
    }

    private SelectionMarkerType selectionMarker = SelectionMarkerType.NO_SELECTION_MARKER;
    private boolean selectionMarkerActive = false;

    private String label = null;

    /**
     * Calculates necessary sizes and initializes a board tile.
     *
     * @param editorTileSet
     *            editor tile set to which this tile belongs to
     * @param tileDimension
     *            tile size
     * @param column
     *            column this tile is in
     * @param row
     *            row this tile is in
     */
    public BoardTile(final EditorTileSet editorTileSet, final Dimension tileDimension, final int column, final int row) {

        super();

        if (editorTileSet == null || column < 0 || row < 0) {
            throw new IllegalArgumentException("Parameter not valid.");
        }

        this.editorTileSet = editorTileSet;
        this.column = column;
        this.row = row;

        setPreferredSize(new Dimension(tileWidth, tileHeight));

        calculateSizes(tileDimension);

        addListener();
    }

    /**
     * Calculates dimensions for painting the actual tile.
     *
     * @param tileDimension
     *            dimension given by the BoardPanel resulting from available space and amount of
     *            necessary tiles.
     */
    private void calculateSizes(final Dimension tileDimension) {

        // calculate fractions of tile size
        tileWidth = (int) tileDimension.getWidth();
        tileHeight = (int) tileDimension.getHeight();
        tileWidthHalf = (int) (tileDimension.getWidth() / 2);
        tileHeightHalf = (int) (tileDimension.getHeight() / 2);
        tileWidthQuarter = (int) (tileDimension.getWidth() / 4);
        tileHeightQuarter = (int) (tileDimension.getHeight() / 4);

        // set polygon for selection markers
        polygonSelectionMarkerRight = new Polygon();
        polygonSelectionMarkerDown = new Polygon();
        polygonSelectionMarkerRight.addPoint(tileWidthQuarter, tileHeightQuarter);
        polygonSelectionMarkerRight.addPoint(3 * tileWidthQuarter, tileHeightHalf);
        polygonSelectionMarkerRight.addPoint(tileWidthQuarter, 3 * tileHeightQuarter);
        polygonSelectionMarkerDown.addPoint(tileWidthQuarter, tileHeightQuarter);
        polygonSelectionMarkerDown.addPoint(3 * tileWidthQuarter, tileHeightQuarter);
        polygonSelectionMarkerDown.addPoint(tileWidthHalf, 3 * tileHeightQuarter);
    }

    /**
     * Handle mouse events like clicking a tile or moving the mouse into a tiles space.
     */
    private void addListener() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {

                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    editorTileSet.changeActiveField();
                    if (isActive()) {
                        leftMouseButtonWasPressedToUnoccupy = true;
                    } else {
                        leftMouseButtonWasPressedToOccupy = true;
                    }
                    break;
                default:
                    break;
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {

                leftMouseButtonWasPressedToOccupy = false;
                leftMouseButtonWasPressedToUnoccupy = false;
            }

            @Override
            public void mouseEntered(final MouseEvent e) {

                editorTileSet.setActive(column, row);

                if ((leftMouseButtonWasPressedToOccupy && !isActive()) || (leftMouseButtonWasPressedToUnoccupy && isActive())) {
                    editorTileSet.changeActiveField();
                }
            }

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {

                editorTileSet.changeActiveField();
            }
        });
    }

    @Override
    public final void paintComponent(final Graphics g) {

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        paintBackground(g);

        paintBorders(g);

        if (label != null) {
            paintLabel(g);
        }

        paintDecorations(g);
    }

    /**
     * Paints the background of the tile dpending on whether it is part of the caption or play field
     * or if it is the currently active field.
     *
     * @param g
     *            the Graphics object
     */
    private void paintBackground(final Graphics g) {

        // paint background
        if (dormant) {
            g.setColor(BACKGROUND_DORMANT_COLOR);
        } else {
            g.setColor(BACKGROUND_COLOR);
        }
        if (!transparent) {
            g.fillRect(0, 0, tileWidth, tileHeight);
        }

        // paint background for selection marker tiles
        if (selectionMarker != SelectionMarkerType.NO_SELECTION_MARKER) {
            g.setColor(markerBackgroundColor);
            g.fillRect(0, 0, tileWidth, tileHeight);
        }

        // paint active tile
        if (active) {
            g.setColor(activecolor);
            g.fillRect(2, 2, tileWidth - 4, tileHeight - 4);
        }
    }

    /**
     * Paints borders of tile when set.
     *
     * @param g
     *            the Graphics object
     */
    private void paintBorders(final Graphics g) {

        if (drawBorderNorth) {
            g.setColor(BORDER_COLOR);
            g.drawLine(0, 0, tileWidth, 0);
        }
        if (drawBorderSouth) {
            g.setColor(BORDER_COLOR);
            g.drawLine(0, tileHeight - 1, tileWidth, tileHeight - 1);
        }
        if (drawBorderWest) {
            g.setColor(BORDER_COLOR);
            g.drawLine(0, 0, 0, tileHeight);
        }
        if (drawBorderEast) {
            g.setColor(BORDER_COLOR);
            g.drawLine(tileWidth - 1, 0, tileWidth - 1, tileHeight);
        }
    }

    /**
     * Paints decorations like when a tile is marked or crossed by the player. Also the selection
     * marker are painted when this tile belongs to the captions.
     *
     * @param g
     *            the Graphics object
     */
    private void paintDecorations(final Graphics g) {

        // paint marked tile
        if (marked) {
            if (dormant) {
                g.setColor(FOREGROUND_DORMANT_COLOR);
            } else {
                g.setColor(FOREGROUND_COLOR);
            }

            g.fillRect(4, 4, tileWidth - 8, tileHeight - 8);
        }

        // paint tile cross
        if (crossed) {
            g.setColor(BORDER_COLOR);
            g.drawLine(3, 3, tileWidth - 4, tileHeight - 4);
            g.drawLine(tileWidth - 4, 3, 3, tileHeight - 4);
        }

        // build polygon and paint selection marker
        if (selectionMarkerActive) {
            g.setColor(markerColor);
            switch (selectionMarker) {
            case SELECTION_MARKER_RIGHT:
                g.fillPolygon(polygonSelectionMarkerRight);
                break;
            case SELECTION_MARKER_DOWN:
                g.fillPolygon(polygonSelectionMarkerDown);
                break;
            default:
                assert false : selectionMarker;
                break;
            }
        }

        // paint a single line to cross out caption numbers
        if (crossedSingleLine) {
            g.setColor(crossedSingleLineColor);
            g.drawLine(7, 7, tileWidth - 8, tileHeight - 8);
        }
    }

    /**
     * Paints the label on this tile if one is set.
     *
     * @param g
     *            the Graphics object
     */
    private void paintLabel(final Graphics g) {

        g.setColor(TEXT_COLOR);
        g.setFont(FontFactory.createTileFont(tileWidth / 2));

        switch (label.length()) {
        case 0:
            break;
        case 1:
            g.drawString(label, tileWidthHalf - 5, tileHeightHalf + 7);
            break;
        case 2:
            g.drawString(label, tileWidthHalf - 10, tileHeightHalf + 7);
            break;
        default:
            g.drawString(label, tileWidthHalf, tileHeightHalf);
            break;
        }
    }

    /**
     * Gets if this tile is the currently active tile in the board.
     *
     * @return true, if tile is active tile
     */
    public final boolean isActive() {

        return active;
    }

    /**
     * Sets if this tile is the currently active tile in the board. This field can be used to
     * indicate for playing with keys instead of mouse where a field will be marked or occupied.
     *
     * @param active
     *            if tile is active tile
     */
    public final void setActive(final boolean active) {

        if (this.active != active) {
            this.active = active;
            repaint();
        }
    }

    /**
     * Gets if this tile is dormant.
     *
     * @return true, if tile is dormant
     */
    public final boolean isDormant() {

        return dormant;
    }

    /**
     * Sets if this tile is dormant, meaning it will be optical accentuated.
     *
     * @param dormant
     *            if tile is dormant
     */
    public final void setDormant(final boolean dormant) {

        if (this.dormant != dormant) {
            this.dormant = dormant;
            repaint();
        }
    }

    /**
     * Gets if this tile is transparent.
     *
     * @return true, if tile is transparent
     */
    public final boolean isTransparent() {

        return transparent;
    }

    /**
     * Sets if this tile is transparent, meaning no background will be painted.
     *
     * @param transparent
     *            if tile is transparent
     */
    public final void setTransparent(final boolean transparent) {

        if (this.transparent != transparent) {
            this.transparent = transparent;
            repaint();
        }
    }

    /**
     * Sets label for this tile. As font for the label the tile font (
     * <code>FontFactory.createTileFont()</code>) is used.
     *
     * @param x
     *            label to set for this tile
     */
    public final void setLabel(final String x) {
        label = x;
        repaint();
    }

    /**
     * Gets the label for this tile.
     *
     * @return label of this tile
     */
    public final String getLabel() {

        return label;
    }

    /**
     * Gets whether to draw the northern border.
     *
     * @return true, if northern border should be painted
     */
    public final boolean isDrawBorderNorth() {

        return drawBorderNorth;
    }

    /**
     * Sets whether to draw the northern border.
     *
     * @param drawBorderNorth
     *            if northern border should be painted
     */
    public final void setDrawBorderNorth(final boolean drawBorderNorth) {

        this.drawBorderNorth = drawBorderNorth;
        repaint();
    }

    /**
     * Gets whether to draw the southern border.
     *
     * @return true, if southern border should be painted
     */
    public final boolean isDrawBorderSouth() {

        return drawBorderSouth;
    }

    /**
     * Sets whether to draw the southern border.
     *
     * @param drawBorderSouth
     *            if southern border should be painted
     */
    public final void setDrawBorderSouth(final boolean drawBorderSouth) {

        this.drawBorderSouth = drawBorderSouth;
        repaint();
    }

    /**
     * Gets whether to draw the western border.
     *
     * @return true, if western border should be painted
     */
    public final boolean isDrawBorderWest() {

        return drawBorderWest;
    }

    /**
     * Sets whether to draw the western border.
     *
     * @param drawBorderWest
     *            if western border should be painted
     */
    public final void setDrawBorderWest(final boolean drawBorderWest) {

        this.drawBorderWest = drawBorderWest;
        repaint();
    }

    /**
     * Gets whether to draw the eastern border.
     *
     * @return true, if eastern border should be painted
     */
    public final boolean isDrawBorderEast() {

        return drawBorderEast;
    }

    /**
     * Sets whether to draw the eastern border.
     *
     * @param drawBorderEast
     *            if eastern border should be painted
     */
    public final void setDrawBorderEast(final boolean drawBorderEast) {

        this.drawBorderEast = drawBorderEast;
        repaint();
    }

    /**
     * Gets whether tile should be marked.
     *
     * @return true, if tile should be marked
     */
    public final boolean isMarked() {

        return marked;
    }

    /**
     * Sets whether tile should be marked.
     *
     * @param marked
     *            whether tile should be marked
     */
    public final void setMarked(final boolean marked) {

        if (this.marked != marked) {
            this.marked = marked;
            repaint();
        }
    }

    /**
     * Gets if tile should be crossed.
     *
     * @return true, if tile should be crossed
     */
    public final boolean isCrossed() {

        return crossed;
    }

    /**
     * Sets if tile should be crossed.
     *
     * @param crossed
     *            if tile should be crossed
     */
    public final void setCrossed(final boolean crossed) {

        if (this.crossed != crossed) {
            this.crossed = crossed;
            repaint();
        }
    }

    /**
     * Get if tile should be crossed with one line.
     *
     * @return true, if tile should be crossed by a single line
     */
    public final boolean isCrossedSingleLine() {

        return crossedSingleLine;
    }

    /**
     * Sets if tile should be crossed with one line.
     *
     * @param crossedSingleLine
     *            if tile should be crossed by a single line
     */
    public final void setCrossedSingleLine(final boolean crossedSingleLine) {

        if (this.crossedSingleLine != crossedSingleLine) {
            this.crossedSingleLine = crossedSingleLine;
            repaint();
        }
    }

    /**
     * Gets what kind of selection marker the tile should paint.
     *
     * @return type of selection marker
     */
    public final SelectionMarkerType getSelectionMarker() {

        return selectionMarker;
    }

    /**
     * Sets what kind of selection marker the tile should paint.
     *
     * @param selectionMarker
     *            type of selection marker to be set
     */
    public final void setSelectionMarker(final SelectionMarkerType selectionMarker) {

        this.selectionMarker = selectionMarker;
        repaint();
    }

    /**
     * Gets whether the selection marker of this tile should be active.
     *
     * @return true, if selection marker is active
     */
    public final boolean isSelectionMarkerActive() {

        return selectionMarkerActive;
    }

    /**
     * Sets whether the selection marker of this tile should be active, meaning be painted.
     *
     * @param selectionMarkerActive
     *            if selection marker should be active
     */
    public final void setSelectionMarkerActive(final boolean selectionMarkerActive) {

        this.selectionMarkerActive = selectionMarkerActive;
        repaint();
    }

    /**
     * Gets column of this tile.
     *
     * @return column of this tile
     */
    public final int getColumn() {

        return column;
    }

    /**
     * Sets column of this tile.
     *
     * @param column
     *            column of this tile
     */
    public final void setColumn(final int column) {

        this.column = column;
    }

    /**
     * Gets row of this tile.
     *
     * @return row of this tile
     */
    public final int getRow() {

        return row;
    }

    /**
     * Sets row of this tile.
     *
     * @param row
     *            row of this tile
     */
    public final void setRow(final int row) {

        this.row = row;
    }

    /**
     * Handle resizing of tile dimension.
     *
     * @param tileDimension
     *            New tile dimension.
     */
    public final void handleResize(final Dimension tileDimension) {

        calculateSizes(tileDimension);
        setSize(tileDimension);
        repaint();
    }

    /**
     * Resets two state variables common to all BoardTile instances to trace mouse click-and-drag.
     * When a mouse button is clicked on a tile the state fields are set. This method can be called
     * to manually reset them.
     */
    public final void releaseMouseButton() {

        leftMouseButtonWasPressedToOccupy = false;
        leftMouseButtonWasPressedToUnoccupy = false;
    }
}
