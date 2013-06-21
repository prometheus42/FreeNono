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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameEventHelper;
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

    private static int tileWidth = 20;
    private static int tileHeight = 20;
    private static int tileWidthHalf = 10;
    private static int tileHeightHalf = 10;
    private static int tileWidthQuarter = 5;
    private static int tileHeightQuarter = 5;
    private int column = 0;
    private int row = 0;

    private static Color fgColor = new Color(100, 100, 100);
    private static Color textColor = Color.BLACK;
    private static Color borderColor = Color.BLACK;
    private static Color markerColor;
    private static Color activecolor;
    private static Color backgroundColor;

    private boolean marked = false;
    private boolean crossed = false;
    private boolean active = false;
    private boolean drawBorderNorth = false;
    private boolean drawBorderSouth = false;
    private boolean drawBorderWest = false;
    private boolean drawBorderEast = false;

    public static final int SELECTION_MARKER_RIGHT = 1;
    public static final int SELECTION_MARKER_LEFT = 2;
    public static final int SELECTION_MARKER_DOWN = 3;
    public static final int SELECTION_MARKER_UP = 4;
    private int selectionMarker = 0;
    private boolean selectionMarkerActive = false;

    // attribute interactive signals, if tile should listen to mouse events
    private static final boolean INTERACTIVE_DEFAULT = false;
    private boolean interactive = INTERACTIVE_DEFAULT;

    private String label = null;
    private Font labelFont = null;

    public BoardTile(GameEventHelper eventHelper, ColorModel colorModel,
            Dimension tileDimension, int column, int row) {

        super();

        this.eventHelper = eventHelper;
        this.column = column;
        this.row = row;

        setColorModel(colorModel);

        calculateSizes(tileDimension);

        initialize();
    }

    /**
     * Calculates dimensions for painting the actual tile.
     * 
     * @param tileDimension
     *            dimension given by the BoardPanel resulting from available
     *            space and amount of necessary tiles.
     */
    private void calculateSizes(Dimension tileDimension) {

        tileWidth = (int) tileDimension.getWidth();
        tileHeight = (int) tileDimension.getHeight();
        tileWidthHalf = (int) (tileDimension.getWidth() / 2);
        tileHeightHalf = (int) (tileDimension.getHeight() / 2);
        tileWidthQuarter = (int) (tileDimension.getWidth() / 4);
        tileHeightQuarter = (int) (tileDimension.getHeight() / 4);
    }

    /**
     * Initializing the used font for the tile label.
     */
    private void initialize() {

        labelFont = new Font("FreeSans", Font.PLAIN, tileWidth / 2);
    }

    /**
     * Handle mouse events like clicking a tile or moving the mouse into a tiles
     * space.
     */
    private void addListener() {

        this.setFocusable(true);

        this.addMouseListener(new java.awt.event.MouseAdapter() {

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

    /**
     * Sets interactive mode for this board tile. A tile which is interactive
     * listens to mouse events and reports them via the event system to the game
     * logic.
     * 
     * @param interactive
     *            true, if tile should listen to mouse events.
     */
    public final void setInteractive(boolean interactive) {

        this.interactive = interactive;

        if (interactive)
            addListener();
    }

    /**
     * Returns if this tile is interactive, listening to events and reporting them.
     * 
     * @return true, if tile is interactive.
     */
    public boolean isInteractive() {

        return interactive;
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
        if (selectionMarker != 0) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, tileWidth, tileHeight);
        }

        // paint active tile
        if (active) {
            g.setColor(activecolor);
            g.fillRect(2, 2, tileWidth - 4, tileHeight - 4);
        }

        // paint tile borders
        g.setColor(borderColor);
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
            g.setColor(fgColor);
            g.fillRect(4, 4, tileWidth - 8, tileHeight - 8);
        }

        // paint tile cross
        if (crossed) {
            g.setColor(borderColor);
            g.drawLine(3, 3, tileWidth - 4, tileHeight - 4);
            g.drawLine(tileWidth - 4, 3, 3, tileHeight - 4);
        }

        // paint tile label
        g.setColor(textColor);
        g.setFont(labelFont);
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
        if (selectionMarker != 0) {
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
                break;
            }
            // g.setColor(selectionColor);
            // g.fillPolygon(p2);
            if (selectionMarkerActive) {
                g.setColor(markerColor);
                g.fillPolygon(p2);
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            this.repaint();
        }
    }

    public void setLabel(String x) {
        label = x;
        this.repaint();
    }

    public String getLabel() {
        return label;
    }

    public boolean isDrawBorderNorth() {
        return drawBorderNorth;
    }

    public void setDrawBorderNorth(boolean drawBorderNorth) {
        this.drawBorderNorth = drawBorderNorth;
        repaint();
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        if (this.marked != marked) {
            this.marked = marked;
            this.repaint();
        }
    }

    public boolean isCrossed() {
        return crossed;
    }

    public void setCrossed(boolean crossed) {
        if (this.crossed != crossed) {
            this.crossed = crossed;
            this.repaint();
        }
    }

    public boolean isDrawBorderSouth() {
        return drawBorderSouth;
    }

    public void setDrawBorderSouth(boolean drawBorderSouth) {
        this.drawBorderSouth = drawBorderSouth;
        this.repaint();
    }

    public boolean isDrawBorderWest() {
        return drawBorderWest;
    }

    public void setDrawBorderWest(boolean drawBorderWest) {
        this.drawBorderWest = drawBorderWest;
        this.repaint();
    }

    public boolean isDrawBorderEast() {
        return drawBorderEast;
    }

    public void setDrawBorderEast(boolean drawBorderEast) {
        this.drawBorderEast = drawBorderEast;
        this.repaint();
    }

    public int getSelectionMarker() {
        return selectionMarker;
    }

    public void setSelectionMarker(int selectionMarker) {
        this.selectionMarker = selectionMarker;
        this.repaint();
    }

    public boolean isSelectionMarkerActive() {
        return selectionMarkerActive;
    }

    public void setSelectionMarkerActive(boolean selectionMarkerActive) {
        this.selectionMarkerActive = selectionMarkerActive;
        this.repaint();
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void handleResize(Dimension tileDimension) {

        calculateSizes(tileDimension);
        setSize(tileDimension);
        repaint();
    }

    public Dimension getPreferredSize() {

        return new Dimension(tileWidth, tileHeight);
    }

    public void setColorModel(ColorModel colorModel) {

        backgroundColor = colorModel.getUpColor();
        activecolor = colorModel.getStrangeColor();
        markerColor = colorModel.getDownColor();
    }

    public void releaseMouseButton() {

        occupyWhileDraggingMouse = false;
        markWhileDraggingMouse = false;
    }

}
