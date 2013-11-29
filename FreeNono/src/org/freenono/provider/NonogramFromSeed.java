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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;

/**
 * Provides a random nonogram based on a given seed.
 * <p>
 * The public field <code>GENERATE_NEW_NONOGRAM_HASH</code> gives a string by
 * which a special entry can be identified. This nonogram provider allows to
 * create a new nonogram pattern based on a given seed string.
 * 
 * @author Christian Wichmann, Martin Wichmann
 */
public class NonogramFromSeed implements NonogramProvider {

    private static Logger logger = Logger.getLogger(NonogramFromSeed.class);

    private static final int MIN_HEIGHT = 10;
    private static final int MAX_HEIGHT = 35;
    private static final int MIN_WIDTH = 10;
    private static final int MAX_WIDTH = 35;
    private int height = MIN_HEIGHT;
    private int width = MIN_WIDTH;

    /* Constants for creating nonograms consisting of random circles. */
    private static final double CIRCLE_PER_FIELDS_RATIO = 2.8 / 100;
    private static final int MAXIMUM_RADIUS = 13;
    private static final int MINIMUM_RADIUS = 3;

    private Random rng = null;
    private static int ranNonoCounter = 1;

    private Nonogram currentNonogram = null;
    private NonogramProvider nextNonogram = null;
    private NonogramProvider previousNonogram = null;
    private CourseFromSeed course = null;
    private String seed = "";
    private RandomTypes randomTypeForCourse = RandomTypes.DEFAULT;

    /**
     * Types of randomly generated nonograms.
     * 
     * @author Martin Wichmann
     */
    public enum RandomTypes {

        /**
         * Randomly chosen type of randomly generated nonograms. :-)
         */
        RANDOM,

        /**
         * Random nonograms where exactly one half of the pattern is set.
         */
        HALFNHALF,

        /**
         * Completely random distribution on the pattern.
         */
        FULLRANDOM,

        /**
         * Random paths through the pattern.
         */
        RANDOMWAYS,

        /**
         * Random nonogram patterns consisting of circles.
         */
        CIRCLES,

        /**
         * Default method for generating new nonogram patterns.
         */
        DEFAULT
    }

    /**
     * Initializes a new nonogram from a given seed. No parameter of this
     * constructor should be null. If the given seed is an <b>empty</b> string,
     * this <code>NonogramProvider</code> represents a later newly generated
     * nonogram from a not yet known seed.
     * 
     * @param seed
     *            seed to generate new random nonogram, should never be null
     * @param randomType
     *            type of random nonograms that should be generated, should
     *            never be null
     * @param c
     *            course which contains this nonogram, should never be null
     */
    public NonogramFromSeed(final String seed, final RandomTypes randomType,
            final CourseFromSeed c) {

        if (seed == null || randomType == null || c == null) {
            throw new IllegalArgumentException(
                    "Seed, random type and course parameter should no be null!");
        }

        course = c;
        randomTypeForCourse = randomType;

        plantSeed(seed);
    }

    @Override
    public final Nonogram fetchNonogram() {

        if (currentNonogram == null) {
            if ("".equals(seed)) {
                currentNonogram = new Nonogram("New random nonogram",
                        DifficultyLevel.UNDEFINED, new boolean[1][1]);
            } else {
                generateNonogramBySeed();
            }
        }
        return currentNonogram;
    }

    @Override
    public final String getName() {

        return seed;
    }

    @Override
    public final String getDescription() {

        return "";
    }

