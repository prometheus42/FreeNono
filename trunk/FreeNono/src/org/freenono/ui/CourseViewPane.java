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
package org.freenono.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CourseProvider;
import org.freenono.interfaces.NonogramProvider;

public class CourseViewPane extends JPanel {

	private static final long serialVersionUID = 1160970301029289041L;

	private static Logger logger = Logger.getLogger(CourseViewPane.class);

	private JScrollPane scrollPane = null;
	private JPanel buttonPane = null;
	private CourseProvider courseProvider = null;

	public CourseViewPane(CourseProvider cp) {

		this.courseProvider = cp;

		initialize(courseProvider.getNonogramList().size());
		
		buildView();
	}

	private void initialize(int boxCount) {

		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.add(scrollPane);
		
		buttonPane = new JPanel();
		
		// build gridLayout
		// GridLayout gridLayout = new GridLayout();
		// gridLayout.setRows(boxCount / 6 + 1);
		// gridLayout.setColumns(6);
		// this.setLayout(gridLayout);
		
		buttonPane.setLayout(new FlowLayout());
		//buttonPane.setPreferredSize(new Dimension(350, 500));

	}

	private void buildView() {

		for (NonogramProvider np : courseProvider.getNonogramProvider()) {
			buildButton(np.fetchNonogram().getHash());
		}

		validate();
	}

	private void buildButton(String hash) {

		File thumb = new File(MainUI.DEFAULT_THUMBNAILS_PATH, hash);
		JButton button = null;
		
		if (thumb.exists()) {

			// Toolkit.getDefaultToolkit().getImage(thumb);
			try {
				button = new JButton(new ImageIcon(thumb.toURI().toURL()));
			} catch (MalformedURLException e) {
				logger.warn("Could not load existing thumbnail!");
			}
		} else {
			button = new JButton(new ImageIcon(getClass().getResource(
					"/resources/icon/courseViewEmpty.png")));
		}
		
		button.setPreferredSize(new Dimension(75,75));
		buttonPane.add(button);
	}

}
