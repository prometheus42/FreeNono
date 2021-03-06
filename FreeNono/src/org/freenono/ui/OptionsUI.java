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
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.swing.AbstractAction;
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
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicSpinnerUI;

import org.apache.log4j.Logger;
import org.freenono.controller.Control;
import org.freenono.controller.Manager;
import org.freenono.controller.Settings;
import org.freenono.model.game_modes.GameModeType;

/**
 * Shows UI to view and change all options.
 *
 * @author Martin Wichmann, Christian Wichmann
 */
public class OptionsUI extends JDialog {

    /*
     * How to add new options:
     *
     * - create JComponent Object
     *
     * - call addTab() and addOption()
     *
     * - modify load and save settings methods
     *
     * - also: there are probably some xml changes necessary
     */

    private static final long serialVersionUID = 1650619963343405427L;

    private static Logger logger = Logger.getLogger(OptionsUI.class);

    private JTabbedPane tabbedPane;
    private final Map<String, LinkedHashMap<String, JComponent>> panelMap;
    private int tempMaxWidth = 0;
    private int tempMaxHeight = 0;
    private int tempCompMaxWidth = 0;
    private int tempCompMaxHeight = 0;
    private int maxColoumns = 0;

    private final Settings currentSettings;
    private final Settings gameSettings;

    private boolean programRestartNecessary = false;
    private boolean gameRestartNecessary = false;

    private JSpinner maxFailCount = null;
    private JSpinner maxTime = null;
    private SpinnerDateModel spinnerDateModel = null;
    private JCheckBox markInvalid = null;
    private JCheckBox showNonogramName = null;
    private JCheckBox playMusic = null;
    private JCheckBox playEffects = null;
    private JCheckBox hidePlayfield = null;
    private JCheckBox crossCaptions = null;
    private JCheckBox markCompleteRowsColumns = null;
    private JCheckBox searchForUpdates = null;
    private JCheckBox activateChat = null;
    private JComboBox<GameModeType> gameModes = null;
    private JComboBox<Locale> gameLocale = null;
    private ColorChooser baseColorChooser = null;
    private ColorChooser textColorChooser = null;

    private static final String OPTIONS_TAB_SOUND = Messages.getString("OptionsUI.Sound");
    private static final String OPTIONS_TAB_GAME = Messages.getString("OptionsUI.Game");
    private static final String OPTIONS_TAB_GUI = Messages.getString("OptionsUI.GUI");
    private static final String OPTIONS_TAB_CONTROL = Messages.getString("OptionsUI.Control");
    private static final String OPTIONS_TAB_NETWORK = Messages.getString("OptionsUI.Network");

    /**
     * Button class which stores a control type and the assigned key code for given control. It gets
     * the current key codes from main settings object and automatically sets button label. If a
     * different key code is assigned to a control, the new code is NOT saved in the settings
     * object!
     *
     * @author Christian Wichmann
     */
    private class KeyAssignmentButton extends JButton {

        private static final long serialVersionUID = -3129245798190003304L;

        private final Control control;
        private int keyCode;

        /**
         * Initializes this button with its control.
         *
         * @param control
         *            control for which this button is used
         */
        public KeyAssignmentButton(final Control control) {

            this.control = control;
            keyCode = currentSettings.getKeyCodeForControl(control);
            setText(KeyEvent.getKeyText(keyCode));
        }

        /**
         * Gets the control for this button.
         *
         * @return control for this button
         */
        public Control getControl() {

            return control;
        }

        /**
         * Gets key code for this buttons control.
         *
         * @return Key code for this buttons control.
         */
        public int getKeyCode() {

            return keyCode;
        }

        /**
         * Sets the key code for this buttons control.
         *
         * @param keyCode
         *            Key code for this buttons control.
         */
        public void setKeyCode(final int keyCode) {

            this.keyCode = keyCode;
            setText(KeyEvent.getKeyText(keyCode));
        }
    }

