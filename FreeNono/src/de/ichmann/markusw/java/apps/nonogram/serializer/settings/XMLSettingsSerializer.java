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
package de.ichmann.markusw.java.apps.nonogram.serializer.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.ichmann.markusw.java.apps.nonogram.exception.InvalidFormatException;
import de.ichmann.markusw.java.apps.nonogram.exception.ParameterException;
import de.ichmann.markusw.java.apps.nonogram.model.Highscores;
import de.ichmann.markusw.java.apps.nonogram.model.Nonogram;
import de.ichmann.markusw.java.apps.nonogram.model.Settings;

public class XMLSettingsSerializer implements SettingsSerializer {

	public static final String DEFAULT_FILE_EXTENSION = "settings";

	private static Logger logger = Logger.getLogger(XMLSettingsSerializer.class);

	private ErrorHandler errorHandler = new ErrorHandler() {

		// TODO ad error handling here?

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



	/* XML file handling */

	public Settings loadSettings(File f) throws InvalidFormatException,
	IOException {

		try {

			FileInputStream is = new FileInputStream(f);

			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			Document doc = parser.parse(is);

			// TODO check, why this will cause an error
			Validator validator = getXMLValidator();
			validator.validate(new DOMSource(doc));

			Element root = doc.getDocumentElement();

			Settings s = loadXMLSettings(root);
			return s;

		} catch (SAXException e) {
			// TODO handle exception, add log message here
			throw new InvalidFormatException(
					"unable to load file, because a SAX error occured", e);
		} catch (ParserConfigurationException e) {
			// TODO handle exception, add log message here
			throw new InvalidFormatException(
					"unable to load file, because a parser error occured", e);
		}
	}

	public void saveSettings(Settings s, File f) throws IOException {
		// TODO implement here

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

		} catch (ParserConfigurationException e) {
			// TODO handle exception, add log message here
			throw new IOException(
					"unable to save file, because no parser could be created",
					e);
		} catch (TransformerException e) {
			// TODO handle exception, add log message here
			throw new IOException(
					"unable to save file, because no parser could be created",
					e);
		}
	}



	/* Setting helper methods */

	private Settings loadXMLSettings(Element root)
	throws InvalidFormatException {

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
	throws InvalidFormatException {

		try {

			String name = element.getAttribute("name");
			String value = element.getAttribute("value");

			if ("MaxFailCount".equals(name)) {
				settings.setMaxTime(Integer.parseInt(value));
			} else if ("MaxTime".equals(name)) {
				settings.setMaxTime(Long.parseLong(value));
			} else if ("MarkInvalidMoves".equals(name)) {
				settings.setMarkInvalid(Boolean.parseBoolean(value));
			} else if ("CountMarkedFields".equals(name)) {
				settings.setCountMarked(Boolean.parseBoolean(value));
			}

		} catch (NumberFormatException e) {
			// value parameter doesn't contain a valid setting value
			// TODO handle exception, add log message here
			throw new InvalidFormatException(
					"unable to load setting, because the value has an invalid format",
					e);
		}
	}

	private void saveXMLSettings(Settings s, Document doc,
			Element element) throws DOMException {

		Element settings = doc.createElement("Settings");
		element.appendChild(settings);

		saveXMLSetting("MaxFailCount", Integer.toString(s.getMaxFailCount()),
				doc, settings);
		saveXMLSetting("MaxTime", Long.toString(s.getMaxTime()), doc, settings);
		saveXMLSetting("MarkInvalidMoves",
				Boolean.toString(s.getMarkInvalid()), doc, settings);
		saveXMLSetting("CountMarkedFields", Boolean
				.toString(s.getCountMarked()), doc, settings);

	}

	private void saveXMLSetting(String name, String value, Document doc,
			Element settings) throws DOMException {

		Element setting = doc.createElement("Setting");
		settings.appendChild(setting);
		setting.setAttribute("id", name);
		setting.setAttribute("name", value);

	}



	/* other helper methods */

	private Validator getXMLValidator() throws SAXException {

		// TODO implement error handler with a valid flag
		// TODO reset error handler flags here

		SchemaFactory schemaFactory = SchemaFactory
		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setErrorHandler(errorHandler);
		Schema schemaXSD = schemaFactory.newSchema(XMLSettingsSerializer.class
				.getResource("/xsd/settings.xsd"));

		Validator validator = schemaXSD.newValidator();
		return validator;
	}
}
