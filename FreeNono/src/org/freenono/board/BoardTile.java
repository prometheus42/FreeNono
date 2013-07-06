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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.ui.FontFactory;
import org.freenono.ui.colormodel.ColorModel;

/**
 * Paints one tile of the board. The tile can be part of the playfield or of the
 * captions around the board. Borders, label, etc. will be painted based on set
 * options (marked, crossed, active, ...).
 * 
 * @author Christian Wichmann
 */
public class BoardTile extends JComponent {

    private static final long serialVersionUID = -8166203161723979426L;

    // private static Logger logger = Logger.getLogger(BoardTile.class);

    private GameEventHelper eventHelper;

    private static boolean occupyWhileDraggingMouse = false;
    private static boolean unoccupyWhileDraggingMouse = false;
    private static boolean markWhileDraggingMouse = false;
    private static boolean unmarkWhileDraggingMouse = false;

    private static final int TILE_DEFAULT_SIZE = 20;
    private static int tileWidth = TILE_DEFAULT_SIZE;
    private static int tileHeight = TILE_DEFAULT_SIZE;
    private static int tileWidthHalf = TILE_DEFAULT_SIZE / 2;
    private static int tileHeightHalf = TILE_DEFAULT_SIZE / 2;
    private static int tileWidthQuarter = TILE_DEFAULT_SIZE / 4;
    private static int tileHeightQuarter = TILE_DEFAULT_SIZE / 4;

    private int column = 0;
    private int row = 0;

    private static final Color FOREGROUND_COLOR = new Color(100, 100, 100);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static Color markerColor;
    private static Color activecolor;
    private static Color backgroundColor;
    private static Color crossedSingleLineColor;

    private boolean marked = false;
    private boolean crossed = false;
    private boolean active = false;
    private boolean crossedSingleLine = false;
    private boolean drawBorderNorth = false;
    private boolean drawBorderSouth = false;
    private boolean drawBorderWest = false;
    private boolean drawBorderEast = false;

    /**
     * Indicates which kind of selection marker should be painted. Currently
     * only <code>SELECTION_MARKER_RIGHT</code> and
     * <code>SELECTION_MARKER_DOWN</code> are implemented.
     * 
     * @author Christian Wichmann
     */
    public enum SelectionMarkerType {
        SELECTION_MARKER_RIGHT, SELECTION_MARKER_LEFT, SELECTION_MARKER_DOWN, SELECTION_MARKER_UP, NO_SELECTION_MARKER
    }

    private SelectionMarkerType selectionMarker = SelectionMarkerType.NO_SELECTION_MARKER;
    private boolean selectionMarkerActive = false;

    // attribute interactive signals, if tile should listen to mouse events
    private static final boolean INTERACTIVE_DEFAULT = false;
    private boolean interactive = INTERACTIVE_DEFAULT;

    private String label = null;

    /**
     * Construct a board tile and setting all sizes and colors.
     * @param eventHelper
     *            Event helper
     * @param colorModel
     *            Color model to get the colors from
     * @param tileDimension
     *            Tile dimension
     * @param column
     *            Column this tile is placed in
     * @param row
     *            Row this tile is placed in
     */
    public BoardTile(final GameEventHelper eventHelper,
            final ColorModel colorModel, final Dimension tileDimension,
            final int column, final int row) {

        super();

        this.eventHelper = eventHelper;
        this.column = column;
        this.row = row;

        setColorModel(colorModel);

        calculateSizes(tileDimension);
    }

    /**
     * Calculates dimensions for painting the actual tile.
     * 
     * @param tileDimension
     *            dimension given by the BoardPanel resulting from available
     *            space and amount of necessary tiles.
     */
    private void calculateSizes(final Dimension tileDimension) {

        tileWidth = (int) tileDimension.getWidth();
        tileHeight = (int) tileDimension.getHeight();
        tileWidthHalf = (int) (tileDimension.getWidth() / 2);
        tileHeightHalf = (int) (tileDimension.getHeight() / 2);
        tileWidthQuarter = (int) (tileDimension.getWidth() / 4);
        tileHeightQuarter = (int) (tileDimension.getHeight() / 4);
    }

    /**
     * Handle mouse events like clicking a tile or moving the mouse into a tiles
     * space.
     */
    private void addListener() {

        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {

            public void mousePressed(final MouseEvent e) {

                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    if (!isMarked()) {
                        occupyWhileDraggingMouse = true;
                    } else if (isMarked()) {
                        unoccupyWhileDraggingMouse = true;
                    }
                    break;

                case MouseEvent.BUTTON3:
                    if (!isCrossed()) {
                        markWhileDraggingMouse = true;
                    } else if (isCrossed()) {
                        unmarkWhileDraggingMouse = true;
                    }
                    break;
                default:
                    break;
                }

                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    eventHelper.fireOccupyFieldEvent(new FieldControlEvent(
                            this, column, row));
                    break;
                case MouseEvent.BUTTON3:
                    eventHelper.fireMarkFieldEvent(new FieldControlEvent(this,
                            column, row));
                    break;
                default:
                    break;
                }
            }

