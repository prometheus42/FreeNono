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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.log4j.Logger;
import org.freenono.model.Nonogram;

public class NonogramButton extends JButton {

	private static final long serialVersionUID = 6516455428864083473L;

	private static Logger logger = Logger.getLogger(NonogramButton.class);

	private Nonogram nonogram = null;
	private NonogramChooserUI nonogramChooserUI = null;


	public NonogramButton(NonogramChooserUI nc, Nonogram n) {

		this.nonogram = n;
		this.nonogramChooserUI = nc;

		initialize();

		addListeners();
	}

	private void initialize() {

		File thumb = new File(MainUI.DEFAULT_THUMBNAILS_PATH,
				nonogram.getHash());

		if (thumb.exists()) {

			// Toolkit.getDefaultToolkit().getImage(thumb);
			try {
				this.setIcon(new ImageIcon(thumb.toURI().toURL()));
			} catch (MalformedURLException e) {
				logger.warn("Could not load existing thumbnail!");
			}
			this.setToolTipText(nonogram.getName());
		} else {
			this.setIcon(new ImageIcon(getClass().getResource(
					"/resources/icon/courseViewEmpty.png")));
		}

		this.setPreferredSize(new Dimension(90, 90));
	}

	private void addListeners() {

		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				nonogramChooserUI.setChosenNonogram(nonogram);
			}
		});
	}

}
