package de.ichmann.markusw.java.apps.freenono.model;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ControlSettings {

	public enum Control {
		moveUp, moveDown, moveLeft, moveRight, markField, occupyField, quitGame, 
		stopGame, pauseGame, startGame, resumeGame, restartGame, quitProgramm, 
		showOptions, showAbout
	};

	private Map<Control, Integer> controls = new HashMap<Control, Integer>();

	public ControlSettings() {
		
		setDefaults();
		
	}

	private void setDefaults() {
		
		controls.put(Control.moveUp, KeyEvent.VK_UP);
		controls.put(Control.moveDown, KeyEvent.VK_DOWN);
		controls.put(Control.moveLeft, KeyEvent.VK_LEFT);
		controls.put(Control.moveRight, KeyEvent.VK_RIGHT);
		controls.put(Control.markField, KeyEvent.VK_COMMA);
		controls.put(Control.occupyField, KeyEvent.VK_PERIOD);
		controls.put(Control.quitGame, KeyEvent.VK_ESCAPE);
		controls.put(Control.startGame, KeyEvent.VK_F1);
		controls.put(Control.restartGame, KeyEvent.VK_F2);
		controls.put(Control.pauseGame, KeyEvent.VK_F3);
		controls.put(Control.resumeGame, KeyEvent.VK_F4);
		controls.put(Control.stopGame, KeyEvent.VK_F5);		
		controls.put(Control.showOptions, KeyEvent.VK_F6);
		controls.put(Control.showAbout, KeyEvent.VK_F7);
		controls.put(Control.quitProgramm, KeyEvent.VK_F8);
		
	}
	
	public void setControl(Control control, Integer keyCode) {
		
		controls.put(control, keyCode);
		
	}
	
	public Integer getControl(Control control) {
		
		return controls.get(control);
				
	}
	
	public  Map<Control, Integer> getControls() {
		
		return controls;
		
	}

}
