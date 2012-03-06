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
package org.freenono.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavPlayer {

	private File wavFile = null;
	private Clip clip = null;
	private AudioInputStream audioInputStream = null;
	private AudioFormat audioFormat = null;
	private SourceDataLine sourceDataLine = null;
	private float volume = 1;

	public WavPlayer(File wavFile, int volume) {

		this.wavFile = wavFile;
		setVolume(volume);
		openWAV();

	}

	public void openWAV() {

		try {

			audioInputStream = AudioSystem.getAudioInputStream(wavFile);
			audioFormat = audioInputStream.getFormat();
			int size = (int) (audioFormat.getFrameSize() * audioInputStream
					.getFrameLength());
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat, size);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			// Adjust the volume on the output line.
			if (sourceDataLine
					.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl volumeControl = (FloatControl) sourceDataLine
						.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(volume);
			}
			sourceDataLine.start();

		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startWAV() {

		int cnt;
		byte[] tempBuffer = new byte[64];

		try {

			// reset audio system to start
			sourceDataLine.flush();
			audioInputStream = AudioSystem.getAudioInputStream(wavFile);

			while ((cnt = audioInputStream.read(tempBuffer, 0,
					tempBuffer.length)) != -1) {
				if (cnt > 0) {
					// Write data to the internal buffer of the data line
					// where it will be delivered to the speaker.
					sourceDataLine.write(tempBuffer, 0, cnt);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				audioInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void openWAVold(File wavFile) {

		try {

			audioInputStream = AudioSystem.getAudioInputStream(wavFile);
			audioFormat = audioInputStream.getFormat();
			int size = (int) (audioFormat.getFrameSize() * audioInputStream
					.getFrameLength());
			byte[] audio = new byte[size];
			DataLine.Info info = new DataLine.Info(Clip.class, audioFormat,
					size);
			audioInputStream.read(audio, 0, size);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioFormat, audio, 0, size);

		} catch (LineUnavailableException e) {
			// TODO: Handle Exception
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void startWAVold() {

		if (!this.clip.isRunning()) {

			// clip.stop();
			clip.setMicrosecondPosition(0);
			clip.flush();
			clip.start();

		}

	}

	public void closeLines() {

		try {
			clip.close();
			audioInputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void finalize() throws Throwable {
		closeLines();
		super.finalize();
	}

	public File getWavFile() {
		return wavFile;
	}

	public void setWavFile(File wavFile) {
		this.wavFile = wavFile;
	}

	/**
	 * @return the volume
	 */
	public int getVolume() {
		return (int) (volume * 255);
	}

	/**
	 * @param volume
	 *            the volume to set as int between 0 and 255
	 */
	public void setVolume(int volume) {
		this.volume = (float) volume / 255;
	}

}
