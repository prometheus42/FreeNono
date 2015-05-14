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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.restlet.resource.ResourceException;

/**
 * Provides a collection from a Nonoserver.
 *
 * @author Christian Wichmann
 */
public class CollectionFromServer implements CollectionProvider {

    private static Logger logger = Logger.getLogger(CollectionFromServer.class);

    private static final int NONO_SERVER_PORT = 6666;
    private String serverURL = null;
    private String providerName = null;
    private List<CourseProvider> courseProviderList = null;
    private List<String> courseList = null;
    private ServerProviderHelper serverProviderHelper = null;

    /**
     * Initializes a collection of courses stored on a NonoServer.
     *
     * @param serverURL
     *            address under which server is available
     * @param name
     *            name of this provider as identification
     */
    public CollectionFromServer(final String serverURL, final String name) {

        this.serverURL = serverURL;
        this.providerName = name;

        if (serverURL == null) {
            throw new NullPointerException("Parameter serverURL is null");
        }

        // load files in separate thread
        final Thread loadThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (connectServer()) {
                        prepareCourseProviders();
                    }
                } catch (final MalformedURLException e) {

                    logger.error("Invalid server URL: " + serverURL);

                } catch (final NullPointerException e) {

                    logger.error("Invalid server URL: " + serverURL);
                }
            }
        };
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * Connects to NonoServer with given address.
     *
     * @return true, if connection was established
     * @throws MalformedURLException
     *             if server url was illegal
     */
    private synchronized boolean connectServer() throws MalformedURLException {

        URL server = null;

        server = new URL(serverURL);

        if (server != null) {
            setProviderName(providerName + " (" + server.getHost() + ")");
            serverProviderHelper =
                    new ServerProviderHelper(server.getProtocol() + "://" + server.getHost() + ":" + String.valueOf(NONO_SERVER_PORT));
        }

        // TODO Return value should show if connection was established.
        return true;
    }

    /**
     * Prepares course providers by getting all nonograms from server via
     * <code>ServerProviderHelper</code> class.
     */
    private synchronized void prepareCourseProviders() {

        logger.debug("Preparing all CourseProviders.");

        courseProviderList = new ArrayList<CourseProvider>();

        // get courses from server
        try {
            courseList = serverProviderHelper.getCourseList();

        } catch (final ResourceException e) {

            logger.error("Server under given URL not responding.");
        }

        // add them to list
        for (final String c : courseList) {
            courseProviderList.add(new CourseFromServer(c, serverProviderHelper));
        }

        // sort list
        Collections.sort(courseProviderList, CourseProvider.NAME_ASCENDING_ORDER);
    }

    @Override
    public final synchronized List<String> getCourseList() {

        return courseList;
    }

    @Override
    public final synchronized List<CourseProvider> getCourseProvider() {

        return Collections.unmodifiableList(courseProviderList);
    }

    @Override
    public final String getProviderName() {

        if (providerName == null) {
            return "NonoServer: " + serverURL;
        } else {
            return providerName;
        }
    }

    @Override
    public final void setProviderName(final String name) {

        this.providerName = name;
    }

    @Override
    public final String toString() {

        return providerName;
    }

    /**
     * Changes url for NonoServer and tries to connect to new server.
     *
     * @param serverURL
     *            server url
     */
    public final void changeServerURL(final String serverURL) {

        this.serverURL = serverURL;

        try {
            if (connectServer()) {
                prepareCourseProviders();
            }
        } catch (final MalformedURLException e) {

            logger.error("Invalid server URL: " + serverURL);

        } catch (final NullPointerException e) {

            logger.error("Invalid server URL: " + serverURL);
        }
    }

    /**
     * Returns server url for this provider.
     *
     * @return server url
     */
    public final String getServerURL() {

        return serverURL;
    }

    @Override
    public final int getNumberOfNonograms() {

        int n = 0;

        for (final CourseProvider cp : courseProviderList) {

            n += cp.getNumberOfNonograms();
        }

        return n;
    }

    @Override
    public final Iterator<CourseProvider> iterator() {

        return Collections.unmodifiableList(courseProviderList).iterator();
    }

    @Override
    public final void addCollectionListener(final CollectionListener l) {

        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public final void removeCollectionListener(final CollectionListener l) {

        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
