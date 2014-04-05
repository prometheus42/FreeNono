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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.Seed;
import org.freenono.model.Seeds;
import org.freenono.model.data.Course;
import org.freenono.model.data.Nonogram;
import org.freenono.serializer.XMLSeedsSerializer;
import org.freenono.ui.Messages;

/**
 * Default course for nonogram by seed. This Course provides only one random
 * nonogram generated by the given seed.
 * 
 * @author Christian Wichmann
 */
public class CourseFromSeed implements CourseProvider {

    private static Logger logger = Logger.getLogger(CourseFromSeed.class);

    private List<NonogramProvider> nonogramProviderList = null;
    private Course course = null;
    private String seedFile = null;
    private Seeds seedList = null;

    /**
     * Initializes a course provider for random nonograms.
     * 
     * @param seedFile
     *            File which contains previous entered seeds.
     */
    public CourseFromSeed(final String seedFile) {

        this.seedFile = seedFile;

        loadSeeds();
    }

    /**
     * Loads seeds form file.
     */
    private void loadSeeds() {

        try {

            File tmp = new File(seedFile);

            if (tmp.exists() && tmp.isFile()) {
                seedList = XMLSeedsSerializer.load(tmp);
            }

        } catch (NullPointerException e) {

            logger.warn("NullPointerException when loading seeds file.");
        }

        if (seedList == null) {

            logger.warn("Seeds could not be loaded from file!");
        }

        // create nonogramProvider for all loaded seeds in seedList
        nonogramProviderList = new ArrayList<NonogramProvider>();

        if (seedList != null) {

            for (int i = 0; i < seedList.getNumberOfSeeds(); i++) {

                nonogramProviderList.add(new NonogramFromSeed(seedList.get(i)
                        .getSeedString(), this));
            }

        } else {

            seedList = new Seeds();
        }
    }

    /**
     * Saves previously used seeds to file.
     */
    private void saveSeeds() {

        try {

            XMLSeedsSerializer.save(seedList, new File(seedFile));

        } catch (NullPointerException e) {

            logger.error("Could not save seeds in XML file.");

        }
    }

    /**
     * Deletes seeds file and all stored seeds.
     */
    public final void clearSeeds() {

        seedList = new Seeds();

        nonogramProviderList = new ArrayList<NonogramProvider>();

        new File(seedFile).delete();
    }

    @Override
    public final List<String> getNonogramList() {

        List<String> nonogramList = new ArrayList<String>();

        for (int i = 0; i < seedList.getNumberOfSeeds(); i++) {

            nonogramList.add(seedList.get(i).getSeedString());
        }

        return nonogramList;
    }

    @Override
    public final List<NonogramProvider> getNonogramProvider() {

        return Collections.unmodifiableList(nonogramProviderList);
    }

    @Override
    public final Course fetchCourse() {

        if (course == null) {
            List<Nonogram> nonogramList = new ArrayList<Nonogram>();
            for (NonogramProvider np : nonogramProviderList) {
                nonogramList.add(np.fetchNonogram());
            }
            course = new Course(getCourseName(), nonogramList);
        }

        assert course != null;
        return course;
    }

    @Override
    public final String getCourseName() {

        return Messages.getString("NonogramChooserUI.NonogramBySeedText");
    }

    /**
     * Generates a new random nonogram from a given seed and adds seed to list.
     * 
     * @param seed
     *            Seed to generate a new nonogram from.
     * @return NonogramProvider for randomly generated nonogram.
     */
    public final NonogramFromSeed generateSeededNonogram(final String seed) {

        // add new seed to seed list and save list in xml file
        seedList.addSeed(new Seed(seed, Calendar.getInstance()));
        saveSeeds();

        // instantiate new nonogramProvider for new seed and add it to list
        NonogramFromSeed tmp = new NonogramFromSeed(seed, this);
        nonogramProviderList.add(tmp);

        return tmp;
    }

    @Override
    public final String toString() {

        return getCourseName();
    }

    @Override
    public final int getNumberOfNonograms() {

        return nonogramProviderList.size();
    }

    /**
     * Gets the next nonogram for a given NonogramProvider.
     * 
     * @param np
     *            NonogramProvider for which next nonogram should be found.
     * @return NonogramProvider for next nonogram.
     */
    protected final NonogramProvider getNextNonogram(final NonogramProvider np) {

        NonogramProvider next = null;

        try {

            final int index = nonogramProviderList.indexOf(np) + 1;
            next = nonogramProviderList.get(index);

        } catch (IndexOutOfBoundsException e) {

            logger.debug("No next nonogram available.");
        }

        return next;
    }

    /**
     * Gets the previous nonogram for a given NonogramProvider.
     * 
     * @param np
     *            NonogramProvider for which previous nonogram should be found.
     * @return NonogramProvider for previous nonogram.
     */
    protected final NonogramProvider getPreviousNonogram(
            final NonogramProvider np) {

        NonogramProvider previous = null;

        try {

            final int index = nonogramProviderList.indexOf(np) - 1;
            previous = nonogramProviderList.get(index);

        } catch (IndexOutOfBoundsException e) {

            logger.debug("No previous nonogram available.");
        }

        return previous;
    }
}