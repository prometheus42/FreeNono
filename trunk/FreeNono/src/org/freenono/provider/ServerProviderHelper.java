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
package org.freenono.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.freenono.model.Nonogram;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.XMLNonogramSerializer;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServerProviderHelper {

	private static Logger logger = Logger.getLogger(ServerProviderHelper.class);

	private String nonoServer = null;
	private static ClientResource resource = null;

	public ServerProviderHelper(String nonoServer) {

		this.nonoServer = nonoServer;

		connectServer();

	}

	private void connectServer() {

		// Create the client resource
		resource = new ClientResource(nonoServer);

		// TODO configure connection further?
	}

	public List<String> getCourseList() throws ResourceException, IOException {

		List<String> result = new ArrayList<String>();

		InputStream is = resource.getChild("courseList")
				.get(MediaType.TEXT_XML).getStream();

		DocumentBuilder parser = null;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("An error occurred when parsing the response of the server.");
		}
		Document doc = null;
		try {
			doc = parser.parse(is);
		} catch (SAXException e) {
			logger.error("An error occurred when parsing the response of the server.");
		}

		// TODO validate this xml format

		if (doc != null) {

			Element root = doc.getDocumentElement();

			if (root != null) {

				NodeList courseList = root.getElementsByTagName("Course");

				for (int i = 0; i < courseList.getLength(); i++) {
					Element course = (Element) courseList.item(i);
					result.add(course.getTextContent());
				}
			}
		}

		return result;
	}

	public List<String> getNonogramList(String course)
			throws ResourceException, IOException {

		List<String> result = new ArrayList<String>();

		InputStream is = resource.getChild(course).get(MediaType.TEXT_XML)
				.getStream();

		DocumentBuilder parser = null;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("An error occurred when parsing the response of the server.");
		}
		Document doc = null;
		try {
			doc = parser.parse(is);
		} catch (SAXException e) {
			logger.error("An error occurred when parsing the response of the server.");
		}

		// TODO validate this xml format occurred

		if (doc != null) {

			Element root = doc.getDocumentElement();
			Element nonograms = (Element) root
					.getElementsByTagName("Nonograms").item(0);

			if (nonograms != null) {

				NodeList nonogramList = nonograms
						.getElementsByTagName("Nonogram");

				for (int i = 0; i < nonogramList.getLength(); i++) {
					Element nonogram = (Element) nonogramList.item(i);
					result.add(nonogram.getAttribute("name"));
				}
			}
		}

		return result;

	}

	public Nonogram getNonogram(String course, String nonogram)
			throws ResourceException, IOException {

		Nonogram result[] = null;

		InputStream is = resource.getChild(course + "/" + nonogram)
				.get(MediaType.TEXT_XML).getStream();

		XMLNonogramSerializer ns = new XMLNonogramSerializer();
		try {
			result = ns.load(is);
		} catch (NullPointerException e) {
			logger.error("Null pointer encountered during nonogram serializing.");
		} catch (NonogramFormatException e) {
			logger.error("nvalid nonogram file format.");
		}

		return result[0];
	}

	// public static void main(String[] args) {
	//
	// ServerProviderHelper sph = new ServerProviderHelper(
	// "http://192.168.10.1:6666");
	//
	// try {
	// String course = sph.getCourseList().get(0);
	// String nonogram = sph.getNonogramList(course).get(0);
	// System.out.println(sph.getNonogram(course, nonogram));
	//
	//
	// } catch (ResourceException e) {
	// logger.error("Server under given URL not responding.");
	// } catch (IOException e) {
	// logger.error("Server under given URL not responding.");
	// }
	//
	// }

	public String getNonoServer() {

		return nonoServer;
	}

	public void setNonoServer(String nonoServer) {

		this.nonoServer = nonoServer;
	}

}
