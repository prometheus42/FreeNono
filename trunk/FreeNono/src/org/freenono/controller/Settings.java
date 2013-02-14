/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Markus Wichmann, Christian Wichmann
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
package org.freenono.controller;

import java.awt.Color;

import org.apache.log4j.Logger;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.model.GameModeType;
import org.freenono.controller.ControlSettings.Control;

public class Settings {

	private static Logger logger = Logger.getLogger(Settings.class);

	private GameEventHelper eventHelper;
	private ControlSettings controlSettings = new ControlSettings();
	private ColorModel currentColorModel;

	private final boolean USE_MAX_FAIL_COUNT_DEFAULT = true;
	private boolean useMaxFailCount = true;

	private final int MAX_FAIL_COUNT_DEFAULT = 5;
	private int maxFailCount = MAX_FAIL_COUNT_DEFAULT;

	private final boolean USE_MAX_TIME_DEFAULT = true;
	private boolean useMaxTime = true;

	private final long MAX_TIME_DEFAULT = 1800000;
	private long maxTime = MAX_TIME_DEFAULT;

	private final boolean MARK_INVALID_DEFAULT = true;
	private boolean markInvalid = MARK_INVALID_DEFAULT;

	private final boolean COUNT_MARKED_DEFAULT = false;
	private boolean countMarked = COUNT_MARKED_DEFAULT;

	private final boolean PLAY_AUDIO_DEFAULT = true;
	private boolean playAudio = PLAY_AUDIO_DEFAULT;

	private final boolean HIDE_PLAYFIELD_DEFAULT = false;
	private boolean hidePlayfield = HIDE_PLAYFIELD_DEFAULT;
	
	private final GameModeType GAME_MODE_DEFAULT = GameModeType.PENALTY;
	private GameModeType gameMode = GAME_MODE_DEFAULT;
	
	private final Color BASE_COLOR_DEFAULT = new Color(143, 231, 200);
	private Color baseColor = BASE_COLOR_DEFAULT;

	
	public Settings() {

		super();
		
		currentColorModel = new ColorModelSimple(baseColor);
	}

	public void resetSettings() {

		setCountMarked(COUNT_MARKED_DEFAULT);
		setMarkInvalid(MARK_INVALID_DEFAULT);
		setMaxFailCount(MAX_FAIL_COUNT_DEFAULT);
		setUseMaxFailCount(USE_MAX_FAIL_COUNT_DEFAULT);
		setMaxTime(MAX_FAIL_COUNT_DEFAULT);
		setUseMaxTime(USE_MAX_TIME_DEFAULT);
		setPlayAudio(PLAY_AUDIO_DEFAULT);
		setHidePlayfield(HIDE_PLAYFIELD_DEFAULT);
		setGameMode(GAME_MODE_DEFAULT);
		setBaseColor(BASE_COLOR_DEFAULT);
	}

	public int getMaxFailCount() {
		return maxFailCount;
	}

	public void setMaxFailCount(int maxFailCount) {

		if (this.maxFailCount != maxFailCount) {
			this.maxFailCount = maxFailCount;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public boolean getUseMaxTime() {
		return useMaxTime;
	}

	public void setUseMaxTime(boolean useMaxTime) {
		if (this.useMaxTime != useMaxTime) {
			this.useMaxTime = useMaxTime;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(long maxTime) {
		if (this.maxTime != maxTime) {
			this.maxTime = maxTime;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public boolean getUseMaxFailCount() {
		return useMaxFailCount;
	}

	public void setUseMaxFailCount(boolean useMaxFailCount) {
		if (this.useMaxFailCount != useMaxFailCount) {
			this.useMaxFailCount = useMaxFailCount;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public boolean getMarkInvalid() {
		return markInvalid;
	}

	public void setMarkInvalid(boolean markInvalid) {
		if (this.markInvalid != markInvalid) {
			this.markInvalid = markInvalid;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public boolean getCountMarked() {
		return countMarked;
	}

	public void setCountMarked(boolean countMarked) {
		if (this.countMarked != countMarked) {
			this.countMarked = countMarked;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public boolean getPlayAudio() {
		return playAudio;
	}

	public void setPlayAudio(boolean playAudio) {
		if (this.playAudio != playAudio) {
			this.playAudio = playAudio;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public boolean getHidePlayfield() {
		return hidePlayfield;
	}

	public void setHidePlayfield(boolean hidePlayfield) {
		if (this.hidePlayfield != hidePlayfield) {
			this.hidePlayfield = hidePlayfield;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	/**
	 * @return the gameMode
	 */
	public GameModeType getGameMode() {
		
		return gameMode;
	}

	/**
	 * @param gameMode
	 *            the gameMode to set
	 */
	public void setGameMode(GameModeType gameMode) {
		
		if (this.gameMode != gameMode) {
			this.gameMode = gameMode;

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}

	public Integer getKeyCodeForControl(Control ct) {
		
		return controlSettings.getControl(ct);
	}

	public void setEventHelper(GameEventHelper eventHelper) {
		
		this.eventHelper = eventHelper;
	}

	public ControlSettings getControlSettings() {
		
		return controlSettings;
	}

	
	public Color getBaseColor() {
		
		return baseColor;
	}

	public void setBaseColor(Color baseColor) {
		
		if (this.baseColor != baseColor) {
			
			this.baseColor = baseColor;
			this.currentColorModel = new ColorModelSimple(baseColor);

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}
	
	public ColorModel getColorModel() {
		
		return currentColorModel;
	}

}
