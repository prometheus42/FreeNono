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

import javax.swing.JPanel;

import java.awt.Frame;
import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.WindowConstants;

/**
 * Shows an about dialog. Is deprecated by AboutDialog2.
 * @author Christian Wichmann
 */
@Deprecated
public class AboutDialog extends JDialog {

    private static final long serialVersionUID = -78784201445320344L;

    // private static Logger logger = Logger.getLogger(AboutDialog.class);

    private JPanel jContentPane = null;

    private JEditorPane jPane = null;

    /**
     * Constructor for about dialog.
     * @param owner
     *            Frame owner
     */
    public AboutDialog(final Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {
        final int dialogWidth = 600;
        final int dialogHeight = 400;
        this.setSize(dialogWidth, dialogHeight);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);

        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane.
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            BorderLayout borderLayout = new BorderLayout();
            jContentPane.setLayout(borderLayout);
            jContentPane.add(getJPane(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * Create and return a jpane.
     * @return JEditorPane
     */
    private JEditorPane getJPane() {
        if (jPane == null) {
            jPane = new JEditorPane();
            jPane.setEditable(false);

            /* close About box when mouse is clicked or key is pressed */
            jPane.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(final java.awt.event.KeyEvent e) {
                    close();
                }

                @Override
                public void keyTyped(final java.awt.event.KeyEvent e) {
                    close();
                }

                @Override
                public void keyReleased(final java.awt.event.KeyEvent e) {
                    close();
                }
            });
            jPane.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent e) {
                    close();
                }

                @Override
                public void mousePressed(final java.awt.event.MouseEvent e) {
                    close();
                }

                @Override
                public void mouseReleased(final java.awt.event.MouseEvent e) {
                    close();
                }
            });

            /* insert content for editor pane */
            String type = "text/html";
            jPane.setContentType(type);
            jPane.setEditorKit(jPane.getEditorKitForContentType(type));
            String content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
            content += "<html><body style=\"text-orientation:center;background-color:#A68FE7;\"><div style=\"background-color:#E7E08F;";
            content += "width:460px;height:300px;padding: 10px;margin: 15px;border:3px green solid\">";
            content += "<h1 style=\"letter-spacing: 0.25em;text-shadow: #AAA 2px 2px 2px;border-bottom: 1px solid black;";
            content += "padding: 0 0 0.25em 0;\">FreeNono::About</h1><dl style=\"font-family:monospace;color:black;";
            content += "margin:10em;border:2px solid #8B77C3;\">";
            content += "<dt>Software Architect</dt><dd>Markus Wichmann</dd>";
            content += "<dt>Graphical Concept and Software Developer</dt><dd>Christian Wichmann</dd>";
            content += "<dt>Musical Coordinator</dt><dd>Martin Wichmann</dd>";
            content += "</dl></div></body></html>";
            jPane.setText(content);
        }
        return jPane;
    }

    /**
     * Method to close the dialog.
     */
    private void close() {
        setVisible(false);
        dispose();
    }

}
