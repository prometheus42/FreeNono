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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getTitle());
		this.add(getScrollPane());
		this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.validate();
		
		buttonPane.requestFocusInWindow();
	}

	private JScrollPane getScrollPane() {
		
		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(buildButtonPane());

		// TODO Dynamically Change the scroll pane's client's size
		// (use getPreferredScrollableViewportSize?) 
		scrollPane.setPreferredSize(new Dimension(650, 450));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		return scrollPane;
	}

	private JLabel getTitle() {

		titleLabel = new JLabel(courseProvider.getCourseName());
		titleLabel.setFont(new Font("LCDMono2", Font.PLAIN, 24));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		return titleLabel;
	}

	private JPanel buildButtonPane() {

		logger.debug("Build course view for course "
				+ courseProvider.getCourseName() + ".");

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
