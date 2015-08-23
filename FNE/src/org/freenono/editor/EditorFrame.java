/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
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
package org.freenono.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.Nonogram;
import org.freenono.serializer.data.CourseFormatException;
import org.freenono.serializer.data.NonogramFormatException;
import org.freenono.serializer.data.SimpleNonogramSerializer;
import org.freenono.serializer.data.XMLNonogramSerializer;
import org.freenono.serializer.data.ZipCourseSerializer;
import org.freenono.ui.common.SplashScreen;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * Main frame of FreeNonoEditor.
 *
 * @author Christian Wichmann
 */
public class EditorFrame extends JFrame {

    private static Logger logger = Logger.getLogger(EditorFrame.class);

    private static final long serialVersionUID = 5991986713803903723L;

    private static final String NONO_SERVER = "http://127.0.0.1:6666";

    private JPanel contentPane = null;
    private JMenuBar menuBar = null;
    private JMenuItem saveItem = null;
    private JMenuItem saveAsItem = null;
    private JMenuItem propertiesItem = null;
    private JMenuItem publishItem = null;
    private JPanel boardPanel = null;
    private PropertyDialog propertyDialog = null;
    private CourseViewDialog courseViewDialog = null;

    private Nonogram currentNonogram = null;
    private File currentOpenFile = null;
    private EditorTileSet boardComponent = null;

    private final XMLNonogramSerializer xmlNonogramSerializer = new XMLNonogramSerializer();
    private final ZipCourseSerializer zipCourseSerializer = new ZipCourseSerializer();

    private static final int DEFAULT_TILE_SIZE = 34;

    /**
     * Initializes this editor frame.
     */
    public EditorFrame() {

        super();

        // showSplashscreen();

        initialize();
    }

    /**
     * Initializes this editor frame and loads nonogram file.
     *
     * @param file
     *            nonogram file to be loaded
     */
    public EditorFrame(final File file) {

        this();

        loadNonogram(file);
    }

