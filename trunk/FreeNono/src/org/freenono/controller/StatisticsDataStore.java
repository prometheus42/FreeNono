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
package org.freenono.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.freenono.controller.achievements.Achievement;
import org.freenono.ui.common.Tools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Stores all overall statistical data using a XML file. Access to values is restricted so that
 * values can be read but can only be incremented by one. Arbitrary write access is not allowed.
 * 
 * @author Christian Wichmann
 */
public final class StatisticsDataStore {

    private static Logger logger = Logger.getLogger(StatisticsDataStore.class);

    public static final String USER_STATISTICS_PATH = System.getProperty("user.home") + Tools.FILE_SEPARATOR + ".FreeNono"
            + Tools.FILE_SEPARATOR + "statistics.xml";

    private static StatisticsDataStore instance;
    private String currentStatisticsFile = "";

    /**
     * Holds statistical data for a single nonogram pattern identified by its hash value. For each
     * nonogram is stored, how many times it was played (started by the user), lost and won. Values
     * can only ones be set by the default constructor and incremented by one.
     * 
     * @author Christian Wichmann
     */
    private class NonogramStatistics {

        private int played = 0;
        private int won = 0;
        private int lost = 0;

        /**
         * Initializes a <code>NonogramStatistics</code> instance for a new nonogram pattern.
         * 
         * @param played
         *            number of times the nonogram was played
         * @param won
         *            number of times the nonogram was won
         * @param lost
         *            number of times the nonogram was lost
         */
        public NonogramStatistics(final int played, final int won, final int lost) {

            this.played = played;
            this.lost = lost;
            this.won = won;
        }

        /**
         * Gets number of times the nonogram was played.
         * 
         * @return number of times the nonogram was played
         */
        public final int getPlayed() {
            return played;
        }

        /**
         * Increment number of times the nonogram was played by one.
         */
        public final void incrementPlayed() {
            played++;
        }

        /**
         * Gets number of times the nonogram was won.
         * 
         * @return number of times the nonogram was won
         */
        public final int getWon() {
            return won;
        }

        /**
         * Increment number of times the nonogram was won by one.
         */
        public final void incrementWon() {
            won++;
        }

        /**
         * Gets number of times the nonogram was lost.
         * 
         * @return number of times the nonogram was lost
         */
        public final int getLost() {
            return lost;
        }

        /**
         * Increment number of times the nonogram was lost by one.
         */
        public final void incrementLost() {
            lost++;
        }
    }

    private int overallFieldsCorrectlyOccupied = 0;
    private int overallFieldsWronglyOccupied = 0;
    private int overallFieldsMarked = 0;
    private final Map<String, NonogramStatistics> listOfStatistics = new HashMap<String, NonogramStatistics>();
    private final Map<Achievement, Boolean> achievementAccomplishment = new HashMap<Achievement, Boolean>();

    /**
     * Hide utility class constructor.
     */
    private StatisticsDataStore() {

        // initialize map of all achievements with false
        for (Achievement achievement : Achievement.values()) {
            achievementAccomplishment.put(achievement, false);
        }
    }

    /**
     * Returns an instance of the {@link StatisticsDataStore} to access a file with statistical
     * information in XML format.
     * 
     * @param path
     *            path to file with statistical data
     * @return instance of <code>StatisticsDataStore</code>
     */
    public static StatisticsDataStore getInstance(final String path) {

        if (instance == null) {

            instance = new StatisticsDataStore();
        }

        if (!instance.currentStatisticsFile.equals(path)) {

            instance.currentStatisticsFile = path;
            instance.loadStatisticsFromFile(new File(path));
        }

        return instance;
    }

    /**
     * Returns an instance of the {@link StatisticsDataStore} to access the default file with
     * statistical information in XML format.
     * 
     * @return instance of <code>StatisticsDataStore</code>
     */
    public static StatisticsDataStore getInstance() {

        if (instance == null) {
            instance = new StatisticsDataStore();
            instance.currentStatisticsFile = USER_STATISTICS_PATH;
            instance.loadStatisticsFromFile(new File(USER_STATISTICS_PATH));
        }

        return instance;
    }

