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
package org.freenono.event;

import org.freenono.model.Nonogram;
import org.freenono.model.Token;

/**
 * Event used for all actions on the board, like marking or occupying a field.
 * 
 * @author Markus Wichmann, Christian Wichmann
 */
public class FieldControlEvent extends GameEvent {

    private static final long serialVersionUID = 127977031064256552L;

    /**
     * Types of field control events.
     * 
     * @author Christian Wichmann
     */
    public enum FieldControlType {
        NONE, FIELD_OCCUPIED, FIELD_MARKED, FIELD_UNMARKED, 
        ACTIVE_FIELD_CHANGED, WRONG_FIELD_OCCUPIED, MARK_FIELD, 
        OCCUPY_FIELD, FIELD_UNOCCUPIED
    };

    private FieldControlType fieldControlType = FieldControlType.NONE;
    private Nonogram pattern = null;
    private Token[][] field = null;

    private  int fieldColumn;
    private int fieldRow;

    /**
     * Initializes a field control event to signal a move on the board.
     * 
     * @param source
     *            Source where event was fired.
     * @param fieldControlType
     *            Type of field control event.
     * @param fieldColumn
     *            Column of field that has changed or should change
     * @param fieldRow
     *            Row of field that has changed or should change
     */
    public FieldControlEvent(final Object source, final FieldControlType fieldControlType,
            final int fieldColumn, final int fieldRow) {

        super(source, GameEventType.FieldControlEvent);

        this.fieldControlType = fieldControlType;
        this.fieldColumn = fieldColumn;
        this.fieldRow = fieldRow;
    }

    /**
     * Initializes a field control event to signal a move on the board.
     * 
     * @param source
     *            Source where event was fired.
     * @param fieldColumn
     *            Column of field that has changed or should change
     * @param fieldRow
     *            Row of field that has changed or should change
     */
    public FieldControlEvent(final Object source, final int fieldColumn,
            final int fieldRow) {

        super(source, GameEventType.FieldControlEvent);

        this.fieldControlType = FieldControlType.NONE;
        this.fieldColumn = fieldColumn;
        this.fieldRow = fieldRow;
    }

    /**
     * Gets column of concerned field.
     * 
     * @return Column of field.
     */
    public final int getFieldColumn() {

        return fieldColumn;
    }

    /**
     * Sets column of concerned field.
     * 
     * @param fieldColumn Column of field.
     */
    public final void setFieldColumn(final int fieldColumn) {

        this.fieldColumn = fieldColumn;
    }

    /**
     * Gets row of concerned field.
     * 
     * @return Row of field.
     */
    public final int getFieldRow() {

        return fieldRow;
    }

    /**
     * Sets row of concerned field.
     * 
     * @param fieldRow Row of field.
     */
    public final void setFieldRow(final int fieldRow) {

        this.fieldRow = fieldRow;
    }

    /**
     * Gets nonogram pattern for this event.
     * 
     * @return Nonogram pattern for this event.
     */
    public final Nonogram getPattern() {

        return pattern;
    }

    /**
     * Sets nonogram pattern for this event.
     * 
     * @param pattern
     *            nonogram pattern for this event.
     */
    public final void setPattern(final Nonogram pattern) {

        this.pattern = pattern;
    }

    /**
     * Gets a two-dimensional field of Token.
     * 
     * @return Two-dimensional Token field.
     */
    public final Token[][] getField() {

        return field;
    }

    /**
     * Sets a two-dimensional field of Token.
     * 
     * @param field Two-dimensional Token field.
     */
    public final void setField(final Token[][] field) {

        this.field = field;
    }

    /**
     * Gets type of FieldControlEvent defined in FieldControlType enum.
     * 
     * @return the type of FieldControlEvent.
     */
    public final FieldControlType getFieldControlType() {

        return fieldControlType;
    }

    /**
     * Sets type of FieldControlEvent defined in FieldControlType enum.
     * 
     * @param fieldControlType
     *            Type of FieldControlEvent.
     */
    public final void setFieldControlType(final FieldControlType fieldControlType) {

        this.fieldControlType = fieldControlType;
    }

}
