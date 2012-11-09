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
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.StateChangeEvent;

public class AudioProvider {

	private static Logger logger = Logger.getLogger(AudioProvider.class);
	
	private static final boolean PLAY_SFX_DEFAULT = false;
	private boolean playSFX = PLAY_SFX_DEFAULT;
	private static final boolean PLAY_MUSIC_DEFAULT = false;
	private boolean playMusic = PLAY_MUSIC_DEFAULT;

	private static final int VOLUME_SFX_DEFAULT = 200;
	private int volumeSFX = VOLUME_SFX_DEFAULT;
	private static final int VOLUME_MUSIC_DEFAULT = 200;
	private int volumeMusic = VOLUME_MUSIC_DEFAULT;
	
	private Settings settings = null;

	private List<String> bgMusicFiles = null;
	private List<String> midiMusicFiles = null;
	private long bgPosition = 0L;

	private Sequencer midi_sequencer = null;
	private Synthesizer midi_synthesizer = null;

	
	public enum SFXType {
		OccupySFX, FieldChangedSFX, WronglyOccupiedSFX, GameOverSFX, GameWonSFX
	};

	private Map<SFXType, WavPlayer> sfxPlayer = new HashMap<SFXType, WavPlayer>();
	private Map<SFXType, String> sfxFiles = new HashMap<SFXType, String>();
	private OggPlayer bgMusic = null;
	

	private GameEventHelper eventHelper = null;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void OccupyField(FieldControlEvent e) {
			if (playSFX) {
				//sfxPlayer.get(SFXType.OccupySFX).playSoundFile();
			}
		}

		@Override
		public void MarkField(FieldControlEvent e) {
			//
		}

		@Override
		public void WrongFieldOccupied(FieldControlEvent e) {
			if (playSFX) {
				//sfxPlayer.get(SFXType.WronglyOccupiedSFX).playSoundFile();
			}
		}

