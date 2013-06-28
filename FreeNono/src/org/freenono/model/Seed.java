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

import java.util.Calendar;

/**
 * Represents a seed which has been entered by a user through UI. Saved are the
 * seed string and the date when the seed has been entered.
 * 
 * @author Christian Wichmann
 */
public final class Seed {

    private String seedString = null;
    private Calendar dateTime = null;

    /**
     * Initializes a seed with its string and the date.
     * 
     * @param seedString
     *            seed string
     * @param dateTime
     *            date that seed was used
     */
    public Seed(final String seedString, final Calendar dateTime) {

        this.setSeedString(seedString);
        this.setDateTime(dateTime);
    }

    /**
     * Gets string of this seed.
     * 
     * @return string of seed
     */
    public String getSeedString() {

        if (seedString != null) {
            return seedString;
        } else {
            return "";
        }
    }

    /**
     * Sets string for this seed.
     * 
     * @param seedString
     *            seed string to be set
     */
    private void setSeedString(final String seedString) {

        this.seedString = seedString;
    }

    /**
     * Gets date from this seed.
     * 
     * @return date from seed
     */
    public Calendar getDateTime() {

        if (dateTime != null) {
            return dateTime;
        } else {
            return Calendar.getInstance();
        }
    }

    /**
     * Sets date for this seed.
     * 
     * @param dateTime
     *            date to be set
     */
    private void setDateTime(final Calendar dateTime) {

        this.dateTime = dateTime;
    }

}
