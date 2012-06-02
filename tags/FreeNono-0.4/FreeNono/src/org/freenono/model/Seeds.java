/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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

import java.util.ArrayList;
import java.util.List;

public class Seeds {

	private List<Seed> seedList = null;
	
	public Seeds() {
	
		seedList = new ArrayList<Seed>();
	}
	
	public void addSeed(Seed seed) {
		
		seedList.add(seed);
	}
	
	public void removeSeed(Seed seed) {
		
		seedList.remove(seed);
	}
	
	public Seed get(int index) {
		
		return seedList.get(index);
	}
	
	public int getNumberOfSeeds() {
		
		return seedList.size();
	}
}
