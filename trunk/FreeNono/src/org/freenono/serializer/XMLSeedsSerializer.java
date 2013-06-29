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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Serializes Seeds given by the user to generate random nonograms into an xml
 * file.
 * 
 * @author Christian Wichmann
 */
public class XMLSeedsSerializer {

    private static Logger logger = Logger.getLogger(XMLSeedsSerializer.class);

    private ErrorHandler errorHandler = new ErrorHandler() {

        // TODO add error handling here?

        @Override
        public void warning(final SAXParseException exception)
                throws SAXException {
            // TODO Auto-generated method stub
        }

        @Override
        public void fatalError(final SAXParseException exception)
                throws SAXException {
            // TODO Auto-generated method stub

        }

        @Override
        public void error(final SAXParseException exception)
                throws SAXException {
            // TODO Auto-generated method stub

        }
    };

    private Validator validator = null;

    /*
     * load methods
     */

    /**
     * Loads seeds from a given file.
     * 
     * @param f
     *            file to load seeds from
     * @return seeds that were loaded
     */
    public final Seeds load(final File f) {

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

        } catch (FileNotFoundException e) {

            logger.warn("Could not find seed file.");

        } catch (IOException e) {

            logger.warn("Could not open seed file.");
        }

        return seedList;
    }

    /*
     * save methods
     */

    /**
     * Saves a list of seeds into a given file.
     * 
     * @param s
     *            seeds to be saved
     * @param f
     *            file to save seeds in
     */
    public final void save(final Seeds s, final File f) {

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

            logger.warn("unable to save file, because no parser could be created");

        } catch (TransformerException e) {

            logger.warn("unable to save file, because no parser could be created");
        }
    }

    /*
     * Seeds helper methods
     */

    /**
     * Parse xml seed file and load them.
     * 
     * @param root
     *            root element of xml seed file
     * @return list of seeds
     */
    private Seeds loadXMLSeeds(final Element root) {

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

    /**
     * Loads a single seed from file and stores it in the seed list.
     * 
     * @param seeds
     *            list of seeds to add the new one
     * @param element
     *            xml element to load
     */
    private void loadXMLSeed(final Seeds seeds, final Element element) {

        String seedString = element.getAttribute("seedString");
        String inputDate = element.getAttribute("inputDate");

        // Create a new seed with its string and a date (parsed xsd:datetime
        // type from xml)
        Seed tmp = new Seed(seedString,
                DatatypeConverter.parseDateTime(inputDate));

        seeds.addSeed(tmp);
    }

    /**
     * Saves a list of seeds into a xml seed file.
     * 
     * @param s
     *            list of seeds to be saved
     * @param doc
     *            xml document
     * @param element
     *            xml root element
     */
    private void saveXMLSeeds(final Seeds s, final Document doc,
            final Element element) {

        Element seeds = doc.createElement("Seeds");
        element.appendChild(seeds);

        for (int i = 0; i < s.getNumberOfSeeds(); i++) {

            Seed tmp = s.get(i);
            saveXMLSeed(tmp.getSeedString(), tmp.getDateTime(), doc, seeds);
        }
    }

    /**
     * Saves a single seed into a xml seed file.
     * 
     * @param seedString
     *            String object representing the seed
     * @param dateTime
     *            date when seed was entered by user
     * @param doc
     *            xml document
     * @param seedsElement
     *            xml root element to append new seeds
     */
    private void saveXMLSeed(final String seedString, final Calendar dateTime,
            final Document doc, final Element seedsElement) {

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

    /*
     * other helper methods
     */

    /**
     * Returns a validator to check seed file for wrong format.
     * 
     * @return validator to check seed file
     * @throws SAXException
     *             if sax error occurs during parsing
     */
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
