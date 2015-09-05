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
package org.freenono.provider;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;
import org.restlet.resource.ResourceException;

/**
 * Provides a nonogram from a Nonoserver.
 * 
 * @author Christian Wichmann
 */
public class NonogramFromServer implements NonogramProvider {

    private static Logger logger = Logger.getLogger(NonogramFromServer.class);

    private Nonogram nonogram = null;
    private ServerProviderHelper serverProviderHelper = null;
    private String nonogramName = null;
    private String courseName = null;

    /**
     * Initializes a nonogram provider for a single nonogram available on a
     * NonoServer.
     * 
     * @param nonogramName
     *            name of nonogram in given course
     * @param courseName
     *            name of course
     * @param serverProviderHelper
     *            helper instance to read data from server
     */
    public NonogramFromServer(final String nonogramName,
            final String courseName,
            final ServerProviderHelper serverProviderHelper) {

        this.nonogramName = nonogramName;
        this.courseName = courseName;
        this.serverProviderHelper = serverProviderHelper;
    }

    @Override
    public final Nonogram fetchNonogram() {

        if (nonogram != null) {
            return nonogram;
        } else {
            try {
                nonogram = serverProviderHelper.getNonogram(courseName,
                        nonogramName);
            } catch (ResourceException e) {
                logger.error("Server under given URL not responding.");
            }
            return nonogram;
        }
    }

    @Override
    public final String getName() {

        return nonogramName;
    }

    @Override
    public final String getDescription() {

        return fetchNonogram().getDescription();
    }

    @Override
    public final DifficultyLevel getDifficulty() {

        return fetchNonogram().getDifficulty();
    }

    @Override
    public final String getAuthor() {

        return fetchNonogram().getAuthor();
    }

    @Override
    public final long getDuration() {

        return fetchNonogram().getDuration();
    }

    @Override
    public final String toString() {

        return nonogramName;

    }

    @Override
    public final int width() {

        return fetchNonogram().width();
    }

    @Override
    public final int height() {

        return fetchNonogram().height();
    }

    @Override
    public final NonogramProvider getNextNonogram() {

        // TODO implement this method
        return null;
    }

    @Override
    public final NonogramProvider getPreviousNonogram() {

        // TODO implement this method
        return null;
    }

}
