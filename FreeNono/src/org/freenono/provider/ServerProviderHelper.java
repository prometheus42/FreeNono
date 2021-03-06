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
package org.freenono.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.freenono.model.data.Nonogram;
import org.freenono.serializer.data.NonogramFormatException;
import org.freenono.serializer.data.XMLNonogramSerializer;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper class for getting nonograms from a NonoServer.
 *
 * @author Christian Wichmann
 */
public class ServerProviderHelper {

    private static Logger logger = Logger.getLogger(ServerProviderHelper.class);

    private String nonoServer = null;
    private ClientResource resource = null;

    /**
     * Initializes a helper instance.
     *
     * @param nonoServer
     *            String object locating the NonoServer
     */
    public ServerProviderHelper(final String nonoServer) {

        this.nonoServer = nonoServer;

        connectServer();
    }

    /**
     * Connects server under given address.
     */
    private void connectServer() {

        // TODO Save root reference for nonogram server: rootReference = new Reference(nonoServer);
    }

    /**
     * Returns a list of all course names.
     *
     * @return list of course names
     */
    public final List<String> getCourseList() {

        final List<String> result = new ArrayList<String>();

        resource = new ClientResource(nonoServer);

        InputStream is = null;
        try {
            is = resource.getChild("courseList").get(MediaType.TEXT_XML).getStream();
        } catch (final ResourceException e1) {
            logger.error("Server under given URL not responding.");

        } catch (final IOException e1) {
            logger.error("Server under given URL not responding.");
        }

        DocumentBuilder parser = null;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        } catch (final ParserConfigurationException e) {
            logger.error("An error occurred when parsing the response of the server.");
        }
        Document doc = null;
        try {
            doc = parser.parse(is);

        } catch (final SAXException e) {
            logger.error("An error occurred when parsing the response of the server.");

        } catch (final IOException e) {
            logger.error("Server under given URL not responding.");
        }

        // TODO validate this xml format

        if (doc != null) {

            final Element root = doc.getDocumentElement();

            if (root != null) {
                final NodeList courseList = root.getElementsByTagName("Course");

                for (int i = 0; i < courseList.getLength(); i++) {
                    final Element course = (Element) courseList.item(i);
                    result.add(course.getAttribute("name"));
                }
            }
        }

        return result;
    }

    /**
     * Returns a list of all nonograms in a course on a NonoServer.
     *
     * @param course
     *            course from which all nonograms should be returned
     * @return list of nonograms from course
     */
    public final List<String> getNonogramList(final String course) {

        final List<String> result = new ArrayList<String>();

        // building relative reference to course
        final Reference nonogramReference = new Reference(Reference.encode(course));

        resource = new ClientResource(nonoServer);

        InputStream is = null;
        try {
            is = resource.getChild(nonogramReference).get(MediaType.TEXT_XML).getStream();
        } catch (final ResourceException e1) {

            logger.error("Server under given URL not responding.");

        } catch (final IOException e1) {

            logger.error("Server under given URL not responding.");
        }

        DocumentBuilder parser = null;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            logger.error("An error occurred when parsing the response of the server.");
        }
        Document doc = null;
        try {
            doc = parser.parse(is);

        } catch (final SAXException e) {

            logger.error("An error occurred when parsing the response of the server.");

        } catch (final IOException e) {

            logger.error("Server under given URL not responding.");
        }

        // TODO validate this xml format

        if (doc != null) {

            final Element root = doc.getDocumentElement();
            final Element nonograms = (Element) root.getElementsByTagName("Nonograms").item(0);

            if (nonograms != null) {

                final NodeList nonogramList = nonograms.getElementsByTagName("Nonogram");

                for (int i = 0; i < nonogramList.getLength(); i++) {
                    final Element nonogram = (Element) nonogramList.item(i);
                    result.add(nonogram.getAttribute("name"));
                }
            }
        }

        return result;
    }

    /**
     * Gets a nonogram from a course on a NonoServer.
     *
     * @param course
     *            course from which to get nonogram
     * @param nonogram
     *            name of nonogram that should be get from server
     * @return nonogram get from server
     */
    public final Nonogram getNonogram(final String course, final String nonogram) {

        Nonogram[] result = null;

        // building relative reference to nonogram
        final Reference nonogramReference = new Reference(Reference.encode(course)).addSegment(nonogram);

        resource = new ClientResource(nonoServer);

        InputStream is = null;
        try {
            is = resource.getChild(nonogramReference).get(MediaType.TEXT_XML).getStream();
        } catch (final ResourceException e1) {

            logger.error("Server under given URL not responding.");

        } catch (final IOException e1) {

            logger.error("Server under given URL not responding.");
        }

        final XMLNonogramSerializer ns = new XMLNonogramSerializer();
        try {
            result = ns.load(is);

        } catch (final NullPointerException e) {

            logger.error("Null pointer encountered during nonogram serializing.");

        } catch (final NonogramFormatException e) {

            logger.error("Invalid nonogram file format.");

        } catch (final IOException e) {

            logger.error("Server under given URL not responding.");
        }

        if (result[0] != null) {

            result[0].setOriginPath(resource.getReference().addSegment(course).addSegment(nonogram).toUrl());
        }

        return result[0];
    }

    /**
     * Gets address of NonoServer.
     *
     * @return server address
     */
    public final String getNonoServer() {

        return nonoServer;
    }

    /**
     * Sets address of NonoServer.
     *
     * @param nonoServer
     *            server address to be set
     */
    public final void setNonoServer(final String nonoServer) {

        this.nonoServer = nonoServer;
    }

}