		@Override
		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				if (playSFX) {
					sfxPlayer.get(SFXType.GameOverSFX).play();
				}
				if (playMusic) {
					stopBGMusic();
				}
				break;
			case solved:
				if (playSFX) {
					sfxPlayer.get(SFXType.GameWonSFX).play();
				}
				if (playMusic) {
					stopBGMusic();
				}
				break;
			case running:
				if (playMusic) {
					startBGMusic();
				}
				break;
			case userStop:
				if (playMusic) {
					stopBGMusic();
				}
				break;
			case paused:
				// TODO implement pause and resume of background music!
				if (playMusic) {
					pauseBGMusic();
				}
				break;
			default:
				break;
			}

		}

		public void ProgramControl(ProgramControlEvent e) {
			
			if (e.getPct() == ProgramControlType.QUIT_PROGRAMM)
				closeAudio();
		}

		public void OptionsChanged(ProgramControlEvent e) {

			// TODO allow starting and stopping of audio while game is running!
			if (settings.getPlayAudio() != playMusic) {
				// stop or start background music
			}
		}

	};

	
	@Deprecated
	public AudioProvider() {
		
		this(PLAY_SFX_DEFAULT, PLAY_MUSIC_DEFAULT);
	}
	
	@Deprecated
	public AudioProvider(boolean playSFX, boolean playMusic) {
		
		setPlaySFX(playSFX);
		setPlayMusic(playMusic);

		initData();
		initAudio();
	}

	public AudioProvider(GameEventHelper eventHelper, Settings settings) {

		setEventHelper(eventHelper);
		this.settings = settings;
		
		setPlaySFX(settings.getPlayAudio());
		setPlayMusic(settings.getPlayAudio());

		initData();
		initAudio();
	}
	
	
	public void setEventHelper(GameEventHelper eventHelper) {
		
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	public void removeEventHelper() {

		if (eventHelper != null) {
			eventHelper.removeGameListener(gameAdapter);
			this.eventHelper = null;
		}
	}
	
	
	private void initData() {
		
		// set filenames for all music and sound effect files
		midiMusicFiles = new ArrayList<String>();
		midiMusicFiles.add("/resources/music/theme_A.mid");
		bgMusicFiles = new ArrayList<String>();
		bgMusicFiles.add("/resources/music/theme_A.ogg");
		// bgMusicFiles.add("/resources/music/theme_B.ogg");
		sfxFiles.put(SFXType.OccupySFX, "/resources/sounds/occupy.wav");
		sfxFiles.put(SFXType.FieldChangedSFX, "/resources/sounds/change_field.wav");
		sfxFiles.put(SFXType.WronglyOccupiedSFX,"/resources/sounds/wrongly_occupied.wav");
		sfxFiles.put(SFXType.GameOverSFX,"/resources/sounds/game_over.wav");
		sfxFiles.put(SFXType.GameWonSFX,"/resources/sounds/game_won.wav");
	}

	
	private void initAudio() {

		// initialize WavPlayer for every effect in the game
		if (playSFX) {
			for (SFXType x : SFXType.values()) {
				sfxPlayer.put(x,
						new WavPlayer(getClass().getResource(sfxFiles.get(x)),
								volumeSFX));
			}
		}

		// initialize background music as OggPLayer
		if (playMusic) {
			
			if (bgMusic == null) {

				Collections.shuffle(bgMusicFiles);
				URL audioFile = getClass().getResource(bgMusicFiles.get(0));
				logger.debug("Try to instantiate ogg player with music file " + audioFile);
				bgMusic = new OggPlayer(audioFile, volumeSFX);
			}
		}
		
		// initialize background midi music
		//initMIDI();
	}

	private void startBGMusic() {

		if (bgMusic != null) {
			bgMusic.play();
		}
	}

	private void pauseBGMusic() {

		if (bgMusic != null) {
			bgMusic.pause();
		}
	}
	
	private void stopBGMusic() {

		if (bgMusic != null) {
			bgMusic.stop();
		}
	}

	public void closeAudio() {

		// close all audio lines on WavPlayers
		for (AudioPlayer w : sfxPlayer.values()) {

			if (w != null)
				w.closePlayer();
		}

		if (bgMusic != null)
			bgMusic.closePlayer();

		closeMIDI();
	}

	protected void finalize() throws Throwable {

		closeAudio();
		removeEventHelper();
		super.finalize();
	};

	
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
	 *            the volume to which the sound effects are set between 0 and
	 *            255
	 */
	public void setVolumeSFX(int volumeSFX) {
		this.volumeSFX = volumeSFX;
	}
	
	
	public int getVolumeMusic() {
		return volumeMusic;
	}

	/**
	 * @param volumeMusic
	 *            the volume to which the background music is set between 0 and
	 *            255
	 */
	public void setVolumeMusic(int volumeMusic) {
		this.volumeMusic = volumeMusic;
	}


	
	/**
	 * initMIDI initializes the JAVA MIDI sub system
	 * 
	 * source of inspiration:
	 * http://www.jsresources.org/examples/SimpleMidiPlayer.java.html
	 */
	private void initMIDI() {

		logger.debug("init MIDI");

		Sequence sequence = null;

		logger.debug("Try to load music resource " + midiMusicFiles.get(0));

		Collections.shuffle(midiMusicFiles);
		URL midifile = getClass().getResource(midiMusicFiles.get(0));

		logger.debug("Try to load music file " + midifile);

		try {
			sequence = MidiSystem.getSequence(midifile);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			midi_sequencer = MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			logger.error("Can not open new line for midi output!");
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
		 * termination of the VM, we close the synthesizer and exit manually!
		 */
		if (midi_synthesizer != null) {
			midi_synthesizer.close();
		}
		//System.exit(0);
	}
	
	/**
	 * startMidiMusic: Play back of the file opened in initMIDI() starts at the
	 * saved position and loops infinitely.
	 */
	private void startMidiMusic() {

		if (playMusic) {
			midi_sequencer.setTickPosition(bgPosition);
			midi_sequencer.start();
		}
	}

	/**
	 * stopMidiMusic: Play back of BG music stops an actual position is stored in
	 * variable bgPosition depending on the value of storePosition
	 */
	private void stopMidiMusic(boolean storePosition) {

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
