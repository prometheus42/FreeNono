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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.freenono.controller.ControlSettings.Control;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.QuizEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.GameState;
import org.freenono.model.Token;
import org.freenono.model.data.Nonogram;

/**
 * Sets up the playfield. It uses a BoardTileSet with painted borders and all
 * previous marked or occupied fields. This class fires all user events that
 * concern marking and occupying fields on the board!
 * 
 * @author Christian Wichmann
 */
public class BoardTileSetPlayfield extends BoardTileSet implements Scrollable {

    private static final long serialVersionUID = 723055953042228828L;

    private boolean gameRunning = false;
    private boolean markFields = false;
    private boolean unmarkFields = false;
    private boolean occupyFields = false;
    private boolean doHidePlayfield = false;

    private final List<Integer> rowsToHint = new ArrayList<Integer>();
    private final List<Integer> columnsToHint = new ArrayList<Integer>();

    private Token[][] oldBoard = null;
    private GamepadAdapter gamepadAdapter = null;

    private static Logger logger = Logger
            .getLogger(BoardTileSetPlayfield.class);

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void optionsChanged(final ProgramControlEvent e) {

            for (int i = 0; i < getTileSetHeight(); i++) {
                for (int j = 0; j < getTileSetWidth(); j++) {

                    getBoard()[i][j].setColorModel(getSettings()
                            .getColorModel());
                    getBoard()[i][j].repaint();
                }
            }
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .releaseMouseButton();
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .setActive(false);
                gameRunning = false;
                if (gamepadAdapter != null) {
                    gamepadAdapter.stopPolling();
                    gamepadAdapter = null;
                }
                break;

            case SOLVED:
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .releaseMouseButton();
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .setActive(false);
                gameRunning = false;
                if (gamepadAdapter != null) {
                    gamepadAdapter.stopPolling();
                    gamepadAdapter = null;
                }
                solveBoard();
                break;

            case USER_STOP:
                if (gamepadAdapter != null) {
                    gamepadAdapter.stopPolling();
                    gamepadAdapter = null;
                }
                break;

            case PAUSED:
                // clear board during pause
                if (doHidePlayfield) {

                    clearBoard();
                }
                break;

            case RUNNING:
                gameRunning = true;
                if (e.getOldState() == GameState.PAUSED) {

                    // restore board after pause
                    if (doHidePlayfield) {
                        restoreBoard();
                    }
                }
                break;
            case NONE:
                break;
            default:
                assert false : e.getNewState();
                break;
            }

        }

        @Override
        public void fieldOccupied(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()].setMarked(true);
            }
            if (getSettings().getMarkCompleteRowsColumns()) {
                checkIfRowIsComplete(e.getFieldRow());
                checkIfColumnIsComplete(e.getFieldColumn());
            }
        }

        @Override
        public void fieldUnoccupied(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()]
                        .setMarked(false);
            }
            if (getSettings().getMarkCompleteRowsColumns()) {
                checkIfRowIsComplete(e.getFieldRow());
                checkIfColumnIsComplete(e.getFieldColumn());
            }
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()]
                        .setCrossed(true);
            }
            if (getSettings().getMarkCompleteRowsColumns()) {
                checkIfRowIsComplete(e.getFieldRow());
                checkIfColumnIsComplete(e.getFieldColumn());
            }
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()]
                        .setCrossed(false);
            }
            if (getSettings().getMarkCompleteRowsColumns()) {
                checkIfRowIsComplete(e.getFieldRow());
                checkIfColumnIsComplete(e.getFieldColumn());
            }
        }

        @Override
        public void changeActiveField(final FieldControlEvent e) {

            if (gameRunning) {

                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .setActive(false);
                setActiveFieldColumn(e.getFieldColumn());
                setActiveFieldRow(e.getFieldRow());
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .setActive(true);

                checkKeyStillPressed();
            }
        }

        @Override
        public void askQuestion(final QuizEvent e) {

            // Resets internal variables of currently active board tile to
            // prevent bug where mouse button stays 'active' after user is
            // asked a question (in GameModeQuestions).
            getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                    .releaseMouseButton();
        }

    };

    /**
     * Constructor that initializes internal data structures and paint borders
     * of game board.
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
    public BoardTileSetPlayfield(final GameEventHelper eventHelper,
            final Nonogram pattern, final Settings settings,
            final Dimension tileDimension) {

        super(eventHelper, pattern, settings, tileDimension);

        doHidePlayfield = getSettings().getHidePlayfield();

        eventHelper.addGameListener(gameAdapter);

        setTileSetWidth(pattern.width());
        setTileSetHeight(pattern.height());
        oldBoard = new Token[getTileSetHeight()][getTileSetWidth()];

        buildBoardGrid();

        paintBorders();

        addKeyBindingsMove();
        addKeyBindingsChange();
        try {
            Class.forName("net.java.games.input.Controller");
            gamepadAdapter = new GamepadAdapter(this);
        } catch (ClassNotFoundException e) {
            logger.warn("No JInput libs can be found.");
        }

        // set all board tiles interactive to activate their mouse listener
        for (int i = 0; i < getTileSetHeight(); i++) {
            for (int j = 0; j < getTileSetWidth(); j++) {
                getBoard()[i][j].setInteractive(true);
                getBoard()[i][j].setTransparent(false);
            }
        }
        getBoard()[0][0].setActive(true);

        // setting this component not opaque prevents a bug which
        // causes faulty painting of ColumnHeaderView and RowHeaderView
        // when scrolling the board
        setOpaque(false);

        // initialize lists with all rows and columns for giving player hints
        for (int i = 0; i < getPattern().height(); i++) {
            rowsToHint.add(i);
        }
        for (int i = 0; i < getPattern().width(); i++) {
            columnsToHint.add(i);
        }
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
     * Add key bindings for all controls to move on the field.
     */
    private void addKeyBindingsMove() {
        /*
         * TODO Use ChangedSettings event to update key bindings!
         */
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings().getKeyCodeForControl(Control.MOVE_LEFT),
                        0), "Left");
        getActionMap().put("Left", new AbstractAction() {
            private static final long serialVersionUID = 3526487415521380900L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveLeft();
                Rectangle view = getVisibleRect();
                int dx = 0;
                if (getActiveFieldColumn() * getTileDimension().width < view.width / 2) {
                    dx = -getScrollableBlockIncrement(view,
                            SwingConstants.HORIZONTAL, -1);
                } else {
                    dx = 0;
                }
                view.translate(dx, 0);
                scrollRectToVisible(view);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings().getKeyCodeForControl(Control.MOVE_RIGHT),
                        0), "Right");
        getActionMap().put("Right", new AbstractAction() {
            private static final long serialVersionUID = 3526487416521380900L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveRight();
                Rectangle view = getVisibleRect();
                int dx = 0;
                if (getActiveFieldColumn() * getTileDimension().width > view.width / 2) {
                    dx = getScrollableBlockIncrement(view,
                            SwingConstants.HORIZONTAL, 1);
                } else {
                    dx = 0;
                }
                view.translate(dx, 0);
                scrollRectToVisible(view);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(
                        getSettings().getKeyCodeForControl(Control.MOVE_UP), 0),
                        "Up");
        getActionMap().put("Up", new AbstractAction() {
            private static final long serialVersionUID = 3526481415521380900L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveUp();
                Rectangle view = getVisibleRect();
                int dy = 0;
                if (getActiveFieldRow() * getTileDimension().height < view.height / 2) {
                    dy = -getScrollableBlockIncrement(view,
                            SwingConstants.VERTICAL, -1);
                } else {
                    dy = 0;
                }
                view.translate(0, dy);
                scrollRectToVisible(view);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings().getKeyCodeForControl(Control.MOVE_DOWN),
                        0), "Down");
        getActionMap().put("Down", new AbstractAction() {
            private static final long serialVersionUID = -8632221802324267954L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                moveActiveDown();
                Rectangle view = getVisibleRect();
                int dy = 0;
                if (getActiveFieldRow() * getTileDimension().height > view.height / 2) {
                    dy = getScrollableBlockIncrement(view,
                            SwingConstants.VERTICAL, 1);
                } else {
                    dy = 0;
                }
                view.translate(0, dy);
                scrollRectToVisible(view);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("HOME"), "GoToHome");
        getActionMap().put("GoToHome", new AbstractAction() {
            private static final long serialVersionUID = 7128510030273601411L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO scroll to left
                getEventHelper().fireChangeActiveFieldEvent(
                        new FieldControlEvent(this, 0, getActiveFieldRow()));
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("END"), "GoToEnd");
        getActionMap().put("GoToEnd", new AbstractAction() {
            private static final long serialVersionUID = 7132502544255656098L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO scroll to right
                getEventHelper().fireChangeActiveFieldEvent(
                        new FieldControlEvent(this, getPattern().width() - 1,
                                getActiveFieldRow()));
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("PAGE_UP"), "GoToTop");
        getActionMap().put("GoToTop", new AbstractAction() {
            private static final long serialVersionUID = 7128510030273601411L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO scroll to top
                getEventHelper().fireChangeActiveFieldEvent(
                        new FieldControlEvent(this, getActiveFieldColumn(), 0));
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("PAGE_DOWN"), "GoToBottom");
        getActionMap().put("GoToBottom", new AbstractAction() {
            private static final long serialVersionUID = 7132502544255656098L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO scroll to bottom
                getEventHelper().fireChangeActiveFieldEvent(
                        new FieldControlEvent(this, getActiveFieldColumn(),
                                getPattern().height() - 1));
            }
        });
    }

    /**
     * Add key bindings for all controls to change fields on the board.
     */
    private void addKeyBindingsChange() {

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings().getKeyCodeForControl(Control.MARK_FIELD),
                        0, false), "Mark");
        getActionMap().put("Mark", new AbstractAction() {
            private static final long serialVersionUID = 1268229779077582261L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                // save what should be done, when key is not released but
                // active field changed
                if (getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .isCrossed()) {
                    unmarkFields = true;
                    markFields = false;
                } else {
                    markFields = true;
                    unmarkFields = false;
                }
                markActiveField();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings().getKeyCodeForControl(Control.MARK_FIELD),
                        0, true), "MarkReleased");
        getActionMap().put("MarkReleased", new AbstractAction() {
            private static final long serialVersionUID = 6743457677218700547L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                markFields = false;
                unmarkFields = false;
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings()
                                .getKeyCodeForControl(Control.OCCUPY_FIELD), 0,
                        false), "Occupy");
        getActionMap().put("Occupy", new AbstractAction() {
            private static final long serialVersionUID = 8228569120230316012L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                occupyFields = true;
                occupyActiveField();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(
                        getSettings()
                                .getKeyCodeForControl(Control.OCCUPY_FIELD), 0,
                        true), "OccupyReleased");
        getActionMap().put("OccupyReleased", new AbstractAction() {
            private static final long serialVersionUID = -4733029188707402453L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                occupyFields = false;
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("H"), "Hint");
        getActionMap().put("Hint", new AbstractAction() {
            private static final long serialVersionUID = -4486665509995510699L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                giveHint();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("G"), "HintCurrentRowColumn");
        getActionMap().put("HintCurrentRowColumn", new AbstractAction() {
            private static final long serialVersionUID = -4486665509995510699L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                giveHintForCurrentRowColumn();
            }
        });
    }

    /**
     * Paint the borders.
     */
    private void paintBorders() {

        final int dividerMainGrid = 5;

        for (int i = 0; i < getTileSetHeight(); i++) {

            for (int j = 0; j < getTileSetWidth(); j++) {

                getBoard()[i][j].setDrawBorderWest(true);

                if ((j + 1) % dividerMainGrid == 0
                        || (j + 1) == getTileSetWidth()) {
                    getBoard()[i][j].setDrawBorderEast(true);
                }

                getBoard()[i][j].setDrawBorderNorth(true);

                if ((i + 1) % dividerMainGrid == 0
                        || (i + 1) == getTileSetHeight()) {
                    getBoard()[i][j].setDrawBorderSouth(true);
                }
            }
        }
    }

    /**
     * Checks if keys for occupying or marking of fields are still pressed. If
     * so the active field will be marked or occupied accordingly.
     */
    private void checkKeyStillPressed() {

        if (markFields
                && !(getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .isCrossed())) {

            markActiveField();

        } else if (unmarkFields
                && getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .isCrossed()) {

            markActiveField();
        }

        if (occupyFields) {

            occupyActiveField();
        }
    }

    /**
     * Fire OccupyFieldEvent.
     */
    public final void occupyActiveField() {

        getEventHelper().fireOccupyFieldEvent(
                new FieldControlEvent(this, getActiveFieldColumn(),
                        getActiveFieldRow()));

    }

    /**
     * Fire MarkFieldEvent.
     */
    public final void markActiveField() {

        getEventHelper().fireMarkFieldEvent(
                new FieldControlEvent(this, getActiveFieldColumn(),
                        getActiveFieldRow()));

    }

    /**
     * Set the active field to 'column', 'row'.
     * @param column
     *            Column of active field.
     * @param row
     *            Row of active field.
     */
    public final void setActive(final int column, final int row) {
        if (column >= 0 && column < getTileSetWidth() && row >= 0
                && row < getTileSetHeight()) {

            getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                    .setActive(false);
            setActiveFieldColumn(column);
            setActiveFieldRow(row);
            getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                    .setActive(true);
        }
    }

    /**
     * Fire ChangeActiveFieldEvent (Left), if active field is set.
     */
    public final void moveActiveLeft() {

        if (getActiveFieldColumn() > 0) {

            getEventHelper().fireChangeActiveFieldEvent(
                    new FieldControlEvent(this, getActiveFieldColumn() - 1,
                            getActiveFieldRow()));
        }
    }

    /**
     * Fire ChangeActiveFieldEvent (Right), if active field is set.
     */
    public final void moveActiveRight() {

        if (getActiveFieldColumn() < getTileSetWidth() - 1) {

            getEventHelper().fireChangeActiveFieldEvent(
                    new FieldControlEvent(this, getActiveFieldColumn() + 1,
                            getActiveFieldRow()));
        }
    }

    /**
     * Fire ChangeActiveFieldEvent (Up), if active field is set.
     */
    public final void moveActiveUp() {

        if (getActiveFieldRow() > 0) {

            getEventHelper().fireChangeActiveFieldEvent(
                    new FieldControlEvent(this, getActiveFieldColumn(),
                            getActiveFieldRow() - 1));
        }
    }

    /**
     * Fire ChangeActiveFieldEvent (Down), if active field is set.
     */
    public final void moveActiveDown() {

        if (getActiveFieldRow() < getTileSetHeight() - 1) {

            getEventHelper().fireChangeActiveFieldEvent(
                    new FieldControlEvent(this, getActiveFieldColumn(),
                            getActiveFieldRow() + 1));
        }
    }

    /**
     * Clear the board and store state for later use.
     */
    public final void clearBoard() {

        for (int i = 0; i < getTileSetHeight(); i++) {

            for (int j = 0; j < getTileSetWidth(); j++) {

                oldBoard[i][j] = Token.FREE;

                if (getBoard()[i][j].isMarked()) {
                    oldBoard[i][j] = Token.OCCUPIED;
                }

                if (getBoard()[i][j].isCrossed()) {
                    oldBoard[i][j] = Token.MARKED;
                }

                getBoard()[i][j].setMarked(false);
                getBoard()[i][j].setCrossed(false);
            }
        }
    }

    /**
     * Restore board after {@link BoardTileSetPlayfield#clearBoard()} has been
     * called.
     */
    public final void restoreBoard() {

        for (int i = 0; i < getTileSetHeight(); i++) {

            for (int j = 0; j < getTileSetWidth(); j++) {

                if (oldBoard[i][j] == Token.MARKED) {
                    getBoard()[i][j].setCrossed(true);
                }

                if (oldBoard[i][j] == Token.OCCUPIED) {
                    getBoard()[i][j].setMarked(true);
                }
            }
        }
    }

    /**
     * Automatically solve the whole board.
     */
    public final void solveBoard() {

        for (int i = 0; i < getTileSetHeight(); i++) {
            for (int j = 0; j < getTileSetWidth(); j++) {
                getBoard()[i][j].setCrossed(false);
                if (getPattern().getFieldValue(j, i)) {
                    getBoard()[i][j].setMarked(true);
                } else {
                    getBoard()[i][j].setMarked(false);
                }
            }
        }
    }

    /**
     * Give player a hint by solving a whole row and a whole column on the
     * board.
     */
    private void giveHint() {

        logger.info("Giving user a hint :-)");

        // give hint only when some rows and columns are not yet hinted (fixes
        // an possible IndexOutOfBoundsException)
        if (!columnsToHint.isEmpty() && !rowsToHint.isEmpty()) {

            Collections.shuffle(columnsToHint);
            Collections.shuffle(rowsToHint);
            int x = columnsToHint.get(0);
            int y = rowsToHint.get(0);
            columnsToHint.remove(0);
            rowsToHint.remove(0);

            for (int i = 0; i < getTileSetHeight(); i++) {
                setActive(x, i);
                if (getPattern().getFieldValue(x, i)) {
                    occupyActiveField();
                } else {
                    if (!(getBoard()[i][x].isCrossed())) {
                        markActiveField();
                    }
                }
            }

            for (int i = 0; i < getTileSetWidth(); i++) {
                setActive(i, y);
                if (getPattern().getFieldValue(i, y)) {
                    occupyActiveField();
                } else {
                    if (!(getBoard()[y][i].isCrossed())) {
                        markActiveField();
                    }
                }
            }
        }
    }

    /**
     * Give player a hint by solving the row/column of the currently active
     * field.
     */
    private void giveHintForCurrentRowColumn() {

        logger.info("Giving user a hint :-)");

        final int x = getActiveFieldColumn();
        final int y = getActiveFieldRow();

        for (int i = 0; i < getTileSetHeight(); i++) {
            setActive(x, i);
            if (getPattern().getFieldValue(x, i)) {
                occupyActiveField();
            } else {
                if (!(getBoard()[i][x].isCrossed())) {
                    markActiveField();
                }
            }
        }

        for (int i = 0; i < getTileSetWidth(); i++) {
            setActive(i, y);
            if (getPattern().getFieldValue(i, y)) {
                occupyActiveField();
            } else {
                if (!(getBoard()[y][i].isCrossed())) {
                    markActiveField();
                }
            }
        }

        setActive(x, y);
    }

    /**
     * Checks whether row is completely finished i.e. all fields are either
     * occupied or marked by the player. If true this row is colored.
     * 
     * @param row
     *            row to check
     */
    private void checkIfRowIsComplete(final int row) {

        boolean isComplete = true;

        for (int i = 0; i < getTileSetWidth(); i++) {

            if (!(getBoard()[row][i].isCrossed() || getBoard()[row][i]
                    .isMarked())) {
                isComplete = false;
                break;
            }
        }

        for (int i = 0; i < getTileSetWidth(); i++) {
            if (isComplete) {
                getBoard()[row][i].setDormant(true);
            }
            // TODO Implement good dedormantizing!
        }
    }

    /**
     * Checks whether row is completely finished i.e. all fields are either
     * occupied or marked by the player. If true this column is colored.
     * 
     * @param column
     *            column to check
     */
    private void checkIfColumnIsComplete(final int column) {

        boolean isComplete = true;

        for (int i = 0; i < getTileSetHeight(); i++) {

            if (!(getBoard()[i][column].isCrossed() || getBoard()[i][column]
                    .isMarked())) {
                isComplete = false;
                break;
            }
        }

        for (int i = 0; i < getTileSetHeight(); i++) {
            if (isComplete) {
                getBoard()[i][column].setDormant(true);
            }
            // TODO Implement good dedormantizing!
        }
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
            return getTileDimension().height;
        } else if (orientation == SwingConstants.HORIZONTAL) {
            return getTileDimension().width;
        } else {
            return 0;
        }
    }

    @Override
    public final int getScrollableUnitIncrement(final Rectangle visibleRect,
            final int orientation, final int direction) {

        if (orientation == SwingConstants.VERTICAL) {
            return getTileDimension().height;
        } else if (orientation == SwingConstants.HORIZONTAL) {
            return getTileDimension().width;
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