    /**
     * Loads statistical data from a given file.
     * 
     * @param statisticsFile
     *            file to load statistical data from
     */
    private void loadStatisticsFromFile(final File statisticsFile) {

        if (statisticsFile == null) {
            throw new IllegalArgumentException("File argument should not be null.");
        }
        if (statisticsFile.isDirectory()) {
            throw new IllegalArgumentException("File argument should not be a directory.");
        }

        if (statisticsFile.exists()) {
            logger.debug("Loading statistical data from file...");

            try {
                final FileInputStream is = new FileInputStream(statisticsFile);
                final DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document doc = parser.parse(is);

                final Validator validator = getXMLValidator();
                validator.validate(new DOMSource(doc));

                final Element root = doc.getDocumentElement();

                /*
                 * Get statistical data.
                 */

                final Node statistics = root.getElementsByTagName("Statistics").item(0);
                final NodeList statisticalData = statistics.getChildNodes();

                for (int i = 0; i < statisticalData.getLength(); i++) {
                    final Node temp = statisticalData.item(i);

                    if ("OverallMarked".equals(temp.getNodeName())) {
                        overallFieldsMarked = Integer.valueOf(temp.getTextContent());

                    } else if ("OverallWronglyOccupied".equals(temp.getNodeName())) {
                        overallFieldsWronglyOccupied = Integer.valueOf(temp.getTextContent());

                    } else if ("OverallCorrectlyOccupied".equals(temp.getNodeName())) {
                        overallFieldsCorrectlyOccupied = Integer.valueOf(temp.getTextContent());

                    } else if ("NonogramStatistics".equals(temp.getNodeName())) {
                        final String nonogramHash = temp.getAttributes().getNamedItem("nonogram").getNodeValue();

                        final int p = Integer.valueOf(temp.getAttributes().getNamedItem("played").getNodeValue());
                        final int w = Integer.valueOf(temp.getAttributes().getNamedItem("won").getNodeValue());
                        final int l = Integer.valueOf(temp.getAttributes().getNamedItem("lost").getNodeValue());
                        listOfStatistics.put(nonogramHash, new NonogramStatistics(p, w, l));
                    }
                }

                /*
                 * Get achievements data.
                 */

                final Node achievements = root.getElementsByTagName("Achievements").item(0);
                if (achievements != null) {
                    final NodeList achievementsData = achievements.getChildNodes();

                    for (int i = 0; i < achievementsData.getLength(); i++) {
                        final Node temp = achievementsData.item(i);

                        if ("AchievementAccomplishment".equals(temp.getNodeName())) {
                            final String type = temp.getAttributes().getNamedItem("type").getNodeValue();
                            final boolean accomplished = Boolean.valueOf(temp.getAttributes().getNamedItem("accomplished").getNodeValue());
                            achievementAccomplishment.put(Achievement.valueOf(type), accomplished);
                        }
                    }
                }

            } catch (SAXException e) {
                logger.warn("Statistics file could not be parsed correctly: " + e.getMessage());

            } catch (ParserConfigurationException e) {
                logger.warn("Statistics file could not be parsed correctly: " + e.getMessage());

            } catch (FileNotFoundException e) {
                logger.warn("Statistics file could not be found.");

            } catch (IOException e) {
                logger.error("Statistics file could not be loaded.");

            }

            logger.debug("Loaded statistical data from file.");
        }
    }

    /**
     * Saves statistical data to the last loaded statistics file.
     */
    public void saveStatisticsToFile() {

        saveStatisticsToFile(new File(currentStatisticsFile));
    }