    /**
     * Initializes editor frame.
     */
    private void initialize() {

        setSize(1000, 850);
        setLocationRelativeTo(null);
        setName(Messages.getString("EditorFrame.Title"));
        setTitle(Messages.getString("EditorFrame.FNE"));

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(final WindowEvent e) {
            }

            @Override
            public void windowIconified(final WindowEvent e) {
            }

            @Override
            public void windowDeiconified(final WindowEvent e) {
            }

            @Override
            public void windowDeactivated(final WindowEvent e) {
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                performExit();
            }

            @Override
            public void windowClosed(final WindowEvent e) {
            }

            @Override
            public void windowActivated(final WindowEvent e) {
            }
        });

        setJMenuBar(getMenu());
        setContentPane(getEditorPane());

        propertyDialog = new PropertyDialog(this);
    }

    /**
     * Handles resize of this frame by resizing containing editor tile set.
     *
     * @param newSize
     *            new dimension of editor tiles
     */
    @SuppressWarnings("unused")
    private void handleResize(final Dimension newSize) {

        if (boardComponent != null) {

            final int tileHeight = (int) ((newSize.getHeight() - menuBar.getHeight()) / currentNonogram.height());
            final int tiledWidth = (int) (newSize.getWidth() / currentNonogram.width());
            final int tileSize = Math.min(tileHeight, tiledWidth) - 5;

            boardComponent.handleResize(new Dimension(tileSize, tileSize));
        }
    }

    /**
     * Initializes the menu bar.
     *
     * @return menu bar for editor frame
     */
    private JMenuBar getMenu() {

        if (menuBar == null) {

            // create the menu bar.
            menuBar = new JMenuBar();

            // create file menu
            JMenu menu = new JMenu(Messages.getString("EditorFrame.FileMenu"));
            menu.setMnemonic(KeyEvent.VK_F);
            menu.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.FileMenuTooltip"));
            menuBar.add(menu);

            // create menu items for file menu
            JMenuItem menuItem = new JMenuItem(Messages.getString("EditorFrame.NewNonogram"), KeyEvent.VK_N);
            menuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.NewNonogramTooltip"));
            menu.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    // create new nonogram
                    createNewNonogram();
                }
            });

            menuItem = new JMenuItem(Messages.getString("EditorFrame.LoadNonogram"), KeyEvent.VK_L);
            menuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.LoadNonogramTolltip"));
            menu.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    openNonogram();
                }
            });

            saveItem = new JMenuItem(Messages.getString("EditorFrame.SaveNonogram"), KeyEvent.VK_S);
            saveItem.setEnabled(false);
            saveItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.SaveNonogramTooltip"));
            menu.add(saveItem);
            saveItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    saveNonogram();
                }
            });

            saveAsItem = new JMenuItem(Messages.getString("EditorFrame.SaveNonogramAs"), KeyEvent.VK_A);
            saveAsItem.setEnabled(false);
            saveAsItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.SaveNonogramAsTooltip"));
            menu.add(saveAsItem);
            saveAsItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    saveNonogramAs();
                }
            });

            menu.addSeparator();

            publishItem = new JMenuItem(Messages.getString("EditorFrame.PublishNonogram"), KeyEvent.VK_P);
            publishItem.setEnabled(false);
            publishItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.PublishNonogramTooltip"));
            menu.add(publishItem);
            publishItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    publishNonogram();
                }
            });

            menu.addSeparator();

            propertiesItem = new JMenuItem(Messages.getString("EditorFrame.Properties"), KeyEvent.VK_R);
            propertiesItem.setEnabled(false);
            propertiesItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.PropertiesTooltip"));
            menu.add(propertiesItem);
            propertiesItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    showPropertiesDialog();
                }
            });

            menu.addSeparator();

            menuItem = new JMenuItem(Messages.getString("EditorFrame.Exit"), KeyEvent.VK_X);
            menuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.ExitTooltip"));
            menu.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    performExit();
                }
            });

            // create help menu
            menu = new JMenu(Messages.getString("EditorFrame.HelpMenu"));
            menu.setMnemonic(KeyEvent.VK_H);
            menu.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.HelpMenuTooltip"));
            menuBar.add(menu);

            menuItem = new JMenuItem(Messages.getString("EditorFrame.Help"), KeyEvent.VK_H);
            menuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.HelpTooltip"));
            menu.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    showHelpDialog();
                }
            });

            menuItem = new JMenuItem(Messages.getString("EditorFrame.About"), KeyEvent.VK_A);
            menuItem.getAccessibleContext().setAccessibleDescription(Messages.getString("EditorFrame.AboutTooltip"));
            menu.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    showAboutDialog();
                }
            });
        }

        return menuBar;
    }

    /**
     * Initializes a empty content pane and stuffs it in a scroll pane. This content pane will be
     * later filled by <code>buildBoard</code> method.
     *
     * @return scroll pane containing no content yet
     * @see EditorFrame#buildBoard()
     */
    private JScrollPane getEditorPane() {

        JScrollPane scrollPane = null;

        if (contentPane == null) {
            contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());
            scrollPane = new JScrollPane(contentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        return scrollPane;
    }

    /**
     * Builds board out of a editor tile set.
     */
    private void buildBoard() {

        if (boardPanel == null) {

            boardPanel = new JPanel() {
                private static final long serialVersionUID = -5144877072997396393L;

                @Override
                protected void paintComponent(final Graphics g) {
                    final Graphics2D g2 = (Graphics2D) g;
                    BufferedImage cache = null;
                    if (cache == null || cache.getHeight() != getHeight()) {
                        cache = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
                        final Graphics2D g2d = cache.createGraphics();

                        final GradientPaint paint = new GradientPaint(0, 0, new Color(143, 231, 200), 0, getHeight(), Color.WHITE);
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
        repaint();

        boardComponent = new EditorTileSet(currentNonogram, new Dimension(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE));
        boardPanel.add(boardComponent);
        contentPane.add(boardPanel, BorderLayout.CENTER);

        validate();
    }

    /**
     * Exits this frame and FNE.
     */
    private void performExit() {

        this.setVisible(false);
        this.dispose();
        System.exit(1);
    }

    /**
     * Creates a new nonogram.
     */
    private void createNewNonogram() {

        // set file pointer to null
        currentOpenFile = null;
        saveItem.setEnabled(false);
        saveAsItem.setEnabled(true);
        propertiesItem.setEnabled(true);
        publishItem.setEnabled(true);

        // get user input for width and height of nonogram
        propertyDialog.setVisible(true);
        currentNonogram = propertyDialog.getNonogram();

        if (currentNonogram != null) {

            buildBoard();
        }

    }

    /**
     * Saves currently edited nonogram as specific file.
     */
    private void saveNonogramAs() {

        final JFileChooser fc = new JFileChooser();
        File file;

        // set filter for file chooser
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".nonogram");
            }

            @Override
            public String getDescription() {
                return Messages.getString("EditorFrame.NonogramFiles");
            }
        });

        fc.setSelectedFile(new File(currentNonogram.getName() + ".nonogram"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            file = fc.getSelectedFile();

            // TODO add string concatenation of standard file extension!
            // if (file.getName().toLowerCase().endsWith(".nonogram"))
            // file.

            try {
                xmlNonogramSerializer.save(file, currentNonogram);
            } catch (final NullPointerException e) {
                logger.error("Null pointer encountered during nonogram serializing.");
            } catch (final IOException e) {
                logger.error("Could not write serialized nonogram to output stream.");
            }

            currentOpenFile = file;
            saveItem.setEnabled(true);
            saveAsItem.setEnabled(true);
            propertiesItem.setEnabled(true);
            publishItem.setEnabled(true);
        }

    }

    /**
     * Saves currently edited nonogram.
     */
    private void saveNonogram() {

        if (currentOpenFile != null) {
            try {
                xmlNonogramSerializer.save(currentOpenFile, currentNonogram);

            } catch (final NullPointerException e) {

                logger.error("The open nonogram could not be saved because an error occured."); //$NON-NLS-1$

            } catch (final IOException e) {

                logger.error("The open nonogram could not be saved because an error occured."); //$NON-NLS-1$
            }
        }
    }

    /**
     * Opens a new nonogram from file.
     */
    private void openNonogram() {

        final JFileChooser fc = new JFileChooser();

        // set filters for file chooser
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".nonogram");
            }

            @Override
            public String getDescription() {
                return Messages.getString("EditorFrame.NonogramFileType");
            }
        });
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".nonopack");
            }

            @Override
            public String getDescription() {
                return Messages.getString("EditorFrame.CourseFileType");
            }
        });

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            currentOpenFile = fc.getSelectedFile();

            if ("nonogram".equals(getExtension(currentOpenFile))) {

                loadNonogram(currentOpenFile);

            } else {

                loadNonogramFromCourse(currentOpenFile);
            }

        }
    }

    /**
     * Gets the extension of a file.
     *
     * @param f
     *            file to get extension of
     * @return extension of given file
     */
    private static String getExtension(final File f) {

        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;

        // String extension = "";
        // int i = fileName.lastIndexOf('.');
        // int p = Math.max(fileName.lastIndexOf('/'),
        // fileName.lastIndexOf('\\');
        // if (i > p) {
        // extension = fileName.substring(i+1);
        // }
    }

    /**
     * Loads a nonogram from a given file.
     *
     * @param file
     *            file to load nonogram from
     */
    private void loadNonogram(final File file) {

        Nonogram[] n = null;

        if (file.getName().endsWith("." + XMLNonogramSerializer.DEFAULT_FILE_EXTENSION)) {

            try {
                n = xmlNonogramSerializer.load(file);

            } catch (final NullPointerException e) {

                logger.error("The chosen nonogram could not be loaded because an error occured.");

            } catch (final IOException e) {

                logger.error("The chosen nonogram could not be loaded because an error occured.");

            } catch (final NonogramFormatException e) {

                logger.error("The chosen nonogram could not be loaded because an error occured.");
            }

        } else if (file.getName().endsWith("." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {

            final SimpleNonogramSerializer simpleNonogramSerializer = new SimpleNonogramSerializer();

            try {
                n = simpleNonogramSerializer.load(file);

            } catch (final NullPointerException e) {

                logger.error("The chosen nonogram could not be loaded because an error occured.");

            } catch (final IOException e) {

                logger.error("The chosen nonogram could not be loaded because an error occured.");

            } catch (final NonogramFormatException e) {

                logger.error("The chosen nonogram could not be loaded because an error occured.");
            }
        }

        // choose Nonogram from read file to edit
        currentNonogram = n[0];

        finishLoading();
    }

    /**
     * Loads a nonogram from a given course file.
     *
     * @param file
     *            course file to load nonogram from
     */
    private void loadNonogramFromCourse(final File file) {

        Course c = null;

        try {

            c = zipCourseSerializer.load(file);

        } catch (final NullPointerException e) {

            logger.error("An error occured during loading of course file.");

        } catch (final IOException e) {

            logger.error("An error occured during loading of course file.");

        } catch (final NonogramFormatException e) {

            logger.error("An error occured during loading of course file.");

        } catch (final CourseFormatException e) {

            logger.error("An error occured during loading of course file.");
        }

        if (c != null) {

            logger.debug("Opened course view dialog to choose nonogram to edit.");
            courseViewDialog = new CourseViewDialog(this, c);
            currentNonogram = courseViewDialog.getChosenNonogram();

            finishLoading();
        }
    }

    /**
     * Finishes loading.
     */
    private void finishLoading() {

        // paint board only if one nonogram was chosen
        if (currentNonogram != null) {

            logger.debug("Nonogram " + currentNonogram.getName() + " was chosen. Board will be build.");
            buildBoard();

            saveItem.setEnabled(true);
            saveAsItem.setEnabled(true);
            propertiesItem.setEnabled(true);
            publishItem.setEnabled(true);
        } else {

            logger.warn("No nonogram was chosen to be opened.");
        }
    }

    /**
     * Publish currently edited nonogram on a NonoServer.
     */
    private void publishNonogram() {

        final String courseName = "Testing";

        // ...serialize picked nonogram
        final XMLNonogramSerializer ns = new XMLNonogramSerializer();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ns.save(baos, currentNonogram);
        } catch (final NullPointerException e) {
            logger.error("Null pointer encountered during nonogram serializing.");
        } catch (final IOException e) {
            logger.error("Could not write serialized nonogram to output stream.");
        }

        // send nonogram via network
        URL serverURL = null;
        try {
            serverURL = new URL(NONO_SERVER + "/" + courseName + "/" + currentNonogram.getName());
        } catch (final MalformedURLException e) {
            logger.debug("Invalid URL for NonoServer!");
        }
        final ClientResource resource = new ClientResource(serverURL.toString());

        // write from ByteArrayOutputStream into Representation?!
        final Representation rep = new OutputRepresentation(MediaType.TEXT_XML) {

            @Override
            public void write(final OutputStream arg0) throws IOException {
                baos.writeTo(arg0);
            }
        };
        resource.put(rep);
    }

    /**
     * Shows property dialog for currently edited nonogram.
     */
    private void showPropertiesDialog() {

        propertyDialog.setNonogram(currentNonogram);
        propertyDialog.setVisible(true);
        currentNonogram = propertyDialog.getNonogram();
        buildBoard();

    }

    /**
     * Shows a help dialog.
     */
    private void showHelpDialog() {

        // TODO show help dialog
    }

    /**
     * Shows a about dialog.
     */
    private void showAboutDialog() {

        showSplashscreen();
    }

    /**
     * Shows a splash sreen.
     */
    private void showSplashscreen() {

        // show splash screen
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final SplashScreen splash = new SplashScreen("/resources/icon/splashscreen_fne.png");
                splash.setVisible(true);
            }
        });
    }
}
