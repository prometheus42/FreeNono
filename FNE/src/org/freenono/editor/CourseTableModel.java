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
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.freenono.model.Course;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;


public class CourseTableModel implements TableModel {

	private static Logger logger = Logger.getLogger(CourseTableModel.class);

	private EventListenerList listeners = new EventListenerList();
	
	private Course course = null;
	
	private int rowCount = 100;
	private int columnCount = 6;
	
	
	@Override
	public int getRowCount() {

		return rowCount;
	}

	@Override
	public int getColumnCount() {
		
		return columnCount;
	}

	@Override
	public String getColumnName(int columnIndex) {
		
		switch (columnIndex) {
		case 0:
			return "Name";

		case 1:
			return "Autor";

		case 2:
			return "Schwierigkeit";

		case 3:
			return "Level";

		case 4:
			return "Höhe";

		case 5:
			return "Breite";

		default:
			return "";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {
		case 0:
			return String.class;

		case 1:
			return String.class;

		case 2:
			return DifficultyLevel.class;

		case 3:
			return Integer.class;

		case 4:
			return Integer.class;

		case 5:
			return Integer.class;

		default:
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		Nonogram n = course.getNonogram(rowIndex);
		
		switch (columnIndex) {
		case 0:
			return n.getName();

		case 1:
			return n.getAuthor();

		case 2:
			return n.getDifficulty();

		case 3:
			return n.getLevel();

		case 4:
			return n.height();

		case 5:
			return n.width();

		default:
			return "";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
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
