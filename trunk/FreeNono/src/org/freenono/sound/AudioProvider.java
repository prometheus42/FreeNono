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
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;

public class AudioProvider {

	private static Logger logger = Logger.getLogger(AudioProvider.class);

	private static final boolean PLAY_SFX_DEFAULT = true;
	private boolean playSFX = PLAY_SFX_DEFAULT;
	private static final boolean PLAY_MUSIC_DEFAULT = true;
	private boolean playMusic = PLAY_MUSIC_DEFAULT;
	private String bgMusicFile = "/music/theme_A.mid";
	private long bgPosition = 0L;

	private Sequencer midi_sequencer = null;
	private Synthesizer midi_synthesizer = null;

	private GameEventHelper eventHelper;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void StateChanged(GameEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				playGameOverSFX();
				stopBGMusic(false);
				break;
			case solved:
				playGameWonSFX();
				stopBGMusic(false);
				break;
			case running:
				startBGMusic();
				break;
			case userStop:
				stopBGMusic(false);
				break;
			case paused:
				stopBGMusic(true);
				break;
			default:
				startBGMusic();
				break;
			}

		}

		@Override
		public void FieldOccupied(GameEvent e) {
			playOccupySFX();
		}

		@Override
		public void FieldMarked(GameEvent e) {
			playOccupySFX();
		}

		@Override
		public void WrongFieldOccupied(GameEvent e) {
			playWrongFieldOccupiedSFX();
		}

	};

	public AudioProvider() {
		this(PLAY_SFX_DEFAULT, PLAY_MUSIC_DEFAULT);
	}

	public AudioProvider(boolean playAudio) {
		this(playAudio, playAudio);
	}

	public AudioProvider(boolean playSFX, boolean playMusic) {
		this.setPlaySFX(playSFX);
		this.setPlayMusic(playMusic);
		this.initMIDI();
	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	public void quitProgram() {
		closeMIDI();
	}

	public boolean getPlaySFX() {
		return playSFX;
	}

	public void setPlaySFX(boolean playSFX) {
		this.playSFX = playSFX;
	}

	public boolean getPlayMusic() {
		return playMusic;
	}

	public void setPlayMusic(boolean playMusic) {
		this.playMusic = playMusic;
	}

	private void playGameOverSFX() {
		playWAV(getClass().getResource("/sounds/game_over.wav"));
	}

	private void playGameWonSFX() {
		playWAV(getClass().getResource("/sounds/game_won.wav"));
	}

	private void playOccupySFX() {
		playWAV(getClass().getResource("/sounds/occupy.wav"));
	}

	private void playFieldChangedSFX() {
		playWAV(getClass().getResource("/sounds/change_field.wav"));
	}

	protected void playWrongFieldOccupiedSFX() {
		playWAV(getClass().getResource("/sounds/wrongly_occupied.wav"));
	}

	/**
	 * playWAV plays the given file exactly one time
	 */
	private void playWAV(URL wavfile) {
		if (playSFX) {
			try {
				AudioInputStream audioInputStream = AudioSystem
						.getAudioInputStream(new File(wavfile.getFile()));
				AudioFormat af = audioInputStream.getFormat();
				int size = (int) (af.getFrameSize() * audioInputStream
						.getFrameLength());
				byte[] audio = new byte[size];
				DataLine.Info info = new DataLine.Info(Clip.class, af, size);
				audioInputStream.read(audio, 0, size);
				Clip clip = (Clip) AudioSystem.getLine(info);
				clip.open(af, audio, 0, size);
				clip.start();
				// TODO: split this function into two: initWAV and playWAV to
				// repair the problem with too little audio lines when clicking
				// fast onto much tiles!
				// clip.close();
			} catch (MalformedURLException e) {
				// TODO: Handle Exception
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// audioInputStream.close();
			}
		}
	}

	/**
	 * initMIDI initializes the JAVA MIDI sub system
	 * 
	 * source of inspiration:
	 * http://www.jsresources.org/examples/SimpleMidiPlayer.java.html
	 */
	private void initMIDI() {

		Sequence sequence = null;
		URL midifile = getClass().getResource(bgMusicFile);

		try {
			// File midiFile = new File(midifile.toURI());
			sequence = MidiSystem.getSequence(new File(midifile.getFile()));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			midi_sequencer = MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		if (midi_sequencer == null) {
			return;
		}

		// open the Sequencer to become usable
		try {
			midi_sequencer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return;
		}

		// tell the sequencer which sequence it has to play
		try {
			midi_sequencer.setSequence(sequence);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return;
		}

		// set up the destinations the sequence should be played on.
		/*
		 * FIX: sequencers and synthesizers are sometimes one object, in which
		 * case, nothing more has to be done. Otherwise a link between the two
		 * objects has to be manually created.
		 */
		if (!(midi_sequencer instanceof Synthesizer)) {
			// try to get default synthesizer, open it and chain it to sequencer
			try {
				midi_synthesizer = MidiSystem.getSynthesizer();
				midi_synthesizer.open();
				Receiver synthReceiver = midi_synthesizer.getReceiver();
				Transmitter seqTransmitter = midi_sequencer.getTransmitter();
				seqTransmitter.setReceiver(synthReceiver);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

		midi_sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
	}

	private void closeMIDI() {
		/*
		 * FIX: To correct a bug in the Sun JDK 1.3/1.4 that prevents correct
		 * termination of the VM, we close the synthesizer an exit manually!
		 */
		midi_sequencer.close();
		if (midi_synthesizer != null) {
			midi_synthesizer.close();
		}
		System.exit(0);
	}

	/**
	 * startBGMusic: Play back of the file opened in initMIDI() starts at the
	 * saved position and loops infinitely.
	 */
	private void startBGMusic() {
		if (playMusic) {
			midi_sequencer.setTickPosition(bgPosition);
			midi_sequencer.start();
		}
	}

	/**
	 * stopBGMusic: Play back of BG music stops an actual position is stored in
	 * variable bgPosition depending on the value of storePosition
	 */
	private void stopBGMusic(boolean storePosition) {
		if (playMusic) {
			if (storePosition) {
				bgPosition = midi_sequencer.getTickPosition();
			} else {
				bgPosition = 0L;
			}
			midi_sequencer.stop();
		}
	}
}
