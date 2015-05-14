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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Controller.PortType;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;

import org.apache.log4j.Logger;

/**
 * Polls game pad regularly and calls methods of BoardSetPlayfield accordingly.
 *
 * @author Christian Wichmann
 */
public final class GamepadAdapter {

    private static Logger logger = Logger.getLogger(GamepadAdapter.class);

    private List<Controller> listOfCurrentControllers = new ArrayList<Controller>();
    private final float axisLimit = 0.75f;

    private Timer timer = new Timer();
    private PollTask pollTask;

    /**
     * Number of times after which a controller signal is used.
     */
    private final int debounceLimit = 8;
    /**
     * Interval at which all controllers are polled and checked.
     */
    private final int pollInterval = 150;

    private final BoardTileSetPlayfield field;

    /**
     * Enum defining possible conditions under which actions are executed by GamepadActionHandler.
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
     * Class holding an action that should be executed when some condition of the polled data is
     * met.
     *
     * @author Christian Wichmann
     */
    private class GamepadActionHandler {

        private final Identifier id;
        private final Condition condition;
        private final AbstractAction action;

        private int pressCounter = 0;

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
        public GamepadActionHandler(final Identifier identifier, final Condition condition, final AbstractAction action) {

            this.id = identifier;
            this.condition = condition;
            this.action = action;
        }

        /**
         * Evaluates if identifier means this component and executes action of this handler when
         * condition of it is fulfilled.
         *
         * @param polledIdentifier
         *            identifier that was
         * @param polledData
         *            data that was polled for component with given identifier
         * @return true, if action was executed
         */
        public boolean evaluate(final Identifier polledIdentifier, final float polledData) {

            if (polledIdentifier.equals(id)) {
                switch (condition) {
                case GREATER_AXIS_LIMIT:
                    if (polledData > axisLimit) {
                        performAction();
                        return true;
                    } else {
                        pressCounter = 0;
                    }
                    break;
                case GREATER_ZERO:
                    if (polledData > 0) {
                        performAction();
                        return true;
                    } else {
                        pressCounter = 0;
                    }
                    break;
                case LESSER_AXIS_LIMIT:
                    if (polledData < -axisLimit) {
                        performAction();
                        return true;
                    } else {
                        pressCounter = 0;
                    }
                    break;
                case LESSER_ZERO:
                    if (polledData < 0) {
                        performAction();
                        return true;
                    } else {
                        pressCounter = 0;
                    }
                    break;
                case ZERO:
                    if (polledData == 0) {
                        performAction();
                        return true;
                    } else {
                        pressCounter = 0;
                    }
                    break;
                default:
                    break;
                }
            }
            return false;
        }

        /**
         * Perform given action of this handler.
         */
        private void performAction() {

            if (pressCounter % debounceLimit == 0) {
                action.actionPerformed(new ActionEvent(this, 0, ""));
            }
            pressCounter++;
        }
    }

    private final List<GamepadActionHandler> handlers = new ArrayList<GamepadActionHandler>();

    /**
     * Timer that is called every second to fire an timer event.
     */
    class PollTask extends TimerTask {
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

        this.field = field;

        initGamepad();

        addGamepadActionHandlers();

        // start timer
        if (listOfCurrentControllers != null) {
            pollTask = new PollTask();
            timer.schedule(pollTask, 0, pollInterval);
        }
    }

    /**
     * Initialize game pad and set current controllers.
     */
    private void initGamepad() {

        final ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        final Controller[] cs = ce.getControllers();

        for (final Controller controller : cs) {
            logger.debug("Found controller: " + controller.getName() + ", " + controller.getType() + controller.getPortNumber()
                    + controller.getPortType());

            if (controller.getType() == Type.STICK || controller.getType() == Type.GAMEPAD) {

                /*
                 * Fix for the XBox controller. Skips all controller with Unknown port type because
                 * every controller shows up twice in the list. Then all event would be polled
                 * twice!
                 */
                if (controller.getPortType() == PortType.UNKNOWN) {
                    continue;
                }
                listOfCurrentControllers.add(controller);
            }
        }
    }

    /**
     * Adds all handlers for every action.
     */
    private void addGamepadActionHandlers() {

        handlers.add(new GamepadActionHandler(Identifier.Button.THUMB, Condition.GREATER_ZERO, new AbstractAction() {
            private static final long serialVersionUID = -6179407258987388782L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.occupyActiveField();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Button.THUMB2, Condition.GREATER_ZERO, new AbstractAction() {
            private static final long serialVersionUID = -6160111413515531130L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.markActiveField();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Button.B, Condition.GREATER_ZERO, new AbstractAction() {
            private static final long serialVersionUID = -6179407258987388782L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.occupyActiveField();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Button.A, Condition.GREATER_ZERO, new AbstractAction() {
            private static final long serialVersionUID = -6160111413515531130L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.markActiveField();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.X, Condition.GREATER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = 7743570200954070252L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveRight();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.X, Condition.LESSER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = -4680845396931897544L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveLeft();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.RX, Condition.GREATER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = 7743570200954070252L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveRight();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.RX, Condition.LESSER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = -4680845396931897544L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveLeft();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.Y, Condition.GREATER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = 7949183970480623622L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveDown();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.Y, Condition.LESSER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = 9121117401739452551L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveUp();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.RY, Condition.GREATER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = 7949183970480623622L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveDown();
                }
            }
        }));
        handlers.add(new GamepadActionHandler(Identifier.Axis.RY, Condition.LESSER_AXIS_LIMIT, new AbstractAction() {
            private static final long serialVersionUID = 9121117401739452551L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (field != null) {
                    field.moveActiveUp();
                }
            }
        }));
    }

    /**
     * Poll game pad for its components and their state of all current controllers.
     */
    private void pollGamepad() {

        for (final Controller c : listOfCurrentControllers) {
            // c.poll();
            // EventQueue queue = c.getEventQueue();
            // Event event = new Event();
            // while (queue.getNextEvent(event)) {
            // Component comp = event.getComponent();
            // // logger.debug("Controller event: " + comp + " - "
            // // + event.getValue());
            // for (GamepadActionHandler h : handlers) {
            // h.evaluate(comp.getIdentifier(), event.getValue());
            // }
            // }

            if (c.poll()) {
                final Component[] controls = c.getComponents();
                for (final Component component : controls) {
                    for (final GamepadActionHandler h : handlers) {
                        h.evaluate(component.getIdentifier(), component.getPollData());
                    }
                }
            }
        }
    }

    /**
     * Stops timer task that does the polling of the game pad.
     */
    public void stopPolling() {

        timer.cancel();
        timer = null;
        listOfCurrentControllers = null;
    }
}
