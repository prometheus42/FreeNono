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
import java.awt.event.KeyEvent;
import java.util.Random;

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
import org.freenono.model.Nonogram;
import org.freenono.model.Token;

/**
 * Sets up the playfield. It uses a BoardTileSet with painted borders and all
 * previous marked or occupied fields. This class fires all user events that
 * concern marking and occupying fields on the board!
 * 
 * @author Christian Wichmann
 */
public class BoardTileSetPlayfield extends BoardTileSet {

    private static final long serialVersionUID = 723055953042228828L;

    private boolean gameRunning = false;
    private boolean markFields = false;
    private boolean unmarkFields = false;
    private boolean occupyFields = false;

    private Token[][] oldBoard = null;

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
            case gameOver:
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .releaseMouseButton();
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .setActive(false);
                gameRunning = false;
                break;

            case solved:
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .releaseMouseButton();
                getBoard()[getActiveFieldRow()][getActiveFieldColumn()]
                        .setActive(false);
                gameRunning = false;
                solveBoard();
                break;

            case userStop:
                break;

            case paused:
                // clear board during pause
                if (getSettings().getHidePlayfield()) {

                    clearBoard();
                }
                break;

            case running:
                gameRunning = true;
                if (e.getOldState() == GameState.paused) {

                    // restore board after pause
                    if (getSettings().getHidePlayfield()) {
                        restoreBoard();
                    }
                }
                break;
            case none:
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
            checkIfRowIsComplete(e.getFieldRow());
            checkIfColumnIsComplete(e.getFieldColumn());
        }

        @Override
        public void fieldUnoccupied(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()]
                        .setMarked(false);
            }
            checkIfRowIsComplete(e.getFieldRow());
            checkIfColumnIsComplete(e.getFieldColumn());
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()]
                        .setCrossed(true);
            }
            checkIfRowIsComplete(e.getFieldRow());
            checkIfColumnIsComplete(e.getFieldColumn());
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {
            if (gameRunning) {
                getBoard()[e.getFieldRow()][e.getFieldColumn()]
                        .setCrossed(false);
            }
            checkIfRowIsComplete(e.getFieldRow());
            checkIfColumnIsComplete(e.getFieldColumn());
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

        eventHelper.addGameListener(gameAdapter);

        setTileSetWidth(pattern.width());
        setTileSetHeight(pattern.height());
        oldBoard = new Token[getTileSetHeight()][getTileSetWidth()];

        buildBoardGrid();

        paintBorders();

        addListeners();

        // set all board tiles interactive to activate their mouse listener
        for (int i = 0; i < getTileSetHeight(); i++) {
            for (int j = 0; j < getTileSetWidth(); j++) {
                getBoard()[i][j].setInteractive(true);
                getBoard()[i][j].setTransparent(false);
            }
        }

        getBoard()[0][0].setActive(true);

        setOpaque(false);
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
     * Adding Listeners for key and mouse events on the nonogram board.
     */
    private void addListeners() {

        this.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(final KeyEvent evt) {

                int keyCode = evt.getKeyCode();

                if (keyCode == getSettings().getKeyCodeForControl(
                        Control.moveLeft)) {

                    moveActiveLeft();

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.moveRight)) {

                    moveActiveRight();

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.moveUp)) {

                    moveActiveUp();

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.moveDown)) {

                    moveActiveDown();

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.markField)) {

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

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.occupyField)) {

                    occupyFields = true;

                    occupyActiveField();

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.hint)) {

                    giveHint();
                }
            }

            public void keyReleased(final KeyEvent e) {

                int keyCode = e.getKeyCode();

                if (keyCode == getSettings().getKeyCodeForControl(
                        Control.markField)) {

                    markFields = false;
                    unmarkFields = false;

                } else if (keyCode == getSettings().getKeyCodeForControl(
                        Control.occupyField)) {

                    occupyFields = false;
                }
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
     * Mark a random field as a hint to the user.
     */
    public final void giveHint() {

        logger.debug("Giving user a hint :-)");

        Random rnd = new Random();
        int y = rnd.nextInt(getTileSetHeight());
        int x = rnd.nextInt(getTileSetWidth());

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
        }
    }
}
