/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Christian Wichmann
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
package org.freenono.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;

public class AskQuestionButton extends JButton {

	private static final long serialVersionUID = -5626497463053035645L;

	// private String text;
	private int innerMargin = 8;
	private int outerMargin = 4;
	private int widthSegment;
	private int heightSegment;
	private int height;
	private int width;

	private Font textFont;
	private Color textColor = new Color(200, 200, 200);

	// fields for calculating text width and height
	private static final Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
	static {
		map.put(TextAttribute.FAMILY, "Sans Serif");
		map.put(TextAttribute.SIZE, new Float(18.0));
	}
	private static AttributedString text;
	private int paragraphStart;
	private int paragraphEnd;
	private LineBreakMeasurer lineMeasurer;

	public AskQuestionButton(String text) {

		super();

		this.text = new AttributedString(text, map);

		// this.textFont = new Font("Free Sans", Font.PLAIN, 16);

		setPreferredSize(new Dimension(500, 150));
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

		// do calculations
		width = this.getWidth();
		height = this.getHeight();
		widthSegment = width / 10;
		heightSegment = height / 2;

		// background
		g2d.setColor(new Color(0, 0, 60));
		g2d.fillRect(0, 0, width, height);

		Polygon p1 = new Polygon();
		p1.addPoint(widthSegment, outerMargin);
		p1.addPoint(width - widthSegment, outerMargin);
		p1.addPoint(width - outerMargin, heightSegment);
		p1.addPoint(width - widthSegment, height - outerMargin);
		p1.addPoint(widthSegment, height - outerMargin);
		p1.addPoint(outerMargin, heightSegment);
		p1.addPoint(widthSegment, outerMargin);
		g2d.setColor(textColor);
		g2d.fillPolygon(p1);

		Polygon p2 = new Polygon();
		p2.addPoint(widthSegment, innerMargin);
		p2.addPoint(width - widthSegment, innerMargin);
		p2.addPoint(width - innerMargin, heightSegment);
		p2.addPoint(width - widthSegment, height - innerMargin);
		p2.addPoint(widthSegment, height - innerMargin);
		p2.addPoint(innerMargin, heightSegment);
		p2.addPoint(widthSegment, innerMargin);
		g2d.setColor(new Color(0, 0, 110));
		g2d.fillPolygon(p2);

		g2d.setColor(textColor);
		paintFont(g2d);
		// g2d.setFont(textFont);
		// g2d.drawString(text, widthSegment, heightSegemnt);
	}

	private void paintFont(Graphics2D g2d) {

		if (lineMeasurer == null) {
			AttributedCharacterIterator paragraph = text.getIterator();
			paragraphStart = paragraph.getBeginIndex();
			paragraphEnd = paragraph.getEndIndex();
			FontRenderContext frc = g2d.getFontRenderContext();
			lineMeasurer = new LineBreakMeasurer(paragraph, frc);
		}

		// Set break width to width of Component.
		float breakWidth = (float) (getSize().width - widthSegment * 2);
		float drawPosY = (float) heightSegment / 3;
		// Set position to the index of the first character in the paragraph.
		lineMeasurer.setPosition(paragraphStart);

		// Get lines until the entire paragraph has been displayed.
		while (lineMeasurer.getPosition() < paragraphEnd) {

			// Retrieve next layout. A cleverer program would also cache
			// these layouts until the component is re-sized.
			TextLayout layout = lineMeasurer.nextLayout(breakWidth);

			// Compute pen x position. If the paragraph is right-to-left we
			// will align the TextLayouts to the right edge of the panel.
			// Note: this won't occur for the English text in this sample.
			// Note: drawPosX is always where the LEFT of the text is placed.
			float drawPosX = layout.isLeftToRight() ? widthSegment
					: breakWidth - layout.getAdvance();

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(g2d, drawPosX, drawPosY);

			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}
	}

	// public static void main(String[] args) {
	//
	// JFrame frame = new JFrame();
	// frame.setLayout(new GridLayout());
	// frame.add(new AskQuestionButton(
	// "Testfrage: Wie viele Häschen skgj slfkgj ösfldjgljsfd lkgj slfdkjg lsfjdgklj sfdlkgj lksfdjglk jsfdlgh itzen auf dem Feld?"));
	// //frame.setSize(500, 150);
	// frame.setVisible(true);
	// }
}
