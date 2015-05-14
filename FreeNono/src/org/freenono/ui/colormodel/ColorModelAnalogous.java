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
 * Provides a color model based on analogous colors.
 * 
 * Algorithm "borrowed" from Agave by Jonathon Jongsma.
 * 
 * @author Martin Wichmann
 */
public class ColorModelAnalogous extends ColorModel {

    /**
     * Constructor for ColorModelAnalogous.
     * @param c
     *            Basecolor
     */
    public ColorModelAnalogous(final Color c) {
        super(c);
    }

    /**
     * Calculate and store the colors.
     */
    protected final void calculateColors() {
        // ANALOGOUS (sinngemäße Farben, 3 colors)
        final float[] baseColorHSB =
                Color.RGBtoHSB(this.getBaseColor().getRed(), this.getBaseColor().getGreen(), this.getBaseColor().getBlue(), null);

        final float divider = (float) 12.0;

        final float offset = (float) (1.0 / divider);
        final float newHue1 = (float) (baseColorHSB[0] - 2.0 * offset);
        final Color c1 = Color.getHSBColor(newHue1, baseColorHSB[1], baseColorHSB[2]);
        final float newHue2 = (float) (baseColorHSB[0] - 1.0 * offset);
        final Color c2 = Color.getHSBColor(newHue2, baseColorHSB[1], baseColorHSB[2]);
        final float newHue3 = (float) (baseColorHSB[0] + 1.0 * offset);
        final Color c3 = Color.getHSBColor(newHue3, baseColorHSB[1], baseColorHSB[2]);
        final float newHue4 = (float) (baseColorHSB[0] + 2.0 * offset);
        final Color c4 = Color.getHSBColor(newHue4, baseColorHSB[1], baseColorHSB[2]);

        this.setTopColor(this.getBaseColor());
        this.setBottomColor(c3);
        this.setUpColor(c1);
        this.setDownColor(c2);
        this.setCharmColor(c3);
        this.setStrangeColor(c4);
    }

}
