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

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Shows a help dialog.
 *
 * @author Christian Wichmann
 */
public class HelpDialog extends JDialog {

    private static final long serialVersionUID = -78784201445320344L;

    // private static Logger logger = Logger.getLogger(HelpDialog.class);

    private JPanel jContentPane = null;

    private JEditorPane jPane = null;

    /**
     * Help dialog constructor.
     * @param owner
     *            Frame owner
     */
    public HelpDialog(final Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {
        final int dialogWidth = 500;
        final int dialogHeight = 500;
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
            final BorderLayout borderLayout = new BorderLayout();
            jContentPane.setLayout(borderLayout);
            jContentPane.add(getJPane(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * Create and return JPane containing the help text.
     * @return JEditorPane containing the text
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
            final String type = "text/html";
            jPane.setContentType(type);
            jPane.setEditorKit(jPane.getEditorKitForContentType(type));
            final StringBuilder content = new StringBuilder(2048);
            content.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"><html>");
            content.append("<body style=\"font:Ubuntu,Verdana, Arial;color:black;text-orientation:center;background-color:#E7E08F;\">");
            content.append("<div style=\"font-size:115%;padding: 10px;margin: 15px;border:3px green solid\">");
            content.append("<h1 style=\"color:black;font-size:140%;text-shadow: #AAA 2px 2px 2px;border-bottom: 1px solid black;");
            content.append("padding: 0 0 0.25em 0;\">How to play ");
            content.append("<span style=\"letter-spacing: 2em;font-variant:small-caps;\">FreeNono</span></h1>");
            content.append("<p>FreeNono is an open-source implementation of the Nonogram game — a logic puzzle ");
            content.append("in which cells in a grid have to be colored or left blank according to numbers given ");
            content.append("at the side of the grid to reveal a hidden picture.</p>");
            content.append("<p>To win the the game you have to clear this hidden picture. By deducing from the ");
            content.append("given numbers you can mark (left mouse click) this fields. ");
            content.append("All fields that do not belong to the picture you can mark as empty (right mouse click). ");
            content.append("After you marked all fields of the picture correctly you win the game.</p></div>");
            content.append("<div style=\"color:#C88FE7;font-size:small;padding: 10px;margin: 15px;border:3px green solid;\">");
            content.append("The FreeNono project is released under the GNU General Public License v2 or newer.</div");
            content.append("</body></html>");
            // color orange: #E79D8F
            // color lilac: #C88FE7
            jPane.setText(content.toString());
        }
        return jPane;
    }

    /**
     * Closes the help dialog.
     */
    private void close() {
        setVisible(false);
        dispose();
    }

}
