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

import java.util.Date;
import java.util.EventObject;

import org.freenono.model.GameState;
import org.freenono.model.Nonogram;

public class GameEvent extends EventObject {

	private static final long serialVersionUID = 854958592468069527L;

	/*
	 * TODO: change GameEventType only to the following possibilities:
	 * program_control, time_event, field_event, options_event!!!
	 */
	public enum GameEventType {
		NONE, FIELD_OCCUPIED, FIELD_MARKED, FIELD_UNMARKED, ACTIVE_FIELD_CHANGED, WRONG_FIELD_OCCUPIED, MARK_FIELD, OCCUPY_FIELD, STATE_CHANGED, TIMER, OPTIONS_CHANGED, PROGRAM_CONTROL
	};

	public enum ProgramControlType {
		START_GAME, STOP_GAME, PAUSE_GAME, RESTART_GAME, RESUME_GAME, QUIT_PROGRAMM, SHOW_OPTIONS, SHOW_ABOUT, NONOGRAM_CHOSEN
	};

	/*
	 * TODO: add control type enums for options_events, field_events and
	 * time_events!!!
	 */

	private Nonogram pattern = null;
	private GameEventType gameEventType = GameEventType.NONE;
	private GameState oldState;
	private GameState newState;
	private ProgramControlType pct;
	private int fieldColumn;
	private int fieldRow;
	private String comment;
	private Date gameTime;
	private int failCount;

	public GameEvent(Object source) {
		super(source);
		this.oldState = null;
		this.newState = null;
		fieldColumn = 0;
		fieldRow = 0;
		setPct(null);
	}

	public GameEvent(Object source, ProgramControlType pct, Nonogram pattern) {
		super(source);
		this.setPct(pct);
		fieldColumn = 0;
		fieldRow = 0;
		oldState = null;
		newState = null;
		this.pattern = pattern;
	}

	public GameEvent(Object source, Date gameTime) {
		super(source);
		this.oldState = null;
		this.newState = null;
		fieldColumn = 0;
		fieldRow = 0;
		setPct(null);
		this.gameTime = gameTime;
	}

	public GameEvent(Object source, int failCount) {
		super(source);
		this.oldState = null;
		this.newState = null;
		fieldColumn = 0;
		fieldRow = 0;
		setPct(null);
		this.failCount = failCount;
	}

	public GameEvent(Object source, ProgramControlType pct) {
		super(source);
		this.setPct(pct);
		fieldColumn = 0;
		fieldRow = 0;
		oldState = null;
		newState = null;
	}

	public GameEvent(Object source, GameState oldState, GameState newState) {
		super(source);
		this.oldState = oldState;
		this.newState = newState;
		fieldColumn = 0;
		fieldRow = 0;
		setPct(null);
	}

	public GameEvent(Object source, int fieldColumn, int fieldRow) {
		super(source);
		this.fieldColumn = fieldColumn;
		this.fieldRow = fieldRow;
		oldState = null;
		newState = null;
		setPct(null);
	}

	public GameState getOldState() {
		return oldState;
	}

	public void setOldState(GameState oldState) {
		this.oldState = oldState;
	}

	public GameState getNewState() {
		return newState;
	}

	public void setNewState(GameState newState) {
		this.newState = newState;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public GameEventType getGameEventType() {
		return gameEventType;
	}

	public void setGameEventType(GameEventType gameEventType) {
		this.gameEventType = gameEventType;
	}

	/**
	 * @return the pct
	 */
	public ProgramControlType getPct() {
		return pct;
	}

	/**
	 * @param pct
	 *            the pct to set
	 */
	public void setPct(ProgramControlType pct) {
		this.pct = pct;
	}

	/**
	 * @return the gameTime
	 */
	public Date getGameTime() {
		return gameTime;
	}

	/**
	 * @param gameTime
	 *            the gameTime to set
	 */
	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}

	/**
	 * @return the failCount
	 */
	public int getFailCount() {
		return failCount;
	}

	/**
	 * @param failCount
	 *            the failCount to set
	 */
	public void setFailCount(int failCount) {
		this.failCount = failCount;
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
