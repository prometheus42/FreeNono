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

import org.freenono.model.CaptionOrientation;
import org.freenono.model.data.Nonogram;

/**
 * Event used for all actions on the board, like marking or occupying a field.
 * 
 * @author Markus Wichmann, Christian Wichmann
 */
public class FieldControlEvent extends GameEvent {

    /*
     * TODO Change this class from tagged class to class hierarchy. Make this
     * class super class for various field related events and move each
     * constructor into one of these classes.
     */

    private static final long serialVersionUID = 127977031064256552L;

    /**
     * Types of field control events.
     * 
     * @author Christian Wichmann
     */
    public enum FieldControlType {
        NONE, FIELD_OCCUPIED, FIELD_MARKED, FIELD_UNMARKED, 
        ACTIVE_FIELD_CHANGED, WRONG_FIELD_OCCUPIED, MARK_FIELD,
        OCCUPY_FIELD, FIELD_UNOCCUPIED, CROSS_OUT_CAPTION
    };

    private FieldControlType fieldControlType = FieldControlType.NONE;
    private Nonogram pattern = null;
    private CaptionOrientation orientation = null;

    private int fieldColumn;
    private int fieldRow;
    private int caption;

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
    public FieldControlEvent(final Object source,
            final FieldControlType fieldControlType, final int fieldColumn,
            final int fieldRow) {

        super(source, GameEventType.FIELD_CONTROL_EVENT);

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

        super(source, GameEventType.FIELD_CONTROL_EVENT);

        this.fieldControlType = FieldControlType.NONE;
        this.fieldColumn = fieldColumn;
        this.fieldRow = fieldRow;
    }

    /**
     * Initializes a field control event to handle everything that happens to
     * the captions.
     * 
     * @param source
     *            source where event was fired.
     * @param fieldColumn
     *            column which caption is affected
     * @param fieldRow
     *            row which caption is affected
     * @param orientation
     *            orientation of caption that is affected
     * @param caption
     *            number of caption that is affected
     */
    public FieldControlEvent(final Object source,
            final CaptionOrientation orientation, final int fieldColumn,
            final int fieldRow, final int caption) {

        super(source, GameEventType.FIELD_CONTROL_EVENT);

        this.fieldControlType = FieldControlType.NONE;
        this.orientation = orientation;
        this.fieldColumn = fieldColumn;
        this.fieldRow = fieldRow;
        this.caption = caption;
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
     * @param fieldColumn
     *            Column of field.
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
     * @param fieldRow
     *            Row of field.
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
     * Gets the type of this field control event as defined in enumeration
     * FieldControlType.
     * 
     * @return the type of FieldControlEvent.
     */
    public final FieldControlType getFieldControlType() {

        return fieldControlType;
    }

    /**
     * Sets the type of this field control event as defined in enumeration
     * FieldControlType.
     * 
     * @param fieldControlType
     *            Type of FieldControlEvent.
     */
    public final void setFieldControlType(
            final FieldControlType fieldControlType) {

        this.fieldControlType = fieldControlType;
    }

    /**
     * Gets orientation of caption that is affected.
     * 
     * @return orientation of affected caption
     */
    public final CaptionOrientation getOrientation() {

        return orientation;
    }

    /**
     * Sets orientation of caption that is affected.
     * 
     * @param orientation
     *            orientation to be set
     */
    public final void setOrientation(final CaptionOrientation orientation) {

        this.orientation = orientation;
    }

    /**
     * Gets number of caption that is affected.
     * 
     * @return number of affected caption
     */
    public final int getCaption() {

        return caption;
    }

    /**
     * Sets number of caption that is affected.
     * 
     * @param caption
     *            number of caption that is affected
     */
    public final void setCaption(final int caption) {

        this.caption = caption;
    }

}
