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
package org.freenono.controller;

import java.awt.Color;

/**
 * Provides an interface for a color model. Every color model has to define six
 * colors by whatever method seems ok based on an base color. The six colors are
 * grouped as follows: Top-Bottom, Up-Down, Strange-Charm.
 * 
 * @author Christian Wichmann
 */
public abstract class ColorModel {

    protected Color baseColor;

    public Color getBaseColor() {

        return baseColor;
    }

    public void setBaseColor(Color baseColor) {

        this.baseColor = baseColor;
    }

    public abstract Color getTopColor();

    public abstract Color getBottomColor();

    public abstract Color getCharmColor();

    public abstract Color getStrangeColor();

    public abstract Color getUpColor();

    public abstract Color getDownColor();
}
