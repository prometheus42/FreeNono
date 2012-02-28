/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2011 Markus Wichmann
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
package org.freenono.model;

/**
 * @author Markus Wichmann
 *
 */
public class Course {

	private String name = "";
	private Nonogram[] nonograms = new Nonogram[0];
	
	public Course(String name, Nonogram ... nonograms) {
		setName(name);
		setNonograms(nonograms);
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	public Nonogram[] getNonograms() {
		return nonograms;
	}
	
	public int getNonogramCount() {
		return nonograms != null ? nonograms.length : 0;
	}
	
	public Nonogram getNonogram(int index) {
		return nonograms[index];
	}
	
	void setNonograms(Nonogram[] n){
		nonograms = n;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
