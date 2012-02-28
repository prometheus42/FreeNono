package de.ichmann.markusw.java.apps.freenono.event;

import java.util.EventObject;

import de.ichmann.markusw.java.apps.freenono.model.GameState;

public class GameEvent extends EventObject {

	private static final long serialVersionUID = 854958592468069527L;

	public enum GameEventType {
		NONE, FIELD_OCCUPIED, FIELD_MARKED, ACTIVE_FIELD_CHANGED, STATE_CHANGED, 
		TIMER, OPTIONS_CHANGED, WRONG_FIELD_OCCUPIED
	}

	private GameEventType gameEventType = GameEventType.NONE;
	private GameState oldState;
	private GameState newState;
	private int fieldColumn;
	private int fieldRow;
	private String comment;

	public GameEvent(Object source) {
		super(source);
		this.oldState = null;
		this.newState = null;
		fieldColumn = 0;
		fieldRow = 0;
	}

	public GameEvent(Object source, GameState oldState, GameState newState) {
		super(source);
		this.oldState = oldState;
		this.newState = newState;
		fieldColumn = 0;
		fieldRow = 0;
	}

	public GameEvent(Object source, int fieldColumn, int fieldRow) {
		super(source);
		this.fieldColumn = fieldColumn;
		this.fieldRow = fieldRow;
		oldState = null;
		newState = null;
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

}
