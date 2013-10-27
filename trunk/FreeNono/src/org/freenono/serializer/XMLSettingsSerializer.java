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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

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
import org.freenono.controller.ControlSettings;
import org.freenono.controller.ControlSettings.Control;
import org.freenono.controller.Settings;
import org.freenono.model.game_modes.GameModeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Serializes FreeNono settings as xml file.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public final class XMLSettingsSerializer implements SettingsSerializer {

    public static final String DEFAULT_FILE_EXTENSION = "settings";

    private static Logger logger = Logger
            .getLogger(XMLSettingsSerializer.class);

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
    public Settings load(final File f) throws SettingsFormatException {

        Settings s = null;

        try {

            FileInputStream is = new FileInputStream(f);

            DocumentBuilder parser = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = parser.parse(is);

            Validator validator = getXMLValidator();
            validator.validate(new DOMSource(doc));

            Element root = doc.getDocumentElement();

            s = loadXMLSettings(root);

            logger.info("Settings loaded successfully from file " + f.getName());

        } catch (SAXException e) {
            logger.warn("SAXException when loading settings file.");
            throw new SettingsFormatException(
                    "unable to load file, because a SAX error occured");

        } catch (ParserConfigurationException e) {
            logger.warn("ParserConfigurationException in save()");
            throw new SettingsFormatException(
                    "unable to load file, because a parser error occured");

        } catch (FileNotFoundException e) {
            if (s == null) {
                s = new Settings();
            }
            logger.warn("Could not load settings file. Using default settings!");

        } catch (IOException e) {
            if (s == null) {
                s = new Settings();
            }
            logger.warn("Could not load settings file. Using default settings!");
        }

        return s;
    }

    /*
     * save methods
     */

    @Override
    public void save(final Settings s, final File f) {

        try {

            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("FreeNono");
            doc.appendChild(root);

            saveXMLSettings(s, doc, root);

            Source source = new DOMSource(doc);
            Result result = new StreamResult(f);

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(source, result);

            logger.info("Settings saved successfully in file " + f.getName());

        } catch (ParserConfigurationException e) {
            logger.warn("unable to save file, because no parser could be created");

        } catch (TransformerException e) {
            logger.warn("unable to save file, because no parser could be created");

        }
    }

    /*
     * Setting helper methods
     */

    /**
     * Loads settings from a xml document.
     * 
     * @param root
     *            xml root element
     * @return settings object
     * @throws SettingsFormatException
     *             if settings file has wrong file format
     */
    private Settings loadXMLSettings(final Element root)
            throws SettingsFormatException {

        Settings retObj = null;

        Element settings = (Element) root.getElementsByTagName("Settings")
                .item(0);
        if (settings != null) {

            retObj = new Settings();
            NodeList settingList = settings.getElementsByTagName("Setting");

            for (int i = 0; i < settingList.getLength(); i++) {
                Element setting = (Element) settingList.item(i);
                loadXMLSetting(retObj, setting);
            }

        }
        return retObj;
    }

    /**
     * Loads a single setting from a xml document.
     * 
     * @param settings
     *            settings object to store setting in
     * @param element
     *            element to parse
     * @throws SettingsFormatException
     *             if settings file has wrong file format
     */
    @SuppressWarnings("deprecation")
    private void loadXMLSetting(final Settings settings, final Element element)
            throws SettingsFormatException {

        try {

            String name = element.getAttribute("name");
            String value = element.getAttribute("value");

            ControlSettings ct = settings.getControlSettings();

            if ("MaxFailCount".equals(name)) {
                settings.setMaxFailCount(Integer.parseInt(value));
            } else if ("UseMaxFailCount".equals(name)) {
                settings.setUseMaxFailCount(Boolean.parseBoolean(value));
            } else if ("MaxTime".equals(name)) {
                settings.setMaxTime(Long.parseLong(value));
            } else if ("UseMaxTime".equals(name)) {
                settings.setUseMaxTime(Boolean.parseBoolean(value));
            } else if ("MarkInvalidMoves".equals(name)) {
                settings.setMarkInvalid(Boolean.parseBoolean(value));
            } else if ("CountMarkedFields".equals(name)) {
                settings.setCountMarked(Boolean.parseBoolean(value));
            } else if ("PlayAudio".equals(name)) {
                settings.setPlayAudio(Boolean.parseBoolean(value));
            } else if ("PlayMusic".equals(name)) {
                settings.setPlayMusic(Boolean.parseBoolean(value));
            } else if ("PlayEffects".equals(name)) {
                settings.setPlayEffects(Boolean.parseBoolean(value));
            } else if ("HidePlayfieldAtPause".equals(name)) {
                settings.setHidePlayfield(Boolean.parseBoolean(value));
            } else if ("CrossOutCaptions".equals(name)) {
                settings.setCrossCaptions(Boolean.parseBoolean(value));
            } else if ("MarkCompleteRowsColumns".equals(name)) {
                settings.setMarkCompleteRowsColumns(Boolean.parseBoolean(value));
            } else if ("ShowNonogramName".equals(name)) {
                settings.setShowNonogramName(Boolean.parseBoolean(value));
            } else if ("GameMode".equals(name)) {
                settings.setGameMode(GameModeType.valueOf(value));
            } else if ("ControlLeft".equals(name)) {
                ct.setControl(Control.MOVE_LEFT, Integer.parseInt(value));
            } else if ("ControlRight".equals(name)) {
                ct.setControl(Control.MOVE_RIGHT, Integer.parseInt(value));
            } else if ("ControlUp".equals(name)) {
                ct.setControl(Control.MOVE_UP, Integer.parseInt(value));
            } else if ("ControlDown".equals(name)) {
                ct.setControl(Control.MOVE_DOWN, Integer.parseInt(value));
            } else if ("ControlMark".equals(name)) {
                ct.setControl(Control.MARK_FIELD, Integer.parseInt(value));
            } else if ("ControlOccupy".equals(name)) {
                ct.setControl(Control.OCCUPY_FIELD, Integer.parseInt(value));
            } else if ("BaseColor".equals(name)) {
                settings.setBaseColor(new Color(Integer.parseInt(value)));
            } else if ("GameLocale".equals(name)) {
                settings.setGameLocale(new Locale(value));
            } else if ("AskForPlayerName".equals(name)) {
                settings.setAskForPlayerName(Boolean.parseBoolean(value));
            } else if ("PlayerName".equals(name)) {
                settings.setPlayerName(value);
            } else if ("SearchForUpdates".equals(name)) {
                settings.setSearchForUpdates(Boolean.parseBoolean(value));
            }

        } catch (NumberFormatException e) {

            // value parameter doesn't contain a valid setting value
            logger.debug("Unable to load setting, because the value has an invalid format");

            throw new SettingsFormatException(
                    "unable to load setting, because the value has an invalid format");
        }
    }

    /**
     * Saves settings into a xml settings file.
     * 
     * @param s
     *            settings object to be saved
     * @param doc
     *            xml document
     * @param element
     *            xml root element
     */
    @SuppressWarnings("deprecation")
    private void saveXMLSettings(final Settings s, final Document doc,
            final Element element) {

        Element settings = doc.createElement("Settings");
        element.appendChild(settings);

        ControlSettings ct = s.getControlSettings();

        saveXMLSetting("MaxFailCount", Integer.toString(s.getMaxFailCount()),
                doc, settings);
        saveXMLSetting("UseMaxFailCount",
                Boolean.toString(s.getUseMaxFailCount()), doc, settings);
        saveXMLSetting("MaxTime", Long.toString(s.getMaxTime()), doc, settings);
        saveXMLSetting("UseMaxTime", Boolean.toString(s.getUseMaxTime()), doc,
                settings);
        saveXMLSetting("MarkInvalidMoves",
                Boolean.toString(s.getMarkInvalid()), doc, settings);
        saveXMLSetting("CountMarkedFields",
                Boolean.toString(s.getCountMarked()), doc, settings);
        saveXMLSetting("PlayAudio", Boolean.toString(s.getPlayAudio()), doc,
                settings);
        saveXMLSetting("PlayMusic", Boolean.toString(s.isPlayMusic()), doc,
                settings);
        saveXMLSetting("PlayEffects", Boolean.toString(s.isPlayEffects()), doc,
                settings);
        saveXMLSetting("HidePlayfieldAtPause",
                Boolean.toString(s.getHidePlayfield()), doc, settings);
        saveXMLSetting("CrossOutCaptions",
                Boolean.toString(s.getCrossCaptions()), doc, settings);
        saveXMLSetting("MarkCompleteRowsColumns",
                Boolean.toString(s.getMarkCompleteRowsColumns()), doc, settings);
        saveXMLSetting("ShowNonogramName",
                Boolean.toString(s.isShowNonogramName()), doc, settings);
        saveXMLSetting("GameMode", s.getGameMode().name(), doc, settings);
        saveXMLSetting("ControlLeft",
                Integer.toString(ct.getControl(Control.MOVE_LEFT)), doc,
                settings);
        saveXMLSetting("ControlRight",
                Integer.toString(ct.getControl(Control.MOVE_RIGHT)), doc,
                settings);
        saveXMLSetting("ControlUp",
                Integer.toString(ct.getControl(Control.MOVE_UP)), doc, settings);
        saveXMLSetting("ControlDown",
                Integer.toString(ct.getControl(Control.MOVE_DOWN)), doc,
                settings);
        saveXMLSetting("ControlMark",
                Integer.toString(ct.getControl(Control.MARK_FIELD)), doc,
                settings);
        saveXMLSetting("ControlOccupy",
                Integer.toString(ct.getControl(Control.OCCUPY_FIELD)), doc,
                settings);
        saveXMLSetting("BaseColor",
                Integer.toString(s.getBaseColor().getRGB()), doc, settings);
        saveXMLSetting("GameLocale", s.getGameLocale().toString(), doc,
                settings);
        saveXMLSetting("AskForPlayerName",
                Boolean.toString(s.shouldAskForPlayerName()), doc, settings);
        saveXMLSetting("PlayerName", s.getPlayerName(), doc, settings);
        saveXMLSetting("SearchForUpdates",
                Boolean.toString(s.shouldSearchForUpdates()), doc, settings);
    }

    /**
     * Saves a single setting as xml.
     * 
     * @param name
     *            name of setting to be saved
     * @param value
     *            value of setting to be saved
     * @param doc
     *            xml document
     * @param settings
     *            xml root element
     */
    private void saveXMLSetting(final String name, final String value,
            final Document doc, final Element settings) {

        Element setting = doc.createElement("Setting");
        settings.appendChild(setting);
        setting.setAttribute("name", name);
        setting.setAttribute("value", value);
    }

    /*
     * other helper methods
     */

    /**
     * Returns a validator to check settings xml file.
     * 
     * @return validator to check file
     * @throws SAXException
     *             if sax error occurs during parsing
     */
    private Validator getXMLValidator() throws SAXException {

        // TODO implement error handler with a valid flag
        // TODO reset error handler flags here

        if (validator == null) {
            SchemaFactory schemaFactory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setErrorHandler(errorHandler);
            Schema schemaXSD = schemaFactory
                    .newSchema(XMLSettingsSerializer.class
                            .getResource("/resources/xsd/settings.xsd"));

            validator = schemaXSD.newValidator();
        }
        return validator;
    }
}
