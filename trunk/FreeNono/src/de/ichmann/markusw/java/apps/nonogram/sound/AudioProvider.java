package de.ichmann.markusw.java.apps.nonogram.sound;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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

import de.ichmann.markusw.java.apps.nonogram.event.GameListener;
import de.ichmann.markusw.java.apps.nonogram.model.Game;
import de.ichmann.markusw.java.apps.nonogram.model.GameState;

import org.apache.log4j.Logger;

public class AudioProvider {
	
	private static Logger logger = Logger.getLogger(AudioProvider.class);

	private static final boolean PLAY_SFX_DEFAULT = true;
	private boolean playSFX = PLAY_SFX_DEFAULT;
	private static final boolean PLAY_MUSIC_DEFAULT = true;
	private boolean playMusic = PLAY_MUSIC_DEFAULT;
	private String bgMusic = "/music/theme_A.mid";
	
	private static Sequencer midi_sequencer = null;
	private static Synthesizer midi_synthesizer = null;
	
	private GameListener gameListener = new GameListener() {

		@Override
		public void Timer() {
		}

		@Override
		public void StateChanged(GameState oldState, GameState newState) {

			switch (newState) {
			case gameOver:
				playGameOverSFX();
				stopBGMusic();
				break;
			case solved:
				playGameWonSFX();
				stopBGMusic();
				break;
			case running:
				startBGMusic();
				break;
			case userStop:
			case paused:
				stopBGMusic();
			default:
				break;
			}

		}

		@Override
		public void FieldOccupied(int x, int y) {
		}

		@Override
		public void FieldMarked(int x, int y) {
		}
	};
	
	public AudioProvider() {
		this(PLAY_SFX_DEFAULT, PLAY_MUSIC_DEFAULT);
	}
	
	public AudioProvider (boolean playSFX, boolean playMusic) {
		this.setPlaySFX(playSFX);
		this.setPlayMusic(playMusic);
		this.initMIDI();
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

	
	protected void playGameOverSFX() {
		playWAV(getClass().getResource("/sounds/game_over.wav"));
	}

	protected void playGameWonSFX() {
		playWAV(getClass().getResource("/sounds/game_won.wav"));
	}
	
	public void addAsListener(Game game) {
		game.addGameListener(gameListener);
	}
	
	
	/**
	 * playWAV plays the given file exactly one time
	 */
	private void playWAV(URL filename) {
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File(filename.toURI()));
			AudioFormat af = audioInputStream.getFormat();
			int size = (int) (af.getFrameSize() * audioInputStream
					.getFrameLength());
			byte[] audio = new byte[size];
			DataLine.Info info = new DataLine.Info(Clip.class, af, size);
			audioInputStream.read(audio, 0, size);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(af, audio, 0, size);
			clip.start();
		} catch (MalformedURLException e) {
			// TODO: Handle Exception
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO: Handle Exception
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * initMIDI initializes the JAVA MIDI sub system
	 */
	private void initMIDI() {

		Sequence sequence = null;
		URL midifile = getClass().getResource(bgMusic);
		
		try {
			File midiFile = new File(midifile.toURI());
			sequence = MidiSystem.getSequence(midiFile);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO: Handle Exceptions
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

		/*
		 * There is a bug in the Sun jdk1.3/1.4. It prevents correct termination
		 * of the VM. So we have to exit ourselves. To accomplish this, we
		 * register a Listener to the Sequencer. It is called when there are
		 * "meta" events. Meta event 47 is end of track.
		 * 
		 * Thanks to Espen Riskedal for finding this trick.
		 */
//		midi_sequencer.addMetaEventListener(new MetaEventListener() {
//			public void meta(MetaMessage event) {
//				if (event.getType() == 47) {
//					midi_sequencer.close();
//					if (midi_synthesizer != null) {
//						midi_synthesizer.close();
//					}
//					System.exit(0);
//				}
//			}
//		});

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
		 * FIX: sequencers and synthesizers are sometimes one object, 
		 * in which case, nothing more has to be done. Otherwise a link
		 * between the two objects has to be manually created. 
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
	}
	
	private void closeMIDI() {
		midi_sequencer.close();
		if (midi_synthesizer != null) {
			midi_synthesizer.close();
		}
		System.exit(0);
	}
	
	private void startBGMusic() {
		midi_sequencer.start();
	}
	
	private void stopBGMusic() {
		midi_sequencer.stop();
	}
}
