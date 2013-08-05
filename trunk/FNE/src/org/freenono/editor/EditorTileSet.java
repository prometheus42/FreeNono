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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.freenono.model.data.Nonogram;

/**
 * Shows a tile set where the user can edit a nonogram.
 * 
 * @author Christian Wichmann
 */
public class EditorTileSet extends JComponent implements Scrollable {

    // private static Logger logger = Logger.getLogger(EditorTileSet.class);

    private static final long serialVersionUID = 7925588477738889049L;

    private Nonogram pattern;

    private static final int TILESET_WIDTH_DEFAULT = 10;
    private static final int TILESET_HEIGHT_DEFAULT = 10;

    private int tileSetWidth = TILESET_WIDTH_DEFAULT;
    private int tileSetHeight = TILESET_HEIGHT_DEFAULT;
    private Dimension tileDimension;

    private BoardTile[][] board = null;
    private int activeFieldColumn = 0;
    private int activeFieldRow = 0;

    private boolean occupyFields = false;
    private boolean unoccupyFields = false;

    /**
     * Initializes a tile set to display nonogram data.
     * 
     * @param pattern
     *            nonogram pattern to display
     * @param tileDimension
     *            dimension of one tile of this set
     */
    public EditorTileSet(final Nonogram pattern, final Dimension tileDimension) {

        this.pattern = pattern;
        this.tileDimension = tileDimension;

        tileSetWidth = pattern.width();
        tileSetHeight = pattern.height();

        initialize();

        addListeners();
        addKeyBindings();

        paintBorders();

        setTileSetToNonogram();
    }