    @Override
    public final DifficultyLevel getDifficulty() {

        return DifficultyLevel.UNDEFINED;
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
    public final int width() {
        return fetchNonogram().width();
    }

    @Override
    public final int height() {

        return fetchNonogram().height();
    }

    @Override
    public final String toString() {

        return getName();
    }

    /**
     * Plant seed to generate a new random nonogram. The given seed is saved in
     * the nonogramProvider and the new nonogram is generated.
     * 
     * @param seed
     *            the seed with which to generate a new random nonogram
     */
    private void plantSeed(final String seed) {

        this.seed = seed;

        generateNonogramBySeed();
    }

    /**
     * Generates a new <code>Nonogram</code> by calculating a hash from the
     * given text. The width and height of the new nonogram is calculated by
     * modulo the hashed value and the seed value for the random number
     * generator results from the first 64 bit of the hash.
     * <p>
     * The newly generated random nonogram is saved in
     * <code>currentNonogram</code>.
     */
    private void generateNonogramBySeed() {

        // get the text input by the user...
        byte[] bytesOfMessage = null;

        try {
            // TODO check if UTF-8 is the correct encoding to set?!
            bytesOfMessage = seed.getBytes("UTF-8");

        } catch (UnsupportedEncodingException e1) {
            logger.warn("Seed input by user is not correctly encoded. UTF-8 expected!");
        }

        // ...digest byte array to hash...
        MessageDigest md = null;
        String hashFunction = "MD5";
        try {
            md = MessageDigest.getInstance(hashFunction);
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Hash function " + hashFunction
                    + " not available on this system.");
        }
        byte[] thedigest = md.digest(bytesOfMessage);
        BigInteger bigintdigest = new BigInteger(thedigest);

        // ...generate long from byte array to use...
        final long seedValue = bigintdigest.longValue();
        rng = new Random(seedValue);

        // ..in the constructing of a new Nonogram!
        height = (rng.nextInt() % (MAX_HEIGHT - MIN_HEIGHT)) + MIN_HEIGHT;
        width = (rng.nextInt() % (MAX_WIDTH - MIN_WIDTH)) + MIN_WIDTH;

        currentNonogram = createRandomNonogram(randomTypeForCourse);
    }

    /**
     * Creates a random nonogram.
     * 
     * @param type
     *            type of the random nonogram. Type 0 uses a random type
     * @return generated nonogram or null if no nonogram could be generated
     */
    private Nonogram createRandomNonogram(final RandomTypes type) {

        RandomTypes randomType = type;

        if (height < MIN_HEIGHT) {
            height = MIN_HEIGHT;
        }

        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }

        if (randomType == RandomTypes.RANDOM) {

            int tmp = RandomTypes.values().length;

            do {

                randomType = RandomTypes.values()[rng.nextInt(tmp)];

            } while (randomType == RandomTypes.RANDOM);
        }

        Nonogram n = null;

        /*
         * Build and generate a new random nonogram by the given method.
         */
        switch (randomType) {
        case HALFNHALF:
            n = halfnhalf();
            break;
        case FULLRANDOM:
            n = fullRandomNono();
            break;
        case RANDOMWAYS:
            n = randomWays();
            break;
        case CIRCLES:
            n = randomCircles();
            break;
        default:
            n = fullRandomNono();
            break;
        }

        n.setDescription(getDescription());
        n.setAuthor(System.getProperty("user.name"));
        // TODO Use game-wide player name as author!

        ranNonoCounter++;

