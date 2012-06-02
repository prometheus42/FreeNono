/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
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
package org.freenono;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.freenono.model.Course;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.freenono.serializer.XMLCourseSerializer;
import org.freenono.serializer.ZipCourseSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Glyph2Nono {

	private List<Kanji> chars = new ArrayList<Kanji>();
	private List<BufferedImage> pics = new ArrayList<BufferedImage>();
	private String courseName = null;
	
	private Font font = null;
	private int imgWidth = 30, imgHeight = 30;

	public class Kanji {
		private String name = null;
		private String description = null;
		private String kanji = null;
		
		public Kanji(String name, String kanji, String description) {
			this.name = name;
			this.description = description;
			this.kanji = kanji;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getKanji() {
			return kanji;
		}
		public void setKanji(String kanji) {
			this.kanji = kanji;
		}
	}
	
	public static void main(String[] args) {

		new Glyph2Nono();
	}

	
	public Glyph2Nono() {
		
		font = new Font("MS Mincho", Font.PLAIN, 32); //"MS Gothic"
		
		courseName = "JLPT 1 Kanji";//"Kanji Class 6"
	
		// load kanji information from html files
		//loadCharsFromHTML("/home/christian/Desktop/Nonogramme/Klasse6Kanji.html");
		
		// load kanji into data structure from string
		loadChars();
		
		// convert all saved kanji to nonograms
		convertCharToImage();
		convertImageToNonogram();
	}
	
	public void loadChars() {

		String jlpt1 = "日一国人年大十二本中長出三時行見月分後前生五間上東四今金九入学高円子外八六下来気小七山話女北午百書先名川千水半男西電校語土木聞食車何南万毎白天母火右読友左休父雨";
		
		for (int i = 0; i < jlpt1.length(); i++) {
			
			String name = String.valueOf(jlpt1.charAt(i));
			String description = "Kanji " + name;
			chars.add(new Kanji(name, name, description));
		}
	}
	
	public void loadCharsFromHTML(String filename) {
		
		DocumentBuilder parser;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = parser.parse(new FileInputStream(new File(filename)));

			Element root = doc.getDocumentElement();
			
			Element table = (Element) root.getElementsByTagName("table").item(0);
			
			if (table != null) {
				
				NodeList rowList = table.getElementsByTagName("tr");

				// run through all rows of table
				for (int i = 0; i < rowList.getLength(); i++) {
					
					Node node = rowList.item(i);
					NodeList columnFromRow = node.getChildNodes();
					
					String name = null;
					String description = null;
					String kanji = null;
					
					// read all children of row and assign data to data structures
					for (int j = 0; j < columnFromRow.getLength(); j++) {
					
						Node node2 = columnFromRow.item(j);
						
						switch (j) {
						case 1:
							kanji = node2.getTextContent();
							name = kanji;
							break;
						case 3:
							name = name + " - " + node2.getTextContent();
							break;
						case 5:
							description = "On'yomi: " + node2.getTextContent() + ", ";
							break;
						case 7:
							description = description + "Kun'yomi: " + node2.getTextContent();
							break;
						default:
							break;
						}
					}
					System.out.println(name+" - "+description);
					chars.add(new Kanji(name, kanji, description));
				}
			}

		} catch (ParserConfigurationException e) {

			e.printStackTrace();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (SAXException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void convertCharToImage() {

		for (Kanji cc : chars) {

			BufferedImage img = new BufferedImage(imgWidth, imgHeight,
					BufferedImage.TYPE_BYTE_GRAY);

			Graphics g = img.getGraphics();
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, imgWidth, imgHeight);
			g.setColor(Color.BLACK);
			g.setFont(font);

			FontMetrics fm = g.getFontMetrics(font);
			Rectangle2D rect = fm.getStringBounds(cc.getKanji(), g);

			int textHeight = (int) (rect.getHeight());
			int textWidth = (int) (rect.getWidth());
			// Center text horizontally and vertically
			int x = (imgWidth - textWidth) / 2;
			int y = (imgHeight - textHeight) / 2 + fm.getAscent();

			g.drawString(cc.getKanji(), x, y); // Draw the string.

			pics.add(img);
		}
	}
	
	private void convertImageToNonogram() {
		
		List<Nonogram> listNonograms = new ArrayList<Nonogram>();
		
		for (BufferedImage img : pics) {
			
			boolean[][] field = new boolean[imgWidth][imgHeight];
			
			for (int i = 0; i < img.getHeight(); i++) {
				
				for (int j = 0; j < img.getWidth(); j++) {
					
					field[i][j] = img.getRGB(j, i) == -16777216 ? true : false;
					//System.out.println(img.getRGB(j, i));
				}
			}
			
			Nonogram n = new Nonogram(chars.get(pics.indexOf(img)).getName(), 
					DifficultyLevel.undefined, field);
			n.setAuthor("Christian Wichmann");
			n.setLevel(pics.indexOf(img));
			n.setDifficulty(DifficultyLevel.undefined);
			n.setDescription(chars.get(pics.indexOf(img)).getDescription());
			
			listNonograms.add(n);
		}

		Course c = new Course(courseName, listNonograms);
		try {
			// new XMLCourseSerializer().save(new File(
			// "/home/christian/.FreeNono/nonograms/kanjitest/"), c);
			new ZipCourseSerializer().save(new File(
					"/home/christian/.FreeNono/nonograms/"), c);
			
		} catch (NullPointerException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}