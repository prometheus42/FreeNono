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
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.freenono.model.Course;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.freenono.serializer.XMLCourseSerializer;
import org.freenono.serializer.ZipCourseSerializer;


public class Glyph2Nono {

	public static List<String> chars = new ArrayList<String>();
	public static List<BufferedImage> pics = new ArrayList<BufferedImage>();
	public static Map<String, String> chars2 = new HashMap<String, String>();
	
	public static Font font = null;
	public static int imgWidth = 30, imgHeight = 30;

	
	public static void main(String[] args) {

		font = new Font("MS Mincho", Font.PLAIN, 32);//"MS Gothic"
		
		loadChars();
		
		//loadCharsFromHTML();

		convertCharToImage();
		
		convertImageToNonogram();
	}

	
	public static void loadChars() {

		String jlpt1 = "日一国人年大十二本中長出三時行見月分後前生五間上東四今金九入学高円子外八六下来気小七山話女北午百書先名川千水半男西電校語土木聞食車何南万毎白天母火右読友左休父雨";
		
		for (int i = 0; i < jlpt1.length(); i++) {
			
			chars.add(String.valueOf(jlpt1.charAt(i)));
		}
	}
	
	public static void loadCharsFromHTML() {
		
	}

	private static void convertCharToImage() {

		for (String cc : chars) {

			BufferedImage img = new BufferedImage(imgWidth, imgHeight,
					BufferedImage.TYPE_BYTE_GRAY);

			Graphics g = img.getGraphics();
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, imgWidth, imgHeight);
			g.setColor(Color.BLACK);
			g.setFont(font);

			FontMetrics fm = g.getFontMetrics(font);
			Rectangle2D rect = fm.getStringBounds(cc, g);

			int textHeight = (int) (rect.getHeight());
			int textWidth = (int) (rect.getWidth());
			// Center text horizontally and vertically
			int x = (imgWidth - textWidth) / 2;
			int y = (imgHeight - textHeight) / 2 + fm.getAscent();

			g.drawString(cc, x, y); // Draw the string.

			try {

				ImageIO.write((RenderedImage) img, "png", new File(
						"/home/christian/Desktop/" + cc + ".png"));

			} catch (IOException e) {

				e.printStackTrace();
			}
			
			pics.add(img);
		}
	}
	
	private static void convertImageToNonogram() {
		
		List<Nonogram> listNonograms = new ArrayList<Nonogram>();
		
		for (BufferedImage img : pics) {
			
			boolean[][] field = new boolean[imgWidth][imgHeight];
			
			for (int i = 0; i < img.getHeight(); i++) {
				
				for (int j = 0; j < img.getWidth(); j++) {
					
					field[i][j] = img.getRGB(j, i) == -16777216 ? true : false;
					//System.out.println(img.getRGB(j, i));
				}
			}
			
			Nonogram n = new Nonogram(chars.get(pics.indexOf(img)), DifficultyLevel.undefined, field);
			n.setAuthor("Christian Wichmann");
			n.setLevel(pics.indexOf(img));
			n.setDifficulty(DifficultyLevel.undefined);
			n.setDescription("Kanji " + chars.get(pics.indexOf(img)));
			
			listNonograms.add(n);
		}

		Course c = new Course("JLPT 1 Kanji", listNonograms);
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