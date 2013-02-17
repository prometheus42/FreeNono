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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.CourseProvider;
import org.restlet.resource.ResourceException;

public class CollectionFromServer implements CollectionProvider {

	private static Logger logger = Logger.getLogger(CollectionFromServer.class);

	private static final int nonoServerPort = 6666;
	private String serverURL = null;
	private String providerName = null;
	private List<CourseProvider> courseProviderList = null;
	private List<String> courseList = null;
	private ServerProviderHelper serverProviderHelper = null;

	public CollectionFromServer(final String serverURL, String name) {

		this.serverURL = serverURL;
		this.providerName = name;

		if (serverURL == null) {
			throw new NullPointerException("Parameter serverURL is null");
		}

		// load files in separate thread
		Thread loadThread = new Thread() {
			public void run() {
				try {
					if (connectServer())
						prepareCourseProviders();
				} catch (MalformedURLException e) {
					logger.warn("Invalid server URL: " + serverURL);
				} catch (NullPointerException e) {
					logger.warn("Invalid server URL: " + serverURL);
				}
			}
		};
		loadThread.setDaemon(true);
		loadThread.start();
	}

	private synchronized boolean connectServer() throws MalformedURLException {

		URL server = null;

		server = new URL(serverURL);

		if (server != null) {
			setProviderName(providerName + " (" + server.getHost() + ")");
			serverProviderHelper = new ServerProviderHelper(
					server.getProtocol() + "://" + server.getHost() + ":"
							+ String.valueOf(nonoServerPort));
			return true;
		}
		
		return false;
	}
	
	private synchronized void prepareCourseProviders() {

		logger.debug("Preparing all CourseProviders.");

		courseProviderList = new ArrayList<CourseProvider>();

		// create courseProvider
		try {
			courseList = serverProviderHelper.getCourseList();
		} catch (ResourceException e) {
			logger.error("Server under given URL not responding.");
		} catch (IOException e) {
			logger.error("Server under given URL not responding.");
		}
		for (String c : courseList) {
			courseProviderList
					.add(new CourseFromServer(c, serverProviderHelper));
		}
	}
	

	@Override
	public synchronized List<String> getCourseList() {

		return courseList;
	}

	@Override
	public synchronized List<CourseProvider> getCourseProvider() {

		return courseProviderList;
	}

	
	@Override
	public String getProviderName() {

		if (providerName == null)
			return "NonoServer: " + serverURL;
		else
			return providerName;

	}

	@Override
	public void setProviderName(String name) {

		this.providerName = name;

	}
	
	public String toString() {
		
		return providerName;
	}
	
	public void changeServerURL(String serverURL) {

		this.serverURL = serverURL;

		try {
			if (connectServer())
				prepareCourseProviders();
		} catch (MalformedURLException e) {
			logger.warn("Invalid server URL: " + serverURL);
		} catch (NullPointerException e) {
			logger.warn("Invalid server URL: " + serverURL);
		}
	}

	public String getServerURL() {
		
		return serverURL;
	}

}
