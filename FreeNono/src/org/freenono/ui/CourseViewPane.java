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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.freenono.interfaces.CourseProvider;
import org.freenono.interfaces.NonogramProvider;

public class CourseViewPane extends JPanel {

	private static final long serialVersionUID = 1160970301029289041L;

	private static Logger logger = Logger.getLogger(CourseViewPane.class);

	private CourseProvider courseProvider = null;
	private NonogramChooserUI nonogramChooserUI = null;
	
	private JScrollPane scrollPane = null;
	private JPanel buttonPane = null;
	private JLabel titleLabel = null;

	
	public CourseViewPane(NonogramChooserUI nc, CourseProvider cp) {

		this.courseProvider = cp;
		this.nonogramChooserUI = nc;

		initialize();
	}

	private void initialize() {

		this.setLayout(new BorderLayout());
		this.add(getTitle(), BorderLayout.NORTH);

		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(buildButtonPane());

		// TODO Dynamically Change the scroll pane's client's size
		// (use getPreferredScrollableViewportSize?) 
		scrollPane.setPreferredSize(new Dimension(625, 625));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private JLabel getTitle() {

		titleLabel = new JLabel(courseProvider.getCourseName());
		titleLabel.setFont(new Font("Ubuntu", Font.ITALIC, 24));
		
		return titleLabel;
	}

	private JPanel buildButtonPane() {

		buttonPane = new JPanel();
		
		buttonPane.setLayout(new FlowLayout());

		List<String> nonogramList = courseProvider.getNonogramList();

		if (nonogramList != null) {

			buttonPane.setPreferredSize(new Dimension(600, (int)
					(100 * (nonogramList.size() / 6.))));

			for (NonogramProvider np : courseProvider.getNonogramProvider()) {

				buttonPane.add(new NonogramButton(nonogramChooserUI, np
						.fetchNonogram()));
			}
		}

		return buttonPane;
	}

}