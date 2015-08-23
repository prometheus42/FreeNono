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

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.freenono.model.data.Course;
import org.freenono.model.data.DifficultyLevel;
import org.freenono.model.data.Nonogram;

/**
 * Table model to show course information.
 *
 * @author Christian Wichmann
 */
public class CourseTableModel extends DefaultTableModel {

    // implements TableModel extends AbstractTableModel

    private static final long serialVersionUID = -6279961563928143383L;

    private static Logger logger = Logger.getLogger(CourseTableModel.class);

    private final EventListenerList listeners = new EventListenerList();

    private final String[] columnNames = {Messages.getString("CourseTableModel.LevelColumn"),
            Messages.getString("CourseTableModel.NameColumn"), Messages.getString("CourseTableModel.AuthorColumn"),
            Messages.getString("CourseTableModel.DifficultyColumn"), Messages.getString("CourseTableModel.HeightColumn"),
            Messages.getString("CourseTableModel.WidthColumn")};
    private final Class<?>[] columnClasses = {Integer.class, String.class, String.class, DifficultyLevel.class, Integer.class,
            Integer.class, String.class};

    private int rowCount = 100;

    private Course course = null;

    @Override
    public final int getRowCount() {

        return rowCount;
    }

    @Override
    public final int getColumnCount() {

        return columnNames.length;
    }

    @Override
    public final String getColumnName(final int columnIndex) {

        return columnNames[columnIndex];
    }

    @Override
    public final Class<?> getColumnClass(final int columnIndex) {

        return columnClasses[columnIndex];
    }

    @Override
    public final boolean isCellEditable(final int rowIndex, final int columnIndex) {

        return columnIndex == 3;
    }

    @Override
    public final Object getValueAt(final int rowIndex, final int columnIndex) {

        final Nonogram n = course.getNonogram(rowIndex);

        switch (columnIndex) {
        case 0:
            return n.getLevel();

        case 1:
            return n.getName();

        case 2:
            return n.getAuthor();

        case 3:
            return n.getDifficulty();

        case 4:
            return n.height();

        case 5:
            return n.width();

        default:
            return ""; //$NON-NLS-1$
        }
    }

    @Override
    public final void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {

        if (columnIndex == 3) {

            final Nonogram n = course.getNonogram(rowIndex);
            n.setLevel((Integer) aValue);
            logger.debug("Changed level of nonogram " + n.getName() + " to " + n.getLevel() + ".");
        }
    }

    @Override
    public final synchronized void addTableModelListener(final TableModelListener l) {

        listeners.add(TableModelListener.class, l);
    }

    @Override
    public final synchronized void removeTableModelListener(final TableModelListener l) {

        listeners.remove(TableModelListener.class, l);
    }

    /**
     * Gets course to be shown in this table.
     *
     * @return course of this table
     */
    public final Course getCourse() {

        return course;
    }

    /**
     * Sets course to be shown in this table.
     *
     * @param course
     *            course to be shown in this table
     */
    public final void setCourse(final Course course) {

        this.course = course;
        rowCount = course.getNonogramCount();
    }

    /**
     * Gets nonogram from course based on its row in the course table.
     *
     * @param rowIndex
     *            row of nonogram to return
     * @return nonogram for a given row in this table
     */
    public final Nonogram getNonogramFromRow(final int rowIndex) {

        return course.getNonogram(rowIndex);
    }
}
