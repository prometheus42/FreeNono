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
package org.freenono.serializer.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;

/**
 * @author Markus Wichmann
 */
public class StAXNonogramSerializer implements NonogramSerializer {

    private static final Logger LOGGER = Logger
            .getLogger(StAXNonogramSerializer.class);
    private static final char FIELD_FREE_CHAR = '_';
    private static final char FIELD_OCCUPIED_CHAR = 'x';

    public static final String DEFAULT_FILE_EXTENSION = "nonogram";

    private XMLInputFactory inputFactory = null;
    private XMLOutputFactory outputFactory = null;

    /**
     * Returns the local instance of XMLInputFactory to save some time.
     * @return {@link XMLInputFactory} object
     */
    private XMLInputFactory getInputFactory() {
        if (inputFactory == null) {
            LOGGER.debug("Initialising input XMLInputFactory for FastXMLSerializer");
            inputFactory = XMLInputFactory.newInstance();
        }
        return inputFactory;
    }

    /**
     * Returns the local instance of XMLOutputFactory to save some time.
     * @return {@link XMLOutputFactory} object
     */
    private XMLOutputFactory getOutputFactory() {
        if (outputFactory == null) {
            LOGGER.debug("Initialising input XMLOutputFactory for FastXMLSerializer");
            outputFactory = XMLOutputFactory.newInstance();
        }
        return outputFactory;
    }

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

