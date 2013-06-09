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
package org.freenono.controller;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;


/**
 * Saves key codes for controls defined in the enum Control. Not all control
 * settings are necessarily used in FreeNono.
 * 
 * @author Martin Wichmann, Christian Wichmann
 */
public class ControlSettings {

	private static Logger logger = Logger.getLogger(ControlSettings.class);

	public enum Control {
		moveUp, moveDown, moveLeft, moveRight, markField, occupyField, quitGame, 
		stopGame, pauseGame, startGame, resumeGame, restartGame, quitProgramm, 
		showOptions, showAbout, hint
	};

	private Map<Control, Integer> controls = new HashMap<Control, Integer>();

	private GameEventHelper eventHelper;
	
	
	public ControlSettings() {
		
		setDefaults();	
	}

	private void setDefaults() {
		
		controls.put(Control.moveUp, KeyEvent.VK_UP);
		controls.put(Control.moveDown, KeyEvent.VK_DOWN);
		controls.put(Control.moveLeft, KeyEvent.VK_LEFT);
		controls.put(Control.moveRight, KeyEvent.VK_RIGHT);
		controls.put(Control.markField, KeyEvent.VK_COMMA);
		controls.put(Control.occupyField, KeyEvent.VK_PERIOD);
		controls.put(Control.quitGame, KeyEvent.VK_ESCAPE);
		controls.put(Control.startGame, KeyEvent.VK_F1);
		controls.put(Control.restartGame, KeyEvent.VK_F2);
		controls.put(Control.pauseGame, KeyEvent.VK_F3);
		controls.put(Control.resumeGame, KeyEvent.VK_F4);
		controls.put(Control.stopGame, KeyEvent.VK_F5);		
		controls.put(Control.showOptions, KeyEvent.VK_F6);
		controls.put(Control.showAbout, KeyEvent.VK_F7);
		controls.put(Control.quitProgramm, KeyEvent.VK_F8);
		controls.put(Control.hint, KeyEvent.VK_H);
		
	}
	
	public void setControl(Control control, Integer keyCode) {
		
		if (controls.get(control) != keyCode) {
			
			logger.debug("Setting new key code for control " + control);
			
			controls.put(control, keyCode);

			if (eventHelper != null) {
				eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
						this, ProgramControlType.OPTIONS_CHANGED));
			}
		}
	}
	
	public Integer getControl(Control control) {
		
		return controls.get(control);
	}
	
	public  Map<Control, Integer> getControls() {
		
		return controls;	
	}
	
	
	public void setEventHelper(GameEventHelper eventHelper) {
		
		this.eventHelper = eventHelper;
	}

}
