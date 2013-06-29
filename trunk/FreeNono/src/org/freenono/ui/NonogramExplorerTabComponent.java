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
package org.freenono.ui;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * 
 * @author Christian Wichmann
 */
public class NonogramExplorerTabComponent extends JComponent {

    private static final long serialVersionUID = 6035509805491207825L;

    /**
     * Initializes a new tab component for NonogramExplorer.
     * 
     * @param text text for element
     * @param icon icon for element
     */
    public NonogramExplorerTabComponent(final String text, final ImageIcon icon) {

        add(new JLabel(text));
        add(Box.createHorizontalGlue());
        add(new JLabel(icon));
    }

}
