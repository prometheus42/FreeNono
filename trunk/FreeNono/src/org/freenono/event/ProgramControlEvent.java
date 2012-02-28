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


public class ProgramControlEvent extends GameEvent {

	private static final long serialVersionUID = -6463642216777461887L;

	public enum ProgramControlType {
		START_GAME, STOP_GAME, PAUSE_GAME, RESTART_GAME, RESUME_GAME, QUIT_PROGRAMM, 
		SHOW_OPTIONS, SHOW_ABOUT, NONOGRAM_CHOSEN, OPTIONS_CHANGED
	};

	private ProgramControlType pct = null;
	
	// TODO: remove Nonogram from this class and GameAdapter of Manager!
	private Nonogram pattern = null;
	

	public ProgramControlEvent(Object source, ProgramControlType pct) {
		super(source, GameEventType.ProgramControlEvent);
		this.setPct(pct);
		this.setPattern(null);
	}

	public ProgramControlEvent(Object source, ProgramControlType pct,
			Nonogram currentNonogram) {
		super(source, GameEventType.ProgramControlEvent);
		this.setPct(pct);
		this.setPattern(currentNonogram); 
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
	 * @return the pattern
	 */
	public Nonogram getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Nonogram pattern) {
		this.pattern = pattern;
	}

}
