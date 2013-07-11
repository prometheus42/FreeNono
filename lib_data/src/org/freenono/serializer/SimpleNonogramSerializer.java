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
package org.freenono.serializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.freenono.model.Tools;

/**
 * @author Markus Wichmann
 */
public class SimpleNonogramSerializer implements NonogramSerializer {

    private static final char FIELD_FREE_CHAR = ' ';
    private static final char FIELD_OCCUPIED_CHAR = 'x';

    public static final String DEFAULT_FILE_EXTENSION = "nono";

    private static Logger logger = Logger
            .getLogger(SimpleNonogramSerializer.class);

    /* load methods */
    @Override
    public final Nonogram[] load(final File f) throws IOException,
            NonogramFormatException {

        // do some parameter checks
        if (f == null) {
            // unable to use a file that is null ;-)
            throw new NullPointerException("File parameter is null");
        }
        if (f.isDirectory()) {
            // unable to use a directory to load a nonogram
            throw new IOException("unable to use a directory to load nonograms");
        }
        if (!f.exists()) {
            // no need to add to "throws"-list, because FileNotFoundException is
            // an IOException
            throw new FileNotFoundException("specified file doesn't exist");
        }

        FileReader fr = null;
        Nonogram[] n;
        try {
            // create the corresponding FileReader an deserialize the nonograms
            fr = new FileReader(f);
            n = load(fr);
        } finally {
            try {
                fr.close();
            } catch (Exception e) {
                logger.warn("Unable to close FileReader");
            }
        }

        return n;
    }

    /**
     * Load nonograms from already opened InputStream.
     * @param is
     *            InputStream to use.
     * @return Array of nonograms.
     * @throws IOException
     *             Thrown if 'file' is directory.
     * @throws NonogramFormatException
     *             Thrown if file is not well formed
     */
    public final Nonogram[] load(final InputStream is) throws IOException,
            NonogramFormatException {

        // do some parameter checks
        if (is == null) {
            throw new NullPointerException("InputStream paremeter is null");
        }

        Nonogram[] n;
        InputStreamReader isr = null;
        try {

            // create the corresponding InputStreamReader and call another load
            // method
            isr = new InputStreamReader(is, "UTF-8");
            n = load(isr);

        } catch (UnsupportedEncodingException e) {

            // this exception will never occure, because "UTF-8" should be
            // supported on every platform
            throw new IOException("UTF-8 encoding not found", e);

        } finally {

            try {
                isr.close();
            } catch (Exception e) {
                logger.warn("Unable to close InputStreamReader");
            }

        }

        return n;
    }

    /**
     * Load nonograms from Reader.
     * @param r
     *            Reader to use.
     * @return List of nonograms.
     * @throws IOException
     *             If File is not ready.
     * @throws NonogramFormatException
     *             If file is not well formed.
     */
    private Nonogram[] load(final Reader r) throws IOException,
            NonogramFormatException {

        // do some parameter checks
        if (r == null) {
            throw new NullPointerException("Reader paremeter is null");
        }
        if (!r.ready()) {
            throw new IOException("InputStreamReader not ready");
        }

        List<Nonogram> lst = new ArrayList<Nonogram>();
        Nonogram n = null;
        BufferedReader reader = null;
        try {

            String line, tmp;
            reader = new BufferedReader(r);

            while ((line = reader.readLine()) != null && line.length() > 0) {
                String name = line.trim();

                line = reader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                if (tokenizer.countTokens() != 2) {
                    throw new NonogramFormatException("");
                }

                tmp = tokenizer.nextToken();
                int width = Integer.parseInt(tmp);

                tmp = tokenizer.nextToken();
                int height = Integer.parseInt(tmp);

                boolean[][] field = new boolean[height][width];

                int y = 0;
                while (y < height && reader.ready()) {
                    line = reader.readLine();
                    if (line.length() != width) {
                        throw new NonogramFormatException(
                                "File contains wrong line lengths");
                    }
                    for (int x = 0; x < line.length(); x++) {
                        field[y][x] = getFieldValue(line.charAt(x));
                    }
                    y++;
                }

                if (y != height) {
                    throw new NonogramFormatException(
                            "File contains not enough lines");
                }

                n = new Nonogram(name, DifficultyLevel.undefined, field);
                lst.add(n);
            }

        } catch (NullPointerException e) {
            throw new NonogramFormatException(
                    "Unable to read Nonogram input file");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return lst.toArray(new Nonogram[0]);
    }

    /* save methods */
    @Override
    public final void save(final File f, final Nonogram... n)
            throws IOException {

        // do some parameter checks
        if (f == null) {
            // unable to use a file that is null ;-)
            throw new NullPointerException("File parameter is null");
        }
        if (f.isDirectory()) {
            // unable to use a directory to save a nonogram
            throw new IOException("unable to use a directory to load nonograms");
        }

        if (n == null) {
            // there is no nonogram to save
            throw new NullPointerException("Nonogram[] parameter is null");
        }
        if (n.length == 0) {
            // there is also no nonogram to save
            throw new NullPointerException(
                    "No nonogram was specified as parameter");
        }
        // there is also no CLN (Cow-Level-Nonogram)

        if (f.exists()) {
            // at least trigger a log message, if the file already exists
            logger.warn("specified output file already exists, it will be overwritten");
        }

        FileWriter fw = null;
        try {
            // create the corresponding FileWriter and serialize the nonograms
            fw = new FileWriter(f);
            save(fw, n);
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                logger.warn("Unable to close FileWriter");
            }
        }
    }

