/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Martin Wichmann, Christian Wichmann
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
package org.freenono.ui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.freenono.controller.ControlSettings;
import org.freenono.controller.Settings;
import org.freenono.model.GameModeType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Shows UI to view and change all options.
 * 
 * @author Martin Wichmann, Christian Wichmann
 *  
 * How to add new options:
 * - create JComponent Object
 * - call addTab() and addOption()
 * - modify load and save settings methods
 * - also: there are probably some xml changes necessary
 */
public class OptionsUI extends JDialog {

	private static final long serialVersionUID = 1650619963343405427L;

	private static Logger logger = Logger.getLogger(OptionsUI.class);

	private JTabbedPane tabbedPane;
	private LinkedHashMap<String, LinkedHashMap<String, JComponent>> panelMap;
	private int tempMaxWidth = 0;
	private int tempMaxHeight = 0;
	private int tempCompMaxWidth = 0;
	private int tempCompMaxHeight = 0;
	private int maxColoumns = 0;

	private Settings settings;
	private ControlSettings csettings;

	private int buttonLeft = 0;
	private int buttonRight = 0;
	private int buttonUp = 0;
	private int buttonDown = 0;
	private int buttonMark = 0;
	private int buttonOccupy = 0;

	private JButton buttonConfigLeft = null;
	private JButton buttonConfigRight = null;
	private JButton buttonConfigUp = null;
	private JButton buttonConfigDown = null;
	private JButton buttonConfigMark = null;
	private JButton buttonConfigOccupy = null;
	private JButton buttonColorChooser = null;

	private JCheckBox useMaxFailCount = null;
	private JSpinner maxFailCount = null;
	private JCheckBox useMaxTime = null;
	private JSpinner maxTime = null;
	private JCheckBox markInvalid = null;
	private JCheckBox countMarked = null;
	private JCheckBox playAudio = null;
	private JCheckBox hidePlayfield = null;
	private JComboBox gameModes = null;

