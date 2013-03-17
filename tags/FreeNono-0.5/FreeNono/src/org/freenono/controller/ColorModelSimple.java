/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Christian Wichmann
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
 * Implements a very simple color model by switching the RGB parts of the base
 * color and delivering them.
 * 
 * @author Christian Wichmann
 * 
 */
public class ColorModelSimple extends ColorModel {

	public ColorModelSimple(Color c) {
	
		setBaseColor(c);
	}
	
	@Override
	public Color getTopColor() {
		
		return baseColor;
	}

	@Override
	public Color getBottomColor() {
		
		return new Color(baseColor.getGreen(), baseColor.getRed(), baseColor.getBlue());
	}

	@Override
	public Color getCharmColor() {
		
		return new Color(baseColor.getGreen(), baseColor.getBlue(), baseColor.getRed());
	}

	@Override
	public Color getStrangeColor() {
		
		return new Color(baseColor.getBlue(), baseColor.getRed(), baseColor.getGreen());
	}

	@Override
	public Color getUpColor() {
		
		return new Color(baseColor.getRed(), baseColor.getBlue(), baseColor.getGreen());
	}

	@Override
	public Color getDownColor() {
		
		return new Color(baseColor.getBlue(), baseColor.getGreen(), baseColor.getRed());
	}

}
