/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
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
package org.freenono.editor;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.freenono.model.Course;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;


public class CourseTableModel extends DefaultTableModel {
	// implements TableModel   extends AbstractTableModel

	private static final long serialVersionUID = -6279961563928143383L;

	private static Logger logger = Logger.getLogger(CourseTableModel.class);

	private EventListenerList listeners = new EventListenerList();

	private final String[] columnNames = {
			Messages.getString("CourseTableModel.LevelColumn"), 
			Messages.getString("CourseTableModel.NameColumn"), 
			Messages.getString("CourseTableModel.AuthorColumn"),
			Messages.getString("CourseTableModel.DifficultyColumn"), 
			Messages.getString("CourseTableModel.HeightColumn"), 
			Messages.getString("CourseTableModel.WidthColumn") };
	private final Class<?>[] columnClasses = { Integer.class, String.class,
			String.class, DifficultyLevel.class, Integer.class, Integer.class,
			String.class };

	private int rowCount = 100;

	private Course course = null;

	
	@Override
	public int getRowCount() {

		return rowCount;
	}

	@Override
	public int getColumnCount() {

		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex) {

		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		return columnClasses[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		if (columnIndex == 3)
			return true;
		else
			return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Nonogram n = course.getNonogram(rowIndex);

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
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (columnIndex == 3) {

			Nonogram n = course.getNonogram(rowIndex);
			n.setLevel((Integer) aValue);
			logger.debug("Changed level of nonogram " + n.getName() + " to " //$NON-NLS-1$ //$NON-NLS-2$
					+ n.getLevel() + "."); //$NON-NLS-1$
		}
	}

	@Override
	public synchronized void addTableModelListener(TableModelListener l) {

		listeners.add(TableModelListener.class, l);
	}

	@Override
	public synchronized void removeTableModelListener(TableModelListener l) {

		listeners.remove(TableModelListener.class, l);
	}

	public Course getCourse() {

		return course;
	}

	public void setCourse(Course course) {

		this.course = course;
		rowCount = course.getNonogramCount();
	}

	public Nonogram getNonogramFromRow(int rowIndex) {

		return course.getNonogram(rowIndex);
	}

}
