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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

import org.freenono.model.data.Nonogram;

/**
 * Shows a tile set where the user can edit a nonogram.
 * 
 * @author Christian Wichmann
 */
public class EditorTileSet extends JComponent {

    // private static Logger logger = Logger.getLogger(EditorTileSet.class);

    private static final long serialVersionUID = 7925588477738889049L;

    private Nonogram pattern;

    protected static final int TILESET_WIDTH_DEFAULT = 10;
    protected static final int TILESET_HEIGHT_DEFAULT = 10;

    private int tileSetWidth = TILESET_WIDTH_DEFAULT;
    private int tileSetHeight = TILESET_HEIGHT_DEFAULT;
    private Dimension tileDimension;

    private BoardTile[][] board = null;
    private String[][] labels;
    private String[][] labelsOld;

    private int activeFieldColumn = 0;
    private int activeFieldRow = 0;

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

        this.tileSetWidth = pattern.width();
        this.tileSetHeight = pattern.height();

        initialize();

        addListeners();
        paintBorders();

        setTileSetToNonogram();

    }

    /**
     * Initializes this tile set.
     */
    private void initialize() {

        // get array for tile attributes
        labels = new String[tileSetHeight][tileSetWidth];

        // build gridLayout
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(tileSetHeight);
        gridLayout.setColumns(tileSetWidth);
        this.setLayout(gridLayout);

        // fill grid with tiles
        board = new BoardTile[tileSetHeight][tileSetWidth];
        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                board[i][j] = new BoardTile(tileDimension, j, i);
                board[i][j].setMinimumSize(tileDimension);
                board[i][j].setPreferredSize(tileDimension);
                board[i][j].setColumn(j);
                board[i][j].setRow(i);
                this.add(board[i][j]);
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
     * Sets all labels for this tile set. All labels will be set to given label
     * and store it internally.
     * 
     * @param newLabels
     *            two-dimensional array of labels
     */
    public final void setLabels(final String[][] newLabels) {

        labelsOld = labels;
        labels = newLabels;

        for (int i = 0; i < tileSetHeight; i++) {
            for (int j = 0; j < tileSetWidth; j++) {
                if (labels[i][j] != labelsOld[i][j]) {
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
    public final int getTileSetWidth() {

        return tileSetWidth;
    }

    /**
     * Gets height of tile set.
     * 
     * @return tile set height
     */
    public final int getTileSetHeight() {

        return tileSetHeight;
    }

    /**
     * Adding Listeners for key and mouse events on the nonogram board.
     */
    private void addListeners() {
        // set this Component focusable to capture key events
        this.setFocusable(true);
        this.grabFocus();

        // add Listener for mouse and keyboard usage
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                // Since the user clicked on us, let us get focus!
                requestFocusInWindow();
            }

            public void mousePressed(final MouseEvent e) {
                Point p = e.getPoint();
                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    handleClick(p);
                    changeActiveField();
                    break;
                case MouseEvent.BUTTON3:
                    break;
                default:
                    break;
                }
            }
        });
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(final MouseEvent e) {
                Point p = e.getPoint();
                handleMouseMovement(p);
            }
        });

        // TODO Change this key listener to key bindings!
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(final KeyEvent evt) {
                int keyCode = evt.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    moveActiveLeft();
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    moveActiveRight();
                } else if (keyCode == KeyEvent.VK_UP) {
                    moveActiveUp();
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    moveActiveDown();
                } else if (keyCode == KeyEvent.VK_COMMA) {
                    changeActiveField();
                } else if (keyCode == KeyEvent.VK_SPACE) {
                    changeActiveField();
                } else if (keyCode == KeyEvent.VK_HOME) {
                    setActive(0, activeFieldRow);
                } else if (keyCode == KeyEvent.VK_END) {
                    setActive(tileSetWidth - 1, activeFieldRow);
                } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                    setActive(activeFieldColumn, tileSetHeight - 1);
                } else if (keyCode == KeyEvent.VK_PAGE_UP) {
                    setActive(activeFieldColumn, 0);
                }
            }
        });
    }

    /**
     * Handle mouse movement.
     * 
     * @param p
     *            point to handle mouse click on
     */
    protected final void handleMouseMovement(final Point p) {

        // TODO Change handling of mouse movement to mouseEnter and mouseLeave
        // events.

        Component c = this.findComponentAt(p);
        if (c instanceof BoardTile) {
            // deactivate old tile...
            board[activeFieldRow][activeFieldColumn].setActive(false);
            // ...find coordinates for clicked tile by searching the board...
            for (int i = 0; i < tileSetHeight; i++) {
                for (int j = 0; j < tileSetWidth; j++) {
                    if (((BoardTile) c).equals(board[i][j])) {
                        activeFieldRow = i;
                        activeFieldColumn = j;
                    }
                }
            }
            // ...and set it as active tile.
            board[activeFieldRow][activeFieldColumn].setActive(true);
        }

    }

    /**
     * Handles mouse clicks on tile set.
     * 
     * @param p
     *            point where mouse was clicked
     */
    private void handleClick(final Point p) {
        Component c = this.findComponentAt(p);
        if (c instanceof BoardTile) {
            // find coordinates for clicked tile...
            for (int i = 0; i < tileSetHeight; i++) {
                for (int j = 0; j < tileSetWidth; j++) {
                    if (((BoardTile) c).equals(board[i][j])) {
                        activeFieldRow = i;
                        activeFieldColumn = j;
                    }
                }
            }
        }

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
}
