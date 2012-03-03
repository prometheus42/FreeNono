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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.StateChangeEvent;

public class AudioProvider {

	private static Logger logger = Logger.getLogger(AudioProvider.class);

	private static final boolean PLAY_SFX_DEFAULT = true;
	private boolean playSFX = PLAY_SFX_DEFAULT;
	private static final boolean PLAY_MUSIC_DEFAULT = true;
	private boolean playMusic = PLAY_MUSIC_DEFAULT;
	
	private static final int VOLUME_SFX_DEFAULT = 127;
	private int volumeSFX = VOLUME_SFX_DEFAULT;
	private static final int VOLUME_MUSIC_DEFAULT = 127;
	private int volumeMusic = VOLUME_MUSIC_DEFAULT;

	private List<String> bgMusicFiles = null;
	private long bgPosition = 0L;

	private Sequencer midi_sequencer = null;
	private Synthesizer midi_synthesizer = null;

	public enum SFXType {
		playOccupySFX("/resources/sounds/occupy.wav"), 
		playFieldChangedSFX("/resources/sounds/change_field.wav"), 
		playWronglyOccupiedSFX("/resources/sounds/wrongly_occupied.wav"), 
		playGameOverSFX("/resources/sounds/game_over.wav"), 
		playGameWonSFX("/resources/sounds/game_won.wav");
		
		private final String filename;

		SFXType(String wavFile) {
			filename = wavFile;
		};
	};

	private Map<SFXType, WavPlayer> sfx = new HashMap<SFXType, WavPlayer>();

	private GameEventHelper eventHelper = null;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void OccupyField(FieldControlEvent e) {
			if (playSFX) {
				sfx.get(SFXType.playOccupySFX).startWAV();
			}
		}

		@Override
		public void MarkField(FieldControlEvent e) {
			//
		}

		@Override
		public void WrongFieldOccupied(FieldControlEvent e) {
			if (playSFX) {
				sfx.get(SFXType.playWronglyOccupiedSFX).startWAV();
			}
		}

		@Override
		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				if (playSFX) {
					sfx.get(SFXType.playGameOverSFX).startWAV();
				}
				stopBGMusic(false);
				break;
			case solved:
				if (playSFX) {
					sfx.get(SFXType.playGameWonSFX).startWAV();
				}
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

		public void ProgramControl(ProgramControlEvent e) {
			if (e.getPct() == ProgramControlType.QUIT_PROGRAMM)
				closeAudio();
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

		bgMusicFiles = new ArrayList<String>();
		bgMusicFiles.add("/resources/music/theme_A.mid");
		//bgMusicFiles.add("/music/theme_B.mid");
		
		this.initMIDI();
		this.initWAV();

	}

	private void initWAV() {

		// initialize WavPlayer for every sfx in the game
		for (SFXType x : SFXType.values()) {
			sfx.put(x, new WavPlayer(new File(getClass().getResource(
					x.filename).getFile()), volumeSFX ) );
		}

	}

	private void closeWAV() {

		// close all audio lines on WavPlayers
		for (SFXType x : SFXType.values()) {
			sfx.get(x).closeLines();
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
		
		Collections.shuffle(bgMusicFiles);
		URL midifile = getClass().getResource(bgMusicFiles.get(0));

		try {
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

		// set the midi volume for all 16 channels
		MidiChannel[] channels = midi_synthesizer.getChannels();
		for (int i = 0; i < channels.length; i++) {
			channels[i].controlChange(7, volumeMusic);
		}
		// (see http://www.codezealot.org/archives/27)
		// ShortMessage volMessage = new ShortMessage();
		// for (int i = 0; i < 16; i++) {
		// try {
		// volMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 7,
		// volumeMusic);
		// } catch (InvalidMidiDataException e) {
		// }
		// synthReceiver.send(volMessage, -1);
		// }

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

	public void closeAudio() {

		closeMIDI();
		closeWAV();

	}

	protected void finalize() throws Throwable {

		closeAudio();

	};

	public void setEventHelper(GameEventHelper eventHelper) {

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);

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

	public int getVolumeSFX() {
		return volumeSFX;
	}

	/**
	 * @param volumeSFX
	 *  	the volume to which the sound effects are set between 0 and 255
	 */
	public void setVolumeSFX(int volumeSFX) {
		this.volumeSFX = volumeSFX;
	}

	public int getVolumeMusic() {
		return volumeMusic;
	}

	/**
	 * @param volumeMusic
	 *   	the volume to which the background music is set between 0 and 255
	 */
	public void setVolumeMusic(int volumeMusic) {
		this.volumeMusic = volumeMusic;
	}

}
