/*****************************************************************************
 * Nonotector - Detector to import nonograms from scanned images
 * Copyright (c) 2013 Christian Wichmann
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
package org.freenono.nonotector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Nonotector extends JFrame  {

	private static final long serialVersionUID = 3976275447399859391L;
	
	private JMenuBar menuBar;
	private File currentOpenFile;
	private File lastOpenedDirectory = null;
	private JMenuItem convertMenuItem;
	private JMenuItem saveMenuItem;	
	private JCheckBoxMenuItem searchBoundsMenuItem;
	private ImagePanel panel;
	
	public static PropertyDialog propertyDialog;
	public static boolean searchBounds = true;

	
	public Nonotector() {
	
		initialize();
		addListeners();
	}
	
	
	private void addListeners() {
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				performExit();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}


	private void initialize() {

		setSize(800, 600);
		setLocationRelativeTo(null);
		//setLocationByPlatform(true);
		setName("Nonotector");
		setTitle("Nonotector");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setJMenuBar(getMenu());
		
		propertyDialog = new PropertyDialog(this);
		
		setVisible(true);
	}
	
	private JMenuBar getMenu() {
		
		if (menuBar == null) {

			JMenu menu;
			JMenuItem menuItem;

			// create the menu bar.
			menuBar = new JMenuBar();

			// create file menu
			menu = new JMenu("File");
			menu.setMnemonic(KeyEvent.VK_F);
			menu.getAccessibleContext().setAccessibleDescription("File");
			menuBar.add(menu);

			// create menu items for file menu
			menuItem = new JMenuItem("Load image...", KeyEvent.VK_L);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Load image file");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadImage();
				}
			});

			searchBoundsMenuItem = new JCheckBoxMenuItem("Search bounds automatically", true);
			searchBoundsMenuItem.setMnemonic(KeyEvent.VK_B);
			searchBoundsMenuItem.getAccessibleContext()
					.setAccessibleDescription("Search for hotspots");
			menu.add(searchBoundsMenuItem);
			searchBoundsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					searchBounds = !searchBounds;
				}
			});

			convertMenuItem = new JMenuItem("Convert regions", KeyEvent.VK_C);
			convertMenuItem.setEnabled(false);
			convertMenuItem.getAccessibleContext().setAccessibleDescription(
					"Start converting images to nonograms");
			menu.add(convertMenuItem);
			convertMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					panel.convert();
					panel.deleteSelections();

					validate();
					
					saveMenuItem.setEnabled(true);
				}
			});
			
			saveMenuItem = new JMenuItem("Save nonograms...", KeyEvent.VK_S);
			saveMenuItem.setEnabled(false);
			saveMenuItem.getAccessibleContext().setAccessibleDescription("Save nonograms to directory");
			menu.add(saveMenuItem);
			saveMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveNonograms();
				}
			});

			menu.addSeparator();

			menuItem = new JMenuItem("Properties...", KeyEvent.VK_P);
			menuItem.getAccessibleContext().setAccessibleDescription("Set properties");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showPropertyDialog();
				}
			});
			
			menu.addSeparator();

			menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
			menuItem.getAccessibleContext().setAccessibleDescription("Exit programm");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					performExit();
				}
			});

			// create edit menu
			menu = new JMenu("Edit");
			menu.setMnemonic(KeyEvent.VK_E);
			menu.getAccessibleContext().setAccessibleDescription("Edit image");
			menuBar.add(menu);

			// create menu items for edit menu
			menuItem = new JMenuItem("Rotate clockwise", KeyEvent.VK_R);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Rotate shown image clockwise");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.rotateImage(90);
					panel.deleteSelections();
					pack();
				}
			});
			
			menuItem = new JMenuItem("Rotate counterclockwise", KeyEvent.VK_O);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Rotate shown image counterclockwise");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.rotateImage(-90);
					panel.deleteSelections();
					pack();
				}
			});
						
			// create help menu
			menu = new JMenu("Help");
			menu.setMnemonic(KeyEvent.VK_H);
			menu.getAccessibleContext().setAccessibleDescription("Get help");
			menuBar.add(menu);

			menuItem = new JMenuItem("Help", KeyEvent.VK_H);
			menuItem.getAccessibleContext().setAccessibleDescription("Get help");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});

			menuItem = new JMenuItem("About", KeyEvent.VK_A);
			menuItem.getAccessibleContext().setAccessibleDescription("About Nonotector"); 
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
		}

		return menuBar;
	}

	private void loadImage() {
		
		final JFileChooser fc = new JFileChooser();
		
		if (lastOpenedDirectory != null) {
			
			fc.setCurrentDirectory(lastOpenedDirectory);
		}

		// set filters for file chooser
		fc.setFileFilter(new FileNameExtensionFilter("PNG image", "png"));

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			currentOpenFile = fc.getSelectedFile();
			
			if (getExtension(currentOpenFile).equals("png")) {
				
				showImage();
				
			} else {
			
				//TODO handle this!
			}
			
			lastOpenedDirectory = fc.getCurrentDirectory();
		}
	}

	private void showImage() {
		
		if (panel != null) {
			
			getContentPane().remove(panel);
			panel = null;
		}
		
		panel = new ImagePanel(currentOpenFile);
		getContentPane().add(panel);
		
		convertMenuItem.setEnabled(true);
		
		pack();
	}
	
	public String getExtension(File f) {

		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
	
	private void saveNonograms() {
		
		final JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			NonogramStore.saveNonogramsToFile(fc.getSelectedFile());
		}
	}
	
	private void performExit() {
		
		dispose();
	}
	
	private void showPropertyDialog() {
		
		propertyDialog.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		
		new Nonotector();
	}

}
