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
package org.freenono;

import org.apache.log4j.Logger;
import org.freenono.controller.Manager;

/**
 * Main runner class for starting FreeNono with its default swing GUI.
 * @author Markus Wichmann, Christian Wichmann
 */
public final class RunUI {

    private static Logger logger = Logger.getLogger(RunUI.class);

    private static Manager manager;

    /**
     * Hide constructor of utility class.
     */
    private RunUI() {
    }

    /**
     * Main runnable of FreeNono. Starts the default swing UI.
     * 
     * @param args
     *            command line arguments, not used currently
     */
    public static void main(final String[] args) {

        try {
            /* instantiate game manager */
            manager = new Manager();
            manager.startSwingUI();

        } catch (NullPointerException e) {

            logger.error("Null pointer occurred somewhere in FreeNono :-(");
            manager = null;
            System.exit(1);
        }
    }
}
