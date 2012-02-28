/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
 * Copyright (c) 2012 Christian Wichmann
 * 
 * File name: $HeadURL$
 * Revision: $Revision$
 * Last modified: $Date$
 * Last modified by: $Author$
 * $Id$
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
package org.freenono.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.freenono.model.Nonogram;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.SimpleNonogramSerializer;
import org.freenono.serializer.XMLNonogramSerializer;
import org.freenono.ui.AboutUI;
import org.freenono.ui.SplashScreen;

public class EditorFrame extends JFrame {

	private static final long serialVersionUID = 5991986713803903723L;

	private JPanel contentPane = null;
	private JMenuBar menuBar = null;
	private JMenuItem saveItem = null;
	private JMenuItem saveAsItem = null;
	private JMenuItem propertiesItem = null;
	private JPanel boardPanel = null;
	private PropertyDialog propertyDialog = null;

	private Nonogram currentNonogram = null;
	private File currentOpenFile = null;
	private EditorTileSet boardComponent = null;

	private XMLNonogramSerializer xmlNonogramSerializer = new XMLNonogramSerializer();

	public EditorFrame() {

		super();

		// show splash screen
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SplashScreen splash = new SplashScreen(
						"/icon/splashscreen_fne.png");
				splash.setVisible(true);
			}
		});

		initialize();

		// add component Listener for handling the resize operation
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
				handleResize(c.getSize());
			}
		});

	}

	public EditorFrame(File file) {
		
		this();

		loadNonogram(file);
	}

	/**
	 * This method initializes EditorFrame
	 * 
	 * @return void
	 */
	private void initialize() {

		this.setSize(1000, 850);
		this.setLocationRelativeTo(null);
		this.setName("EditorFrame");
		this.setTitle("FNE");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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

		this.setJMenuBar(getMenu());
		this.setContentPane(getEditorPane());

		this.propertyDialog = new PropertyDialog(this);

	}

	private void handleResize(Dimension newSize) {
		if (boardComponent != null) {

			int tileHeight = (int) ((newSize.getHeight() - menuBar.getHeight()) / currentNonogram
					.height());
			int tiledWidth = (int) (newSize.getWidth() / currentNonogram
					.width());
			int tileSize = Math.min(tileHeight, tiledWidth) - 5;

			boardComponent.handleResize(new Dimension(tileSize, tileSize));

		}
	}

	/**
	 * This method initializes the menuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getMenu() {

		if (menuBar == null) {

			JMenu menu;
			JMenuItem menuItem;

			// create the menu bar.
			menuBar = new JMenuBar();

			// create file menu
			menu = new JMenu("File");
			menu.setMnemonic(KeyEvent.VK_F);
			menu.getAccessibleContext().setAccessibleDescription("File Menu");
			menuBar.add(menu);

			// create menu items for file menu
			menuItem = new JMenuItem("New Nonogram", KeyEvent.VK_N);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Create new Nonogram");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// create new nonogram
					createNewNonogram();
				}
			});

			menuItem = new JMenuItem("Load Nonogram...", KeyEvent.VK_L);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Load existing Nonogram");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openNonogram();
				}
			});

			saveItem = new JMenuItem("Save Nonogram", KeyEvent.VK_S);
			saveItem.setEnabled(false);
			saveItem.getAccessibleContext().setAccessibleDescription(
					"Save created Nonogram");
			menu.add(saveItem);
			saveItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveNonogram();
				}
			});

			saveAsItem = new JMenuItem("Save Nonogram as...", KeyEvent.VK_A);
			saveAsItem.setEnabled(false);
			saveAsItem.getAccessibleContext().setAccessibleDescription(
					"Save created Nonogram");
			menu.add(saveAsItem);
			saveAsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveNonogramAs();
				}
			});

			menu.addSeparator();

			propertiesItem = new JMenuItem("Properties...", KeyEvent.VK_P);
			propertiesItem.setEnabled(false);
			propertiesItem.getAccessibleContext().setAccessibleDescription(
					"Change Nonogram Properties");
			menu.add(propertiesItem);
			propertiesItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showPropertiesDialog();
				}
			});

			menu.addSeparator();

			menuItem = new JMenuItem("Exit Program", KeyEvent.VK_X);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Exit Program");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					performExit();
				}
			});

			// create help menu
			menu = new JMenu("Help");
			menu.setMnemonic(KeyEvent.VK_H);
			menu.getAccessibleContext().setAccessibleDescription("Help Menu");
			menuBar.add(menu);

			menuItem = new JMenuItem("Help", KeyEvent.VK_H);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Get Help for FNE");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showHelpDialog();
				}
			});

			menuItem = new JMenuItem("About", KeyEvent.VK_A);
			menuItem.getAccessibleContext().setAccessibleDescription(
					"About Box");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showAboutDialog();
				}
			});
		}

		return menuBar;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getEditorPane() {
		if (contentPane == null) {
			contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout());
			// contentPane.add(new JButton(), BorderLayout.NORTH);
		}
		return contentPane;
	}

	private void buildBoard() {

		if (boardPanel == null) {
			boardPanel = new JPanel() {
				private static final long serialVersionUID = -5144877072997396393L;

				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					BufferedImage cache = null;
					if (cache == null || cache.getHeight() != getHeight()) {
						cache = new BufferedImage(2, getHeight(),
								BufferedImage.TYPE_INT_RGB);
						Graphics2D g2d = cache.createGraphics();

						GradientPaint paint = new GradientPaint(0, 0,
								new Color(143, 231, 200), 0, getHeight(),
								Color.WHITE);
						g2d.setPaint(paint);
						g2d.fillRect(0, 0, 2, getHeight());
						g2d.dispose();
					}
					g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
				}
			};
		} else {
			boardPanel.remove(boardComponent);
		}

		// clear all remnants of the old board
		this.repaint();

		// calculating maximum size for boardComponent
		int tileHeight = (this.getHeight() - menuBar.getHeight())
				/ currentNonogram.height();
		int tiledWidth = this.getWidth() / currentNonogram.width();
		int tileSize = Math.min(tileHeight, tiledWidth) - 5;

		boardComponent = new EditorTileSet(currentNonogram, new Dimension(
				tileSize, tileSize));
		boardPanel.add(boardComponent);
		contentPane.add(boardPanel, BorderLayout.CENTER);

		this.validate();

	}

	private void performExit() {

		int answer = JOptionPane.showConfirmDialog(this,
				"Do you really want to exit FNE?", "Exit FNE",
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.OK_OPTION) {

			this.setVisible(false);
			this.dispose();
			System.exit(1);

		}
	}

	protected void createNewNonogram() {

		// set file pointer to null
		currentOpenFile = null;
		saveItem.setEnabled(false);
		saveAsItem.setEnabled(true);
		propertiesItem.setEnabled(true);

		// get user input for width and height of nonogram
		propertyDialog.setVisible(true);
		currentNonogram = propertyDialog.getNonogram();

		if (currentNonogram != null) {

			buildBoard();
		}

	}

	protected void saveNonogramAs() {

		final JFileChooser fc = new JFileChooser();
		File file;

		// set filter for file chooser
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".nonogram");
			}

			@Override
			public String getDescription() {
				return "Nonogram Files";
			}
		});

		fc.setSelectedFile(new File(currentNonogram.getName() + ".nonogram"));

		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

			file = fc.getSelectedFile();

			// TODO: add string concatenation of standard file extension!
			// if (file.getName().toLowerCase().endsWith(".nonogram"))
			// file.

			try {
				xmlNonogramSerializer.save(file, currentNonogram);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			currentOpenFile = file;
			saveItem.setEnabled(true);
			saveAsItem.setEnabled(true);
			propertiesItem.setEnabled(true);
		}

	}

	protected void saveNonogram() {

		if (currentOpenFile != null) {
			try {
				xmlNonogramSerializer.save(currentOpenFile, currentNonogram);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected void openNonogram() {

		final JFileChooser fc = new JFileChooser();
		File file;

		// set filter for file chooser
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".nonogram");
			}

			@Override
			public String getDescription() {
				return "Nonogram Files";
			}
		});

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			file = fc.getSelectedFile();

			loadNonogram(file);

		}
	}

	protected void loadNonogram(File file) {

		Nonogram[] n = null;

		if (file.getName().endsWith(
				"." + XMLNonogramSerializer.DEFAULT_FILE_EXTENSION)) {

			try {
				n = xmlNonogramSerializer.load(file);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonogramFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (file.getName().endsWith(
				"." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {

			SimpleNonogramSerializer simpleNonogramSerializer = new SimpleNonogramSerializer();
			try {
				n = simpleNonogramSerializer.load(file);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonogramFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// paint board only if at least one nonogram were read
		if (n != null) {
			// TODO: add error message!

			// choose Nonogram from read file to edit
			currentNonogram = n[0];
			// TODO: add dialog to choose one of possibly many Nonograms

			buildBoard();

			currentOpenFile = file;
			saveItem.setEnabled(true);
			saveAsItem.setEnabled(true);
			propertiesItem.setEnabled(true);
		}

	}

	protected void showPropertiesDialog() {

		propertyDialog.setNonogram(currentNonogram);
		propertyDialog.setVisible(true);
		currentNonogram = propertyDialog.getNonogram();
		buildBoard();

	}

	protected void showHelpDialog() {
		// TODO Auto-generated method stub

	}

	protected void showAboutDialog() {

		// TODO: show own about dialog
		AboutUI ui = new AboutUI(this);
		ui.setVisible(true);

	}

}
