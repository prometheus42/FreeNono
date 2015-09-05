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
package org.freenono.ui.colormodel;

import java.awt.Color;

/**
 * Implements a very simple color model by switching the RGB parts of the base color and delivering
 * them.
 * 
 * @author Christian Wichmann
 */
public class ColorModelSimple extends ColorModel {

    /**
     * Constructor for ColorModelSimple.
     * @param c
     *            Basecolor
     */
    public ColorModelSimple(final Color c) {
        super(c);
    }

    /**
     * Calculate and store the colors.
     */
    protected final void calculateColors() {
        final Color c0 = this.getBaseColor();

        final Color c1 = new Color(c0.getGreen(), c0.getRed(), c0.getBlue());
        final Color c2 = new Color(c0.getRed(), c0.getBlue(), c0.getGreen());
        final Color c3 = new Color(c0.getBlue(), c0.getGreen(), c0.getRed());
        final Color c4 = new Color(c0.getGreen(), c0.getBlue(), c0.getRed());
        final Color c5 = new Color(c0.getBlue(), c0.getRed(), c0.getGreen());

        this.setTopColor(c0);
        this.setBottomColor(c1);
        this.setUpColor(c2);
        this.setDownColor(c3);
        this.setCharmColor(c4);
        this.setStrangeColor(c5);
    }

}
