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
package de.ichmann.markusw.java.apps.freenono.model;

import de.ichmann.markusw.java.apps.freenono.event.GameEvent;
import de.ichmann.markusw.java.apps.freenono.event.GameEventHelper;

public class Settings {

	private GameEventHelper eventHelper = null;
	
	private final int MAX_FAIL_COUNT_DEFAULT = 5;
	private boolean useMaxFailCount = true;
	private int maxFailCount = MAX_FAIL_COUNT_DEFAULT;

	private final long MAX_TIME_DEFAULT = 300000;
	private boolean useMaxTime = true;
	private long maxTime = MAX_TIME_DEFAULT;

	private final boolean MARK_INVALID_DEFAULT = true;
	private boolean markInvalid = MARK_INVALID_DEFAULT;

	private final boolean COUNT_MARKED_DEFAULT = false;
	private boolean countMarked = COUNT_MARKED_DEFAULT;
	
	private final boolean PLAY_AUDIO_DEFAULT = true;
	private boolean playAudio = PLAY_AUDIO_DEFAULT;
	
	private final boolean HIDE_PLAYFIELD_DEFAULT = false;
	private boolean hidePlayfield = HIDE_PLAYFIELD_DEFAULT;

	public Settings() {
		super();
	}

	public void resetSettings() {
		
		setCountMarked(COUNT_MARKED_DEFAULT);
		setMarkInvalid(MARK_INVALID_DEFAULT);
		setMaxFailCount(MAX_FAIL_COUNT_DEFAULT);
		setMaxTime(MAX_FAIL_COUNT_DEFAULT);
		setPlayAudio(PLAY_AUDIO_DEFAULT);
		setHidePlayfield(HIDE_PLAYFIELD_DEFAULT);
		
	}

	public int getMaxFailCount() {
		return maxFailCount;
	}

	public void setMaxFailCount(int maxFailCount) {
		if (this.maxFailCount != maxFailCount) {
			this.maxFailCount = maxFailCount;
			if (maxFailCount > 0) {
				this.useMaxFailCount = true;
			} else {
				this.useMaxFailCount = false;
			}
		}
		
		if (eventHelper != null) {
			eventHelper.fireOptionsChangedEvent(new GameEvent(this));
		}
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		if (this.maxTime != maxTime) {
			this.maxTime = maxTime;
			if (maxTime > 0) {
				this.useMaxTime = true;
			} else {
				this.useMaxTime = false;
			}
		}
		
		if (eventHelper != null) {
			eventHelper.fireOptionsChangedEvent(new GameEvent(this));
		}
	}

	public boolean getMarkInvalid() {
		return markInvalid;
	}

	public void setMarkInvalid(boolean markInvalid) {
		if (this.markInvalid != markInvalid) {
			this.markInvalid = markInvalid;
		}
		
		if (eventHelper != null) {
			eventHelper.fireOptionsChangedEvent(new GameEvent(this));
		}
	}

	public boolean getCountMarked() {
		return countMarked;
	}

	public void setCountMarked(boolean countMarked) {
		if (this.countMarked != countMarked) {
			this.countMarked = countMarked;
		}
		
		if (eventHelper != null) {
			eventHelper.fireOptionsChangedEvent(new GameEvent(this));
		}
	}

	public boolean getPlayAudio() {
		return playAudio;
	}

	public void setPlayAudio(boolean playAudio) {
		this.playAudio = playAudio;
		
		if (eventHelper != null) {
			eventHelper.fireOptionsChangedEvent(new GameEvent(this));
		}
	}

	public boolean getHidePlayfield() {
		return hidePlayfield;
	}

	public void setHidePlayfield(boolean hidePlayfield) {
		this.hidePlayfield = hidePlayfield;
		
		if (eventHelper != null) {
			eventHelper.fireOptionsChangedEvent(new GameEvent(this));
		}
	}

	public boolean usesMaxFailCount() {
		return useMaxFailCount;
	}

	public boolean usesMaxTime() {
		return useMaxTime;
	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}

}
