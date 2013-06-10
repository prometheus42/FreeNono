/**
 * This package contains all components to paint a board into a
 * scrollable panel with column and row header and status box.
 * 
 * The board as well as the column and row header are build from
 * a base class BoardTile. It paints a tile which can be controlled
 * by its setter to show a label, a cross or an occupied field. 
 * 
 * In the overall UI only the BoardPanel class should be inserted. 
 * All control functions are executed over the event system, defined
 * by the GameEventHelper.
 * 
 * @author Christian Wichmann
 */
package org.freenono.board;