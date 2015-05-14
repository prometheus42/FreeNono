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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Reads and saves nonogram files in the JCross file format.
 *
 * @author Markus Wichmann
 */
public class JCrossSerializer implements NonogramSerializer {

    private static final Logger LOGGER = Logger.getLogger(JCrossSerializer.class);
    private static final char FIELD_FREE_CHAR = '0';
    private static final char FIELD_OCCUPIED_CHAR = '1';

    @Override
    public final Nonogram[] load(final File f) throws IOException, NonogramFormatException {

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new FileInputStream(f));

            String name = null;
            String desc = "";
            String author = "";
            URL url = null;

            final Element rootElement = doc.getDocumentElement();

            final Element nameElement = (Element) rootElement.getElementsByTagName("title").item(0);
            name = nameElement.getTextContent();

            final Element descElement = (Element) rootElement.getElementsByTagName("description").item(0);
            desc = descElement.getTextContent();

            final Element authorElement = (Element) rootElement.getElementsByTagName("author").item(0);
            author = authorElement.getTextContent();

            final Element urlElement = (Element) rootElement.getElementsByTagName("url").item(0);
            url = new URL(urlElement.getTextContent());

            final Element dataElement = (Element) rootElement.getElementsByTagName("data").item(0);
            final boolean[][] field = getValueLines(dataElement.getTextContent());

            // validate values
            for (final boolean[] line1 : field) {
                for (final boolean[] line2 : field) {
                    if (line1.length != line2.length) {
                        LOGGER.warn("inconsistent line lengths");
                        throw new NonogramFormatException("inconsitent line lenghts");
                    }
                }
            }

            // fill real data object
            final Nonogram nonogram = new Nonogram(name, DifficultyLevel.UNDEFINED, field);
            nonogram.setDescription(desc);
            nonogram.setAuthor(author);
            nonogram.setDuration(0);
            nonogram.setLevel(0);
            nonogram.setOriginPath(url);

            return new Nonogram[] {nonogram};
        } catch (NumberFormatException | DOMException | ParserConfigurationException | SAXException e) {
            throw new NonogramFormatException("invalid format");
        }
    }

    /**
     * Retrieves the field data from the given String.
     * @param data
     *            String containing field data
     * @return 2-dimensional boolean array with field values
     * @throws NonogramFormatException
     *             if the given String doesn't contain valid data
     */
    private boolean[][] getValueLines(final String data) throws NonogramFormatException {
        boolean[][] values = null;
        try {
            final StringTokenizer tokenizer = new StringTokenizer(data);
            values = new boolean[tokenizer.countTokens()][];
            int i = 0;
            while (tokenizer.hasMoreElements()) {
                values[i] = getValueLine(tokenizer.nextToken());
                i++;
            }
        } catch (final NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("unable to parse field data", e);
            throw new NonogramFormatException("unable to parse field data ");
        }
        return values;
    }

    /**
     * Retrieves one line of field data from the given string.
     * @param line
     *            String containing field data
     * @return boolean array with field values
     * @throws NonogramFormatException
     *             if the given String doesn't contain valid data
     */
    private boolean[] getValueLine(final String line) throws NonogramFormatException {
        boolean[] values = null;
        try {
            final char[] chars = line.toCharArray();
            values = new boolean[chars.length];
            for (int i = 0; i < chars.length; i++) {
                values[i] = getValue(chars[i]);
            }
        } catch (final NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("Unable to parse line", e);
            throw new NonogramFormatException("unable to parse line ");
        }
        return values;
    }

    /**
     * Gets the boolean value of the given char field representation.
     * @param c
     *            char field value
     * @return boolean field value
     * @throws NonogramFormatException
     *             if the given character is not valid
     */
    private boolean getValue(final char c) throws NonogramFormatException {
        switch (Character.toLowerCase(c)) {
        case FIELD_OCCUPIED_CHAR:
            return true;
        case FIELD_FREE_CHAR:
            return false;
        default:
            LOGGER.warn("Couldn't find Value for character '" + c + "'");
            throw new NonogramFormatException("unknown character '" + c + "'");
        }
    }

    @Override
    public final void save(final File file, final Nonogram... n) {
        throw new UnsupportedOperationException();
    }

    /**
     * Example of how to convert jcross files to a real FreeNono files.
     *
     * @param sourceDir
     *            source directory of jcross files
     * @param destDir
     *            destination directory where FreeNono files are saved to
     * @throws Exception
     *             if an error occurs
     */
    public static void convert(final String sourceDir, final String destDir) throws Exception {

        final JCrossSerializer foreignSerializer = new JCrossSerializer();
        final StAXNonogramSerializer ownSerializer = new StAXNonogramSerializer();

        final List<Nonogram> dataList = new ArrayList<>();
        final File[] listOfNonogramFiles = new File(sourceDir).listFiles();
        if (listOfNonogramFiles != null) {
            for (final File inputFile : listOfNonogramFiles) {
                final Nonogram[] data = foreignSerializer.load(inputFile);
                ownSerializer.save(new File(destDir, File.separatorChar + inputFile.getName()), data);
                dataList.addAll(Arrays.asList(data));
            }
        }
    }
}