            public void mouseReleased(final MouseEvent e) {

                occupyWhileDraggingMouse = false;
                unoccupyWhileDraggingMouse = false;
                markWhileDraggingMouse = false;
                unmarkWhileDraggingMouse = false;
            }

            public void mouseEntered(final MouseEvent e) {

                eventHelper.fireChangeActiveFieldEvent(new FieldControlEvent(
                        this, column, row));

                if (occupyWhileDraggingMouse) {

                    if (!isMarked()) {
                        eventHelper.fireOccupyFieldEvent(new FieldControlEvent(
                                this, column, row));
                    }

                } else if (unoccupyWhileDraggingMouse) {

                    if (isMarked()) {
                        eventHelper.fireOccupyFieldEvent(new FieldControlEvent(
                                this, column, row));
                    }

                } else if (markWhileDraggingMouse) {

                    if (!isCrossed()) {
                        eventHelper.fireMarkFieldEvent(new FieldControlEvent(
                                this, column, row));
                    }
                } else if (unmarkWhileDraggingMouse) {

                    if (isCrossed()) {
                        eventHelper.fireMarkFieldEvent(new FieldControlEvent(
                                this, column, row));
                    }
                }
            }
        });
    }

    @Override
    public final void paintComponent(final Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

        // paint background
        if (selectionMarker != SelectionMarkerType.NO_SELECTION_MARKER) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, tileWidth, tileHeight);
        }

        // paint active tile
        if (active) {
            g.setColor(activecolor);
            g.fillRect(2, 2, tileWidth - 4, tileHeight - 4);
        }

        // paint tile borders
        g.setColor(BORDER_COLOR);
        if (drawBorderNorth) {
            g.drawLine(0, 0, tileWidth, 0);
        }
        if (drawBorderSouth) {
            g.drawLine(0, tileHeight - 1, tileWidth, tileHeight - 1);
        }
        if (drawBorderWest) {
            g.drawLine(0, 0, 0, tileHeight);
        }
        if (drawBorderEast) {
            g.drawLine(tileWidth - 1, 0, tileWidth - 1, tileHeight);
        }

        // paint marked tile
        if (marked) {
            g.setColor(FOREGROUND_COLOR);
            g.fillRect(4, 4, tileWidth - 8, tileHeight - 8);
        }

        // paint tile cross
        if (crossed) {
            g.setColor(BORDER_COLOR);
            g.drawLine(3, 3, tileWidth - 4, tileHeight - 4);
            g.drawLine(tileWidth - 4, 3, 3, tileHeight - 4);
        }

        // paint tile label
        g.setColor(TEXT_COLOR);
        g.setFont(FontFactory.createTileFont(tileWidth / 2));
        if (label != null) {
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

        // build polygon and paint selection marker
        if (selectionMarker != SelectionMarkerType.NO_SELECTION_MARKER) {
            // Polygon p1 = new Polygon();
            Polygon p2 = new Polygon();
            switch (selectionMarker) {
            case SELECTION_MARKER_RIGHT:
                // p1.addPoint(TILE_WIDTH / 3, TILE_HEIGHT / 3);
                // p1.addPoint(2 * TILE_WIDTH / 3, TILE_HEIGHT / 2);
                // p1.addPoint(TILE_WIDTH / 3, 2 * TILE_HEIGHT / 3);
                p2.addPoint(tileWidthQuarter, tileHeightQuarter);
                p2.addPoint(3 * tileWidthQuarter, tileHeightHalf);
                p2.addPoint(tileWidthQuarter, 3 * tileHeightQuarter);
                break;
            case SELECTION_MARKER_DOWN:
                // p1.addPoint(TILE_WIDTH / 3, TILE_HEIGHT / 3);
                // p1.addPoint(2 * TILE_WIDTH / 3, TILE_HEIGHT / 3);
                // p1.addPoint(TILE_WIDTH / 2, 2 * TILE_HEIGHT / 3);
                p2.addPoint(tileWidthQuarter, tileHeightQuarter);
                p2.addPoint(3 * tileWidthQuarter, tileHeightQuarter);
                p2.addPoint(tileWidthHalf, 3 * tileHeightQuarter);
                break;
            default:
                assert false : selectionMarker;
                break;
            }
            // g.setColor(selectionColor);
            // g.fillPolygon(p2);
            if (selectionMarkerActive) {
                g.setColor(markerColor);
                g.fillPolygon(p2);
            }
        }

        if (crossedSingleLine) {

            g.setColor(crossedSingleLineColor);
            g.drawLine(3, 3, tileWidth - 4, tileHeight - 4);
        }
    }

    /**
     * Sets interactive mode for this board tile. A tile which is interactive
     * listens to mouse events and reports them via the event system to the game
     * logic.
     * 
     * @param interactive
     *            true, if tile should listen to mouse events.
     */
    public final void setInteractive(final boolean interactive) {

        this.interactive = interactive;

        if (interactive) {
            addListener();
        }
    }

    /**
     * Returns if this tile is interactive, listening to events and reporting
     * them.
     * 
     * @return true, if tile is interactive.
     */
    public final boolean isInteractive() {

        return interactive;
    }

    /**
     * Getter active.
     * @return Active
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Setter active.
     * @param active
     *            Active
     */
    public final void setActive(final boolean active) {
        if (this.active != active) {
            this.active = active;
            repaint();
        }
    }

    /**
     * Setter label and repaints component.
     * @param x
     *            Label
     */
    public final void setLabel(final String x) {
        label = x;
        repaint();
    }

    /**
     * Getter label.
     * @return Label
     */
    public final String getLabel() {
        return label;
    }

    /**
     * Getter drawBorderNorth.
     * @return DrawBorderNorth
     */
    public final boolean isDrawBorderNorth() {
        return drawBorderNorth;
    }

    /**
     * Setter drawBorderNorth.
     * @param drawBorderNorth
     *            Draw border north
     */
    public final void setDrawBorderNorth(final boolean drawBorderNorth) {
        this.drawBorderNorth = drawBorderNorth;
        repaint();
    }

    /**
     * Getter drawBorderSouth.
     * @return DrawBorderSouth
     */
    public final boolean isDrawBorderSouth() {
        return drawBorderSouth;
    }

    /**
     * Setter drawBorderSouth.
     * @param drawBorderSouth
     *            Draw border south
     */
    public final void setDrawBorderSouth(final boolean drawBorderSouth) {
        this.drawBorderSouth = drawBorderSouth;
        repaint();
    }

    /**
     * Getter drawBorderWest.
     * @return DrawBorderWest
     */
    public final boolean isDrawBorderWest() {
        return drawBorderWest;
    }

    /**
     * Setter drawBorderWest.
     * @param drawBorderWest
     *            Draw border west
     */
    public final void setDrawBorderWest(final boolean drawBorderWest) {
        this.drawBorderWest = drawBorderWest;
        repaint();
    }

    /**
     * Getter drawBorderEast.
     * @return DrawBorderEast
     */
    public final boolean isDrawBorderEast() {
        return drawBorderEast;
    }

    /**
     * Setter drawBorderEast.
     * @param drawBorderEast
     *            Draw border east
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
     * Getter selectionMarker.
     * @return selectionMarker
     */
    public final SelectionMarkerType getSelectionMarker() {
        return selectionMarker;
    }

    /**
     * Setter selectionMarker.
     * @param selectionMarker
     *            Selection marker
     */
    public final void setSelectionMarker(
            final SelectionMarkerType selectionMarker) {
        this.selectionMarker = selectionMarker;
        repaint();
    }

    /**
     * Getter selectionMarkerActive.
     * @return sekectionMarkerActive
     */
    public final boolean isSelectionMarkerActive() {
        return selectionMarkerActive;
    }

    /**
     * Setter selectionMarkerActive.
     * @param selectionMarkerActive
     *            Selection marker active
     */
    public final void setSelectionMarkerActive(
            final boolean selectionMarkerActive) {
        this.selectionMarkerActive = selectionMarkerActive;
        repaint();
    }

    /**
     * Getter column.
     * @return column
     */
    public final int getColumn() {
        return column;
    }

    /**
     * Setter column.
     * @param column
     *            Column
     */
    public final void setColumn(final int column) {
        this.column = column;
    }

    /**
     * Getter row.
     * @return row
     */
    public final int getRow() {
        return row;
    }

    /**
     * Setter row.
     * @param row
     *            Row
     */
    public final void setRow(final int row) {
        this.row = row;
    }

    /**
     * Handle resizing of tile dimension.
     * @param tileDimension
     *            New tile dimension.
     */
    public final void handleResize(final Dimension tileDimension) {

        calculateSizes(tileDimension);
        setSize(tileDimension);
        repaint();
    }

    /**
     * Get preferred size of component.
     * @return Preferred size
     */
    @Override
    public final Dimension getPreferredSize() {

        return new Dimension(tileWidth, tileHeight);
    }

    /**
     * Set the color model, by setting the used colors.
     * @param colorModel
     *            New color model
     */
    public final void setColorModel(final ColorModel colorModel) {

        backgroundColor = colorModel.getUpColor();
        activecolor = colorModel.getStrangeColor();
        markerColor = colorModel.getDownColor();
        crossedSingleLineColor = colorModel.getStrangeColor();
    }

    /**
     * Resets two state variables common to all BoardTile instances to trace
     * mouse click-and-drag. When a mouse button is clicked on a tile the state
     * fields are set. This can result in unwanted behaviour when combined with
     * events.
     * <p>
     * For example when the user is asked a question in quiz mode these state
     * fields are not reseted while the dialog is open. For these situations
     * this method can be called to manually reset them.
     */
    public final void releaseMouseButton() {

        occupyWhileDraggingMouse = false;
        markWhileDraggingMouse = false;
    }

}