        return n;
    }

    /**
     * Generates random nonogram with one half marked.
     * 
     * @return Nonogram, if generated, else null
     */
    private Nonogram halfnhalf() {

        String name = "random " + ranNonoCounter;
        DifficultyLevel difficulty = getDifficulty();

        boolean[][] field = new boolean[height][width];

        int options = rng.nextInt(4);

        if (width == 1 || height == 1) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    field[j][i] = true;
                }
            }
        } else {
            switch (options) {
            case 0:
                for (int i = 0; i < Math.floor((width / 2)); i++) {
                    for (int j = 0; j < height; j++) {
                        field[j][i] = true;
                    }
                }
                break;
            case 1:
                for (int i = (int) Math.floor((width / 2)); i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        field[j][i] = true;
                    }
                }
                break;
            case 2:
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < (int) Math.floor((height / 2)); j++) {
                        field[j][i] = true;
                    }
                }
                break;
            case 3:
                for (int i = 0; i < width; i++) {
                    for (int j = (int) Math.floor((height / 2)); j < height; j++) {
                        field[j][i] = true;
                    }
                }
                break;
            default:
                break;
            }

        }

        Nonogram ret = null;
        try {
            ret = new Nonogram(name, difficulty, field);

        } catch (NullPointerException e) {
            logger.debug("Could not generate random nonogram (halfnhalf).");
        }

        return ret;
    }

    /**
     * Generates a fully random nonogram.
     * 
     * @return Randomly generated nonogram.
     */
    private Nonogram fullRandomNono() {

        String name = getName();
        DifficultyLevel difficulty = getDifficulty();

        boolean[][] field = new boolean[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field[j][i] = (rng.nextInt(2) == 0) ? true : false;
            }
        }

        // One field should at least be true, so the nonogram isn't empty
        field[rng.nextInt(height)][rng.nextInt(width)] = true;

        Nonogram ret = null;
        try {
            ret = new Nonogram(name, difficulty, field);

        } catch (NullPointerException e) {
            logger.debug("Could not generate random nonogram (fullRandomNono).");
        }

        return ret;
    }

    /**
     * Generates a nonogram consisting of random circles.
     * 
     * @return randomly generated nonogram
     */
    private Nonogram randomCircles() {

        String name = getName();
        DifficultyLevel difficulty = getDifficulty();

        boolean[][] field = new boolean[height][width];

        // generate some circles in an Image
        BufferedImage canvas = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = canvas.getGraphics();
        final int numberOfCircles = (int) (width * height * CIRCLE_PER_FIELDS_RATIO);
        for (int i = 0; i < numberOfCircles; i++) {
            final int x = rng.nextInt(width);
            final int y = rng.nextInt(height);
            final int diameter = rng.nextInt(MAXIMUM_RADIUS + 1
                    - MINIMUM_RADIUS)
                    + MINIMUM_RADIUS;
            g.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
        }

        // copy data from Image to boolean field array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field[j][i] = (canvas.getRGB(i, j) > -8388608 ? true : false);
            }
        }

        Nonogram ret = null;
        try {
            ret = new Nonogram(name, difficulty, field);

        } catch (NullPointerException e) {
            logger.debug("Could not generate random nonogram (randomCircles).");
        }

        return ret;
    }

    /**
     * Generates a nonogram which is based on random ways through it.
     * 
     * @return Randomly generated nonogram.
     */
    private Nonogram randomWays() {

        String name = getName();
        DifficultyLevel difficulty = getDifficulty();

        boolean[][] field = new boolean[height][width];

        int endCounter = (int) Math.ceil((height * width) / 5);
        // int endCounter = 5;
        int counter = 0;
        int hMark = rng.nextInt(height);
        int wMark = rng.nextInt(width);

        while (counter <= endCounter) {
            if (!(field[hMark][wMark])) {
                field[hMark][wMark] = true;
                counter++;
            }

            int decisionCoin = rng.nextInt(5);

            switch (decisionCoin) {
            case 0: // left
                wMark = mod((wMark - 1), width);
                break;
            case 1: // right
                wMark = (wMark + 1) % width;
                break;
            case 2: // up
                hMark = mod((hMark - 1), height);
                break;
            case 3: // down
                hMark = (hMark + 1) % height;
                break;
            case 4: // new start
                hMark = rng.nextInt(height);
                wMark = rng.nextInt(width);
                break;
            default:
                break;
            }
        }

        Nonogram ret = null;
        try {
            ret = new Nonogram(name, difficulty, field);

        } catch (NullPointerException e) {
            logger.debug("Could not generate random nonogram (randomWays).");
        }

        return ret;
    }

    /**
     * Calculates the modulo function.
     * 
     * @param x
     *            Parameter x.
     * @param y
     *            Parameter y.
     * @return Result from modulo operation.
     */
    private int mod(final int x, final int y) {

        int result = x % y;
        if (result < 0) {
            result += y;
        }
        return result;
    }

    @Override
    public final NonogramProvider getNextNonogram() {

        if (nextNonogram == null) {
            nextNonogram = course.getNextNonogram(this);
        }
        return nextNonogram;
    }

    @Override
    public final NonogramProvider getPreviousNonogram() {

        if (previousNonogram == null) {
            previousNonogram = course.getPreviousNonogram(this);
        }
        return previousNonogram;
    }

    /**
     * Returns the course provider of this nonogram.
     * 
     * @return course provider of this nonogram
     */
    public final CourseFromSeed getCourseForThisNonogram() {

        return course;
    }
}
