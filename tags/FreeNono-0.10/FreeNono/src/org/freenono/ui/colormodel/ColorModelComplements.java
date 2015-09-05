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
 * Provides a color model based on complement colors.
 * 
 * Algorithm "borrowed" from Agave by Jonathon Jongsma.
 * 
 * @author Martin Wichmann
 */
public class ColorModelComplements extends ColorModel {

    /**
     * Constructor for ColorModelComplements.
     * @param c
     *            Basecolor
     */
    public ColorModelComplements(final Color c) {
        super(c);
    }

    /**
     * Calculate and store the colors.
     */
    protected final void calculateColors() {
        // COMPLEMENTS (Komplementäre Farben, 2 colors)
        final float[] baseColorHSB =
                Color.RGBtoHSB(this.getBaseColor().getRed(), this.getBaseColor().getGreen(), this.getBaseColor().getBlue(), null);

        final float newHue = (float) (baseColorHSB[0] + (1.0 / 2.0));
        final Color c1 = Color.getHSBColor(newHue, baseColorHSB[1], baseColorHSB[2]);
        final Color c2 = c1.brighter();
        final Color c3 = c1.darker();
        final Color c5 = this.getBaseColor().brighter();
        final Color c6 = this.getBaseColor().darker();

        this.setTopColor(c1);
        this.setBottomColor(c2);
        this.setUpColor(c3);
        this.setDownColor(this.getBaseColor());
        this.setCharmColor(c5);
        this.setStrangeColor(c6);
    }
}