	/**
	 * Create the dialog.
	 */
	public OptionsUI(Frame owner, Settings settings) {

		super(owner);
		
		this.settings = settings;
		this.csettings = settings.getControlSettings();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		panelMap = new LinkedHashMap<String, LinkedHashMap<String, JComponent>>();

		setSize(450, 300);
		setLocation(200, 150);
		setLayout(new BorderLayout());

		add(getButtonPane(), BorderLayout.SOUTH);
		add(getTabbedPane(), BorderLayout.CENTER);

		// load options from file
		loadSettings();

		// add all options and tabs to panel map so they can be added to the panel
		addOptionsToPanel();

		// populate tab with added options and resize
		populatePanel();
		
		pack();

		// check the screen resolution and change the size of the dialog if necessary
		Toolkit tk = Toolkit.getDefaultToolkit();
		if ((this.getPreferredSize().getHeight() >= (tk.getScreenSize()
				.getHeight() - 50))
				|| (this.getPreferredSize().getWidth() >= (tk.getScreenSize()
						.getWidth() - 50))) {
			this.setPreferredSize(new Dimension((int) (tk.getScreenSize()
					.getWidth() - 50),
					(int) (tk.getScreenSize().getHeight() - 50)));
		}
	}

	
	private void addOptionsToPanel() {
		
		// fill tabs with options
		addTab(Messages.getString("OptionsUI.Game"));
		addOption(
				Messages.getString("OptionsUI.Game"), 
				Messages.getString("OptionsUI.GameMode"), 
				gameModes);
		addOption(
				Messages.getString("OptionsUI.Game"), 
				Messages.getString("OptionsUI.MaxFailCount"), 
				maxFailCount);
		addOption(
				Messages.getString("OptionsUI.Game"), 
				Messages.getString("OptionsUI.TimeLimit"), 
				maxTime);
		addOption(
				Messages.getString("OptionsUI.Game"), 
				Messages.getString("OptionsUI.MarkFields"), 
				markInvalid);
		addOption(
				Messages.getString("OptionsUI.Game"), 
				Messages.getString("OptionsUI.CountMarked"), 
				countMarked); 
		addOption(
				Messages.getString("OptionsUI.Game"), 
				Messages.getString("OptionsUI.HideFields"), 
				hidePlayfield); 

		
		addTab(Messages.getString("OptionsUI.Sound"));
		addOption(
				Messages.getString("OptionsUI.Sound"), 
				Messages.getString("OptionsUI.PlayAudio"), 
				playAudio);

		
		addTab(Messages.getString("OptionsUI.Control"));
		addOption(
				Messages.getString("OptionsUI.Control"), 
				Messages.getString("OptionsUI.ConfigControls"), 
				new JLabel());
		addOption(Messages.getString("OptionsUI.Control"),
				Messages.getString("OptionsUI.ConfigLeft"), 
				buttonConfigLeft);
		addOption(Messages.getString("OptionsUI.Control"),
				Messages.getString("OptionsUI.ConfigRight"), 
				buttonConfigRight);
		addOption(Messages.getString("OptionsUI.Control"),
				Messages.getString("OptionsUI.ConfigUp"), 
				buttonConfigUp);
		addOption(Messages.getString("OptionsUI.Control"),
				Messages.getString("OptionsUI.ConfigDown"), 
				buttonConfigDown);
		addOption(Messages.getString("OptionsUI.Control"),
				Messages.getString("OptionsUI.ConfigMark"), 
				buttonConfigMark);
		addOption(Messages.getString("OptionsUI.Control"),
				Messages.getString("OptionsUI.ConfigPlace"), 
				buttonConfigOccupy);
		
		addTab(Messages.getString("OptionsUI.GUI"));
		addOption(
				Messages.getString("OptionsUI.GUI"), 
				Messages.getString("OptionsUI.BaseColor"), 
				buttonColorChooser);
	}

	
	private JTabbedPane getTabbedPane() {
		
		// init tab panel
		tabbedPane = new JTabbedPane();

		// create option variables (JCompononents)
		maxFailCount = new JSpinner();
		maxFailCount.setUI(new BasicSpinnerUI());
		maxFailCount.setModel(new SpinnerNumberModel());

		useMaxFailCount = new JCheckBox();
		useMaxFailCount.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateUIStuff();
			}
		});

		SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
		spinnerDateModel.setCalendarField(Calendar.MINUTE);
		maxTime = new JSpinner();
		maxTime.setUI(new BasicSpinnerUI());
		maxTime.setModel(spinnerDateModel);
		maxTime.setEditor(new JSpinner.DateEditor(maxTime, "mm:ss"));

		useMaxTime = new JCheckBox();
		useMaxTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateUIStuff();
			}
		});

		markInvalid = new JCheckBox();
		countMarked = new JCheckBox();
		playAudio = new JCheckBox();
		hidePlayfield = new JCheckBox();
		
		gameModes = new JComboBox(GameModeType.values());

		buttonConfigLeft = new JButton(KeyEvent.getKeyText(settings
				.getKeyCodeForControl(ControlSettings.Control.moveLeft)));
		buttonConfigRight = new JButton(KeyEvent.getKeyText(settings
				.getKeyCodeForControl(ControlSettings.Control.moveRight)));
		buttonConfigUp = new JButton(KeyEvent.getKeyText(settings
				.getKeyCodeForControl(ControlSettings.Control.moveUp)));
		buttonConfigDown = new JButton(KeyEvent.getKeyText(settings
				.getKeyCodeForControl(ControlSettings.Control.moveDown)));
		buttonConfigMark = new JButton(KeyEvent.getKeyText(settings
				.getKeyCodeForControl(ControlSettings.Control.markField)));
		buttonConfigOccupy = new JButton(KeyEvent.getKeyText(settings
				.getKeyCodeForControl(ControlSettings.Control.occupyField)));

		// Set preferred size, so "all" texts can be shown. Just necessary for
		// one button, since this UI handles some stuff too.
		buttonConfigLeft.setPreferredSize(new Dimension(125, 25));

		ActionListener newButtonAssignAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				
				class NewButtonConfigUI extends JDialog {
					
					private static final long serialVersionUID = 8423411694004619728L;
					public int keyEventIntern = 0;

					public NewButtonConfigUI() {
						
						setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						setModalityType(ModalityType.APPLICATION_MODAL);
						setTitle(Messages.getString("OptionsUI.UserKeyPromptTitle"));
						setBounds(200, 200, 350, 100);
						
						add(new JLabel(Messages
							.getString("OptionsUI.UserKeyPrompt"), JLabel.CENTER));
						
						addKeyListener(new KeyListener() {

							@Override
							public void keyReleased(KeyEvent e) {
								keyEventIntern = e.getKeyCode();
								dispose();
							}

							@Override
							public void keyTyped(KeyEvent e) {

							}

							@Override
							public void keyPressed(KeyEvent e) {

							}
						});
					}
				}
				
				NewButtonConfigUI tempUI = new NewButtonConfigUI();
				
				tempUI.setVisible(true);
				
				JButton pressedButton = (JButton) action.getSource();
				
				if (pressedButton.equals(buttonConfigLeft)) {
					buttonConfigLeft.setText(KeyEvent
							.getKeyText(tempUI.keyEventIntern));
					buttonLeft = tempUI.keyEventIntern;
				} else if (pressedButton.equals(buttonConfigRight)) {
					buttonConfigRight.setText(KeyEvent
							.getKeyText(tempUI.keyEventIntern));
					buttonRight = tempUI.keyEventIntern;
				} else if (pressedButton.equals(buttonConfigUp)) {
					buttonConfigUp.setText(KeyEvent
							.getKeyText(tempUI.keyEventIntern));
					buttonUp = tempUI.keyEventIntern;
				} else if (pressedButton.equals(buttonConfigDown)) {
					buttonConfigDown.setText(KeyEvent
							.getKeyText(tempUI.keyEventIntern));
					buttonDown = tempUI.keyEventIntern;
				} else if (pressedButton.equals(buttonConfigMark)) {
					buttonConfigMark.setText(KeyEvent
							.getKeyText(tempUI.keyEventIntern));
					buttonMark = tempUI.keyEventIntern;
				} else if (pressedButton.equals(buttonConfigOccupy)) {
					buttonConfigOccupy.setText(KeyEvent
							.getKeyText(tempUI.keyEventIntern));
					buttonOccupy = tempUI.keyEventIntern;
				}
			};
		};

		buttonConfigLeft.addActionListener(newButtonAssignAction);
		buttonConfigRight.addActionListener(newButtonAssignAction);
		buttonConfigUp.addActionListener(newButtonAssignAction);
		buttonConfigDown.addActionListener(newButtonAssignAction);
		buttonConfigMark.addActionListener(newButtonAssignAction);
		buttonConfigOccupy.addActionListener(newButtonAssignAction);
		
		// elements for gui tab
		buttonColorChooser = new JButton(
				Messages.getString("OptionsUI.ChooseColor")) {
			
			private static final long serialVersionUID = 1L;

			@Override
            public void paintComponent(Graphics g)
            {
                g.setColor(settings.getBaseColor());
                g.fillRect(0, 0, getSize().width, getSize().height);
                super.paintComponent(g);
            }
        };
        buttonColorChooser.setContentAreaFilled(false);
		buttonColorChooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				Color tmp = JColorChooser.showDialog(OptionsUI.this,
						Messages.getString("OptionsUI.ChooseColor"),
						settings.getBaseColor());
				
				if (tmp != null)
					settings.setBaseColor(tmp);
			}
		});
		
		return tabbedPane;
	}

	private JPanel getButtonPane() {

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton(Messages.getString("OptionsUI.OK")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// save options to file
				saveSettings();
				dispose();
			}
		});
		okButton.setActionCommand("OK"); //$NON-NLS-1$
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(
				Messages.getString("OptionsUI.Cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
		buttonPane.add(cancelButton);

		return buttonPane;
	}
	
	/**
	 * Add tab to the list, so it will be added to the options dialog
	 * 
	 * @param title
	 *            name of the tab title
	 */
	private void addTab(String title) {
		
		if (!panelMap.containsKey(title)) {
			panelMap.put(title, new LinkedHashMap<String, JComponent>());
		}
	}

	/**
	 * Add option to a specific tab
	 * 
	 * @param tabTitle
	 *            to which tab should it be added
	 * @param optionTitle
	 *            name of the option
	 * @param comp
	 *            component that represents option value
	 */
	private void addOption(String tabTitle, String optionTitle, JComponent comp) {
		
		LinkedHashMap<String, JComponent> list = panelMap.get(tabTitle);
		list.put(optionTitle, comp);
	}

	
	/**
	 * Build the real panels and tabs from the information added through the
	 * methods addTab and addOption
	 */
	private void populatePanel() {
		
		logger.debug("Populating options panel.");

		Set<Entry<String, LinkedHashMap<String, JComponent>>> set = panelMap
				.entrySet();

		String key = ""; //$NON-NLS-1$
		Set<Entry<String, JComponent>> map = null;

		// iterate through tabs/options and collect some information that is
		// needed
		// to calculate the layout of the dialog
		for (Entry<String, LinkedHashMap<String, JComponent>> e : set) {
			map = null;
			map = e.getValue().entrySet();

			maxColoumns = map.size() > maxColoumns ? map.size() : maxColoumns;

			Dimension temp = null;
			for (Entry<String, JComponent> f : map) {
				temp = new JLabel(f.getKey()).getPreferredSize();
				tempMaxWidth = temp.getWidth() > tempMaxWidth ? (int) temp
						.getWidth() : tempMaxWidth;
				tempMaxHeight = temp.getHeight() > tempMaxHeight ? (int) temp
						.getHeight() : tempMaxHeight;
				temp = f.getValue().getPreferredSize();
				tempCompMaxWidth = temp.getWidth() > tempCompMaxWidth ? (int) temp
						.getWidth() : tempCompMaxWidth;
				tempCompMaxHeight = temp.getHeight() > tempCompMaxHeight ? (int) temp
						.getHeight() : tempCompMaxHeight;
			}
		}

		// create the actual panel with the information given through the list
		// and the previously collected infos
		for (Entry<String, LinkedHashMap<String, JComponent>> e : set) {
			key = ""; //$NON-NLS-1$
			map = null;

			key = e.getKey();
			map = e.getValue().entrySet();

			// create panel for the current tab
			JPanel panel = new JPanel(new GridBagLayout());

			int col = 0;
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 20, 10, 20);
			// add the labels and components the the current tab
			// do some magic with the preferred size, so it looks cool
			for (Entry<String, JComponent> f : map) {
				c.gridx = 0;
				c.gridy = col++;
				JLabel templabel = new JLabel(f.getKey());
				templabel.setPreferredSize(new Dimension(tempMaxWidth,
						tempMaxHeight));
				panel.add(templabel, c);
				c.gridx = 1;
				JComponent tempComp = f.getValue();
				tempComp.setPreferredSize(new Dimension(tempCompMaxWidth,
						tempCompMaxHeight));
				panel.add(tempComp, c);
			}

			// fill up panels with empty labels so the tabs all look the same
			if (map.size() < maxColoumns) {
				for (int j = 0; j < (maxColoumns - map.size()); j++) {
					c.gridx = 0;
					c.gridy = col++;
					JLabel templabel = new JLabel(""); //$NON-NLS-1$
					templabel.setPreferredSize(new Dimension(tempMaxWidth,
							tempMaxHeight));
					panel.add(templabel, c);
					c.gridx = 1;
					templabel.setPreferredSize(new Dimension(tempCompMaxWidth,
							tempCompMaxHeight));
					panel.add(templabel, c);
				}
			}

			// put the panel in a scroll pane, so the content can be seen if the
			// window is small
			JScrollPane scroll = new JScrollPane(panel);
			JPanel tempPanel = new JPanel(new GridLayout(1, 1));
			tempPanel.add(scroll);
			tabbedPane.add(key, tempPanel);
		}
	}

	
	/**
	 * Load settings from the settings object
	 */
	private void loadSettings() {

		gameModes.setSelectedItem(settings.getGameMode());
		useMaxFailCount.setSelected(settings.getUseMaxFailCount());
		maxFailCount.setValue(settings.getMaxFailCount());
		useMaxTime.setSelected(settings.getUseMaxTime());
		
		Calendar c = Calendar.getInstance();
		maxTime.setValue(new Date(settings.getMaxTime()
				- (c.get(Calendar.ZONE_OFFSET) - c.get(Calendar.DST_OFFSET))));

		markInvalid.setSelected(settings.getMarkInvalid());
		countMarked.setSelected(settings.getCountMarked());
		playAudio.setSelected(settings.getPlayAudio());
		hidePlayfield.setSelected(settings.getHidePlayfield());

		buttonLeft = csettings.getControl(ControlSettings.Control.moveLeft);
		buttonRight = csettings.getControl(ControlSettings.Control.moveRight);
		buttonUp = csettings.getControl(ControlSettings.Control.moveUp);
		buttonDown = csettings.getControl(ControlSettings.Control.moveDown);
		buttonMark = csettings.getControl(ControlSettings.Control.markField);
		buttonOccupy = csettings.getControl(ControlSettings.Control.occupyField);

		updateUIStuff();
	}

	
	/**
	 * Save settings to the options object
	 */
	private void saveSettings() {

		Integer i = (Integer) maxFailCount.getValue();
		settings.setMaxFailCount(i.intValue());
		settings.setUseMaxFailCount(useMaxFailCount.isSelected());

		Date d = (Date) maxTime.getValue();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		settings.setMaxTime(d.getTime()
				+ (c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)));

		settings.setGameMode((GameModeType)gameModes.getSelectedItem());
		settings.setUseMaxTime(useMaxTime.isSelected());
		settings.setMarkInvalid(markInvalid.isSelected());
		settings.setCountMarked(countMarked.isSelected());
		settings.setPlayAudio(playAudio.isSelected());
		settings.setHidePlayfield(hidePlayfield.isSelected());

		csettings.setControl(ControlSettings.Control.moveLeft, buttonLeft);
		csettings.setControl(ControlSettings.Control.moveRight, buttonRight);
		csettings.setControl(ControlSettings.Control.moveUp, buttonUp);
		csettings.setControl(ControlSettings.Control.moveDown, buttonDown);
		csettings.setControl(ControlSettings.Control.markField, buttonMark);
		csettings.setControl(ControlSettings.Control.occupyField, buttonOccupy);
	}

	
	/**
	 * Change the labels or so when something is changed
	 */
	private void updateUIStuff() {
		
		if (useMaxTime.isSelected()) {
			maxTime.setEnabled(true);
		} else {
			maxTime.setEnabled(false);
		}

		if (useMaxFailCount.isSelected()) {
			maxFailCount.setEnabled(true);
		} else {
			maxFailCount.setEnabled(false);
		}

		buttonConfigLeft.setText(KeyEvent.getKeyText(buttonLeft));
		buttonConfigRight.setText(KeyEvent.getKeyText(buttonRight));
		buttonConfigUp.setText(KeyEvent.getKeyText(buttonUp));
		buttonConfigDown.setText(KeyEvent.getKeyText(buttonDown));
		buttonConfigMark.setText(KeyEvent.getKeyText(buttonMark));
		buttonConfigOccupy.setText(KeyEvent.getKeyText(buttonOccupy));
	}

}