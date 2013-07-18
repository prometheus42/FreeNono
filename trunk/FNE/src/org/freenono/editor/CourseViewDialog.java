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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.Nonogram;

/**
 * Shows all nonograms of on course to choose one for editing.
 * 
 * @author Christian Wichmann
 */
public class CourseViewDialog extends JDialog {

    private static Logger logger = Logger.getLogger(CourseViewDialog.class);

    private static final long serialVersionUID = 1508331836999609976L;

    private Course course = null;
    private Nonogram chosenNonogram = null;
    private JTable courseTable = null;
    private CourseTableModel courseTableModel = null;

    /**
     * Initializes this course view dialog.
     * 
     * @param parent
     *            parent frame of this dialog
     * @param c
     *            course to show in this dialog
     */
    public CourseViewDialog(final JFrame parent, final Course c) {

        super(parent);

        this.course = c;

        initialize();

        addListeners();
    }

    /**
     * Adds listeners for selections on the table by mouse or key.
     */
    private void addListeners() {

        courseTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(final TableModelEvent e) {

                logger.debug("tableChanged event.");
            }
        });

        courseTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent e) {

                        logger.debug("valueChanged event.");
                    }
                });

        courseTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent e) {

                logger.debug("mouseClicked event.");

                // if (e.getComponent().isEnabled() && e.getButton() ==
                // MouseEvent.BUTTON1 && e.getClickCount() == 2)
                if (e.getClickCount() >= 2) {

                    chosenNonogram = courseTableModel
                            .getNonogramFromRow(courseTable.getSelectedRow());
                    logger.debug("Nonogram " + chosenNonogram.getName()
                            + " from course view chosen by user.");
                }

                // int row = ( (JTable) e.getSource()
                // ).rowAtPoint(e.getPoint());
                // int column = ( (JTable) e.getSource()
                // ).columnAtPoint(e.getPoint());
                // JTable target = (JTable)e.getSource();
                // int row = target.getSelectedRow();
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
            }
        });

        this.addWindowListener(new WindowListener() {

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
    }

    /**
     * Initializes this course view dialog.
     */
    private void initialize() {

        this.setSize(800, 400);
        this.setLocationRelativeTo(null);
        this.setName(Messages.getString("CourseViewDialog.Name")); //$NON-NLS-1$
        this.setTitle(Messages.getString("CourseViewDialog.Title")); //$NON-NLS-1$
        this.setModalityType(ModalityType.DOCUMENT_MODAL);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);

        courseTableModel = new CourseTableModel();
        courseTableModel.setCourse(course);
        courseTable = new JTable(courseTableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getColumnModel().getColumn(0).setMaxWidth(50);
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(75);
        courseTable.getColumnModel().getColumn(4).setMaxWidth(50);
        courseTable.getColumnModel().getColumn(5).setMaxWidth(50);
        courseTable.getTableHeader().setReorderingAllowed(false);
        courseTable.getTableHeader().setResizingAllowed(false);

        this.getContentPane().add(new JScrollPane(courseTable));
        // courseTable.grabFocus();

        this.setVisible(true);
    }

    /**
     * Exits this dialog.
     */
    private void performExit() {

        setVisible(false);
    }

    /**
     * Returns the nonogram the user has chosen out of the given course.
     * 
     * @return chosen nonogram from given course
     */
    public final Nonogram getChosenNonogram() {

        return chosenNonogram;
    }
}
