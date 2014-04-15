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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.model.game_modes.GameModeType;
import org.freenono.net.CoopGame;
import org.freenono.net.CoopGame.CoopGameType;
import org.freenono.net.CoopHandler;
import org.freenono.net.NonoWebConnectionManager;
import org.freenono.provider.CollectionProvider;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.common.FreeNonoDialog;
import org.freenono.ui.explorer.NonogramChooserUI;

/**
 * Shows a dialog to start or join a coop game with two (or more?) players.
 * 
 * @author Christian Wichmann
 */
public class CoopStartDialog extends FreeNonoDialog {

    private static final long serialVersionUID = -8228572015437189814L;

    private static Logger logger = Logger.getLogger(CoopStartDialog.class);

    private ButtonGroup group = new ButtonGroup();
    private JLabel labelGameMode;
    private JComboBox<GameModeType> gameModes;
    private JList<CoopGame> list;
    private JLabel labelChooser;
    private JButton nonogramChoserButton;
    private JRadioButton chooseNewGame;
    private JRadioButton chooseEnterGame;

    private Settings settings;
    private List<CollectionProvider> nonogramProvider;
    private NonogramProvider chosenNonogram;
    private Timer updateGameListTimer;

    /**
     * Initializes a dialog to start or join a coop game.
     * 
     * @param owner
     *            parent window of this dialog
     * @param settings
     *            settings object
     * @param nonogramProvider
     *            list of nonogram collections from which to choose next pattern
     */
    public CoopStartDialog(final Frame owner, final Settings settings,
            final List<CollectionProvider> nonogramProvider) {

        super(owner, settings.getColorModel().getBottomColor(), settings
                .getColorModel().getTopColor());

        this.settings = settings;
        this.nonogramProvider = nonogramProvider;

        initialize();

        addListeners();

        addKeyBindings();

        updateGameListTimer = new Timer();
        updateGameListTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                addCurrentCoopGames();
            }
        }, 0, 100);
    }

    /**
     * Initializes this dialog.
     */
    private void initialize() {

        setTitle("Coop start...");

        // set layout manager
        GridBagLayout layout = new GridBagLayout();
        getContentPane().setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        final int inset = 20;
        c.insets = new Insets(inset, inset, inset, inset);

        chooseNewGame = new JRadioButton("Start new coop game...");
        chooseNewGame.setSelected(true);
        group.add(chooseNewGame);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        add(chooseNewGame, c);

        labelGameMode = new JLabel("Choose game mode to play...", JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(labelGameMode, c);

        gameModes = new JComboBox<GameModeType>(GameModeType.values());
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(gameModes, c);

        JLabel labelNonogram = new JLabel("Choose nonogram to play...",
                JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(labelNonogram, c);

        nonogramChoserButton = new JButton("Choose nonogram...");
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(nonogramChoserButton, c);

        chooseEnterGame = new JRadioButton("Enter existing coop game...");
        group.add(chooseEnterGame);

        c.gridx = 0;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        add(chooseEnterGame, c);

        labelChooser = new JLabel("Choose existing game...", JLabel.CENTER);
        labelChooser.setEnabled(false);
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(labelChooser, c);

        list = new JList<>();
        list.setEnabled(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        c.gridx = 0;
        c.gridy = 5;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(list, c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(getButtonPanel(), c);

        pack();
    }

    /**
     * Adds listeners for all components.
     */
    private void addListeners() {

        chooseNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {

                if (((JRadioButton) e.getSource()).isSelected()) {
                    labelGameMode.setEnabled(true);
                    gameModes.setEnabled(true);

                    labelChooser.setEnabled(false);
                    list.setEnabled(false);

                } else {
                    labelGameMode.setEnabled(false);
                    gameModes.setEnabled(false);

                    labelChooser.setEnabled(true);
                    list.setEnabled(true);
                }
            }
        });

        chooseEnterGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {

                if (((JRadioButton) e.getSource()).isSelected()) {
                    labelGameMode.setEnabled(false);
                    gameModes.setEnabled(false);

                    labelChooser.setEnabled(true);
                    list.setEnabled(true);

                } else {
                    labelGameMode.setEnabled(true);
                    gameModes.setEnabled(true);

                    labelChooser.setEnabled(false);
                    list.setEnabled(false);
                }
            }
        });

        nonogramChoserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                NonogramChooserUI nonoChooser = new NonogramChooserUI(null,
                        nonogramProvider, settings.getColorModel());
                nonoChooser.setVisible(true);
                chosenNonogram = nonoChooser.getChosenNonogram();
                nonoChooser.dispose();
                handleExit();
            }
        });
    }

    /**
     * Adds key bindings for this dialog to exit it.
     */
    private void addKeyBindings() {

        JComponent rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESCAPE"), "QuitCoopDialog");
        rootPane.getActionMap().put("QuitCoopDialog", new AbstractAction() {
            private static final long serialVersionUID = 653149778238948695L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
    }

    /**
     * Adds all currently running coop games to list box.
     */
    private void addCurrentCoopGames() {

        DefaultListModel<CoopGame> listModel;
        listModel = new DefaultListModel<>();

        CoopHandler ch = NonoWebConnectionManager.getInstance()
                .getCoopHandler();
        for (CoopGame game : ch.listAllCoopGames()) {
            listModel.addElement(game);
        }

        list.setModel(listModel);
        list.setSelectedIndex(0);
    }

    /**
     * Returns a panel containing all buttons on the lower margin of this
     * dialog.
     * 
     * @return panel with all buttons
     */
    private JPanel getButtonPanel() {

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(cancelButton);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                handleOk();
            }
        });
        buttonPanel.add(okButton);

        return buttonPanel;
    }

    /**
     * Handles click on the OK button.
     */
    private void handleOk() {

        logger.debug("Chosen game was: " + list.getSelectedValue());
    }

    /**
     * Stops update timer and closes this dialog.
     */
    private void handleExit() {

        setVisible(false);
        updateGameListTimer.cancel();
    }

    /**
     * Returns the <code>CoopGame</code> as result of this dialog. Either it
     * contains the coop game ID for an already announced game or the nonogram
     * pattern for a new coop game to be initiated. by the user.
     * 
     * @return coop game data
     */
    public final CoopGame getCoopGame() {

        String coopGameId = "";
        CoopGameType coopGameType;
        CoopGame cp = null;

        if (chooseEnterGame.isSelected()) {
            logger.debug("Chosen game was: " + list.getSelectedValue());
            coopGameType = CoopGameType.JOINING;
            coopGameId = list.getSelectedValue().getCoopGameId();
            cp = new CoopGame(coopGameType, coopGameId);

        } else if (chooseNewGame.isSelected()) {
            coopGameType = CoopGameType.INITIATING;
            cp = new CoopGame(coopGameType, chosenNonogram.fetchNonogram());

        } else {
            assert false : "Either new coop game is initiated or a game is joined.";
        }

        return cp;
    }

    /**
     * Returns whether user chose to initiate a new coop game or to join an
     * existing one.
     * 
     * @return true, if user chose that a new coop game should be initiated
     */
    public final boolean wasNewCoopGameChosen() {

        boolean result = false;

        return result;
    }
}
