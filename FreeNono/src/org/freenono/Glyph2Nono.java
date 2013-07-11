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
import org.freenono.serializer.ZipCourseSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper tool to automatically render glyphs like japanese characters into
 * nonograms.
 * 
 * @author Christian Wichmann
 */
public final class Glyph2Nono {

    private List<Kanji> chars = new ArrayList<Kanji>();
    private List<BufferedImage> pics = new ArrayList<BufferedImage>();
    private String courseName = null;

    private Font font = null;
    private final int imgWidth = 20;
    private final int imgHeight = 20;

    /**
     * Inner class that represents one Kanji.
     * @author Christian Wichmann
     */
    public class Kanji {
        private String name = null;
        private String description = null;
        private String kanji = null;

        /**
         * Constructor that sets all values.
         * @param name
         *            Name
         * @param kanji
         *            Kanji
         * @param description
         *            Description
         */
        public Kanji(final String name, final String kanji,
                final String description) {
            this.name = name;
            this.description = description;
            this.kanji = kanji;
        }

        /**
         * Getter name.
         * @return name
         */
        public final String getName() {
            return name;
        }

        /**
         * Setter name.
         * @param name
         *            name to set
         */
        public final void setName(final String name) {
            this.name = name;
        }

        /**
         * Getter description.
         * @return description
         */
        public final String getDescription() {
            return description;
        }

        /**
         * Setter description.
         * @param description
         *            description to set
         */
        public final void setDescription(final String description) {
            this.description = description;
        }

        /**
         * Getter kanji.
         * @return kanji
         */
        public final String getKanji() {
            return kanji;
        }

        /**
         * Setter kanji.
         * @param kanji
         *            kanji to set
         */
        public final void setKanji(final String kanji) {
            this.kanji = kanji;
        }
    }

    /**
     * Main method that creates nonograms based on japanese glyphs.
     * @param args
     *            Commandline arguments.
     */
    public static void main(final String[] args) {
        new Glyph2Nono();
    }

    /**
     * Construtor that creates glyphes and converts these to nonograms.
     */
    private Glyph2Nono() {
        // Alternative fonts: "MS Gothic", "MS Mincho"

        // load kanji information from html files
        // font = new Font("Ume P Gothic", Font.PLAIN, 32);
        // courseName = "Kanji Class 6"
        // loadCharsFromHTML("/home/christian/Desktop/Nonogramme/Klasse6Kanji.html");

        // load kanji into data structure from string
        // font = new Font("Ume P Gothic", Font.PLAIN, 32);
        // courseName = "JLPT 1 Kanji";
        // loadChars();

        final int fontSize = 20;

        // set hiragana as glyphs
        font = new Font("Ume UI Gothic", Font.PLAIN, fontSize);
        courseName = "Hiragana";
        loadHiragana();

        // set katakana as glyphs
        font = new Font("Ume UI Gothic", Font.PLAIN, fontSize);
        courseName = "Katakana";
        loadKatakana();

        // convert all saved kanji to nonograms
        convertCharToImage();
        convertImageToNonogram();
    }