    /**
     * Saves statistical data to a given file.
     * 
     * @param statisticsFile
     *            file to save statistical data to
     */
    public void saveStatisticsToFile(final File statisticsFile) {

        if (statisticsFile == null) {
            throw new IllegalArgumentException("File argument should not be null.");
        }
        if (statisticsFile.isDirectory()) {
            throw new IllegalArgumentException("File argument should not be a directory.");
        }

        logger.debug("Saving statistical data to file...");

        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = builder.newDocument();

            final Element root = doc.createElement("FreeNono");
            doc.appendChild(root);

            final Element statistics = doc.createElement("Statistics");
            root.appendChild(statistics);

            /*
             * Add overall statistical values.
             */
            final Node overallMarked = doc.createElement("OverallMarked");
            overallMarked.setTextContent(String.valueOf(overallFieldsMarked));
            statistics.appendChild(overallMarked);

            final Node overallWronglyOccupied = doc.createElement("OverallWronglyOccupied");
            overallWronglyOccupied.setTextContent(String.valueOf(overallFieldsWronglyOccupied));
            statistics.appendChild(overallWronglyOccupied);

            final Node overallCorrectlyOccupied = doc.createElement("OverallCorrectlyOccupied");
            overallCorrectlyOccupied.setTextContent(String.valueOf(overallFieldsCorrectlyOccupied));
            statistics.appendChild(overallCorrectlyOccupied);

            /*
             * Add nonogram statistics for all nonograms in list.
             */
            for (Entry<String, NonogramStatistics> entry : listOfStatistics.entrySet()) {
                final Element nextNonogram = doc.createElement("NonogramStatistics");
                nextNonogram.setAttribute("nonogram", entry.getKey());
                nextNonogram.setAttribute("played", String.valueOf(entry.getValue().getPlayed()));
                nextNonogram.setAttribute("won", String.valueOf(entry.getValue().getWon()));
                nextNonogram.setAttribute("lost", String.valueOf(entry.getValue().getLost()));
                statistics.appendChild(nextNonogram);
            }

            /*
             * Add achievement data.
             */
            final Element achievements = doc.createElement("Achievements");
            root.appendChild(achievements);

            for (Achievement achievement : Achievement.values()) {
                // get accomplishment status for all possible values of the enum
                // and store it in the XML file
                final Element nextAchievement = doc.createElement("AchievementAccomplishment");
                nextAchievement.setAttribute("type", achievement.name());

                boolean accomplished;
                if (achievementAccomplishment.containsKey(achievement)) {
                    accomplished = achievementAccomplishment.get(achievement);
                } else {
                    accomplished = false;
                }
                nextAchievement.setAttribute("accomplished", String.valueOf(accomplished));

                achievements.appendChild(nextAchievement);
            }

            final Source source = new DOMSource(doc);
            final Result result = new StreamResult(statisticsFile);

            final Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(source, result);

        } catch (ParserConfigurationException e) {
            logger.warn("Statistics file could not be parsed correctly: " + e.getMessage());

        } catch (TransformerConfigurationException e) {
            logger.warn("Statistics file could not be saved correctly.");

        } catch (TransformerFactoryConfigurationError e) {
            logger.warn("Statistics file could not be saved correctly.");

        } catch (TransformerException e) {
            logger.warn("Statistics file could not be saved correctly.");
        }

