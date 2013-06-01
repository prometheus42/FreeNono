/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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
import org.freenono.controller.ControlSettings;
import org.freenono.controller.ControlSettings.Control;
import org.freenono.controller.Settings;
import org.freenono.model.GameModeType;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author Markus Wichmann
 *
 */
public class XMLSettingsSerializer implements SettingsSerializer {

	public static final String DEFAULT_FILE_EXTENSION = "settings";

	private static Logger logger = Logger.getLogger(XMLSettingsSerializer.class);

	private ErrorHandler errorHandler = new ErrorHandler() {

		// TODO add error handling here?

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub

		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			// TODO Auto-generated method stub

		}
	};

	private Validator validator = null;
	


	/* load methods */

	public Settings load(File f) throws NullPointerException, IOException, SettingsFormatException {

		try {

			FileInputStream is = new FileInputStream(f);

			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			Document doc = parser.parse(is);

			Validator validator = getXMLValidator();
			validator.validate(new DOMSource(doc));

			Element root = doc.getDocumentElement();

			Settings s = loadXMLSettings(root);
			
			logger.info("Settings loaded successfully from file " + f.getName());
			
			return s;

		} catch (SAXException e) {
			logger.warn("SAXException in save()");
			throw new SettingsFormatException("unable to load file, because a SAX error occured");
		} catch (ParserConfigurationException e) {
			logger.warn("ParserConfigurationException in save()");
			throw new SettingsFormatException("unable to load file, because a parser error occured");
		}
	}

	
	
	/* save methods */
	
	public void save(Settings s, File f) throws NullPointerException, IOException {

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
			logger.warn("ParserConfigurationException in save()");
			throw new IOException("unable to save file, because no parser could be created", e);
		} catch (TransformerException e) {
			logger.warn("TransformerException in save()");
			throw new IOException("unable to save file, because no parser could be created", e);
		}
	}

	

	/* Setting helper methods */

	private Settings loadXMLSettings(Element root)
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

	private void loadXMLSetting(Settings settings, Element element)
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
			} else if ("HidePlayfieldAtPause".equals(name)) {
				settings.setHidePlayfield(Boolean.parseBoolean(value));
			} else if ("ShowNonogramName".equals(name)) {
				settings.setShowNonogramName(Boolean.parseBoolean(value));
			} else if ("GameMode".equals(name)) {
				settings.setGameMode(GameModeType.valueOf(value));
			} else if ("ControlLeft".equals(name)) {
				ct.setControl(Control.moveLeft, Integer.parseInt(value));
			} else if ("ControlRight".equals(name)) {
				ct.setControl(Control.moveRight, Integer.parseInt(value));
			} else if ("ControlUp".equals(name)) {
				ct.setControl(Control.moveUp, Integer.parseInt(value));
			} else if ("ControlDown".equals(name)) {
				ct.setControl(Control.moveDown, Integer.parseInt(value));
			} else if ("ControlMark".equals(name)) {
				ct.setControl(Control.markField, Integer.parseInt(value));
			} else if ("ControlOccupy".equals(name)) {
				ct.setControl(Control.occupyField, Integer.parseInt(value));
			} else if ("BaseColor".equals(name)) {
				settings.setBaseColor(new Color(Integer.parseInt(value)));
			}

		} catch (NumberFormatException e) {
			// value parameter doesn't contain a valid setting value
			// TODO handle exception, add log message here
			throw new SettingsFormatException("unable to load setting, because the value has an invalid format");
		}
	}

	private void saveXMLSettings(Settings s, Document doc, Element element) throws DOMException {

		Element settings = doc.createElement("Settings");
		element.appendChild(settings);
		
		ControlSettings ct = s.getControlSettings(); 

		saveXMLSetting("MaxFailCount", Integer.toString(s.getMaxFailCount()), doc, settings);
		saveXMLSetting("UseMaxFailCount", Boolean.toString(s.getUseMaxFailCount()), doc, settings);
		saveXMLSetting("MaxTime", Long.toString(s.getMaxTime()), doc, settings);
		saveXMLSetting("UseMaxTime", Boolean.toString(s.getUseMaxTime()), doc, settings);
		saveXMLSetting("MarkInvalidMoves", Boolean.toString(s.getMarkInvalid()), doc, settings);
		saveXMLSetting("CountMarkedFields", Boolean.toString(s.getCountMarked()), doc, settings);
		saveXMLSetting("PlayAudio", Boolean.toString(s.getPlayAudio()), doc, settings);
		saveXMLSetting("HidePlayfieldAtPause", Boolean.toString(s.getHidePlayfield()), doc, settings);
		saveXMLSetting("ShowNonogramName", Boolean.toString(s.isShowNonogramName()), doc, settings);
		saveXMLSetting("GameMode", s.getGameMode().name(), doc, settings);
		saveXMLSetting("ControlLeft", Integer.toString(ct.getControl(Control.moveLeft)), doc, settings);
		saveXMLSetting("ControlRight", Integer.toString(ct.getControl(Control.moveRight)), doc, settings);
		saveXMLSetting("ControlUp", Integer.toString(ct.getControl(Control.moveUp)), doc, settings);
		saveXMLSetting("ControlDown", Integer.toString(ct.getControl(Control.moveDown)), doc, settings);
		saveXMLSetting("ControlMark", Integer.toString(ct.getControl(Control.markField)), doc, settings);
		saveXMLSetting("ControlOccupy", Integer.toString(ct.getControl(Control.occupyField)), doc, settings);
		saveXMLSetting("BaseColor", Integer.toString(s.getBaseColor().getRGB()), doc, settings);

	}

	private void saveXMLSetting(String name, String value, Document doc,
			Element settings) throws DOMException {

		Element setting = doc.createElement("Setting");
		settings.appendChild(setting);
		setting.setAttribute("name", name);
		setting.setAttribute("value", value);

	}



	/* other helper methods */
	
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
