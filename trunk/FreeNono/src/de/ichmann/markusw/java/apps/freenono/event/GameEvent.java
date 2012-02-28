package de.ichmann.markusw.java.apps.freenono.event;

import java.util.EventObject;


public class GameEvent extends EventObject {

	private static final long serialVersionUID = 854958592468069527L;
	
	private String comment;

	public GameEvent(Object source, String comment) {
		super(source);
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
	
}
