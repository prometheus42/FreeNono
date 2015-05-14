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
package org.freenono;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.model.data.Nonogram;
import org.freenono.serializer.data.NonogramSerializer;
import org.freenono.serializer.data.SimpleNonogramSerializer;
import org.freenono.serializer.data.XMLNonogramSerializer;

/**
 * Helper tool to convert old format nonograms into the "new" xml nonogram format.
 *
 * @author Markus Wichmann
 */
public final class ConvertNonogram {

    private static Logger logger = Logger.getLogger(ConvertNonogram.class);

    /**
     * Hidden constructor for utility class.
     */
    private ConvertNonogram() {
    }

    /**
     * Main method for converting nonograms.
     * @param args
     *            Commandline arguments.
     */
    public static void main(final String[] args) {
        if (args.length <= 0) {
            logger.error("Please specify at least one nonogram file");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            final File f = new File(args[i]);
            if (f.isFile()) {
                convertNonogram(f);
            } else {
                final List<File> files = getAllNonogramFiles(f);
                for (final File file : files) {
                    convertNonogram(file);
                }
            }
        }
    }

    /**
     * Get all nonogram files recursively in the specified dir.
     * @param dir
     *            Die to search files.
     * @return List of files
     */
    private static List<File> getAllNonogramFiles(final File dir) {

        final List<File> lst = new ArrayList<File>();

        final File[] listOfNonogramFiles = dir.listFiles();
        if (listOfNonogramFiles != null) {
            for (final File file : listOfNonogramFiles) {
                if (file.isDirectory()) {
                    lst.addAll(getAllNonogramFiles(file));
                } else if (file.getName().endsWith("." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
                    lst.add(file);
                }
            }
        }
        return lst;
    }

    /**
     * Convert nonogram from deprecated simple format to new XML format.
     * @param input
     *            Nonogram File to convert.
     */
    private static void convertNonogram(final File input) {
        try {
            final NonogramSerializer xmlNS = new XMLNonogramSerializer();
            final NonogramSerializer simpleNS = new SimpleNonogramSerializer();

            final File output = new File(input.getParentFile().getAbsolutePath(), input.getName());
            final Nonogram[] n = simpleNS.load(input);
            xmlNS.save(output, n);

        } catch (final Exception e) {
            logger.error(input + ": " + e.getMessage());
        }
    }

}
