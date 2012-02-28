/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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

public class FieldControlEvent extends GameEvent {

	private static final long serialVersionUID = 127977031064256552L;

	public enum FieldControlType {
		NONE, FIELD_OCCUPIED, FIELD_MARKED, FIELD_UNMARKED, ACTIVE_FIELD_CHANGED, 
		WRONG_FIELD_OCCUPIED, MARK_FIELD, OCCUPY_FIELD
	};

	protected FieldControlType fieldControlType = FieldControlType.NONE;
	protected Nonogram pattern = null;

	protected int fieldColumn;
	protected int fieldRow;
	

	public FieldControlEvent(Object source, FieldControlType fieldControlType,
			int fieldColumn, int fieldRow) {
		super(source, GameEventType.FieldControlEvent);
		
		this.fieldControlType = fieldControlType;
		this.fieldColumn = fieldColumn;
		this.fieldRow = fieldRow;
	}

	public int getFieldColumn() {
		return fieldColumn;
	}

	public void setFieldColumn(int fieldColumn) {
		this.fieldColumn = fieldColumn;
	}

	public int getFieldRow() {
		return fieldRow;
	}

	public void setFieldRow(int fieldRow) {
		this.fieldRow = fieldRow;
	}

	/**
	 * @return the pattern
	 */
	public Nonogram getPattern() {
		return pattern;
	}

	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(Nonogram pattern) {
		this.pattern = pattern;
	}
	
}
