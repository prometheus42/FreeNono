package de.ichmann.markusw.java.apps.freenono.serializer.nonogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.freenono.exception.InvalidFormatException;
import de.ichmann.markusw.java.apps.freenono.exception.ParameterException;
import de.ichmann.markusw.java.apps.freenono.model.Course;
import de.ichmann.markusw.java.apps.freenono.model.Nonogram;
import de.ichmann.markusw.java.apps.freenono.model.Tools;

public class SimpleNonogramSerializer implements NonogramSerializer {

	private static final char FIELD_FREE_CHAR = ' ';
	private static final char FIELD_OCCUPIED_CHAR = 'x';

	public static final String DEFAULT_FILE_EXTENSION = "nonogram";

	private static Logger logger = Logger
			.getLogger(SimpleNonogramSerializer.class);

	/* public methods */

	@Override
	public Course loadNonogramCource(File dir) throws NullPointerException,
			InvalidFormatException, IOException {
		
		if (dir == null) {
			throw new NullPointerException("The specified File is null");
		}
		
		if (!dir.isDirectory()) {
			throw new IOException("The specified File is no directory");
		}

		List<Nonogram> nonograms = new ArrayList<Nonogram>();

		for (File file : dir.listFiles()) {
			nonograms.add(loadNonogram(file));
		}

		String name = dir.getName();

		Nonogram[] array = nonograms.toArray(new Nonogram[0]);
		Course c = new Course(name, array);

		return c;
	}

	@Override
	public void saveNonogramCourse(File dir, Course c) throws IOException,
			ParameterException {
		
		if (dir == null) {
			throw new NullPointerException("The specified File is null");
		}
		
		if (!dir.isDirectory()) {
			throw new IOException("The specified File is no directory");
		}
		
		if (c == null) {
			throw new NullPointerException("The specified Course is null");
		}
		
		File courseDir = new File(dir, c.getName());
		
		if(!courseDir.mkdirs()) {
			throw new IOException("Unable to create directories");
		}

		for (Nonogram n : c.getNonograms()) {
			File nonogramFile = new File(courseDir, n.getName());
			saveNonogram(nonogramFile, n);
		}
		
	}

	@Override
	public Nonogram loadNonogram(File f) throws InvalidFormatException,
			IOException {

		Nonogram n = null;
		n = new Nonogram();
		BufferedReader reader = null;
		try {

			String line, tmp;
			reader = new BufferedReader(new FileReader(f));

			line = reader.readLine();
			String name = line.trim();

			line = reader.readLine();
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			if (tokenizer.countTokens() != 2) {
				throw new InvalidFormatException("");
			}

			tmp = tokenizer.nextToken();
			int width = Integer.parseInt(tmp);

			tmp = tokenizer.nextToken();
			int height = Integer.parseInt(tmp);

			boolean[][] field = new boolean[height][width];

			int y = 0;
			while (reader.ready()) {
				line = reader.readLine();
				if (line.length() != width) {
					throw new InvalidFormatException(
							"File contains different line lengths");
				}
				for (int x = 0; x < line.length(); x++) {
					field[y][x] = getFieldValue(line.charAt(x));
				}
				y++;
			}

			n = new Nonogram(name, name, "", 0, field);

			return n;

		} catch (ParameterException e) {
			throw new InvalidFormatException(
					"Unable to read Nonogram input file", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Override
	public void saveNonogram(File f, Nonogram n) throws IOException,
			ParameterException {

		FileWriter writer = null;
		try {

			writer = new FileWriter(f);

			writer.write(n.getName());
			writer.write(Tools.NEW_LINE);

			writer.write(Integer.toString(n.width()));
			writer.write(",");
			writer.write(Integer.toString(n.height()));
			writer.write(Tools.NEW_LINE);

			for (int i = 0; i < n.height(); i++) {
				for (int j = 0; j < n.width(); j++) {
					writer.write(getFieldChar(n.getFieldValue(i, j)));
				}
				writer.write(Tools.NEW_LINE);
			}
			writer.flush();

		} catch (IOException e) {
			throw new IOException("Unable to write Nonogram output file", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

	/* static helper methods */

	private static boolean getFieldValue(char c) throws InvalidFormatException {
		switch (c) {
		case FIELD_FREE_CHAR:
			return false;
		case FIELD_OCCUPIED_CHAR:
			return true;
		default:
			// TODO use a real Exception, maybe write one
			throw new InvalidFormatException(
					"The field containes the wrong symbol " + c);
		}
	}

	private static char getFieldChar(boolean b) {
		if (b) {
			return FIELD_OCCUPIED_CHAR;
		} else {
			return FIELD_FREE_CHAR;
		}
	}

}