        logger.debug("Saved statistical data to file.");
    }

    /**
     * Gets validator for statistical XML format.
     * 
     * @return XML validator
     * @throws SAXException
     *             if error occurs during parsing of xsd file
     */
    private static Validator getXMLValidator() throws SAXException {

        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schemaXSD = schemaFactory.newSchema(StatisticsDataStore.class.getResource("/resources/xsd/statistics.xsd"));
        final Validator validator = schemaXSD.newValidator();

        return validator;
    }

    /**
     * Gets number of times that a specific nonogram has been played (started to be played without
     * winning the game). The nonogram is identified by its hash value.
     * 
     * @param hash
     *            hash of nonogram for which to get number of times played
     * @return number of times played
     */
    public int getTimesPlayedForNonogram(final String hash) {

        final NonogramStatistics temp = listOfStatistics.get(hash);
        if (temp != null) {
            return temp.getPlayed();
        } else {
            return 0;
        }
    }

    /**
     * Gets number of times that a specific nonogram has been won (according to the chosen game
     * mode). The nonogram is identified by its hash value.
     * 
     * @param hash
     *            hash of nonogram for which to get number of times won
     * @return number of times won
     */
    public int getTimesWonForNonogram(final String hash) {

        final NonogramStatistics temp = listOfStatistics.get(hash);
        if (temp != null) {
            return temp.getWon();
        } else {
            return 0;
        }
    }

    /**
     * Gets number of times that a specific nonogram has been lost (according to the chosen game
     * mode). The nonogram is identified by its hash value.
     * 
     * @param hash
     *            hash of nonogram for which to get number of times lost
     * @return number of times lost
     */
    public int getTimesLostForNonogram(final String hash) {

        final NonogramStatistics temp = listOfStatistics.get(hash);
        if (temp != null) {
            return temp.getLost();
        } else {
            return 0;
        }
    }

    /**
     * Increments the number of times that a nonogram was played.
     * 
     * @param hash
     *            hash of nonogram for which to get number of times played
     */
    public void incrementTimesPlayedForNonogram(final String hash) {

        final NonogramStatistics temp = listOfStatistics.get(hash);
        if (temp != null) {
            temp.incrementPlayed();
        } else {
            listOfStatistics.put(hash, new NonogramStatistics(1, 0, 0));
        }
    }

    /**
     * Increments the number of times that a nonogram was won.
     * 
     * @param hash
     *            hash of nonogram for which to get number of times won
     */
    public void incrementTimesWonForNonogram(final String hash) {

        final NonogramStatistics temp = listOfStatistics.get(hash);
        if (temp != null) {
            temp.incrementWon();
        } else {
            listOfStatistics.put(hash, new NonogramStatistics(0, 1, 0));
        }
    }

    /**
     * Increments the number of times that a nonogram was lost.
     * 
     * @param hash
     *            hash of nonogram for which to get number of times lost
     */
    public void incrementTimesLostForNonogram(final String hash) {

        final NonogramStatistics temp = listOfStatistics.get(hash);
        if (temp != null) {
            temp.incrementLost();
        } else {
            listOfStatistics.put(hash, new NonogramStatistics(0, 0, 1));
        }
    }

    /**
     * Gets number of fields that have been correctly occupied <b>ever</b>.
     * 
     * @return number of fields that have been correctly occupied
     */
    public int getFieldsCorrectlyOccupied() {

        return overallFieldsCorrectlyOccupied;
    }

    /**
     * Gets number of fields that have been wrongly occupied <b>ever</b>.
     * 
     * @return number of fields that have been wrongly occupied
     */
    public int getFieldsWronglyOccupied() {

        return overallFieldsWronglyOccupied;
    }

    /**
     * Gets number of fields that have been marked <b>ever</b>.
     * 
     * @return number of fields that have been marked
     */
    public int getFieldsMarked() {

        return overallFieldsMarked;
    }

    /**
     * Increments the number of fields that were correctly occupied.
     */
    public void incrementFieldsCorrectlyOccupied() {

        overallFieldsCorrectlyOccupied++;
    }

    /**
     * Increments the number of fields that were wrongly occupied.
     */
    public void incrementFieldsWronglyOccupied() {

        overallFieldsWronglyOccupied++;
    }

    /**
     * Increments the number of fields that were marked.
     */
    public void incrementFieldsMarked() {

        overallFieldsMarked++;
    }

    /**
     * Gets map with all achievements and the information whether they have been accomplished
     * already.
     * 
     * @return map with all achievements
     */
    public Map<Achievement, Boolean> getAchievementAccomplishment() {

        return Collections.unmodifiableMap(achievementAccomplishment);
    }

    /**
     * Sets the accomplishment status for all achievements as defined in the given map. If a
     * achievement is not present in the given parameter the old value stays the same!
     * 
     * @param achievementAccomplishment
     *            accomplishment status to be set
     */
    public void setAchievementAccomplishment(final Map<Achievement, Boolean> achievementAccomplishment) {

        this.achievementAccomplishment.putAll(achievementAccomplishment);
    }
}
