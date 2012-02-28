/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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
package de.ichmann.markusw.java.apps.freenono.serializer.nonogram;

public class ZipSerializer {

	/* Zip file handling */
	//	
	// public Nonogram loadZipFile(File f) throws IOException,
	// ParserConfigurationException, SAXException, InvalidFormatException {
	// // TODO implement here
	//		
	// ZipFile zipFile = new ZipFile(f);
	// ZipEntry nonogramEntry = zipFile.getEntry("nonogram.xml");
	// InputStream is = zipFile.getInputStream(nonogramEntry);
	//
	//		
	// Validator validator = getXMLValidator();
	//
	// DocumentBuilder parser =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder();
	// Document doc = parser.parse(is);
	//
	// validator.validate(new DOMSource(doc));
	//
	// Element root = doc.getDocumentElement();
	//		
	// loadXMLSettings(root);
	//
	// loadXMLHighscores(root);
	//
	// loadXMLNonograms(root);
	//		
	// return null;
	// }
	//	
	// public void saveZipFile(Nonogram n, File f) throws IOException {
	// // TODO implement here
	//	    
	// // ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(f,
	// false));
	// // //zos.setLevel(0); //store only
	// // zos.setLevel(9); //max compression
	// //
	// // ZipEntry nonogramEntry = new ZipEntry("nonogram.xml");
	// // zos.putNextEntry(nonogramEntry);
	// //
	// // // <FreeNono>
	// //
	// // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	// // DocumentBuilder builder = factory.newDocumentBuilder();
	// // Document doc = builder.newDocument();
	// // // TODO will this element be used a document node?
	// // Element root = doc.createElement("FreeNono");
	// // {
	// // // <Settings>
	// // // <Setting name="..." value="..."/>
	// // // <MaxTime>...</MaxTime>
	// // // <MaxMoveCount>...</MaxMoveCount>
	// // // <MarkInvalidMoves>...</MarkInvalidMoves>
	// // // <CountMarkedFields>...</CountMarkedFields>
	// // // </Settings>
	// // Element settings = root.addElement("Settings");
	// // {
	// // Element setting = settings.addElement("Setting");
	// // setting.setAttribute("name", "MaxTime");
	// // setting.setAttribute("value", 300000.toString());
	// //
	// // Element maxTime = settings.addElement("MaxTime");
	// // maxTime.setText(300000.toString());
	// //
	// // Element maxMoveCount = settings.addElement("MaxMoveCount");
	// // maxTime.setText(10.toString());
	// //
	// // Element markInvalidMoves = settings.addElement("MarkInvalidMoves");
	// // maxTime.setText(true.toString());
	// //
	// // Element countMarkedFields = settings.addElement("CountMarkedFields");
	// // maxTime.setText(true.toString());
	// // }
	// //
	// // // <Highscores>
	// // // <Highscore nonogram=".." time="..." score="..."/>
	// // // </Highscores>
	// // Element highscores = root.addElement("Highscores");
	// // {
	// // Element highscore = highscores.addElement("Highscore");
	// // highscore.setAttribute("nonogram", "SingleDot");
	// // highscore.setAttribute("time", 45000.toString());
	// // highscore.setAttribute("score", 10000.toString());
	// // }
	// //
	// // // <Nonograms>
	// // // <Nonogram id="..." height="..." width="..." name="..."
	// difficulty="..." desc="...">
	// // // <line> ... </line>
	// // // </Nonogram>
	// // // </Nonograms>
	// // Element nonograms = root.addElement("Nonograms");
	// // {
	// // Element nonogram = nonograms.addElement("Nonogram");
	// // nonogram.setAttribute("id", "0001");
	// // nonogram.setAttribute("height", 10.toString());
	// // nonogram.setAttribute("width", 10.toString());
	// // nonogram.setAttribute("difficulty", 0.toString());
	// // nonogram.setAttribute("desc", "Somethings in place of anything");
	// // for(int i = 0; i < 10; i++)
	// // {
	// // String s = "";
	// // Element line = nonogram.addElement("line");
	// // for(int j = 0; j < 10; j++)
	// // {
	// // if((i+j) % 2 == 0) {
	// // s += "_" + " ";
	// // }
	// // else {
	// // s += "x" + " ";
	// // }
	// // }
	// // line.setText(s);
	// // }
	// // }
	// // }
	// // XMLWriter writer = new XMLWriter(zos);
	// // writer.write(doc);
	// // writer.close();
	// //
	// // zos.closeEntry();
	// // zos.close();
	// }
	
}