        FileInputStream fis = null;
        Nonogram[] n;
        try {
            // create the corresponding FileReader an deserialize the nonograms
            fis = new FileInputStream(f);
            n = load(fis);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                LOGGER.warn("Unable to close FileReader");
            }
        }

        return n;
    }

    /**
     * Loads an array of Nonogram objects in XML notation from the given input
     * stream.
     * @param is
     *            {@link InputStream} to load data from
     * @return array of {@link Nonogram} objects
     * @throws IOException
     *             if the stream couldn't be read
     * @throws NonogramFormatException
     *             if the stream doesn't contain valid data
     */
    public final Nonogram[] load(final InputStream is) throws IOException,
            NonogramFormatException {

        // do some parameter checks
        if (is == null) {
            throw new NullPointerException("InputStream parameter is null");
        }

        final List<Nonogram> list = new ArrayList<>();
        try {
            Nonogram currentNonogram = null;
            final XMLStreamReader reader = getInputFactory()
                    .createXMLStreamReader(new BufferedInputStream(is));
            do {
                currentNonogram = loadNonogram(reader);
                if (currentNonogram != null) {
                    list.add(currentNonogram);
                }
            } while (currentNonogram != null);

        } catch (final XMLStreamException e) {
            throw new NonogramFormatException("Unable to read file");
        } catch (final NullPointerException e) {
            throw new NonogramFormatException("Unable to read file");
        }

        return list.toArray(new Nonogram[0]);
    }

    /**
     * Reads the next Nonogram from the given XML stream.
     * @param reader
     *            {@link XMLStreamReader} object
     * @return {@link Nonogram} object
     * @throws XMLStreamException
     *             if some data couldn't been read from the stream
     * @throws NonogramFormatException
     *             if the stream doesn't contain valid data
     */
    private Nonogram loadNonogram(final XMLStreamReader reader)
            throws XMLStreamException, NonogramFormatException {
        boolean isDone = false;
        boolean nonogramFound = false;

        String name = "";
        DifficultyLevel diff = DifficultyLevel.UNDEFINED;
        int width = -1;
        int height = -1;
        final List<boolean[]> field = new ArrayList<>();

        String desc = "";
        int duration = 0;
        String author = "";
        int level = 0;
        while (reader.hasNext() && !isDone) {
            final int eventType = reader.next();
            switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
                final String localName = reader.getLocalName();
                if ("Nonogram".equals(localName)) {
                    nonogramFound = true;
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        final String attribName = reader
                                .getAttributeLocalName(i);
                        final String attribValue = reader.getAttributeValue(i);
                        switch (attribName) {
                        case "name":
                            name = attribValue;
                            break;
                        case "desc":
                            desc = attribValue;
                            break;
                        case "author":
                            author = attribValue;
                            break;
                        case "width":
                            width = Integer.parseInt(attribValue);
                            break;
                        case "height":
                            height = Integer.parseInt(attribValue);
                            break;
                        case "difficulty":
                            diff = DifficultyLevel.values()[Integer
                                    .parseInt(attribValue)];
                            break;
                        case "level":
                            level = Integer.parseInt(attribValue);
                            break;
                        case "duration":
                            duration = Integer.parseInt(attribValue);
                            break;
                        default:
                            break;
                        }
                    }
                } else if ("line".equals(localName)) {
                    field.add(getValueLine(reader.getElementText()));
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                if ("Nonogram".equals(reader.getLocalName())) {
                    isDone = true;
                }
                break;
            default:
                break;
            }
        }

        Nonogram nonogram = null;
        if (nonogramFound) {
            // validate values
            if (field.size() != height) {
                LOGGER.warn("wrong number of lines");
                throw new NonogramFormatException(
                        "number of line differ from height");
            }
            for (final boolean[] data : field) {
                if (data.length != width) {
                    LOGGER.warn("wrong number of values in line");
                    throw new NonogramFormatException(
                            "line length differ from width");
                }
            }

            // fill real data object
            nonogram = new Nonogram(name, diff, field.toArray(new boolean[0][]));
            nonogram.setDescription(desc);
            nonogram.setAuthor(author);
            nonogram.setDuration(duration);
            nonogram.setLevel(level);
        }

        return nonogram;
    }

    /**
     * Parses the given String to the boolean values of a Nonogram field.
     * @param line
     *            String
     * @return boolean array containing Nonogram field data
     * @throws NonogramFormatException
     *             if the line contains invalid data
     */
    private boolean[] getValueLine(final String line)
            throws NonogramFormatException {
        boolean[] values = null;
        try {
            final StringTokenizer tokenizer = new StringTokenizer(line);
            values = new boolean[tokenizer.countTokens()];
            int i = 0;
            while (tokenizer.hasMoreElements()) {
                values[i] = getValue(tokenizer.nextToken().charAt(0));
                i++;
            }
        } catch (final NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("Unable to parse line", e);
            throw new NonogramFormatException("unable to parse line ");
        }
        return values;
    }

    /**
     * Gets the boolean field value of its given character representation.
     * @param c
     *            char field value
     * @return boolean field value
     * @throws NonogramFormatException
     *             if the given character is invalid
     */
    private boolean getValue(final char c) throws NonogramFormatException {
        switch (Character.toLowerCase(c)) {
        case FIELD_OCCUPIED_CHAR:
            return true;
        case FIELD_FREE_CHAR:
            return false;
        default:
            LOGGER.warn("Couldn't find Value for character '" + c + "'");
            throw new NonogramFormatException(
                    "couldn't find Value for character '" + c + "'");
        }
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
            throw new IOException("unable to use a directory to save nonograms");
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
            LOGGER.warn("specified output file already exists, it will be overwritten");
        }

        FileOutputStream fos = null;
        try {
            // create the corresponding FileWriter an serialize the nonograms
            fos = new FileOutputStream(f);
            save(fos, n);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                LOGGER.warn("Unable to close FileWriter");
            }
        }
    }

    /**
     * Saves the specified Nonogram objects to the given OutputStream.
     * @param os
     *            {@link OutputStream}
     * @param n
     *            array of {@link Nonogram}
     * @throws IOException
     *             if the data couldn't been written
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

        try {
            final XMLStreamWriter writer = getOutputFactory()
                    .createXMLStreamWriter(new BufferedOutputStream(os),
                            "UTF-8");

            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");

            writer.writeStartElement("FreeNono");
            writer.writeCharacters("\n ");

            writer.writeStartElement("Nonograms");

            for (Nonogram nonogram : n) {
                writer.writeCharacters("\n  ");
                saveNonogram(nonogram, writer);

            }

            writer.writeCharacters("\n ");
            writer.writeEndElement(); // </Nonograms>
            writer.writeCharacters("\n");

            writer.writeEndElement(); // </FreeNono>

            writer.writeEndDocument();
            writer.flush();
            writer.close();
        } catch (final XMLStreamException e) {
            LOGGER.warn("Unable to write XML data", e);
            throw new IOException("Unable to write XML data", e);
        }
    }

    /**
     * Saves one Nonogram using the given XMLStreamWriter.
     * @param n
     *            {@link Nonogram} object
     * @param writer
     *            {@link XMLStreamWriter}
     * @throws XMLStreamException
     *             if the data couldn't been written
     */
    private void saveNonogram(final Nonogram n, final XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeStartElement("Nonogram");
        writer.writeAttribute("name", n.getName());
        writer.writeAttribute("height", Integer.toString(n.height()));
        writer.writeAttribute("width", Integer.toString(n.width()));
        writer.writeAttribute("difficulty",
                Integer.toString(n.getDifficulty().ordinal()));
        writer.writeAttribute("duration", Long.toString(n.getDuration()));
        writer.writeAttribute("level", Integer.toString(n.getLevel()));
        writer.writeAttribute("desc", n.getDescription());
        writer.writeAttribute("author", n.getAuthor());

        for (int y = 0; y < n.height(); y++) {
            writer.writeCharacters("\n   ");
            writer.writeStartElement("line");

            final StringBuilder builder = new StringBuilder();
            for (int x = 0; x < n.width(); x++) {
                builder.append(getChar(n.getFieldValue(x, y)));
                builder.append(" ");
            }
            writer.writeCharacters(builder.toString().trim());
            writer.writeEndElement(); // </line>
        }

        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // </Nonogram>
    }

    /**
     * Gets the char field value of given boolean.
     * @param b
     *            boolean field value
     * @return char representation of field value
     */
    private char getChar(final boolean b) {
        return b ? FIELD_OCCUPIED_CHAR : FIELD_FREE_CHAR;
    }
}