    /**
     * Load hiragana symbols and store in 'chars' map.
     */
    private void loadHiragana() {

        HashMap<String, String> hiragana = new HashMap<String, String>();
        hiragana.put("あ", "a");
        hiragana.put("い", "i");
        hiragana.put("う", "u");
        hiragana.put("え", "e");
        hiragana.put("お", "o");
        hiragana.put("か", "ka");
        hiragana.put("き", "ki");
        hiragana.put("く", "ku");
        hiragana.put("け", "ke");
        hiragana.put("こ", "ko");
        // hiragana.put("きゃ", "kya");
        // hiragana.put("きゅ", "kyu");
        // hiragana.put("きょ", "kyo");
        hiragana.put("さ", "sa");
        hiragana.put("し", "shi");
        hiragana.put("す", "su");
        hiragana.put("せ", "se");
        hiragana.put("そ", "so");
        // hiragana.put("しゃ", "sha");
        // hiragana.put("しゅ", "shu");
        // hiragana.put("しょ", "sho");
        hiragana.put("た", "ta");
        hiragana.put("ち", "chi");
        hiragana.put("つ", "tsu");
        hiragana.put("て", "te");
        hiragana.put("と", "to");
        // hiragana.put("ちゃ", "cha");
        // hiragana.put("ちゅ", "chu");
        // hiragana.put("ちょ", "cho");
        hiragana.put("な", "na");
        hiragana.put("に", "ni");
        hiragana.put("ぬ", "nu");
        hiragana.put("ね", "ne");
        hiragana.put("の", "no");
        // hiragana.put("にゃ", "nya");
        // hiragana.put("にゅ", "nyu");
        // hiragana.put("にょ", "nyo");
        hiragana.put("は", "ha");
        hiragana.put("ひ", "hi");
        hiragana.put("ふ", "fu");
        hiragana.put("へ", "he");
        hiragana.put("ほ", "ho");
        // hiragana.put("ひゃ", "hya");
        // hiragana.put("ひゅ", "hyu");
        // hiragana.put("ひょ", "hyo");
        hiragana.put("ま", "ma");
        hiragana.put("み", "mi");
        hiragana.put("む", "mu");
        hiragana.put("め", "me");
        hiragana.put("も", "mo");
        // hiragana.put("みゃ", "mya");
        // hiragana.put("みゅ", "myu");
        // hiragana.put("みょ", "myo");
        hiragana.put("や", "ya");
        hiragana.put("ゆ", "yu");
        hiragana.put("よ", "yo");
        hiragana.put("ら", "ra");
        hiragana.put("り", "ri");
        hiragana.put("る", "ru");
        hiragana.put("れ", "re");
        hiragana.put("ろ", "ro");
        // hiragana.put("りゃ", "rya");
        // hiragana.put("りゅ", "ryu");
        // hiragana.put("りょ", "ryo");
        hiragana.put("わ", "wa");
        hiragana.put("を", "wo");
        hiragana.put("ん", "n");
        // が ga ぎ gi ぐ gu げ ge ご go ぎゃ gya ぎゅ gyu ぎょ gyo
        // ざ za じ ji ず zu ぜ ze ぞ zo じゃ ja じゅ ju じょ jo
        // だ da ぢ (ji) づ (zu) で de ど do ぢゃ (ja) ぢゅ (ju) ぢょ (jo)
        // ば ba び bi ぶ bu べ be ぼ bo びゃ bya びゅ byu びょ byo
        // ぱ pa ぴ pi ぷ pu ぺ pe ぽ po ぴゃ pya ぴゅ pyu ぴょ pyo

        for (Map.Entry<String, String> e : hiragana.entrySet()) {
            chars.add(new Kanji(e.getValue(), e.getKey(), "Hiragana: "
                    + e.getValue()));
        }
    }

    /**
     * Load katakana symbols and store in 'chars' map.
     */
    private void loadKatakana() {

        HashMap<String, String> katakana = new HashMap<String, String>();
        katakana.put("ア", "a");
        katakana.put("イ", "i");
        katakana.put("ウ", "u");
        katakana.put("エ", "e");
        katakana.put("オ", "o");
        katakana.put("カ", "ka");
        katakana.put("キ", "ki");
        katakana.put("ク", "ku");
        katakana.put("ケ", "ke");
        katakana.put("コ", "ko");
        // hiragana.put("キャ", "kya");
        // hiragana.put("キュ", "kyu");
        // hiragana.put("キョ", "kyo");
        katakana.put("サ", "sa");
        katakana.put("シ", "shi");
        katakana.put("ス", "su");
        katakana.put("セ", "se");
        katakana.put("ソ", "so");
        // hiragana.put("シャ", "sha");
        // hiragana.put("シュ", "shu");
        // hiragana.put("ショ", "sho");
        katakana.put("タ", "ta");
        katakana.put("チ", "chi");
        katakana.put("ツ", "tsu");
        katakana.put("テ", "te");
        katakana.put("ト", "to");
        // hiragana.put("チャ", "cha");
        // hiragana.put("チュ", "chu");
        // hiragana.put("チョ", "cho");
        katakana.put("ナ", "na");
        katakana.put("ニ", "ni");
        katakana.put("ヌ", "nu");
        katakana.put("ネ", "ne");
        katakana.put("ノ", "no");
        // hiragana.put("ニャ", "nya");
        // hiragana.put("ニュ", "nyu");
        // hiragana.put("ニョ", "nyo");
        katakana.put("ハ", "ha");
        katakana.put("ヒ", "hi");
        katakana.put("フ", "fu");
        katakana.put("ヘ", "he");
        katakana.put("ホ", "ho");
        // hiragana.put("ヒャ", "hya");
        // hiragana.put("ヒュ", "hyu");
        // hiragana.put("ヒョ", "hyo");
        katakana.put("マ", "ma");
        katakana.put("ミ", "mi");
        katakana.put("ム", "mu");
        katakana.put("メ", "me");
        katakana.put("モ", "mo");
        // hiragana.put("ミア", "mya");
        // hiragana.put("ミュ", "myu");
        // hiragana.put("ミョ", "myo");
        katakana.put("ヤ", "ya");
        katakana.put("ユ", "yu");
        katakana.put("ヨ", "yo");
        katakana.put("ラ", "ra");
        katakana.put("リ", "ri");
        katakana.put("ル", "ru");
        katakana.put("レ", "re");
        katakana.put("ロ", "ro");
        // hiragana.put("", "rya");
        // hiragana.put("", "ryu");
        // hiragana.put("", "ryo");
        katakana.put("ワ", "wa");
        katakana.put("ヲ", "wo");
        katakana.put("ン", "n");

        for (Map.Entry<String, String> e : katakana.entrySet()) {
            chars.add(new Kanji(e.getValue(), e.getKey(), "Katakana: "
                    + e.getValue()));
        }
    }

