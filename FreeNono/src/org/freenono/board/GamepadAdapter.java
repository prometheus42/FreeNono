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
package org.freenono.board;

import java.awt.event.ActionEvent;
import java.net.IDN;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller.Type;

/**
 * Polls game pad regularly and calls methods of BoardSetPlayfield accordingly.
 * 
 * @author Christian Wichmann
 */
public final class GamepadAdapter {

    private Controller currentController = null;
    private final float axisLimit = 0.75f;

    private Timer timer = new Timer();
    private Task pollTask;

    private BoardTileSetPlayfield field;

    /**
     * Enum defining possible conditions under which actions are executed by
     * GamepadActionHandler.
     * 
     * @author Christian Wichmann
     */
    private enum Condition {

        GREATER_ZERO,

        LESSER_ZERO,

        ZERO,

        GREATER_AXIS_LIMIT,

        LESSER_AXIS_LIMIT
    }

    /**
     * Class holding an action that should be executed when some condition of
     * the polled data is met.
     * 
     * @author Christian Wichmann
     */
    private class GamepadActionHandler {

        private final Identifier id;
        private final int pressCounter = 0;
        private final Condition condition;
        private AbstractAction action;

        /**
         * Initializes a new gamepad action handler.
         * 
         * @param identifier
         *            identifier for which condition must be checked
         * @param condition
         *            condition that should be checked
         * @param action
         *            action to be executed when condition is met
         */
        public GamepadActionHandler(final Identifier identifier,
                final Condition condition, final AbstractAction action) {

            this.id = identifier;
            this.condition = condition;
            this.action = action;
        }

        /**
         * Evaluates if identifier means this component and executes action of
         * this handler when condition of it is fulfilled.
         * 
         * @param polledIdentifier
         *            identifier that was
         * @param polledData
         */
        public boolean evaluate(final Identifier polledIdentifier,
                final float polledData) {

            if (polledIdentifier.equals(id)) {

                switch (condition) {
                case GREATER_AXIS_LIMIT:
                    if (polledData > axisLimit) {
                        action.actionPerformed(new ActionEvent(this, 0, ""));
                        return true;
                    }
                    break;
                case GREATER_ZERO:
                    if (polledData > 0) {
                        action.actionPerformed(new ActionEvent(this, 0, ""));
                        return true;
                    }
                    break;
                case LESSER_AXIS_LIMIT:
                    if (polledData < -axisLimit) {
                        action.actionPerformed(new ActionEvent(this, 0, ""));
                        return true;
                    }
                    break;
                case LESSER_ZERO:
                    if (polledData < 0) {
                        action.actionPerformed(new ActionEvent(this, 0, ""));
                        return true;
                    }
                    break;
                case ZERO:
                    if (polledData == 0) {
                        action.actionPerformed(new ActionEvent(this, 0, ""));
                        return true;
                    }
                    break;
                default:
                    break;
                }
            }
            return false;
        }
    }

    private List<GamepadActionHandler> handlers = new ArrayList<GamepadActionHandler>();

    /**
     * Timer that is called every second to fire an timer event.
     */
    class Task extends TimerTask {
        @Override
        public void run() {
            pollGamepad();
        }
    }

    /**
     * Initializes a new game pad adapter.
     * 
     * @param field
     *            play field which will be affected by gamepad actions
     */
    public GamepadAdapter(final BoardTileSetPlayfield field) {

        // TODO check if JInput classes are there?!

        this.field = field;

        initGamepad();

        pollGamepad();

        // start timer
        pollTask = new Task();
        timer.schedule(pollTask, 0, 75);
    }

    /**
     * Initialize game pad and set current controller.
     */
    private void initGamepad() {

        ControllerEnvironment ce = ControllerEnvironment
                .getDefaultEnvironment();
        Controller[] cs = ce.getControllers();

        // print the name and type of each controller
        // for (int i = 0; i < cs.length; i++)
        // System.out.println(i + ". " + cs[i].getName() + ", "
        // + cs[i].getType());

        for (int i = 0; i < cs.length; i++) {
            if (cs[i].getType() == Type.STICK) {
                currentController = cs[i];
                break;
            }
        }
    }

    /**
     * Poll game pad for its components and their state.
     */
    private void pollGamepad() {

        if (currentController.poll()) {

            Component[] x = currentController.getComponents();

            for (int i = 0; i < x.length; i++) {

                Component component = x[i];

                if (component.getIdentifier().equals(Identifier.Button.THUMB)) {
                    if (component.getPollData() > 0) {
                        field.occupyActiveField();
                    }
                } else if (component.getIdentifier().equals(
                        Identifier.Button.THUMB2)) {
                    if (component.getPollData() > 0) {
                        field.markActiveField();
                    }
                } else if (component.getIdentifier().equals(Identifier.Axis.X)) {

                    if (component.getPollData() > axisLimit) {
                        field.moveActiveRight();
                    } else if (component.getPollData() < -axisLimit) {
                        field.moveActiveLeft();
                    }

                } else if (component.getIdentifier().equals(Identifier.Axis.Y)) {
                    if (component.getPollData() > axisLimit) {
                        field.moveActiveDown();
                    } else if (component.getPollData() < -axisLimit) {
                        field.moveActiveUp();
                    }
                }
            }
        }
    }
}
