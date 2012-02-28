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
package de.ichmann.markusw.java.apps.nonogram.serializer.nonogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.ichmann.markusw.java.apps.nonogram.exception.InvalidFormatException;
import de.ichmann.markusw.java.apps.nonogram.exception.ParameterException;
import de.ichmann.markusw.java.apps.nonogram.model.Course;
import de.ichmann.markusw.java.apps.nonogram.model.Nonogram;

public class XMLNonogramSerializer implements NonogramSerializer {

	private static final char FIELD_FREE_CHAR = '_';
	private static final char FIELD_OCCUPIED_CHAR = 'x';

	public static final String DEFAULT_FILE_EXTENSION = "nonogram";

	private static Logger logger = Logger.getLogger(XMLNonogramSerializer.class);

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



	/* public methods */

	@Override
	public Course loadNonogramCource(File dir) throws NullPointerException,
			InvalidFormatException, IOException {
		
		if (dir == null) {
			throw new NullPointerException("The specified File is null");
		}
		
		if (!dir.isDirectory()) {
			throw new IOException("The specified File is no directory");
		}

		List<Nonogram> nonograms = new ArrayList<Nonogram>();

		for (File file : dir.listFiles()) {
			nonograms.add(loadNonogram(file));
		}

		String name = dir.getName();

		Nonogram[] array = nonograms.toArray(new Nonogram[0]);
		Course c = new Course(name, array);

		return c;
	}

	@Override
	public void saveNonogramCourse(File dir, Course c) throws IOException,
			ParameterException {
		
		if (dir == null) {
			throw new NullPointerException("The specified File is null");
		}
		
		if (!dir.isDirectory()) {
			throw new IOException("The specified File is no directory");
		}
		
		if (c == null) {
			throw new NullPointerException("The specified Course is null");
		}
		
		File courseDir = new File(dir, c.getName());
		
		if(!courseDir.mkdirs()) {
			throw new IOException("Unable to create directories");
		}

		for (Nonogram n : c.getNonograms()) {
			File nonogramFile = new File(courseDir, n.getName());
			saveNonogram(nonogramFile, n);
		}
		
	}
	
	
	@Override
	public Nonogram loadNonogram(File f) throws InvalidFormatException,
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

			List<Nonogram> lst = loadXMLNonograms(root);
			if (lst.size() > 0) {
				return lst.get(0);
			} else {
				// TODO add log message here
				throw new InvalidFormatException(
				"file doesn't contain any nonogram data");
			}
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

	@Override
	public void saveNonogram(File f, Nonogram n) throws IOException {
		// TODO implement here

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



	/* private helper methods */

	private List<Nonogram> loadXMLNonograms(Element root)
	throws InvalidFormatException {

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

	private Nonogram loadXMLNonogram(Element element)
	throws InvalidFormatException {

		String tmp;
		Nonogram nonogram = null;

		int width;
		int height;
		int diff;
		String id;
		String name;
		String desc;
		boolean[][] field;

		id = element.getAttribute("id");
		name = element.getAttribute("name");
		desc = element.getAttribute("desc");

		try {
			tmp = element.getAttribute("width");
			width = Integer.parseInt(tmp);
		} catch (NumberFormatException e) {

			// TODO add log message
			throw new InvalidFormatException(
					"unable to load width, because it has an invalid format", e);
		}

		try {
			tmp = element.getAttribute("height");
			height = Integer.parseInt(tmp);
		} catch (NumberFormatException e) {

			// TODO add log message
			throw new InvalidFormatException(
					"unable to load height, because it has an invalid format",
					e);
		}

		try {
			tmp = element.getAttribute("difficulty");
			diff = Integer.parseInt(tmp);
		} catch (NumberFormatException e) {

			// TODO add log message
			throw new InvalidFormatException(
					"unable to load height, because it has an invalid format",
					e);
		}

		field = new boolean[height][width];

		NodeList lineList = element.getElementsByTagName("line");
		if (lineList.getLength() != height) {
			throw new InvalidFormatException("unable to load field values, because it has the wrong number of lines");
		}

		for (int y = 0; y < lineList.getLength(); y++) {

			// TODO check if lineList.getLength() == height
			Element line = (Element) lineList.item(y);

			String str = line.getTextContent();
			StringTokenizer tokenizer = new StringTokenizer(str, " ");
			if (tokenizer.countTokens() != width) {
				throw new InvalidFormatException("unable to load field values, because it has the wrong number of columns");
			}

			for (int x = 0; tokenizer.hasMoreTokens(); x++) {
				tmp = tokenizer.nextToken();
				if (tmp.length() > 1) {
					throw new InvalidFormatException("unable to load field values, because it has an invalid format");
				}
				field[y][x] = getFieldValue(tmp.charAt(0));
			}
		}

		try{
		nonogram = new Nonogram(id, name, desc, diff, field);
		}
		catch (ParameterException e) {
			throw new InvalidFormatException("unable to create Nonogram object, due to a parameter problem");
		}

		return nonogram;
	}

	private void saveXMLNonograms(List<Nonogram> lst, Document doc,
			Element element) throws DOMException {

		Element nonograms = doc.createElement("Nonograms");
		element.appendChild(nonograms);
		for (Nonogram n : lst) {
			saveXMLNonogram(n, doc, nonograms);
		}
	}

	private void saveXMLNonogram(Nonogram n, Document doc,
			Element nonograms) throws DOMException {

		Element nonogram = doc.createElement("Nonogram");
		nonograms.appendChild(nonogram);
		nonogram.setAttribute("id", n.getId());
		nonogram.setAttribute("name", n.getName());
		nonogram.setAttribute("height", Integer.toString(n.height()));
		nonogram.setAttribute("width", Integer.toString(n.width()));
		nonogram
		.setAttribute("difficulty", Integer.toString(n.getDifficulty()));
		nonogram.setAttribute("desc", n.getDescription());

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

	private Validator getXMLValidator() throws SAXException {

		// TODO implement error handler with a valid flag
		// TODO reset error handler flags here

		SchemaFactory schemaFactory = SchemaFactory
		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setErrorHandler(errorHandler);
		Schema schemaXSD = schemaFactory.newSchema(XMLNonogramSerializer.class
				.getResource("/xsd/nonogram.xsd"));

		Validator validator = schemaXSD.newValidator();
		return validator;
	}

	
	
	/* static helper metods */
	
	private static boolean getFieldValue(char c) throws InvalidFormatException {
		switch (c) {
		case FIELD_FREE_CHAR:
			return false;
		case FIELD_OCCUPIED_CHAR:
			return true;
		default:
			// TODO use a real Exception, maybe write one
			throw new InvalidFormatException(
					"The field containes the wrong symbol " + c);
		}
	}

	private static char getFieldChar(boolean b) {
		if (b) {
			return FIELD_OCCUPIED_CHAR;
		} else {
			return FIELD_FREE_CHAR;
		}
	}

}
