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
 * Provides a color model based on monochromatic colors.
 * 
 * Algorithm "borrowed" from Agave by Jonathon Jongsma.
 * 
 * @author Martin Wichmann
 */
public class ColorModelMonochromatic extends ColorModel {

    /**
     * Constructor for ColorModelMonochromatic.
     * @param c
     *            Basecolor
     */
    public ColorModelMonochromatic(final Color c) {
        super(c);
    }

    /**
     * Calculate and store the colors.
     */
    protected final void calculateColors() {
        // MONOCHROMATIC (Einfarbig, 3 colors)

        // calc monochromatic for basecolor
        final float[] baseColorHSB = Color.RGBtoHSB(this.getBaseColor().getRed(),
                this.getBaseColor().getGreen(), this.getBaseColor().getBlue(),
                null);

        final float saturationThreshold = (float) 10.0;

        float newS1 = baseColorHSB[1];
        float newS2 = baseColorHSB[1];
        float newS3 = baseColorHSB[1];
        float newS4 = baseColorHSB[1];
        float newS5 = baseColorHSB[1];
        float newS6 = baseColorHSB[1];
        float newV1 = baseColorHSB[2];
        float newV2 = baseColorHSB[2];
        float newV3 = baseColorHSB[2];
        float newV4 = baseColorHSB[2];
        float newV5 = baseColorHSB[2];
        float newV6 = baseColorHSB[2];

        if (baseColorHSB[1] < (1.0 / saturationThreshold)) {
            newS1 = (float) ((baseColorHSB[1] + (1.0 * 1.0 / 7.0)) % 1.0);
            newS2 = (float) ((baseColorHSB[1] + (2.0 * 1.0 / 7.0)) % 1.0);
            newS3 = (float) ((baseColorHSB[1] + (3.0 * 1.0 / 7.0)) % 1.0);
            newS4 = (float) ((baseColorHSB[1] + (4.0 * 1.0 / 7.0)) % 1.0);
            newS5 = (float) ((baseColorHSB[1] + (5.0 * 1.0 / 7.0)) % 1.0);
            newS6 = (float) ((baseColorHSB[1] + (6.0 * 1.0 / 7.0)) % 1.0);
        } else {
            newV1 = (float) ((baseColorHSB[2] + (1.0 * 1.0 / 7.0)) % 1.0);
            newV2 = (float) ((baseColorHSB[2] + (2.0 * 1.0 / 7.0)) % 1.0);
            newV3 = (float) ((baseColorHSB[2] + (3.0 * 1.0 / 7.0)) % 1.0);
            newV4 = (float) ((baseColorHSB[2] + (4.0 * 1.0 / 7.0)) % 1.0);
            newV5 = (float) ((baseColorHSB[2] + (5.0 * 1.0 / 7.0)) % 1.0);
            newV6 = (float) ((baseColorHSB[2] + (6.0 * 1.0 / 7.0)) % 1.0);
        }

        final Color c1 = Color.getHSBColor(baseColorHSB[0], newS1, newV1);
        final Color c2 = Color.getHSBColor(baseColorHSB[0], newS2, newV2);
        final Color c3 = Color.getHSBColor(baseColorHSB[0], newS3, newV3);
        final Color c4 = Color.getHSBColor(baseColorHSB[0], newS4, newV4);
        final Color c5 = Color.getHSBColor(baseColorHSB[0], newS5, newV5);
        final Color c6 = Color.getHSBColor(baseColorHSB[0], newS6, newV6);

        // assign colors
        this.setTopColor(c1);
        this.setBottomColor(c2);
        this.setUpColor(c3);
        this.setDownColor(c4);
        this.setCharmColor(c5);
        this.setStrangeColor(c6);
    }

}
