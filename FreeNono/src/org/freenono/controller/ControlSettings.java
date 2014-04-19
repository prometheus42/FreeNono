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
 * Saves key codes for controls defined in the enumeration <code>Control</code>.
 * Not all control settings are necessarily used in FreeNono.
 * 
 * @author Martin Wichmann, Christian Wichmann
 */
public class ControlSettings {

    private static Logger logger = Logger.getLogger(ControlSettings.class);

    /**
     * Defines all possible controls that key codes can be assigned to.
     */
    public enum Control {
        MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, MARK_FIELD, OCCUPY_FIELD, QUIT_GAME, STOP_GAME, PAUSE_GAME, START_GAME, RESUME_GAME, RESTART_GAME, QUIT_PROGRAM, SHOW_OPTIONS, SHOW_ABOUT, HINT
    };

    private Map<Control, Integer> controls = new HashMap<Control, Integer>();

    private GameEventHelper eventHelper = null;

    /**
     * Default constructor. Sets controls to default values.
     */
    public ControlSettings() {

        resetSettings();
    }

    /**
     * Copy constructor to build copy of <code>ControlSettings</code> object
     * with all its data.
     * 
     * @param oldControls
     *            old control settings that should be copied
     */
    public ControlSettings(final ControlSettings oldControls) {

        for (Control c : Control.values()) {
            if (oldControls.getControl(c) != null) {
                controls.put(c, oldControls.getControl(c));
            }
        }
    }

    /**
     * Set controls to default values.
     */
    protected void resetSettings() {

        controls.put(Control.MOVE_UP, KeyEvent.VK_UP);
        controls.put(Control.MOVE_DOWN, KeyEvent.VK_DOWN);
        controls.put(Control.MOVE_LEFT, KeyEvent.VK_LEFT);
        controls.put(Control.MOVE_RIGHT, KeyEvent.VK_RIGHT);
        controls.put(Control.MARK_FIELD, KeyEvent.VK_COMMA);
        controls.put(Control.OCCUPY_FIELD, KeyEvent.VK_PERIOD);
        controls.put(Control.QUIT_GAME, KeyEvent.VK_ESCAPE);
        controls.put(Control.START_GAME, KeyEvent.VK_F1);
        controls.put(Control.RESTART_GAME, KeyEvent.VK_F2);
        controls.put(Control.PAUSE_GAME, KeyEvent.VK_F3);
        controls.put(Control.RESUME_GAME, KeyEvent.VK_F4);
        controls.put(Control.STOP_GAME, KeyEvent.VK_F5);
        controls.put(Control.SHOW_OPTIONS, KeyEvent.VK_F6);
        controls.put(Control.SHOW_ABOUT, KeyEvent.VK_F7);
        controls.put(Control.QUIT_PROGRAM, KeyEvent.VK_F8);
        controls.put(Control.HINT, KeyEvent.VK_H);
    }

    /**
     * Set Control 'control' to key code 'keyCode'.
     * 
     * @param control
     *            control to be set
     * @param keyCode
     *            key code to be set
     */
    public final void setControl(final Control control, final Integer keyCode) {

        if (controls.get(control) != keyCode) {
            logger.debug("Setting new key code for control " + control);

            controls.put(control, keyCode);

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Returns key code for control.
     * 
     * @param control
     *            Control
     * @return key code for control
     */
    public final Integer getControl(final Control control) {

        return controls.get(control);
    }

    /**
     * Set event helper.
     * 
     * @param eventHelper
     *            game event helper
     */
    public final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
    }
}