    /**
     * Load kanjis and store them in 'chars' list.
     */
    @SuppressWarnings("unused")
    private void loadChars() {

        String jlpt1 = "日一国人年大十二本中長出三時行見月分後前生五間上東四今金九入学高円子外八六下来気小七山話女北午百書先名川千水半男西電校語土木聞食車何南万毎白天母火右読友左休父雨";

        for (int i = 0; i < jlpt1.length(); i++) {
            String name = String.valueOf(jlpt1.charAt(i));
            String description = "Kanji " + name;
            chars.add(new Kanji(name, name, description));
        }
    }

    /**
     * Load chars from an html document to 'chars' list.
     * @param filename
     *            html document to parse.
     */
    @SuppressWarnings("unused")
    private void loadCharsFromHTML(final String filename) {

        DocumentBuilder parser;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = parser
                    .parse(new FileInputStream(new File(filename)));

            Element root = doc.getDocumentElement();

            Element table = (Element) root.getElementsByTagName("table")
                    .item(0);

            if (table != null) {

                NodeList rowList = table.getElementsByTagName("tr");

                // run through all rows of table
                for (int i = 0; i < rowList.getLength(); i++) {

                    Node node = rowList.item(i);
                    NodeList columnFromRow = node.getChildNodes();

                    String name = null;
                    String description = null;
                    String kanji = null;

                    // read all children of row and assign data to data
                    // structures
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
                            description = "On'yomi: " + node2.getTextContent()
                                    + ", ";
                            break;
                        case 7:
                            description = description + "Kun'yomi: "
                                    + node2.getTextContent();
                            break;
                        default:
                            break;
                        }
                    }
                    System.out.println(name + " - " + description);
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

    /**
     * Conver the chars in list 'chars' to images.
     */
    private void convertCharToImage() {

        for (Kanji cc : chars) {

            BufferedImage img = new BufferedImage(imgWidth, imgHeight,
                    BufferedImage.TYPE_BYTE_GRAY);

            Graphics g = img.getGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imgWidth, imgHeight);
            g.setColor(Color.BLACK);
            g.setFont(font);

            // TODO select font size according to chosen nonogram size?!
            FontMetrics fm = g.getFontMetrics(font);
            Rectangle2D rect = fm.getStringBounds(cc.getKanji(), g);

            int textHeight = (int) (rect.getHeight());
            int textWidth = (int) (rect.getWidth());

            // Center text horizontally and vertically
            int x = (imgWidth - textWidth) / 2;
            int y = (imgHeight - textHeight) / 2 + fm.getAscent();

            // Draw the string
            g.drawString(cc.getKanji(), x, y);

            pics.add(img);
        }
    }

    /**
     * Convert the images in list 'pics' to nonograms.
     */
    private void convertImageToNonogram() {
        List<Nonogram> listNonograms = new ArrayList<Nonogram>();

        for (BufferedImage img : pics) {
            boolean[][] field = new boolean[imgWidth][imgHeight];

            for (int i = 0; i < img.getHeight(); i++) {
                for (int j = 0; j < img.getWidth(); j++) {
                    // TODO really ugly line; should be replaced.
                    field[i][j] = ((img.getRGB(j, i) == -16777216) ? true
                            : false);
                }
            }

            Nonogram n = new Nonogram(chars.get(pics.indexOf(img)).getName(),
                    DifficultyLevel.NORMAL, field);
            n.setAuthor("Christian Wichmann");
            n.setLevel(pics.indexOf(img));
            n.setDifficulty(DifficultyLevel.NORMAL);
            n.setDescription(chars.get(pics.indexOf(img)).getDescription());

            listNonograms.add(n);
        }

        Course c = new Course(courseName, listNonograms);
        try {

            new ZipCourseSerializer().save(new File(
                    "/home/christian/.FreeNono/nonograms/"), c);

        } catch (NullPointerException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
