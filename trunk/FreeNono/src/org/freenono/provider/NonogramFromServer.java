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

import org.apache.log4j.Logger;
import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.restlet.resource.ResourceException;

public class NonogramFromServer implements NonogramProvider {

	private static Logger logger = Logger.getLogger(NonogramFromServer.class);
	
	private Nonogram nonogram = null;
	private ServerProviderHelper serverProviderHelper = null;
	private String nonogramName = null;
	private String courseName = null;

	
	public NonogramFromServer(String nonogramName, String courseName,
			ServerProviderHelper serverProviderHelper) {

		this.nonogramName  = nonogramName;
		this.courseName = courseName;
		this.serverProviderHelper = serverProviderHelper;
	}


	@Override
	public Nonogram fetchNonogram() {

		if (nonogram != null)
			return nonogram;
		else
		{
			try {
				nonogram = serverProviderHelper.getNonogram(courseName, nonogramName);
			} catch (ResourceException e) {
				logger.error("Server under given URL not responding.");
			} catch (IOException e) {
				logger.error("Server under given URL not responding.");
			}
			return nonogram;
		}
	}

	@Override
	public String getId() {

		return fetchNonogram().getId();
	}

	@Override
	public String getName() {
		
		return fetchNonogram().getName();
	}

	@Override
	public String getDescription() {

		return fetchNonogram().getDescription();
	}

	@Override
	public DifficultyLevel getDifficulty() {

		return fetchNonogram().getDifficulty();
	}
	
	public String toString(){
		
		return nonogramName;
		
	}

	@Override
	public int width() {
		
		return fetchNonogram().width();
	}

	@Override
	public int height() {
		
		return fetchNonogram().height();
	}

}