    /**
     * Initializes this tile set.
     */
    private void initialize() {

        // build gridLayout
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(tileSetHeight);
        gridLayout.setColumns(tileSetWidth);
        setLayout(gridLayout);

        // fill grid with tiles
        board = new BoardTile[tileSetHeight][tileSetWidth];
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j] = new BoardTile(this, tileDimension, j, i);
                board[i][j].setMinimumSize(tileDimension);
                board[i][j].setPreferredSize(tileDimension);
                board[i][j].setColumn(j);
                board[i][j].setRow(i);
                add(board[i][j]);
            }
        }
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
                board[i][j].repaint();
            }
        }
    }

    /**
     * Adding Listeners for key and mouse events on the nonogram board.
     */
    private void addListeners() {

        // add Listener for mouse and keyboard usage
        // addMouseListener(new MouseAdapter() {
        // @Override
        // public void mousePressed(final MouseEvent e) {
        //
        // Point p = e.getPoint();
        // switch (e.getButton()) {
        // case MouseEvent.BUTTON1:
        // handleClick(p);
        // changeActiveField();
        // break;
        // case MouseEvent.BUTTON3:
        // break;
        // default:
        // break;
        // }
        // }
        // });
        // addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
        // public void mouseMoved(final MouseEvent e) {
        // Point p = e.getPoint();
        // handleMouseMovement(p);
        // }
        // });
    }

    /**
     * Add key bindings for all controls.
     */
    private void addKeyBindings() {

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
        getActionMap().put("Left", new AbstractAction() {
            private static final long serialVersionUID = 3526487415521380900L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveLeft();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "Right");
        getActionMap().put("Right", new AbstractAction() {
            private static final long serialVersionUID = 3526487416521380900L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveRight();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Up");
        getActionMap().put("Up", new AbstractAction() {
            private static final long serialVersionUID = 3526481415521380900L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveUp();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Down");
        getActionMap().put("Down", new AbstractAction() {
            private static final long serialVersionUID = -8632221802324267954L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveDown();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("HOME"), "GoToHome");
        getActionMap().put("GoToHome", new AbstractAction() {
            private static final long serialVersionUID = 7128510030273601411L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setActive(0, activeFieldRow);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("END"), "GoToEnd");
        getActionMap().put("GoToEnd", new AbstractAction() {
            private static final long serialVersionUID = 7132502544255656098L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setActive(tileSetWidth - 1, activeFieldRow);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("PAGE_UP"), "GoToTop");
        getActionMap().put("GoToTop", new AbstractAction() {
            private static final long serialVersionUID = 7128510030273601411L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setActive(activeFieldColumn, 0);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("PAGE_DOWN"), "GoToBottom");
        getActionMap().put("GoToBottom", new AbstractAction() {
            private static final long serialVersionUID = 7132502544255656098L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setActive(activeFieldColumn, tileSetHeight - 1);

            }
        });

        String keyStrokeString;

        keyStrokeString = "pressed "
                + KeyEvent.getKeyText(KeyEvent.VK_PERIOD).toUpperCase();
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyStrokeString), "Occupy");
        keyStrokeString = "pressed "
                + KeyEvent.getKeyText(KeyEvent.VK_SPACE).toUpperCase();
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyStrokeString), "Occupy");
        getActionMap().put("Occupy", new AbstractAction() {
            private static final long serialVersionUID = 8228569120230316012L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (board[activeFieldRow][activeFieldColumn].isActive()) {
                    unoccupyFields = true;
                } else {
                    occupyFields = true;
                }
                changeActiveField();
            }
        });

        keyStrokeString = "released "
                + KeyEvent.getKeyText(KeyEvent.VK_PERIOD).toUpperCase();
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyStrokeString), "OccupyReleased");
        keyStrokeString = "released "
                + KeyEvent.getKeyText(KeyEvent.VK_SPACE).toUpperCase();
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyStrokeString), "OccupyReleased");
        getActionMap().put("OccupyReleased", new AbstractAction() {
            private static final long serialVersionUID = -4733029188707402453L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                occupyFields = false;
                unoccupyFields = false;
            }
        });
    }

    /**
     * Paints borders of tile set.
     */
    private void paintBorders() {
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j].setDrawBorderWest(true);
                if ((j + 1) % 5 == 0 || (j + 1) == tileSetWidth) {
                    board[i][j].setDrawBorderEast(true);
                }
                board[i][j].setDrawBorderNorth(true);
                if ((i + 1) % 5 == 0 || (i + 1) == tileSetHeight) {
                    board[i][j].setDrawBorderSouth(true);
                }
            }
        }
    }

    /**
     * Changes active field.
     */
    public final void changeActiveField() {

        if (board[activeFieldRow][activeFieldColumn].isMarked()) {
            board[activeFieldRow][activeFieldColumn].setMarked(false);
            pattern.setFieldValue(false, activeFieldColumn, activeFieldRow);
        } else {
            board[activeFieldRow][activeFieldColumn].setMarked(true);
            pattern.setFieldValue(true, activeFieldColumn, activeFieldRow);
        }
    }

    /**
     * Sets active field.
     * 
     * @param column
     *            column of new active field
     * @param row
     *            row of new active field
     */
    public final void setActive(final int column, final int row) {

        if (column >= 0 && column < tileSetWidth && row >= 0
                && row < tileSetHeight) {
            board[activeFieldRow][activeFieldColumn].setActive(false);
            activeFieldColumn = column;
            activeFieldRow = row;
            board[activeFieldRow][activeFieldColumn].setActive(true);
        }

        changeAfterMove();
    }

    /**
     * Checks if field on board should be either occupied or unoccupied after
     * active field has changed.
     */
    private void changeAfterMove() {

        if (occupyFields
                && !board[activeFieldRow][activeFieldColumn].isActive()) {
            changeActiveField();
        }

        if (unoccupyFields
                && board[activeFieldRow][activeFieldColumn].isActive()) {
            changeActiveField();
        }
    }

    /**
     * Moves active field left.
     */
    private void moveActiveLeft() {

        if (activeFieldColumn > 0) {
            board[activeFieldRow][activeFieldColumn].setActive(false);
            activeFieldColumn -= 1;
            board[activeFieldRow][activeFieldColumn].setActive(true);
        }
        changeAfterMove();
    }

    /**
     * Moves active field right.
     */
    private void moveActiveRight() {

        if (activeFieldColumn < tileSetWidth - 1) {
            board[activeFieldRow][activeFieldColumn].setActive(false);
            activeFieldColumn += 1;
            board[activeFieldRow][activeFieldColumn].setActive(true);
        }
        changeAfterMove();
    }

    /**
     * Moves active field up.
     */
    private void moveActiveUp() {

        if (activeFieldRow > 0) {
            board[activeFieldRow][activeFieldColumn].setActive(false);
            activeFieldRow -= 1;
            board[activeFieldRow][activeFieldColumn].setActive(true);
        }
        changeAfterMove();
    }

    /**
     * Moves active field down.
     */
    private void moveActiveDown() {

        if (activeFieldRow < tileSetHeight - 1) {
            board[activeFieldRow][activeFieldColumn].setActive(false);
            activeFieldRow += 1;
            board[activeFieldRow][activeFieldColumn].setActive(true);
        }
        changeAfterMove();
    }

    /**
     * Clears all tiles of this tile set.
     */
    @SuppressWarnings("unused")
    private void clearBoard() {

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j].setMarked(false);
                board[i][j].setCrossed(false);
            }
        }
    }

    /**
     * Sets all tiles of this tile set to value defined by nonogram pattern.
     */
    private void setTileSetToNonogram() {
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j].setCrossed(false);
                if (pattern.getFieldValue(j, i)) {
                    board[i][j].setMarked(true);
                } else {
                    board[i][j].setMarked(false);
                }
            }
        }
    }

    /**
     * Returns nonogram pattern of this tile set.
     * 
     * @return nonogram pattern of this tile set
     */
    public final Nonogram getPattern() {

        return pattern;
    }

    /**
     * Sets nonogram pattern of this tile set.
     * 
     * @param pattern
     *            nonogram pattern to be set
     */
    public final void setPattern(final Nonogram pattern) {

        this.pattern = pattern;
        setTileSetToNonogram();
    }

    /*
     * Methods implementing Scrollable interface
     */

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
