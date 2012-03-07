/*****************************************************************************
 * NonoServer - A FreeNono server
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
package org.freenono.nonoserver;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class runNonoServer {

	public static void main(String[] args) {

		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 6666);

		NonoServer nonoService = new NonoServer();
		//nonoService.getContext().getParameters()
		//		.add("keystorePassword", "password");
		component.getDefaultHost().attach("", nonoService);

		try {
			component.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