    /**
     * Displays a button rendered in the given color that is currently set. By clicking on the
     * button the set color can be changed.
     *
     * @author Christian Wichmann
     */
    class ColorChooser extends JButton {

        private static final long serialVersionUID = -3113522515604494804L;

        private Color currentColor;

        /**
         * Initializes a new color chooser component to allow users to change the base or text
         * color.
         *
         * @param text
         *            button text on color chooser button
         * @param currentColor
         *            currently set color for this color chooser
         */
        public ColorChooser(final String text, final Color currentColor) {

            super(text);
            this.currentColor = currentColor;

            setContentAreaFilled(false);
            setForeground(getInvertedColor());
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent event) {

                    final Color tmp = JColorChooser.showDialog(OptionsUI.this, Messages.getString("OptionsUI.ChooseColor"), currentColor);

                    if (tmp != null) {
                        ColorChooser.this.currentColor = tmp;
                        ColorChooser.this.setForeground(getInvertedColor());
                    }
                    repaint();
                }
            });
        }

        /**
         * Returns the inverted color of the currently set color. It is used so text can be read
         * well on the colored background.
         *
         * @return inverted color of the currently set color
         */
        private Color getInvertedColor() {

            return new Color(255 - currentColor.getRed(), 255 - currentColor.getGreen(), 255 - currentColor.getBlue());
        }

        @Override
        public void paintComponent(final Graphics g) {

            final Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2.setColor(currentColor);
            g2.fillRect(0, 0, getSize().width, getSize().height);
            super.paintComponent(g2);
        }

        /**
         * Gets currently set color from this color chooser.
         *
         * @return currently set color
         */
        public final Color getCurrentColor() {

            return currentColor;
        }

        /**
         * Sets currently set color for this color chooser.
         *
         * @param currentColor
         *            color to be set for this color chooser
         */
        public final void setCurrentColor(final Color currentColor) {

            this.currentColor = currentColor;
            repaint();
        }

    }

    private KeyAssignmentButton buttonConfigLeft = null;
    private KeyAssignmentButton buttonConfigRight = null;
    private KeyAssignmentButton buttonConfigUp = null;
    private KeyAssignmentButton buttonConfigDown = null;
    private KeyAssignmentButton buttonConfigMark = null;
    private KeyAssignmentButton buttonConfigOccupy = null;

    /**
     * Create the dialog to change options.
     *
     * @param owner
     *            Parent of this dialog.
     * @param settings
     *            Settings object holding all options.
     */
    public OptionsUI(final Frame owner, final Settings settings) {

        super(owner);

        gameSettings = settings;
        currentSettings = new Settings(settings);

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

        setSizeAndLocation();

        addListener();

        addKeyBindings();

        updateUI();
    }

    /**
     * Adds a listener for updating the ui when the game mode is changed.
     */
    private void addListener() {

        // set action handler for game mode combo box
        gameModes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                updateUI();
            }
        });
    }

    /**
     * Adds key bindings for this dialog to exit it.
     */
    private void addKeyBindings() {

        final JComponent rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "QuitStatisticsViewDialog");
        rootPane.getActionMap().put("QuitStatisticsViewDialog", new AbstractAction() {

            private static final long serialVersionUID = 8132652822791902496L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
    }

    /**
     * Checks if dialog is to large for screen and positions dialog.
     */
    private void setSizeAndLocation() {

        final int margin = 50;

        // check the screen resolution and change the size of the dialog if
        // necessary
        final Toolkit tk = Toolkit.getDefaultToolkit();
        if ((getPreferredSize().getHeight() >= (tk.getScreenSize().getHeight() - margin))
                || (getPreferredSize().getWidth() >= (tk.getScreenSize().getWidth() - margin))) {

            setPreferredSize(new Dimension((int) (tk.getScreenSize().getWidth() - margin), (int) (tk.getScreenSize().getHeight() - margin)));
        }
    }

    /**
     * Updates components of ui and enables or disables game options depending on chosen game mode.
     */
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
            markInvalid.setEnabled(false);
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
        default:
            break;
        }
    }

    /**
     * Sets some ui settings and initializes the panes holding buttons and tabs.
     */
    private void initialize() {

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildButtonPane(), BorderLayout.SOUTH);
        add(buildTabbedPane(), BorderLayout.CENTER);
    }

    /**
     * Adds all options to the tabbed pane via methods addTab() and addOption().
     */
    private void addOptionsToPanel() {

        // fill tabs with options
        addTab(OPTIONS_TAB_GAME);
        addOption(OPTIONS_TAB_GAME, Messages.getString("OptionsUI.GameMode"), gameModes);
        addOption(OPTIONS_TAB_GAME, Messages.getString("OptionsUI.MaxFailCount"), maxFailCount);
        addOption(OPTIONS_TAB_GAME, Messages.getString("OptionsUI.TimeLimit"), maxTime);
        addOption(OPTIONS_TAB_GAME, Messages.getString("OptionsUI.MarkFields"), markInvalid);
        addOption(OPTIONS_TAB_GAME, Messages.getString("OptionsUI.CrossCaptions"), crossCaptions);
        addOption(OPTIONS_TAB_GAME, Messages.getString("OptionsUI.MarkCompleteRowsColumn"), markCompleteRowsColumns);

        addTab(OPTIONS_TAB_SOUND);
        addOption(OPTIONS_TAB_SOUND, Messages.getString("OptionsUI.PlayMusic"), playMusic);
        addOption(OPTIONS_TAB_SOUND, Messages.getString("OptionsUI.PlayEffects"), playEffects);

        addTab(OPTIONS_TAB_CONTROL);
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigControls"), new JLabel());
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigLeft"), buttonConfigLeft);
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigRight"), buttonConfigRight);
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigUp"), buttonConfigUp);
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigDown"), buttonConfigDown);
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigMark"), buttonConfigMark);
        addOption(OPTIONS_TAB_CONTROL, Messages.getString("OptionsUI.ConfigPlace"), buttonConfigOccupy);

        addTab(OPTIONS_TAB_GUI);
        addOption(OPTIONS_TAB_GUI, Messages.getString("OptionsUI.GameLocale"), gameLocale);
        addOption(OPTIONS_TAB_GUI, Messages.getString("OptionsUI.BaseColor"), baseColorChooser);
        addOption(OPTIONS_TAB_GUI, Messages.getString("OptionsUI.TextColor"), textColorChooser);
        addOption(OPTIONS_TAB_GUI, Messages.getString("OptionsUI.ShowNonogramName"), showNonogramName);
        addOption(OPTIONS_TAB_GUI, Messages.getString("OptionsUI.HideFields"), hidePlayfield);

        addTab(OPTIONS_TAB_NETWORK);
        addOption(OPTIONS_TAB_NETWORK, Messages.getString("OptionsUI.SearchForUpdates"), searchForUpdates);
        addOption(OPTIONS_TAB_NETWORK, Messages.getString("OptionsUI.ActivateChat"), activateChat);
    }

    /**
     * Initializes all components for the tabbed pane and returns a empty pane on which all
     * components are inserted via methods addTab() and addOption().
     *
     * @return Empty pane for all option components.
     */
    private JTabbedPane buildTabbedPane() {

        tabbedPane = new JTabbedPane();

        // create spinner for maximum fail count
        maxFailCount = new JSpinner();
        maxFailCount.setUI(new BasicSpinnerUI());
        maxFailCount.setModel(new SpinnerNumberModel());

        // create spinner for maximum game time including a model and editor
        spinnerDateModel = new SpinnerDateModel();
        spinnerDateModel.setCalendarField(Calendar.MINUTE);
        maxTime = new JSpinner();
        maxTime.setModel(spinnerDateModel);
        // set format string for editor to show only minutes and seconds
        final JSpinner.DateEditor spinnerDateEditor = new JSpinner.DateEditor(maxTime, "mm:ss");
        // set time zone for editor to avoid bugs where offsets according to the
        // current time zone will be added to or subtracted from the value
        spinnerDateEditor.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
        maxTime.setEditor(spinnerDateEditor);

        // create check boxes for boolean options
        markInvalid = new JCheckBox();
        crossCaptions = new JCheckBox();
        markCompleteRowsColumns = new JCheckBox();
        showNonogramName = new JCheckBox();
        playMusic = new JCheckBox();
        playEffects = new JCheckBox();
        hidePlayfield = new JCheckBox();
        searchForUpdates = new JCheckBox();
        activateChat = new JCheckBox();

        gameModes = new JComboBox<GameModeType>(GameModeType.values());

        // instantiate buttons to assign new keys
        buttonConfigLeft = new KeyAssignmentButton(Control.MOVE_LEFT);
        buttonConfigRight = new KeyAssignmentButton(Control.MOVE_RIGHT);
        buttonConfigUp = new KeyAssignmentButton(Control.MOVE_UP);
        buttonConfigDown = new KeyAssignmentButton(Control.MOVE_DOWN);
        buttonConfigMark = new KeyAssignmentButton(Control.MARK_FIELD);
        buttonConfigOccupy = new KeyAssignmentButton(Control.OCCUPY_FIELD);

        final ActionListener newButtonAssignAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {

                if (event.getSource() instanceof KeyAssignmentButton) {
                    // if a button is pressed to assign a new key, open a dialog
                    // and store the new key code in KeyAssignmentButton.
                    final KeyAssignmentButton pressedButton = (KeyAssignmentButton) event.getSource();
                    final Control chosenControl = pressedButton.getControl();

                    final NewKeyAssignmentDialog temp = new NewKeyAssignmentDialog(currentSettings, chosenControl);

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

        /**
         * Cell renderer to show user friendly names for locales in ComboBox instead of locale
         * abbreviations like "de" and "en".
         *
         * @author Christian Wichmann
         */
        class GameLocaleCellRenderer extends DefaultListCellRenderer {

            private static final long serialVersionUID = 212569063244408202L;

            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
                    final boolean isSelected, final boolean cellHasFocus) {

                final JLabel selectedLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                final Locale selectedLocale = (Locale) value;

                if (!selectedLocale.equals(Locale.ROOT)) {

                    selectedLabel.setText(selectedLocale.getDisplayLanguage());

                } else {

                    selectedLabel.setText(Messages.getString("OptionsUI.GameLocaleDefault"));
                }

                return selectedLabel;
            }
        }
        gameLocale = new JComboBox<Locale>(Manager.SUPPORTED_LANGUAGES);
        gameLocale.setRenderer(new GameLocaleCellRenderer());

        // setup color chooser for base and text color
        baseColorChooser = new ColorChooser(Messages.getString("OptionsUI.ChooseColor"), currentSettings.getBaseColor());
        textColorChooser = new ColorChooser(Messages.getString("OptionsUI.ChooseColor"), currentSettings.getTextColor());

        return tabbedPane;
    }

    /**
     * Builds buttons on a pane.
     *
     * @return Pane with Ok and Cancel buttons.
     */
    private JPanel buildButtonPane() {

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JButton okButton = new JButton(Messages.getString("OptionsUI.OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                // save options to file
                saveSettings();
                dispose();
            }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        final JButton cancelButton = new JButton(Messages.getString("OptionsUI.Cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                dispose();
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);

        final JButton resetToDefaultsButton = new JButton(Messages.getString("OptionsUI.ResetToDefaultsButton"));
        resetToDefaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                /*
                 * Reset all options in Settings class to defaults if user confirms it. Only the
                 * settings in currentSettings will be changed and have later to be copied to
                 * gameSettings.
                 */
                currentSettings.resetSettings();
                loadSettings();
            }
        });
        resetToDefaultsButton.setActionCommand("Reset");
        buttonPane.add(resetToDefaultsButton);

        return buttonPane;
    }

    /**
     * Add tab to the list, so it will be added to the options dialog.
     *
     * @param title
     *            name of the tab title
     */
    private void addTab(final String title) {

        if (!panelMap.containsKey(title)) {
            panelMap.put(title, new LinkedHashMap<String, JComponent>());
        }
    }

    /**
     * Add option to a specific tab.
     *
     * @param tabTitle
     *            to which tab should it be added
     * @param optionTitle
     *            name of the option
     * @param comp
     *            component that represents option value
     */
    private void addOption(final String tabTitle, final String optionTitle, final JComponent comp) {

        final LinkedHashMap<String, JComponent> list = panelMap.get(tabTitle);
        list.put(optionTitle, comp);
    }

    /**
     * Build the real panels and tabs from the information added through the methods addTab() and
     * addOption().
     */
    private void populatePanel() {
        logger.debug("Populating options panel...");

        final int insets = 20;
        final Set<Entry<String, LinkedHashMap<String, JComponent>>> set = panelMap.entrySet();

        Set<Entry<String, JComponent>> map = null;

        // iterate through tabs/options and collect some information that is
        // needed to calculate the layout of the dialog
        for (final Entry<String, LinkedHashMap<String, JComponent>> e : set) {
            map = null;
            map = e.getValue().entrySet();

            maxColoumns = map.size() > maxColoumns ? map.size() : maxColoumns;

            Dimension temp = null;
            for (final Entry<String, JComponent> f : map) {
                temp = new JLabel(f.getKey()).getPreferredSize();
                tempMaxWidth = temp.getWidth() > tempMaxWidth ? (int) temp.getWidth() : tempMaxWidth;
                tempMaxHeight = temp.getHeight() > tempMaxHeight ? (int) temp.getHeight() : tempMaxHeight;
                temp = f.getValue().getPreferredSize();
                tempCompMaxWidth = temp.getWidth() > tempCompMaxWidth ? (int) temp.getWidth() : tempCompMaxWidth;
                tempCompMaxHeight = temp.getHeight() > tempCompMaxHeight ? (int) temp.getHeight() : tempCompMaxHeight;
            }
        }

        // create the actual panel with the information given through the list
        // and the previously collected infos
        for (final Entry<String, LinkedHashMap<String, JComponent>> e : set) {
            buildPanel(insets, e);
        }
    }

    /**
     * Builds a panel for a group of settings and adds it directly to the tabbed pane of this
     * dialog.
     *
     * @param insets
     *            width of insets to be used in this panel
     * @param e
     *            list with all components and their respectively string labels
     */
    private void buildPanel(final int insets, final Entry<String, LinkedHashMap<String, JComponent>> e) {
        String key = "";
        Set<Entry<String, JComponent>> map = null;

        key = e.getKey();
        map = e.getValue().entrySet();

        // create panel for the current tab
        final JPanel panel = new JPanel(new GridBagLayout());

        int col = 0;
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(insets / 2, insets, insets / 2, insets);
        // add the labels and components the the current tab
        // do some magic with the preferred size, so it looks cool
        for (final Entry<String, JComponent> f : map) {
            c.gridx = 0;
            c.gridy = col++;
            final JLabel templabel = new JLabel(f.getKey());
            templabel.setPreferredSize(new Dimension(tempMaxWidth, tempMaxHeight));
            panel.add(templabel, c);
            c.gridx = 1;
            final JComponent tempComp = f.getValue();
            tempComp.setPreferredSize(new Dimension(tempCompMaxWidth, tempCompMaxHeight));
            panel.add(tempComp, c);
        }

        // fill up panels with empty labels so the tabs all look the same
        if (map.size() < maxColoumns) {
            for (int j = 0; j < (maxColoumns - map.size()); j++) {
                c.gridx = 0;
                c.gridy = col++;
                final JLabel templabel = new JLabel("");
                templabel.setPreferredSize(new Dimension(tempMaxWidth, tempMaxHeight));
                panel.add(templabel, c);
                c.gridx = 1;
                templabel.setPreferredSize(new Dimension(tempCompMaxWidth, tempCompMaxHeight));
                panel.add(templabel, c);
            }
        }

        // put the panel in a scroll pane, so the content can be seen if the
        // window is small
        final JScrollPane scroll = new JScrollPane(panel);
        final JPanel tempPanel = new JPanel(new GridLayout(1, 1));
        tempPanel.add(scroll);
        tabbedPane.add(key, tempPanel);
    }

    /**
     * Load settings from the settings object.
     */
    private void loadSettings() {

        gameModes.setSelectedItem(currentSettings.getGameMode());
        maxFailCount.setValue(currentSettings.getMaxFailCount());
        markInvalid.setSelected(currentSettings.getMarkInvalid());
        crossCaptions.setSelected(currentSettings.getCrossCaptions());
        markCompleteRowsColumns.setSelected(currentSettings.getMarkCompleteRowsColumns());
        showNonogramName.setSelected(currentSettings.isShowNonogramName());
        playMusic.setSelected(currentSettings.isPlayMusic());
        playEffects.setSelected(currentSettings.isPlayEffects());
        hidePlayfield.setSelected(currentSettings.getHidePlayfield());
        searchForUpdates.setSelected(currentSettings.shouldSearchForUpdates());
        activateChat.setSelected(currentSettings.shouldActivateChat());
        gameLocale.setSelectedItem(currentSettings.getGameLocale());

        // update color chooser with reseted color
        baseColorChooser.setCurrentColor(currentSettings.getBaseColor());
        textColorChooser.setCurrentColor(currentSettings.getTextColor());

        // update key chooser dialogs
        buttonConfigLeft.setKeyCode(currentSettings.getKeyCodeForControl(Control.MOVE_LEFT));
        buttonConfigRight.setKeyCode(currentSettings.getKeyCodeForControl(Control.MOVE_RIGHT));
        buttonConfigUp.setKeyCode(currentSettings.getKeyCodeForControl(Control.MOVE_UP));
        buttonConfigDown.setKeyCode(currentSettings.getKeyCodeForControl(Control.MOVE_DOWN));
        buttonConfigMark.setKeyCode(currentSettings.getKeyCodeForControl(Control.MARK_FIELD));
        buttonConfigOccupy.setKeyCode(currentSettings.getKeyCodeForControl(Control.OCCUPY_FIELD));

        loadGameTime();
    }

    /**
     * Loads maximum game time from settings and sets minimum and maximum for spinner.
     */
    private void loadGameTime() {

        final Calendar cal = Calendar.getInstance();

        // set current game time
        final Date currentGameTime = new Date(currentSettings.getMaxTime());
        maxTime.setValue(currentGameTime);

        // set minimum game time to 00:00
        cal.setTime(currentGameTime);
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        final Date minDate = cal.getTime();
        spinnerDateModel.setStart(minDate);

        // set maximum game time to 59:59
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        final Date maxDate = cal.getTime();
        spinnerDateModel.setEnd(maxDate);
    }

    /**
     * Save settings to the options object.
     */
    private void saveSettings() {

        checkChangesInSettings();

        final Integer i = (Integer) maxFailCount.getValue();
        currentSettings.setMaxFailCount(i.intValue());

        final Date d = (Date) maxTime.getValue();
        currentSettings.setMaxTime(d.getTime());

        currentSettings.setGameMode((GameModeType) gameModes.getSelectedItem());
        currentSettings.setMarkInvalid(markInvalid.isSelected());
        currentSettings.setCrossCaptions(crossCaptions.isSelected());
        currentSettings.setMarkCompleteRowsColumns(markCompleteRowsColumns.isSelected());
        currentSettings.setShowNonogramName(showNonogramName.isSelected());
        currentSettings.setPlayMusic(playMusic.isSelected());
        currentSettings.setPlayEffects(playEffects.isSelected());
        currentSettings.setHidePlayfield(hidePlayfield.isSelected());
        currentSettings.setSearchForUpdates(searchForUpdates.isSelected());
        currentSettings.setActivateChat(activateChat.isSelected());

        currentSettings.setGameLocale((Locale) gameLocale.getSelectedItem());
        currentSettings.setBaseColor(baseColorChooser.getCurrentColor());
        currentSettings.setTextColor(textColorChooser.getCurrentColor());

        currentSettings.setControl(Control.MOVE_LEFT, buttonConfigLeft.getKeyCode());
        currentSettings.setControl(Control.MOVE_RIGHT, buttonConfigRight.getKeyCode());
        currentSettings.setControl(Control.MOVE_UP, buttonConfigUp.getKeyCode());
        currentSettings.setControl(Control.MOVE_DOWN, buttonConfigDown.getKeyCode());
        currentSettings.setControl(Control.MARK_FIELD, buttonConfigMark.getKeyCode());
        currentSettings.setControl(Control.OCCUPY_FIELD, buttonConfigOccupy.getKeyCode());

        gameSettings.setAllOptions(currentSettings);
    }

    /**
     * Checks whether a restart of FreeNono or of the running game is necessary to adopt setting
     * changes.
     */
    private void checkChangesInSettings() {

        // check maximum game time
        final Date d = (Date) maxTime.getValue();
        if (d.getTime() != currentSettings.getMaxTime()) {
            gameRestartNecessary = true;
        }

        // check fail count
        final Integer i = (Integer) maxFailCount.getValue();
        if (i != currentSettings.getMaxFailCount()) {
            gameRestartNecessary = true;
        }

        // check game mode
        final GameModeType g = (GameModeType) gameModes.getSelectedItem();
        if (g != currentSettings.getGameMode()) {
            gameRestartNecessary = true;
        }

        // check mark failed fields
        if (markInvalid.isSelected() != currentSettings.getMarkInvalid()) {
            gameRestartNecessary = true;
        }

        // check show nonogram name
        if (showNonogramName.isSelected() != currentSettings.isShowNonogramName()) {
            gameRestartNecessary = true;
        }

        // check hide playfield while pausing
        if (hidePlayfield.isSelected() != currentSettings.getHidePlayfield()) {
            gameRestartNecessary = true;
        }

        // check game language
        final Locale l = (Locale) gameLocale.getSelectedItem();
        if (!l.equals(currentSettings.getGameLocale())) {
            programRestartNecessary = true;
        }

        // check if key bindings changed
        if (currentSettings.getKeyCodeForControl(Control.MOVE_LEFT) != buttonConfigLeft.getKeyCode()
                || currentSettings.getKeyCodeForControl(Control.MOVE_RIGHT) != buttonConfigRight.getKeyCode()
                || currentSettings.getKeyCodeForControl(Control.MOVE_DOWN) != buttonConfigDown.getKeyCode()
                || currentSettings.getKeyCodeForControl(Control.MOVE_UP) != buttonConfigUp.getKeyCode()
                || currentSettings.getKeyCodeForControl(Control.MARK_FIELD) != buttonConfigMark.getKeyCode()
                || currentSettings.getKeyCodeForControl(Control.OCCUPY_FIELD) != buttonConfigOccupy.getKeyCode()) {
            gameRestartNecessary = false;
        }
    }

    /**
     * Returns whether a restart of FreeNono is necessary to adopt the setting change. This field is
     * only set when the dialog is already closed.
     *
     * @return whether a restart of FreeNono is necessary
     */
    public final boolean isProgramRestartNecessary() {

        return programRestartNecessary;
    }

    /**
     * Returns whether a restart of the running game is necessary to adopt the setting change. This
     * field is only set when the dialog is already closed.
     *
     * @return whether a restart of the running game is necessary
     */
    public final boolean isGameRestartNecessary() {

        return gameRestartNecessary;
    }
}
