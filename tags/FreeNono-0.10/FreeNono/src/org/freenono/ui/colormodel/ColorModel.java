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
 * Provides an interface for a color model. Every color model has to define six colors by whatever
 * method seems ok based on an base color. The six colors are grouped as follows: Top-Bottom,
 * Up-Down, Strange-Charm.
 *
 * @author Christian Wichmann
 */
public abstract class ColorModel {

    private Color baseColor;

    private Color topColor;
    private Color bottomColor;
    private Color upColor;
    private Color downColor;
    private Color charmColor;
    private Color strangeColor;

    /**
     * Constructor that sets the base color and calls calculateColors().
     * @param c
     *            Basecolor
     */
    public ColorModel(final Color c) {
        this.setBaseColor(c);
        this.calculateColors();
    }

    /**
     * This method needs to set all available colors according to the used algorithm. To create a
     * new algorithm, implement this method.
     */
    protected abstract void calculateColors();

    /**
     * @return the baseColor
     */
    public final Color getBaseColor() {
        return baseColor;
    }

    /**
     * @param baseColor
     *            the baseColor to set
     */
    public final void setBaseColor(final Color baseColor) {
        this.baseColor = baseColor;
    }

    /**
     * @return the topColor
     */
    public final Color getTopColor() {
        return topColor;
    }

    /**
     * @param topColor
     *            the topColor to set
     */
    protected final void setTopColor(final Color topColor) {
        this.topColor = topColor;
    }

    /**
     * @return the bottomColor
     */
    public final Color getBottomColor() {
        return bottomColor;
    }

    /**
     * @param bottomColor
     *            the bottomColor to set
     */
    protected final void setBottomColor(final Color bottomColor) {
        this.bottomColor = bottomColor;
    }

    /**
     * @return the upColor
     */
    public final Color getUpColor() {
        return upColor;
    }

    /**
     * @param upColor
     *            the upColor to set
     */
    protected final void setUpColor(final Color upColor) {
        this.upColor = upColor;
    }

    /**
     * @return the downColor
     */
    public final Color getDownColor() {
        return downColor;
    }

    /**
     * @param downColor
     *            the downColor to set
     */
    protected final void setDownColor(final Color downColor) {
        this.downColor = downColor;
    }

    /**
     * @return the charmColor
     */
    public final Color getCharmColor() {
        return charmColor;
    }

    /**
     * @param charmColor
     *            the charmColor to set
     */
    protected final void setCharmColor(final Color charmColor) {
        this.charmColor = charmColor;
    }

    /**
     * @return the strangeColor
     */
    public final Color getStrangeColor() {
        return strangeColor;
    }

    /**
     * @param strangeColor
     *            the strangeColor to set
     */
    protected final void setStrangeColor(final Color strangeColor) {
        this.strangeColor = strangeColor;
    }

}
