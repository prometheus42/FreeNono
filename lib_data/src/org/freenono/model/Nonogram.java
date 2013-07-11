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

import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Stores a nonogram pattern with all associated information like author,
 * difficulty level, description, etc. It is used at data storage throughout the
 * whole project including subprojects like FNE and NonoServer.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class Nonogram implements Serializable {

    /*
     * TODO make Nonogram immutable?! (Builder pattern or more constructors?)
     */

    private static final long serialVersionUID = -5072283907982515285L;

    private static Logger logger = Logger.getLogger(Nonogram.class);

    public static final Comparator<Nonogram> NAME_ASCENDING_ORDER = new Comparator<Nonogram>() {

        @Override
        public int compare(final Nonogram n1, final Nonogram n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                return n1.getName().compareTo(n2.getName());
            }

        }
    };

    public static final Comparator<Nonogram> NAME_DESCENDING_ORDER = new Comparator<Nonogram>() {

        @Override
        public int compare(final Nonogram n1, final Nonogram n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                return n1.getName().compareTo(n2.getName());
            }

        }
    };

    public static final Comparator<Nonogram> HASH_ASCENDING_ORDER = new Comparator<Nonogram>() {

        @Override
        public int compare(final Nonogram n1, final Nonogram n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                return n1.getHash().compareTo(n2.getHash());
            }

        }
    };

    public static final Comparator<Nonogram> HASH_DESCENDING_ORDER = new Comparator<Nonogram>() {

        @Override
        public int compare(final Nonogram n1, final Nonogram n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                return n1.getHash().compareTo(n2.getHash());
            }

        }
    };

    public static final Comparator<Nonogram> LEVEL_ASCENDING_ORDER = new Comparator<Nonogram>() {

        @Override
        public int compare(final Nonogram n1, final Nonogram n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {

                // if both parameters are not null, compare levels
                if (n1.getLevel() != 0 && n2.getLevel() != 0) {

                    if (n1.getLevel() < n2.getLevel()) {
                        return -1;
                    } else if (n1.getLevel() > n2.getLevel()) {
                        return 1;
                    } else {
                        return 0;
                    }

                    // compare reasonably if some levels are zero...
                } else if (n1.getLevel() == 0 && n2.getLevel() != 0) {
                    return 1;

                } else if (n1.getLevel() != 0 && n2.getLevel() == 0) {
                    return -1;

                    // ...or just use the names
                } else {
                    return n1.getName().compareTo(n2.getName());
                }
            }
        }
    };

    public static final Comparator<Nonogram> LEVEL_DESCENDING_ORDER = new Comparator<Nonogram>() {

        @Override
        public int compare(final Nonogram n1, final Nonogram n2) {

            if (n1 == null && n2 == null) {
                return 0;
            } else if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {

                // if both parameters are not null, compare levels
                if (n1.getLevel() != 0 && n2.getLevel() != 0) {

                    if (n1.getLevel() < n2.getLevel()) {
                        return 1;
                    } else if (n1.getLevel() > n2.getLevel()) {
                        return -1;
                    } else {
                        return 0;
                    }

                    // compare reasonably if some levels are zero...
                } else if (n1.getLevel() == 0 && n2.getLevel() != 0) {
                    return 1;

                } else if (n1.getLevel() != 0 && n2.getLevel() == 0) {
                    return -1;

                    // ...or just use the names
                } else {
                    return n1.getName().compareTo(n2.getName());
                }
            }
        }
    };

    private String name;
    private String desc;
    private String author;
    private int level;
    private DifficultyLevel difficulty;
    private String hash = null;
    private long duration;
    private URL originPath;

    private int width;
    private int height;
    private boolean[][] field;
    private List<int[]> lineNumbers;
    private List<int[]> columnNumbers;

    private boolean captionsCalculated = false;

    /**
     * Nonogram constructor that set the name and difficulty, as well as the
     * field. This constructor also calculates all captions for the field.
     * 
     * @param name
     *            Nonogram name.
     * @param difficulty
     *            Nonogram difficulty.
     * @param field
     *            Nonogram field.
     * @throws NullPointerException
     */
    public Nonogram(final String name, final DifficultyLevel difficulty,
            final boolean[][] field) {

        if (name == null) {
            throw new NullPointerException("Parameter name is null");
        }

        if (field == null) {
            throw new NullPointerException("Parameter field is null");
        }

        setName(name);
        setDescription(desc);
        setDifficulty(difficulty);
        setAuthor("");
        setLevel(0);

        // find and/or calculate size of nonogram
        int tempHeight = field.length;
        int tempWidth = Integer.MAX_VALUE;
        for (int i = 0; i < field.length; i++) {
            if (field[i].length < tempWidth) {
                // TODO what if there are different array lengths?
                tempWidth = field[i].length;
            }
        }
        if (tempWidth == Integer.MAX_VALUE) {
            tempWidth = 0;
        }

        // create internal field array and copy received values
        setSize(tempWidth, tempHeight);
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                this.field[i][j] = field[i][j];
            }
        }
    }

    @Override
    public final String toString() {

        return getName();
    }

    /**
     * Gets name of this nonogram. This <code>String</code> value is shown in
     * user interface when referring to it.
     * 
     * @return name of nonogram
     */
    public final String getName() {

        return name;
    }

    /**
     * Sets name of this nonogram. This <code>String</code> value is shown in
     * user interface when referring to it.
     * 
     * @param name
     *            name of nonogram to be set
     */
    public final void setName(final String name) {

        this.name = name;
    }

    /**
     * Gets description for this nonogram. This attribute contains more
     * information about where the nonogram was taken from or what it shows. No
     * guarantee is given that the description is shown anywhere in the user
     * interface.
     * 
     * @return description for this nonogram
     */
    public final String getDescription() {

        return desc;
    }

    /**
     * Sets description for this nonogram. This attribute contains more
     * information about where the nonogram was taken from or what it shows. No
     * guarantee is given that the description is shown anywhere in the user
     * interface.
     * 
     * @param desc
     *            description for this nonogram to be set
     */
    public final void setDescription(final String desc) {

        this.desc = desc;
    }

    /**
     * Gets difficulty for this nonogram. Difficulty is defined in the
     * enumeration <code>DifficultyLevel</code>.
     * 
     * @return difficulty level
     */
    public final DifficultyLevel getDifficulty() {

        return difficulty;
    }

    /**
     * Sets difficulty for this nonogram. Difficulty is defined in the
     * enumeration <code>DifficultyLevel</code>.
     * 
     * @param difficulty
     *            difficulty level
     */
    public final void setDifficulty(final DifficultyLevel difficulty) {

        this.difficulty = difficulty;
    }

    /**
     * Gets width of this nonogram.
     * 
     * @return width of this nonogram
     */
    public final int width() {

        return width;
    }

    /**
     * Gets height of this nonogram.
     * 
     * @return height of this nonogram
     */
    public final int height() {

        return height;
    }

    /**
     * Gets how long the player has to solve this nonogram. This attribute
     * overrides the option in <code>Settings</code> for maximum game time. But
     * if this duration is used depends on the chosen game mode.
     * 
     * @return duration for this nonogram
     */
    public final long getDuration() {

        return duration;
    }

    /**
     * Sets how long the player has to solve this nonogram. This attribute
     * overrides the option in <code>Settings</code> for maximum game time. But
     * if this duration is used depends on the chosen game mode.
     * 
     * @param duration
     *            duration for this nonogram
     */
    public final void setDuration(final long duration) {

        this.duration = duration;
    }

    /**
     * Gets author of this nonogram.
     * 
     * @return author of this nonogram
     */
    public final String getAuthor() {

        return author;
    }

    /**
     * Sets author of this nonogram.
     * 
     * @param author
     *            author of this nonogram
     */
    public final void setAuthor(final String author) {

        this.author = author;
    }

    /**
     * Gets level of this nonogram. Level is an attribute of nonograms from a
     * course that puts them in an order in which they should be played.
     * 
     * @return level of this nonogram
     */
    public final int getLevel() {

        return level;
    }

    /**
     * Sets level of this nonogram. Level is an attribute of nonograms from a
     * course that puts them in an order in which they should be played.
     * 
     * @param level
     *            level of this nonogram
     */
    public final void setLevel(final int level) {

        this.level = level;
    }

    /**
     * Gets path of origin for this nonogram.
     * 
     * @return origin path
     */
    public final URL getOriginPath() {

        return originPath;
    }

    /**
     * Sets path of origin for this nonogram.
     * 
     * @param originPath
     *            origin path
     */
    public final void setOriginPath(final URL originPath) {

        this.originPath = originPath;
    }

    /**
     * Generates a hash to identify a specific nonograms based on its pattern
     * and other given informations. A nonogram with the same pattern and
     * identical information has the same hash!
     * 
     * @return hash of this nonogram
     */
    private String generateHash() {

        // add all information to string
        StringBuilder strb = new StringBuilder();
        strb.append(name);
        strb.append(author);
        strb.append(desc);
        strb.append(difficulty);
        strb.append(duration);
        strb.append(width);
        strb.append(height);
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                strb.append(field[i][j]);
            }
        }

        // generate hash value
        MessageDigest md = null;
        String hashFunction = "MD5";
        try {
            md = MessageDigest.getInstance(hashFunction);
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Hash " + hashFunction
                    + " not available on this system.");
        }
        // TODO add standard encoding for all information in nonogram class and
        // use it here ->
        byte[] thedigest = md.digest(strb.toString().getBytes());

        // return the string containing the hash value as hex numbers
        StringBuffer sb = new StringBuffer();
        int hexValue = 0;
        for (int i = 0; i < thedigest.length; ++i) {
            // crop digest, since signed flag of int could ruin value
            hexValue = (thedigest[i] & 0xFF);
            // append a left padded hex string
            sb.append(String.format("%02x", hexValue));
        }

        return sb.toString();
    }

    /**
     * Generates hash for this nonogram if necessary and returns it.
     * 
     * @return hash of nonogram
     */
    public final String getHash() {

        if (hash == null) {
            hash = generateHash();
        }
        return hash;
    }

    /**
     * Set size of nonogram. This creates a new field, so an already loaded
     * nonogram will be lost.
     * 
     * @param newWidth
     *            new width of nonogram
     * @param newHeight
     *            new height of nonogram
     */
    public final void setSize(final int newWidth, final int newHeight) {

        width = newWidth;
        height = newHeight;
        field = new boolean[newHeight][newWidth];
    }

    /**
     * Returns the width of the line captions. I.e.: Maximum count of caption
     * numbers in this nonograms lines.
     * 
     * @return line caption width
     * @see Nonogram#getColumnCaptionHeight()
     */
    public final int getLineCaptionWidth() {

        int maxLineNumbers = 0;

        for (int i = 0; i < height(); i++) {
            maxLineNumbers = Math.max(maxLineNumbers, getLineNumberCount(i));
        }

        return maxLineNumbers;
    }

    /**
     * Returns the height of the column captions. I.e.: Maximum count of caption
     * numbers in this nonograms columns.
     * 
     * @return column caption height
     * @see Nonogram#getLineCaptionWidth()
     */
    public final int getColumnCaptionHeight() {

        int maxColumnNumbers = 0;

        for (int i = 0; i < width(); i++) {
            maxColumnNumbers = Math.max(maxColumnNumbers,
                    getColumnNumbersCount(i));
        }

        return maxColumnNumbers;
    }

    /**
     * Get the value of the nonogram at specified position.
     * 
     * @param x
     *            row to get field value from
     * @param y
     *            column to get field value from
     * @return field value at specified position
     */
    public final boolean getFieldValue(final int x, final int y) {

        if (!areCoordinatesValid(x, y)) {
            throw new IndexOutOfBoundsException(
                    "Given coordinates are not valid.");
        }

        return this.field[y][x];
    }

    /**
     * Set the value of the nonogram at specified position.
     * 
     * @param b
     *            Value to be set
     * @param x
     *            Row
     * @param y
     *            Column
     * @throws IndexOutOfBoundsException
     */
    public final void setFieldValue(final boolean b, final int x, final int y) {

        if (!areCoordinatesValid(x, y)) {
            throw new IndexOutOfBoundsException(
                    "Given coordinates are not valid.");
        }

        this.field[y][x] = b;
    }

    /**
     * Checks whether given coordinates are valid.
     * 
     * @param x
     *            row to get field value from
     * @param y
     *            column to get field value from
     * @return true, if given coordinates are valid
     */
    private boolean areCoordinatesValid(final int x, final int y) {

        boolean coordinatesValid = true;

        if (x < 0) {
            coordinatesValid = false;
        }
        if (x >= width()) {
            coordinatesValid = false;
        }
        if (y < 0) {
            coordinatesValid = false;
        }
        if (y >= height()) {
            coordinatesValid = false;
        }

        return coordinatesValid;
    }

    /**
     * Get the hint numbers for the specified line.
     * 
     * @see Nonogram#getColumnNumbers(int)
     * @param y
     *            Line
     * @return Array of hint numbers
     * @throws IndexOutOfBoundsException
     */
    public final int[] getLineNumbers(final int y) {

        if (!captionsCalculated) {
            calculateCaptions();
        }

        if (y < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (y >= height()) {
            throw new IndexOutOfBoundsException();
        }

        return lineNumbers.get(y);
    }

    /**
     * Get the hint numbers for the specified column.
     * 
     * @see Nonogram#getLineNumbers(int)
     * @param x
     *            Column
     * @return Array of hint numbers
     * @throws IndexOutOfBoundsException
     */
    public final int[] getColumnNumbers(final int x) {

        if (!captionsCalculated) {
            calculateCaptions();
        }

        if (x < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (x >= width()) {
            throw new IndexOutOfBoundsException();
        }

        return columnNumbers.get(x);
    }

    /**
     * Gets number of numbers for a specific row.
     * 
     * @param y
     *            row for which number of numbers should be given
     * @return number of numbers in row
     * @throws IndexOutOfBoundsException
     */
    public final int getLineNumberCount(final int y) {

        if (!captionsCalculated) {
            calculateCaptions();
        }

        if (y < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (y >= height()) {
            throw new IndexOutOfBoundsException();
        }

        return lineNumbers.get(y).length;
    }

    /**
     * Gets number of numbers for a specific column.
     * 
     * @param x
     *            column for which number of numbers should be given
     * @return number of numbers in column
     * @throws IndexOutOfBoundsException
     */
    public final int getColumnNumbersCount(final int x) {

        if (!captionsCalculated) {
            calculateCaptions();
        }

        if (x < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (x >= width()) {
            throw new IndexOutOfBoundsException();
        }

        return columnNumbers.get(x).length;
    }

    /**
     * Returns a number for a given row and its index.
     * 
     * @param y
     *            row from which to give number
     * @param index
     *            index of number in row
     * @return number for given column and index
     * @throws IndexOutOfBoundsException
     */
    public final int getLineNumber(final int y, final int index) {

        if (!captionsCalculated) {
            calculateCaptions();
        }

        if (y < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (y >= height()) {
            throw new IndexOutOfBoundsException();
        }

        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (index >= getLineCaptionWidth()) {
            throw new IndexOutOfBoundsException();
        }

        int[] tmp = lineNumbers.get(y);
        if (index < tmp.length) {
            return tmp[index];
        } else {
            return -1;
        }

    }

    /**
     * Returns a number for a given column and its index.
     * 
     * @param x
     *            column from which to give number
     * @param index
     *            index of number in column
     * @return number for given column and index
     * @throws IndexOutOfBoundsException
     */
    public final int getColumnNumber(final int x, final int index) {

        if (!captionsCalculated) {
            calculateCaptions();
        }

        if (x < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (x >= width()) {
            throw new IndexOutOfBoundsException();
        }

        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (index >= getColumnCaptionHeight()) {
            throw new IndexOutOfBoundsException();
        }

        int[] tmp = columnNumbers.get(x);
        if (index < tmp.length) {
            return tmp[index];
        } else {
            return -1;
        }

    }

    /**
     * Calculates numbers for captions for all rows and columns.
     */
    private void calculateCaptions() {

        logger.debug("Calculating column numbers for nonogram.");

        // calculate line numbers
        lineNumbers = new ArrayList<int[]>();
        for (int i = 0; i < height(); i++) {
            int[] tmp = calculateNumbers(i, true);
            lineNumbers.add(tmp);
        }

        // calculate column numbers
        columnNumbers = new ArrayList<int[]>();
        for (int i = 0; i < width(); i++) {
            int[] tmp = calculateNumbers(i, false);
            columnNumbers.add(tmp);
        }

        captionsCalculated = true;
    }

    /**
     * Calculates numbers for a specific row or column.
     * @param index
     *            index of row or column
     * @param horizontal
     *            defines whether it is a row or a column
     * @return array of numbers for given row or column
     */
    private int[] calculateNumbers(final int index, final boolean horizontal) {
        List<Integer> list = new ArrayList<Integer>();

        // get loop upper bound according to horizontal parameter
        int loopLimit = 0;
        if (horizontal) {
            loopLimit = width();
        } else {
            loopLimit = height();
        }

        int tmp = 0;
        boolean currentField = false;
        for (int i = 0; i < loopLimit; i++) {

            // get value of current field according to horizontal parameter
            currentField = false;
            if (horizontal) {
                currentField = field[index][i];
            } else {
                currentField = field[i][index];
            }

            // increase tmp if series continues...
            if (currentField) {
                tmp++;
                // ...or else add tmp to list and reset tmp
            } else if (tmp > 0) {
                list.add(tmp);
                tmp = 0;
            }
        }

        // handle the last set of fields, if available
        if (tmp > 0) {
            list.add(tmp);
            tmp = 0;
        }

        // if there haven't been any occupied fields, so add at least a zero
        if (list.size() <= 0) {
            list.add(0);
        }

        // convert list to array
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }
}
