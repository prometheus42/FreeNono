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
package org.freenono.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of seeds entered by the user.
 * 
 * @author Christian Wichmann
 */
public class Seeds {

    private List<Seed> seedList = null;

    /**
     * Initializes list of seeds.
     */
    public Seeds() {

        seedList = new ArrayList<Seed>();
    }

    /**
     * Adds a new seed to the list.
     * 
     * @param seed
     *            seed to be added
     */
    public final void addSeed(final Seed seed) {

        seedList.add(seed);
    }

    /**
     * Removes a seed from this list.
     * 
     * @param seed
     *            seed to be removed
     */
    public final void removeSeed(final Seed seed) {

        seedList.remove(seed);
    }

    /**
     * Gets a seed by its index.
     * 
     * @param index
     *            index of seed to be returned
     * @return seed for given index
     */
    public final Seed get(final int index) {

        return seedList.get(index);
    }

    /**
     * Returns number of seeds in this list.
     * 
     * @return number of seeds
     */
    public final int getNumberOfSeeds() {

        return seedList.size();
    }
}
