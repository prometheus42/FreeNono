/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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
import java.io.IOException;
import java.util.Calendar;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
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
import org.freenono.model.Seed;
import org.freenono.model.Seeds;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLSeedsSerializer {

	private static Logger logger = Logger.getLogger(XMLSeedsSerializer.class);

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
	public Seeds load(File f) throws NullPointerException, IOException {

		Seeds seedList = null;
		
		try {

			FileInputStream is = new FileInputStream(f);

			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = parser.parse(is);

			Validator validator = getXMLValidator();
			validator.validate(new DOMSource(doc));

			Element root = doc.getDocumentElement();

			seedList = loadXMLSeeds(root);

			logger.info("Seeds loaded successfully from file " + f.getName());

		} catch (SAXException e) {

			logger.warn("SAXException in save()");

		} catch (ParserConfigurationException e) {

			logger.warn("ParserConfigurationException in save()");
		}
		
		return seedList;
	}

	
	/* save methods */
	public void save(Seeds s, File f) throws NullPointerException, IOException {

		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element root = doc.createElement("FreeNono");
			doc.appendChild(root);

			saveXMLSeeds(s, doc, root);

			Source source = new DOMSource(doc);
			Result result = new StreamResult(f);

			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(source, result);

			logger.info("Seeds saved successfully in file " + f.getName());

		} catch (ParserConfigurationException e) {

			logger.warn("ParserConfigurationException in save()");
			throw new IOException(
					"unable to save file, because no parser could be created",
					e);

		} catch (TransformerException e) {

			logger.warn("TransformerException in save()");
			throw new IOException(
					"unable to save file, because no parser could be created",
					e);
		}
	}

	
	/* Seeds helper methods */
	private Seeds loadXMLSeeds(Element root) {

		Seeds retObj = null;

		Element seeds = (Element) root.getElementsByTagName("Seeds").item(0);
		if (seeds != null) {

			retObj = new Seeds();
			NodeList seedList = seeds.getElementsByTagName("Seed");

			for (int i = 0; i < seedList.getLength(); i++) {

				Element seed = (Element) seedList.item(i);
				loadXMLSeed(retObj, seed);
			}
		}
		return retObj;
	}

	private void loadXMLSeed(Seeds seeds, Element element) {

		String seedString = element.getAttribute("seedString");
		String inputDate = element.getAttribute("inputDate");

		Seed tmp = new Seed();

		tmp.setSeedString(seedString);

		// parse xsd:datetime type from xml
		tmp.setDateTime(DatatypeConverter.parseDateTime(inputDate));

		seeds.addSeed(tmp);
	}

	private void saveXMLSeeds(Seeds s, Document doc, Element element)
			throws DOMException {

		Element seeds = doc.createElement("Seeds");
		element.appendChild(seeds);

		for (int i = 0; i < s.getNumberOfSeeds(); i++) {

			Seed tmp = s.get(i);
			saveXMLSeed(tmp.getSeedString(), tmp.getDateTime(), doc, seeds);
		}
	}

	private void saveXMLSeed(String seedString, Calendar dateTime,
			Document doc, Element seedsElement) throws DOMException {

		Element seed = doc.createElement("Seed");
		seedsElement.appendChild(seed);
		seed.setAttribute("seedString", seedString);

		// XMLGregorianCalendar xgcal;
		// xgcal = DatatypeFactory.newInstance()
		// .newXMLGregorianCalendar((GregorianCalendar)dateTime);
		// xgcal.toXMLFormat()
		seed.setAttribute("inputDate",
				DatatypeConverter.printDateTime(dateTime));
	}

	
	/* other helper methods */
	private Validator getXMLValidator() throws SAXException {

		if (validator == null) {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFactory.setErrorHandler(errorHandler);
			Schema schemaXSD = schemaFactory.newSchema(XMLSeedsSerializer.class
					.getResource("/resources/xsd/seeds.xsd"));

			validator = schemaXSD.newValidator();
		}
		return validator;
	}

}
