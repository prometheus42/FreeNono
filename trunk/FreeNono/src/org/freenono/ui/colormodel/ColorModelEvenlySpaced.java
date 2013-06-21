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
 * Provides an color model based on evenly spaced colors (a.k.a. triadic or
 * tetradic for three respectivley four colors).
 * 
 * Algorithm "borrowed" from Agave by Jonathon Jongsma.
 * 
 * @author Christian Wichmann
 */
public class ColorModelEvenlySpaced extends ColorModel {

    /**
     * Constructor for ColorModelAnalogous.
     * @param c
     *            Basecolor
     */
    public ColorModelEvenlySpaced(final Color c) {
        super(c);
    }

    /**
     * Calculate and store the colors.
     */
    protected final void calculateColors() {
        // TRIADS (Dreiergruppe, 3 colors)
        float[] baseColorHSB = Color.RGBtoHSB(this.getBaseColor().getRed(),
                this.getBaseColor().getGreen(), this.getBaseColor().getBlue(),
                null);

        final float divider = (float) 5.0;

        float offset = (float) (1.0 / divider);
        float newHue1 = (float) (baseColorHSB[0] + 2.0 * offset);
        Color c1 = Color.getHSBColor(newHue1, baseColorHSB[1], baseColorHSB[2]);
        float newHue2 = (float) (baseColorHSB[0] + 1.0 * offset);
        Color c2 = Color.getHSBColor(newHue2, baseColorHSB[1], baseColorHSB[2]);
        float newHue3 = (float) (baseColorHSB[0] - 1.0 * offset);
        Color c3 = Color.getHSBColor(newHue3, baseColorHSB[1], baseColorHSB[2]);
        float newHue4 = (float) (baseColorHSB[0] - 2.0 * offset);
        Color c4 = Color.getHSBColor(newHue4, baseColorHSB[1], baseColorHSB[2]);

        this.setTopColor(this.getBaseColor());
        this.setBottomColor(c3);
        this.setUpColor(c1);
        this.setDownColor(c2);
        this.setCharmColor(c3);
        this.setStrangeColor(c4);
    }

}
