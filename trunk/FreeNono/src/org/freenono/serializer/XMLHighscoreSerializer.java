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
import java.io.IOException;

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
import org.freenono.controller.Highscores;
import org.freenono.controller.Score;
import org.freenono.model.game_modes.GameModeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Serializes a list of highscores for different game modes including information like player names,
 * dates, scores, etc.
 * 
 * @author Christian Wichmann
 */
public final class XMLHighscoreSerializer {

    private static Logger logger = Logger.getLogger(XMLHighscoreSerializer.class);

    /**
     * Private constructor so static utility class can not externally be instantiated.
     */
    private XMLHighscoreSerializer() {
    }

    /*
     * Load methods
     */

    /**
     * Loads highscore data from a given file.
     * 
     * @param f
     *            file containing highscore data
     * @return <code>Highscore</code> object containing data
     * @throws HighscoreFormatException
     *             if file format is not valid or could not be read
     */
    public static Highscores loadHighscores(final File f) throws HighscoreFormatException {

        if (f == null) {
            throw new IllegalArgumentException("File argument should not be null.");
        }
        if (f.isDirectory()) {
            throw new IllegalArgumentException("File argument should not be a directory.");
        }

        if (!f.exists()) {
            throw new HighscoreFormatException("No data was loaded because file argument points to a not existing file.");
        }

        logger.debug("Loading highscore data from file...");

        Highscores h = null;

        try {
            final FileInputStream is = new FileInputStream(f);

            final DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = parser.parse(is);

            final Validator validator = getXMLValidator();
            validator.validate(new DOMSource(doc));

            final Element root = doc.getDocumentElement();

            h = loadXMLHighscores(root);

        } catch (SAXException e) {
            throw new HighscoreFormatException("unable to load file, because a SAX error occured");

        } catch (ParserConfigurationException e) {
            throw new HighscoreFormatException("unable to load file, because a parser error occured");

        } catch (FileNotFoundException e) {
            logger.warn("Highscore file could not be found.");

        } catch (IOException e) {
            logger.error("Highscore file could not be loaded.");

        } finally {
            if (h == null) {
                h = new Highscores();
            }
        }

        logger.debug("Loaded highscore data from file.");

        assert h != null;
        return h;
    }

    /**
     * Loads highscore data from a xml tree.
     * 
     * @param root
     *            root of xml tree
     * @return <code>Highscore</code> object containing data
     * @throws HighscoreFormatException
     *             if file format is not valid or could not be read
     */
    private static Highscores loadXMLHighscores(final Element root) throws HighscoreFormatException {

        assert root != null;

        Highscores loadedHighscores = null;

        final Element highscores = (Element) root.getElementsByTagName("Highscores").item(0);
        if (highscores != null) {

            loadedHighscores = new Highscores();
            final NodeList highscoreList = highscores.getElementsByTagName("Highscore");

            for (int i = 0; i < highscoreList.getLength(); i++) {
                final Element highscore = (Element) highscoreList.item(i);
                loadXMLHighscore(loadedHighscores, highscore);
            }
        }

        return loadedHighscores;
    }

    /**
     * Loads a single score from highscore file.
     * 
     * @param highscores
     *            <code>Highscore</code> object to load score data into
     * @param element
     *            xml element to load score form
     * @throws HighscoreFormatException
     *             if file format is not valid or could not be read
     */
    private static void loadXMLHighscore(final Highscores highscores, final Element element) throws HighscoreFormatException {

        assert element != null;
        assert highscores != null;

        String tmp;
        String nonogram = "";
        String player = "";
        GameModeType gameMode;
        long time = 0;
        int score = 0;

        // load attributes
        nonogram = element.getAttribute("nonogram");
        player = element.getAttribute("player");

        tmp = element.getAttribute("gamemode");
        gameMode = GameModeType.valueOf(tmp);

        try {
            tmp = element.getAttribute("time");
            time = Long.parseLong(tmp);
        } catch (NumberFormatException e) {
            throw new HighscoreFormatException("unable to load highscore, because time has an invalid format");
        }

        try {
            tmp = element.getAttribute("score");
            score = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
            throw new HighscoreFormatException("unable to load highscore, because score has an invalid format");
        }

        highscores.addScore(nonogram, gameMode, time, player, score);
    }

    /*
     * Save methods
     */

    /**
     * Saves highscore data to xml file.
     * 
     * @param h
     *            <code>Highscore</code> object containing data
     * @param f
     *            file to save highscore data in
     * @throws HighscoreFormatException
     *             if file format is not valid or could not be read
     */
    public static void saveHighscores(final Highscores h, final File f) throws HighscoreFormatException {

        if (h == null) {
            throw new IllegalArgumentException("Highscore argument should not be null.");
        }

        if (f == null) {
            throw new IllegalArgumentException("File argument should not be null.");
        }

        logger.debug("Saving highscore data to file...");

        try {

            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = builder.newDocument();

            final Element root = doc.createElement("FreeNono");
            doc.appendChild(root);

            saveXMLHighscores(h, doc, root);

            final Source source = new DOMSource(doc);
            final Result result = new StreamResult(f);

            final Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(source, result);

            logger.debug("Saved highscore data to file.");

        } catch (ParserConfigurationException e) {
            throw new HighscoreFormatException("unable to save file, because no parser could be created");

        } catch (TransformerException e) {
            throw new HighscoreFormatException("unable to save file, because no parser could be created");
        }
    }

    /**
     * Saves highscore data to a xml tree.
     * 
     * @param h
     *            <code>Highscore</code> object save into file
     * @param doc
     *            xml document to add score data
     * @param element
     *            xml element to append score data
     */
    private static void saveXMLHighscores(final Highscores h, final Document doc, final Element element) {

        final Element highscores = doc.createElement("Highscores");
        element.appendChild(highscores);

        for (Score score : h.getHighscoreList()) {
            saveXMLHighscore(score, doc, highscores);
        }
    }

    /**
     * Saves a single score to highscore file.
     * 
     * @param scoreToSave
     *            score that should be saved
     * @param doc
     *            xml document to add score data
     * @param highscores
     *            xml element to append score data
     */
    private static void saveXMLHighscore(final Score scoreToSave, final Document doc, final Element highscores) {

        final Element highscore = doc.createElement("Highscore");
        highscores.appendChild(highscore);
        highscore.setAttribute("nonogram", scoreToSave.getNonogram());
        highscore.setAttribute("time", Long.toString(scoreToSave.getTime()));
        highscore.setAttribute("score", Integer.toString(scoreToSave.getScoreValue()));
        highscore.setAttribute("gamemode", scoreToSave.getGamemode().name());
        highscore.setAttribute("player", scoreToSave.getPlayer());
    }

    /*
     * Helper methods
     */

    /**
     * Gets validator for highscore xml format.
     * 
     * @return xml validator
     * @throws SAXException
     *             if error occurs during parsing of xsd file
     */
    private static Validator getXMLValidator() throws SAXException {

        // TODO implement better error handling

        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schemaXSD = schemaFactory.newSchema(XMLHighscoreSerializer.class.getResource("/resources/xsd/highscore.xsd"));
        final Validator validator = schemaXSD.newValidator();

        return validator;
    }
}
