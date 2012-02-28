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
package de.ichmann.markusw.java.apps.nonogram.serializer.highscore;

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

public class XMLHighscoreSerializer {

//	private static final char FIELD_FREE_CHAR = '_';
//	private static final char FIELD_OCCUPIED_CHAR = 'x';
//
//	public static final String DEFAULT_FILE_EXTENSION = "nonogram";
//
//	private static Logger logger = Logger.getLogger(XMLHighscoreSerializer.class);
//
//	private static ErrorHandler errorHandler = new ErrorHandler() {
//
//		// TODO ad error handling here?
//
//		@Override
//		public void warning(SAXParseException exception) throws SAXException {
//			// TODO Auto-generated method stub
//		}
//
//		@Override
//		public void fatalError(SAXParseException exception) throws SAXException {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void error(SAXParseException exception) throws SAXException {
//			// TODO Auto-generated method stub
//
//		}
//	};
//
//
//
//	/* XML file handling */
//
//	public static Highscores loadHighscores(File f)
//	throws InvalidFormatException, IOException {
//
//		try {
//
//			FileInputStream is = new FileInputStream(f);
//
//			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
//			.newDocumentBuilder();
//			Document doc = parser.parse(is);
//
//			// TODO check, why this will cause an error
//			Validator validator = getXMLValidator();
//			validator.validate(new DOMSource(doc));
//
//			Element root = doc.getDocumentElement();
//
//			Highscores h = loadXMLHighscores(root);
//			return h;
//
//		} catch (SAXException e) {
//			// TODO handle exception, add log message here
//			throw new InvalidFormatException(
//					"unable to load file, because a SAX error occured", e);
//		} catch (ParserConfigurationException e) {
//			// TODO handle exception, add log message here
//			throw new InvalidFormatException(
//					"unable to load file, because a parser error occured", e);
//		}
//	}
//
//	public static void saveHighscores(Highscores h, File f) throws IOException {
//		// TODO implement here
//
//		try {
//
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//			.newDocumentBuilder();
//			Document doc = builder.newDocument();
//
//			Element root = doc.createElement("FreeNono");
//			doc.appendChild(root);
//
//			saveXMLHighscores(h, doc, root);
//
//			Source source = new DOMSource(doc);
//			Result result = new StreamResult(f);
//
//			Transformer tf = TransformerFactory.newInstance().newTransformer();
//			tf.setOutputProperty(OutputKeys.INDENT, "yes");
//			tf.transform(source, result);
//
//		} catch (ParserConfigurationException e) {
//			// TODO handle exception, add log message here
//			throw new IOException(
//					"unable to save file, because no parser could be created",
//					e);
//		} catch (TransformerException e) {
//			// TODO handle exception, add log message here
//			throw new IOException(
//					"unable to save file, because no parser could be created",
//					e);
//		}
//	}
//
//
//
//	/* Highscore helper methods */
//
//	private static Highscores loadXMLHighscores(Element root)
//	throws InvalidFormatException {
//
//		Highscores retObj = null;
//
//		Element highscores = (Element) root.getElementsByTagName("Highscores")
//		.item(0);
//		if (highscores != null) {
//
//			retObj = new Highscores();
//			NodeList highscoreList = highscores
//			.getElementsByTagName("Highscore");
//
//			for (int i = 0; i < highscoreList.getLength(); i++) {
//				Element highscore = (Element) highscoreList.item(i);
//				loadXMLHighscore(retObj, highscore);
//			}
//		}
//
//		return retObj;
//	}
//
//	private static void loadXMLHighscore(Highscores highscores, Element element)
//	throws InvalidFormatException {
//
//		String tmp;
//		String nonogram = element.getAttribute("nonogram");
//		long time = 0;
//		int score = 0;
//
//		try {
//			tmp = element.getAttribute("time");
//			time = Long.parseLong(tmp);
//		} catch (NumberFormatException e) {
//
//			// TODO add log message
//			throw new InvalidFormatException(
//					"unable to load highscore, because time has an invalid format",
//					e);
//		}
//
//		try {
//			tmp = element.getAttribute("score");
//			score = Integer.parseInt(tmp);
//		} catch (NumberFormatException e) {
//			throw new InvalidFormatException(
//					"unable to load highscore, because score has an invalid format",
//					e);
//		}
//
//		highscores.addHighscore(nonogram, score, time);
//	}
//
//	private static void saveXMLHighscores(Highscores h, Document doc,
//			Element element) throws DOMException {
//
//		Element highscores = doc.createElement("Highscores");
//		element.appendChild(highscores);
//
//		for (int i = 0; i < h.count(); i++) {
//			saveXMLHighscore(h.getNonogram(i), h.getTime(i), h.getScore(i),
//					doc, highscores);
//		}
//
//	}
//
//	private static void saveXMLHighscore(String nonogram, long time, int score,
//			Document doc, Element highscores) throws DOMException {
//
//		Element highscore = doc.createElement("Highscore");
//		highscores.appendChild(highscore);
//		highscore.setAttribute("nonogram", nonogram);
//		highscore.setAttribute("time", Long.toString(time));
//		highscore.setAttribute("score", Integer.toString(score));
//
//	}
//
//
//
//	/* other helper methods */
//
//	private static Validator getXMLValidator() throws SAXException {
//
//		// TODO implement error handler with a valid flag
//		// TODO reset error handler flags here
//
//		SchemaFactory schemaFactory = SchemaFactory
//		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//		schemaFactory.setErrorHandler(errorHandler);
//		Schema schemaXSD = schemaFactory.newSchema(XMLSettingsSerializer.class
//				.getResource("/xsd/highscore.xsd"));
//
//		Validator validator = schemaXSD.newValidator();
//		return validator;
//	}
}
