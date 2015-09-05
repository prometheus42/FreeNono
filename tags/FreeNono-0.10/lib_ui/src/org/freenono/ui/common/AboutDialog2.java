/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono.ui.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.HoverListener;
import org.xhtmlrenderer.swing.LinkListener;

/**
 * Shows a dialog with a title and a content panel that can display HTML code. The dialog is
 * subclassed from <code>FreeNonoDialog</code> to give it the look and feel of all other dialogs.
 * Currently this dialog is used as about and help dialog.
 *
 * @author Christian Wichmann
 */
public class AboutDialog2 extends FreeNonoDialog {

    private static final long serialVersionUID = -3174417107818960578L;

    private static Logger logger = Logger.getLogger(AboutDialog2.class);

    private FSScrollPane scroll;

    private final String programName;
    private final String programVersion;
    private final URL programDescriptionFile;
    private final URL programIconFile;

    /**
     * Constructor for about dialog.
     *
     * @param programName
     *            program name to display in dialog
     * @param programVersion
     *            version String to be displayed in dialog
     * @param programDescriptionFile
     *            URL to HTML document with description text
     * @param programIconFile
     *            URL to icon file
     * @param backgroundColor
     *            background color for dialog
     * @param foregroundColor
     *            foreground color for dialog
     */
    public AboutDialog2(final String programName, final String programVersion, final URL programDescriptionFile, final URL programIconFile,
            final Color foregroundColor, final Color backgroundColor) {

        super(null, foregroundColor, backgroundColor);

        this.programName = programName;
        this.programVersion = programVersion;
        this.programDescriptionFile = programDescriptionFile;
        this.programIconFile = programIconFile;

        initialize();

        addListener();
    }

    /**
     * Convenience constructor for about dialog that omits program version.
     *
     * @param programName
     *            program name to display in dialog
     * @param programDescriptionFile
     *            URL to HTML document with description text
     * @param programIconFile
     *            URL to icon file
     * @param backgroundColor
     *            background color for dialog
     * @param foregroundColor
     *            foreground color for dialog
     */
    public AboutDialog2(final String programName, final URL programDescriptionFile, final URL programIconFile, final Color foregroundColor,
            final Color backgroundColor) {

        this(programName, "", programDescriptionFile, programIconFile, foregroundColor, backgroundColor);
    }

    /**
     * Initialize the about dialog.
     */
    private void initialize() {

        final int dialogSizeWidth = 550;
        final int dialogSizeHeight = 650;
        setSize(dialogSizeWidth, dialogSizeHeight);
        setTitle(programName);

        // use GridBagLayout as layout manager
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gc = new GridBagConstraints();
        getContentPane().setLayout(layout);
        int currentRow = 0;

        // add icon
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        getContentPane().add(getProgramIcon(), gc);

        // add program name
        final int insetText = 5;
        gc.gridx = 1;
        gc.gridy = currentRow++;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(insetText, insetText, insetText, insetText);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(getProgramNameLabel(), gc);

        // add program version if one was given
        if (!"".equals(programVersion)) {
            gc.gridy = currentRow++;
            gc.anchor = GridBagConstraints.NORTHWEST;
            getContentPane().add(getProgramVersionLabel(), gc);
        }

        // add scroll pane with XHTML panel
        final int insetPanel = 15;
        gc.gridx = 0;
        gc.gridy = currentRow++;
        gc.gridwidth = 2;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 8;
        gc.insets = new Insets(insetPanel, insetPanel, insetPanel, insetPanel);
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.BOTH;
        getContentPane().add(getScrollPane(), gc);

        // add close button
        final int insetButton = 5;
        gc.gridx = 1;
        gc.gridy = currentRow++;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(insetButton, insetButton, insetButton, insetButton);
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        getContentPane().add(getCloseButton(), gc);
    }

    /**
     * Creates and returns a new JButton with listener.
     * @return Close button with listener.
     */
    private JButton getCloseButton() {

        final JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                performExit();
            }
        });
        return closeButton;
    }

    /**
     * Creates and returns a new label displaying programName.
     * @return Label with programName as text.
     */
    private JLabel getProgramNameLabel() {

        final JLabel programNameLabel = new JLabel();
        programNameLabel.setFont(FontFactory.createAboutNameFont());
        programNameLabel.setText(programName);
        return programNameLabel;
    }

    /**
     * Creates and returns a new label displaying programVersion.
     * @return Label with programVersion as text.
     */
    private JLabel getProgramVersionLabel() {

        final JLabel programVersionLabel = new JLabel();
        programVersionLabel.setFont(FontFactory.createAboutVersionFont());
        programVersionLabel.setText(programVersion);
        return programVersionLabel;
    }

    /**
     * Create and return the scroll pane containing the description text.
     * @return Scroll pane containing the description text.
     */
    private FSScrollPane getScrollPane() {

        final XHTMLPanel panel = new XHTMLPanel();
        panel.setOpaque(false);
        panel.setInteractive(false);
        panel.addMouseTrackingListener(new HoverListener() {
            /*
             * Change mouse cursor style when mouse is moved over a link in the XHTML. This could be
             * handled by Flying Saucer (XHTML renderer) when the configuration option
             * "xr.use.listeners" would be set on true. The library would then add default listener
             * to handle things like links. But that would also open clicked URLs in the XHTML
             * panel. So we handle this on our own!
             *
             * See also: http://flyingsaucerproject.github.io/flyingsaucer/r8/guide
             * /users-guide-R8.html
             */
            @Override
            public void onMouseOut(final BasicPanel panel, final Box box) {
                final Element x = box.getElement();
                if ("a".equals(x.getNodeName())) {
                    panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

            @Override
            public void onMouseOver(final BasicPanel panel, final Box box) {
                final Element x = box.getElement();
                if ("a".equals(x.getNodeName())) {
                    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        });
        panel.addMouseTrackingListener(new LinkListener() {
            @Override
            public void linkClicked(final BasicPanel panel, final String uri) {
                /*
                 * Change the behavior of the XHTML panel to show links in the browser of the system
                 * if they are clicked. Default behavior of XHTMLPanel would be to set the URI of
                 * this itself to the clicked link and display it.
                 */
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URI(uri));
                    } catch (final IOException e) {
                        logger.debug("Could not open browser to show url: " + uri);
                    } catch (final URISyntaxException e) {
                        logger.debug("Wrong URI: " + uri);
                    }
                }
            }
        });

        scroll = new FSScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setOpaque(false);
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);

        try {
            panel.setDocument(programDescriptionFile.toString());
        } catch (final Exception e) {
            logger.debug("Could not insert file content into HTML pane.");
        }

        return scroll;
    }

    /**
     * Creates and returns JLabel containing the icon.
     *
     * @return label containing the icon
     */
    private JLabel getProgramIcon() {

        JLabel icon;
        if (programIconFile != null) {
            final ImageIcon image = new ImageIcon(programIconFile);
            icon = new JLabel("", image, JLabel.CENTER);
            icon.setToolTipText(programName);
        } else {
            icon = new JLabel();
        }
        return icon;
    }

    /**
     * Add a new listener, so the ESCAPE key closes about dialog.
     */
    private void addListener() {

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "Close");
        getRootPane().getActionMap().put("Close", new AbstractAction() {
            private static final long serialVersionUID = 5568347460071916523L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                performExit();
            }
        });
    }

    /**
     * Method to exit this dialog.
     */
    private void performExit() {

        setVisible(false);
    }
}
