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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Serializes nonograms from and into a xml file.
 * 
 * @author Markus Wichmann
 */
public class XMLNonogramSerializer implements NonogramSerializer {

    private static Logger logger = Logger
            .getLogger(XMLNonogramSerializer.class);

    private static final char FIELD_FREE_CHAR = '_';
    private static final char FIELD_OCCUPIED_CHAR = 'x';

    public static final String DEFAULT_FILE_EXTENSION = "nonogram";

    private File currentNonogramFile = null;

    private ErrorHandler errorHandler = new ErrorHandler() {

        // TODO add error handling here?

        @Override
        public void warning(final SAXParseException exception)
                throws SAXException {
        }

        @Override
        public void fatalError(final SAXParseException exception)
                throws SAXException {
        }

        @Override
        public void error(final SAXParseException exception)
                throws SAXException {
        }
    };

    private Validator validator = null;

    /*
     * load methods
     */

    @Override
    public final Nonogram[] load(final File f) throws IOException,
            NonogramFormatException {

        this.currentNonogramFile = f;

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
                logger.warn("Unable to close FileReader");
            }
        }

        return n;
    }

    /**
     * Load nonograms from already opened InputStream.
     * 
     * @param is
     *            input stream to use
     * @return array of nonograms
     * @throws IOException
     *             if 'file' is directory.
     * @throws NonogramFormatException
     *             if file is not well formed
     */
    public final Nonogram[] load(final InputStream is) throws IOException,
            NonogramFormatException {

        // do some parameter checks
        if (is == null) {
            throw new NullPointerException("InputStream parameter is null");
        }

        List<Nonogram> lst = null;
        try {

            DocumentBuilder parser = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = parser.parse(is);

            // TODO check if validation is necessary and usable
            final Validator validator = getXMLValidator();
            validator.validate(new DOMSource(doc));

            Element root = doc.getDocumentElement();

            lst = loadXMLNonograms(root);
            if (lst == null || lst.size() <= 0) {
                throw new NonogramFormatException(
                        "file doesn't contain any nonogram data");
            }
        } catch (SAXException e) {
            throw new NonogramFormatException(
                    "unable to load file, because a SAX error occured");
        } catch (ParserConfigurationException e) {
            throw new NonogramFormatException(
                    "unable to load file, because a parser error occured");
        }

        // return list of nonograms, but at least one empty nonogram.
        return lst.toArray(new Nonogram[0]);
    }

    /*
     * save methods
     */

    @Override
    public final void save(final File f, final Nonogram... n)
            throws IOException {

        this.currentNonogramFile = f;

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
            logger.warn("specified output file already exists, it will be overwritten");
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
                logger.warn("Unable to close FileWriter");
            }
        }
    }

    /**
     * Save nonograms to already opened OutputStream.
     * @param os
     *            OutputStream to use.
     * @param n
     *            One or more Nonograms
     * @throws IOException
     *             If file could not be created
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

            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.newDocument();

            // <FreeNono>
            // TODO will this element be used a document node?
            Element root = doc.createElement("FreeNono");
            doc.appendChild(root);

            saveXMLNonograms(Arrays.asList(n), doc, root);

            Source source = new DOMSource(doc);
            Result result = new StreamResult(os);

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(source, result);

        } catch (ParserConfigurationException e) {
            throw new IOException(
                    "unable to save file, because no parser could be created",
                    e);
        } catch (TransformerException e) {
            throw new IOException(
                    "unable to save file, because no parser could be created",
                    e);
        }
    }

    /* private helpers */

    /**
     * Load a list of Nonograms from a XML root element (Freenono).
     * @param root
     *            XML root element.
     * @return List of nonograms
     * @throws NonogramFormatException
     *             If nonogram is not well formed
     */
    private List<Nonogram> loadXMLNonograms(final Element root)
            throws NonogramFormatException {

        ArrayList<Nonogram> list = new ArrayList<Nonogram>();

        Element nonograms = (Element) root.getElementsByTagName("Nonograms")
                .item(0);
        if (nonograms != null) {

            NodeList nonogramList = nonograms.getElementsByTagName("Nonogram");

            for (int i = 0; i < nonogramList.getLength(); i++) {
                Element nonogram = (Element) nonogramList.item(i);
                Nonogram n = loadXMLNonogram(nonogram);
                if (n != null) {
                    list.add(n);
                }
            }
        }

        return list;
    }

    /**
     * Load a single nonogram from a XML nonogram element.
     * @param element
     *            XML root element of single nonogram.
     * @return Parsed nonogram
     * @throws NonogramFormatException
     *             If file is not well formed
     */
    private Nonogram loadXMLNonogram(final Element element)
            throws NonogramFormatException {

        String tmp;
        Nonogram nonogram = null;

        int width;
        int height;
        int level;
        long duration;
        DifficultyLevel diff;
        String name;
        String desc;
        String author;
        boolean[][] field;

        name = element.getAttribute("name");
        desc = element.getAttribute("desc");
        author = element.getAttribute("author");

        try {
            tmp = element.getAttribute("width");
            width = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {

            logger.warn("A wrongly formatted attribute in nonogram file "
                    + currentNonogramFile + " could not be loaded.");
            throw new NonogramFormatException(
                    "unable to load width, because it has an invalid format");
        }

        try {
            tmp = element.getAttribute("height");
            height = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {

            logger.warn("A wrongly formatted attribute in nonogram file "
                    + currentNonogramFile + " could not be loaded.");
            throw new NonogramFormatException(
                    "unable to load height, because it has an invalid format");
        }

        try {
            tmp = element.getAttribute("level");
            // if no duration attribute is given, set it to zero
            if (tmp.length() == 0) {
                tmp = "0";
            }
            level = Integer.parseInt(tmp);

        } catch (NumberFormatException e) {

            level = 0;
            logger.warn("A wrongly formatted attribute in nonogram file "
                    + currentNonogramFile + " could not be loaded.");
            throw new NonogramFormatException(
                    "unable to load level, because it has an invalid format");
        }

        try {
            tmp = element.getAttribute("difficulty");
            diff = DifficultyLevel.values()[Integer.parseInt(tmp)];
        } catch (NumberFormatException e) {

            logger.warn("A wrongly formatted attribute in nonogram file "
                    + currentNonogramFile + " could not be loaded.");
            throw new NonogramFormatException(
                    "unable to load height, because it has an invalid format");
        }

        try {
            tmp = element.getAttribute("duration");
            // if no duration attribute is given, set it to zero
            if (tmp.length() == 0) {
                tmp = "0";
            }
            duration = Integer.parseInt(tmp);

        } catch (NumberFormatException e) {

            duration = 0;
            logger.warn("A wrongly formatted attribute in nonogram file "
                    + currentNonogramFile + " could not be loaded.");
            throw new NonogramFormatException(
                    "unable to load duration, because it has an invalid format");
        }

        field = new boolean[height][width];

        NodeList lineList = element.getElementsByTagName("line");
        if (lineList.getLength() != height) {
            throw new NonogramFormatException(
                    "unable to load field values, because it has the wrong number of lines");
        }

        for (int y = 0; y < lineList.getLength(); y++) {

            // TODO check if lineList.getLength() == height
            Element line = (Element) lineList.item(y);

            String str = line.getTextContent();
            StringTokenizer tokenizer = new StringTokenizer(str, " ");
            if (tokenizer.countTokens() != width) {
                throw new NonogramFormatException(
                        "unable to load field values, because it has the wrong number of columns");
            }

            for (int x = 0; tokenizer.hasMoreTokens(); x++) {
                tmp = tokenizer.nextToken();
                if (tmp.length() > 1) {
                    throw new NonogramFormatException(
                            "unable to load field values, because it has an invalid format");
                }
                field[y][x] = getFieldValue(tmp.charAt(0));
            }
        }

        try {
            nonogram = new Nonogram(name, diff, field);
            nonogram.setDescription(desc);
            nonogram.setDuration(duration);
            nonogram.setAuthor(author);
            nonogram.setLevel(level);

        } catch (NullPointerException e) {
            throw new NonogramFormatException(
                    "unable to create Nonogram object, due to a parameter problem");
        }

        return nonogram;
    }

    /**
     * Save a list of nonograms to XML file.
     * @param lst
     *            List of nonograms
     * @param doc
     *            XML Document to write to.
     * @param element
     *            xml root element
     */
    private void saveXMLNonograms(final List<Nonogram> lst, final Document doc,
            final Element element) {

        Element nonograms = doc.createElement("Nonograms");
        element.appendChild(nonograms);
        for (Nonogram n : lst) {
            saveXMLNonogram(n, doc, nonograms);
        }
    }

    /**
     * Save a single nonogram to XML file.
     * @param n
     *            Nonogram
     * @param doc
     *            XML document to write to
     * @param nonograms
     *            XML root element of this nonogram
     */
    private void saveXMLNonogram(final Nonogram n, final Document doc,
            final Element nonograms) {

        Element nonogram = doc.createElement("Nonogram");
        nonograms.appendChild(nonogram);
        nonogram.setAttribute("name", n.getName());
        nonogram.setAttribute("height", Integer.toString(n.height()));
        nonogram.setAttribute("width", Integer.toString(n.width()));
        nonogram.setAttribute("difficulty",
                Integer.toString(n.getDifficulty().ordinal()));
        nonogram.setAttribute("duration", Long.toString(n.getDuration()));
        nonogram.setAttribute("level", Integer.toString(n.getLevel()));
        nonogram.setAttribute("desc", n.getDescription());
        nonogram.setAttribute("author", n.getAuthor());

        String s;
        for (int y = 0; y < n.height(); y++) {
            Element line = doc.createElement("line");
            nonogram.appendChild(line);
            s = " ";
            for (int x = 0; x < n.width(); x++) {
                s += getFieldChar(n.getFieldValue(x, y));
                s += " ";
            }
            line.setTextContent(s);
        }
    }

    /**
     * Singleton to get a XML validator for nonograms.
     * @return XML Validator
     * @throws SAXException
     *             If validator could not be created.
     */
    private Validator getXMLValidator() throws SAXException {

        // TODO reset error handler flags here

        if (validator == null) {
            SchemaFactory schemaFactory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setErrorHandler(errorHandler);
            Schema schemaXSD = schemaFactory
                    .newSchema(XMLNonogramSerializer.class
                            .getResource("/resources/xsd/nonogram.xsd"));

            validator = schemaXSD.newValidator();
        }
        return validator;
    }

    /*
     * static helpers
     */

    /**
     * Gets the boolean value for specified char values.
     * 
     * <br>
     * FIELD_FREE_CHAR -> false
     * 
     * <br>
     * FIELD_OCCUPIED_CHAR -> true
     * 
     * @param c
     *            char to get value for
     * @return true, if field is occupied and false, if field is free
     * @throws NonogramFormatException
     *             if char is not one of the specified
     */
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
     * 
     * @param b
     *            boolean to use
     * @return character representing field value
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