    /**
     * Save list of nonograms to already opened OutputStream.
     * @param os
     *            OutputStream to use.
     * @param n
     *            One or multiple nonograms.
     * @throws IOException
     *             If file encoding is not supported.
     */
    public final void save(final OutputStream os, final Nonogram... n)
            throws IOException {

        // do some parameter checks
        if (os == null) {
            throw new NullPointerException("OutputStream paremeter is null");
        }
        if (n == null) {
            throw new NullPointerException("Nonogram parameter is null");
        }
        if (n.length == 0) {
            throw new NullPointerException(
                    "No nonogram was specified as parameter");
        }

        OutputStreamWriter osw = null;
        try {

            // create the corresponding OutputStreamWriter and call another save
            // method
            osw = new OutputStreamWriter(os, "UTF-8");
            save(osw, n);

        } catch (UnsupportedEncodingException e) {

            // this exception will never occur, because "UTF-8" should be
            // supported on every platform
            throw new IOException("UTF-8 encoding not found", e);

        } finally {

            try {
                osw.close();
            } catch (Exception e) {
                logger.warn("Unable to close OutputStreamWriter");
            }

        }
    }

    /**
     * Save list of nonograms to already opened OutputStream.
     * @param w
     *            Writer to use.
     * @param n
     *            One or multiple nonograms.
     * @throws IOException
     *             If file encoding is not supported.
     */
    private void save(final Writer w, final Nonogram... n) throws IOException {

        // do some parameter checks
        if (w == null) {
            throw new NullPointerException("Writer paremeter is null");
        }
        if (n == null) {
            throw new NullPointerException("Nonogram parameter is null");
        }
        if (n.length == 0) {
            throw new NullPointerException(
                    "No nonogram was specified as parameter");
        }

        for (Nonogram nonogram : n) {
            try {

                w.write(nonogram.getName());
                w.write(Tools.NEW_LINE);

                w.write(Integer.toString(nonogram.width()));
                w.write(",");
                w.write(Integer.toString(nonogram.height()));
                w.write(Tools.NEW_LINE);

                for (int i = 0; i < nonogram.height(); i++) {
                    for (int j = 0; j < nonogram.width(); j++) {
                        w.write(getFieldChar(nonogram.getFieldValue(i, j)));
                    }
                    w.write(Tools.NEW_LINE);
                }
                w.flush();

            } catch (IOException e) {
                throw new IOException("Unable to write Nonogram output file", e);
            } finally {
                if (w != null) {
                    w.close();
                }
            }
        }

    }

    /*
     * static helper methods
     */
    /**
     * Get the boolean value for specified char values.
     * 
     * <br>
     * FIELD_FREE_CHAR -> false
     * 
     * <br>
     * FIELD_OCCUPIED_CHAR -> true
     * 
     * @param c
     *            character to get field value for
     * @return value corresponding to char
     * @throws NonogramFormatException
     *             if char is not one of the specified.
     */
    // TODO methods also exist in XMLNonogramSerializer
    private static boolean getFieldValue(final char c)
            throws NonogramFormatException {
        switch (c) {
        case FIELD_FREE_CHAR:
            return false;
        case FIELD_OCCUPIED_CHAR:
            return true;
        default:
            throw new NonogramFormatException(
                    "The field containes the wrong symbol " + c);
        }
    }

    /**
     * Get the char for the defined boolean.
     * @param b
     *            Boolean to use
     * @return Char value for Boolean
     * @see SimpleNonogramSerializer#getFieldValue(char)
     */
    private static char getFieldChar(final boolean b) {
        if (b) {
            return FIELD_OCCUPIED_CHAR;
        } else {
            return FIELD_FREE_CHAR;
        }
    }

}
