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
package org.freenono.ui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
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
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.freenono.controller.ControlSettings;
import org.freenono.controller.Manager;
import org.freenono.controller.ControlSettings.Control;
import org.freenono.controller.Settings;
import org.freenono.model.GameModeType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Shows UI to view and change all options.
 * 
 * @author Martin Wichmann, Christian Wichmann
 * 
 */
/*
 * How to add new options: 
 * 
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

    private JSpinner maxFailCount = null;
    private JSpinner maxTime = null;
    private JCheckBox markInvalid = null;
    private JCheckBox showNonogramName = null;
    private JCheckBox playAudio = null;
    private JCheckBox hidePlayfield = null;
    private JComboBox gameModes = null;
    private JComboBox gameLocale = null;
    private JButton buttonColorChooser = null;

    /**
     * Button class which stores a control type and the assigned key code for
     * given control. It gets the current key codes from main settings object
     * and automatically sets button label. If a different key code is assigned
     * to a control, the new code is NOT saved in the settings object!
     * 
     * @author Christian Wichmann
     */
    private class KeyAssignmentButton extends JButton {

        private static final long serialVersionUID = -3129245798190003304L;

        private ControlSettings.Control control;
        private int keyCode;

        public KeyAssignmentButton(ControlSettings.Control control) {

            this.control = control;

            keyCode = settings.getKeyCodeForControl(control);

            setText(KeyEvent.getKeyText(keyCode));
        }

        public ControlSettings.Control getControl() {

            return control;
        }

        public int getKeyCode() {

            return keyCode;
        }

        public void setKeyCode(int keyCode) {

            this.keyCode = keyCode;
            setText(KeyEvent.getKeyText(keyCode));
        }
    }

    private KeyAssignmentButton buttonConfigLeft = null;
    private KeyAssignmentButton buttonConfigRight = null;
    private KeyAssignmentButton buttonConfigUp = null;
    private KeyAssignmentButton buttonConfigDown = null;
    private KeyAssignmentButton buttonConfigMark = null;
    private KeyAssignmentButton buttonConfigOccupy = null;

    /**
     * Create the dialog.
     */
    public OptionsUI(Frame owner, Settings settings) {

        super(owner);

        this.settings = settings;
        this.csettings = settings.getControlSettings();

        panelMap = new LinkedHashMap<String, LinkedHashMap<String, JComponent>>();

        // initialize ui components
        initialize();

        // load options from file
        loadSettings();

        // add all options and tabs to panel map so they can be added to the
        // panel
        addOptionsToPanel();

        // populate tab with added options and resize
        populatePanel();

        pack();

        // check the screen resolution and change the size of the dialog if
        // necessary
        Toolkit tk = Toolkit.getDefaultToolkit();
        if ((this.getPreferredSize().getHeight() >= (tk.getScreenSize()
                .getHeight() - 50))
                || (this.getPreferredSize().getWidth() >= (tk.getScreenSize()
                        .getWidth() - 50))) {
            this.setPreferredSize(new Dimension((int) (tk.getScreenSize()
                    .getWidth() - 50),
                    (int) (tk.getScreenSize().getHeight() - 50)));
        }

        // set action handler for game mode combo box
        gameModes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                updateUI();
            }
        });
        updateUI();
    }

    private void updateUI() {

        switch ((GameModeType) gameModes.getSelectedItem()) {
        case QUIZ:
            maxFailCount.setEnabled(false);
            maxTime.setEnabled(false);
            markInvalid.setEnabled(true);
            break;
        case COUNT_TIME:
            maxFailCount.setEnabled(false);
            maxTime.setEnabled(false);
            markInvalid.setEnabled(true);
            break;
        case MAX_FAIL:
            maxFailCount.setEnabled(true);
            maxTime.setEnabled(false);
            markInvalid.setEnabled(true);
            break;
        case MAX_TIME:
            maxFailCount.setEnabled(false);
            maxTime.setEnabled(true);
            markInvalid.setEnabled(true);
            break;
        case PEN_AND_PAPER:
            maxFailCount.setEnabled(false);
            maxTime.setEnabled(false);
            markInvalid.setEnabled(false);
            break;
        case PENALTY:
            maxFailCount.setEnabled(false);
            maxTime.setEnabled(true);
            markInvalid.setEnabled(true);
            break;
        }
    }

    private void initialize() {

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        setSize(450, 300);
        setLocation(200, 150);
        setLayout(new BorderLayout());

        add(getButtonPane(), BorderLayout.SOUTH);
        add(getTabbedPane(), BorderLayout.CENTER);
    }

    private void addOptionsToPanel() {

        // fill tabs with options
        addTab(Messages.getString("OptionsUI.Game"));
        addOption(Messages.getString("OptionsUI.Game"),
                Messages.getString("OptionsUI.GameMode"), gameModes);
        addOption(Messages.getString("OptionsUI.Game"),
                Messages.getString("OptionsUI.MaxFailCount"), maxFailCount);
        addOption(Messages.getString("OptionsUI.Game"),
                Messages.getString("OptionsUI.TimeLimit"), maxTime);
        addOption(Messages.getString("OptionsUI.Game"),
                Messages.getString("OptionsUI.MarkFields"), markInvalid);

        addTab(Messages.getString("OptionsUI.Sound"));
        addOption(Messages.getString("OptionsUI.Sound"),
                Messages.getString("OptionsUI.PlayAudio"), playAudio);

        addTab(Messages.getString("OptionsUI.Control"));
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigControls"), new JLabel());
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigLeft"), buttonConfigLeft);
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigRight"), buttonConfigRight);
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigUp"), buttonConfigUp);
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigDown"), buttonConfigDown);
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigMark"), buttonConfigMark);
        addOption(Messages.getString("OptionsUI.Control"),
                Messages.getString("OptionsUI.ConfigPlace"), buttonConfigOccupy);

        addTab(Messages.getString("OptionsUI.GUI"));
        addOption(Messages.getString("OptionsUI.GUI"),
                Messages.getString("OptionsUI.GameLocale"), gameLocale);
        addOption(Messages.getString("OptionsUI.GUI"),
                Messages.getString("OptionsUI.BaseColor"), buttonColorChooser);
        addOption(Messages.getString("OptionsUI.GUI"),
                Messages.getString("OptionsUI.ShowNonogramName"),
                showNonogramName);
        addOption(Messages.getString("OptionsUI.GUI"),
                Messages.getString("OptionsUI.HideFields"), hidePlayfield);
    }

    private JTabbedPane getTabbedPane() {

        // init tab panel
        tabbedPane = new JTabbedPane();

        // create option variables (JCompononents)
        maxFailCount = new JSpinner();
        maxFailCount.setUI(new BasicSpinnerUI());
        maxFailCount.setModel(new SpinnerNumberModel());

        SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        spinnerDateModel.setCalendarField(Calendar.MINUTE);
        maxTime = new JSpinner();
        maxTime.setUI(new BasicSpinnerUI());
        maxTime.setModel(spinnerDateModel);
        maxTime.setEditor(new JSpinner.DateEditor(maxTime, "mm:ss"));

        markInvalid = new JCheckBox();
        showNonogramName = new JCheckBox();
        playAudio = new JCheckBox();
        hidePlayfield = new JCheckBox();

        gameModes = new JComboBox(GameModeType.values());

        // instantiate buttons to assign new keys
        buttonConfigLeft = new KeyAssignmentButton(
                ControlSettings.Control.moveLeft);
        buttonConfigRight = new KeyAssignmentButton(
                ControlSettings.Control.moveRight);
        buttonConfigUp = new KeyAssignmentButton(ControlSettings.Control.moveUp);
        buttonConfigDown = new KeyAssignmentButton(
                ControlSettings.Control.moveDown);
        buttonConfigMark = new KeyAssignmentButton(
                ControlSettings.Control.markField);
        buttonConfigOccupy = new KeyAssignmentButton(
                ControlSettings.Control.occupyField);

        ActionListener newButtonAssignAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                if (event.getSource() instanceof KeyAssignmentButton) {

                    // if a button is pressed to assign a new key, open a dialog
                    // and store the new key code in KeyAssignmentButton.
                    KeyAssignmentButton pressedButton = (KeyAssignmentButton) event
                            .getSource();
                    Control chosenControl = pressedButton.getControl();

                    NewKeyAssignmentDialog temp = new NewKeyAssignmentDialog(
                            settings.getControlSettings(), chosenControl);

                    pressedButton.setKeyCode(temp.getNewKeyCode());
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
        /**
         * Cell renderer to show user friendly names for locales in ComboBox
         * instead of locale abbreviations like "de" and "en".
         * 
         * @author Christian Wichmann
         */
        class GameLocaleCellRenderer extends DefaultListCellRenderer {

            private static final long serialVersionUID = 212569063244408202L;

            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {

                JLabel selectedLabel = (JLabel) super
                        .getListCellRendererComponent(list, value, index,
                                isSelected, cellHasFocus);
                Locale selectedLocale = (Locale) value;

                if (!selectedLocale.equals(Locale.ROOT)) {

                    selectedLabel.setText(selectedLocale.getDisplayLanguage());

                } else {

                    selectedLabel.setText(Messages
                            .getString("OptionsUI.GameLocaleDefault"));
                }

                return selectedLabel;
            }
        }
        gameLocale = new JComboBox(Manager.supportedLanguages);
        gameLocale.setRenderer(new GameLocaleCellRenderer());

        buttonColorChooser = new JButton(
                Messages.getString("OptionsUI.ChooseColor")) {

            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
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
        maxFailCount.setValue(settings.getMaxFailCount());

        Calendar c = Calendar.getInstance();
        maxTime.setValue(new Date(settings.getMaxTime()
                - (c.get(Calendar.ZONE_OFFSET) - c.get(Calendar.DST_OFFSET))));

        markInvalid.setSelected(settings.getMarkInvalid());
        showNonogramName.setSelected(settings.isShowNonogramName());
        playAudio.setSelected(settings.getPlayAudio());
        hidePlayfield.setSelected(settings.getHidePlayfield());
        gameLocale.setSelectedItem(settings.getGameLocale());
    }

    /**
     * Save settings to the options object
     */
    private void saveSettings() {

        Integer i = (Integer) maxFailCount.getValue();
        settings.setMaxFailCount(i.intValue());

        Date d = (Date) maxTime.getValue();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        settings.setMaxTime(d.getTime() + (c.get(Calendar.ZONE_OFFSET)
                + c.get(Calendar.DST_OFFSET)));

        settings.setGameMode((GameModeType) gameModes.getSelectedItem());
        settings.setMarkInvalid(markInvalid.isSelected());
        settings.setShowNonogramName(showNonogramName.isSelected());
        settings.setPlayAudio(playAudio.isSelected());
        settings.setHidePlayfield(hidePlayfield.isSelected());

        settings.setGameLocale((Locale) gameLocale.getSelectedItem());

        csettings.setControl(ControlSettings.Control.moveLeft,
                buttonConfigLeft.getKeyCode());
        csettings.setControl(ControlSettings.Control.moveRight,
                buttonConfigRight.getKeyCode());
        csettings.setControl(ControlSettings.Control.moveUp,
                buttonConfigUp.getKeyCode());
        csettings.setControl(ControlSettings.Control.moveDown,
                buttonConfigDown.getKeyCode());
        csettings.setControl(ControlSettings.Control.markField,
                buttonConfigMark.getKeyCode());
        csettings.setControl(ControlSettings.Control.occupyField,
                buttonConfigOccupy.getKeyCode());
    }

}