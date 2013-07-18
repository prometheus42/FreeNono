/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 FreeNono Development Team
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;
import org.freenono.serializer.data.ZipCourseSerializer;

/**
 * Converts simple png images of nonogram level from "Mario's Picross 2".
 * 
 * @author Christian Wichmann
 */
public final class Mp22fn {

    private static Logger logger = Logger.getLogger(Mp22fn.class);

    // File representing the folder that you select using a FileChooser
    static final File DIR = new File(
            "/home/christian/Desktop/marios_picross_2_nonogramme/data/");
    static final File COURSE = new File(
            "/home/christian/Desktop/marios_picross_2_nonogramme/data/");

    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[] {"png"};

    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    /**
     * Hidden constructor of utility class.
     */
    private Mp22fn() {

    }

    /**
     * Starts convertion.
     * 
     * @param args
     *            command line args
     */
    public static void main(final String[] args) {

        loadNonograms();
    }

    /**
     * Loads images and converts them to nonogram course.
     */
    private static void loadNonograms() {
        List<Nonogram> nonogramList = new ArrayList<Nonogram>();

        // make sure it's a directory
        if (DIR.isDirectory()) {
            for (final File f : DIR.listFiles(IMAGE_FILTER)) {
                BufferedImage img = null;

                try {
                    img = ImageIO.read(f);

                    // you probably want something more involved here
                    // to display in your UI
                    String name = f.getName();
                    String[] parts = name.substring(0, name.lastIndexOf('.'))
                            .split("-");
                    int world = Integer.valueOf(parts[1]);
                    int level = Integer.valueOf(parts[2]);
                    int width = img.getWidth();
                    int height = img.getHeight();

                    final int magicNumber = -10000000;
                    boolean[][] field = new boolean[width][height];
                    for (int i = 0; i < img.getWidth(); i++) {
                        for (int j = 0; j < img.getHeight(); j++) {
                            field[j][i] = img.getRGB(i, j) < magicNumber ? true
                                    : false;
                        }
                    }

                    final int levelPerWorld = 10;
                    Nonogram n = new Nonogram("Level " + world + "." + level,
                            DifficultyLevel.UNDEFINED, field);
                    n.setDescription("Mario's Picross 2 Mario World " + world
                            + "." + level);
                    n.setAuthor("Jupiter Co. and Nintendo Co., Ltd. (1996)");
                    n.setLevel((world - 1) * levelPerWorld + level);
                    nonogramList.add(n);
                    Course c = new Course("Mario's Picross 2 - Mario World",
                            nonogramList);
                    ZipCourseSerializer zip = new ZipCourseSerializer();
                    zip.save(COURSE, c);

                } catch (final IOException e) {
                    logger.debug("Error when loading image files.");
                }
            }
        }
    }

}
