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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.freenono.controller.Settings;
import org.freenono.model.game_modes.GameModeType;

/**
 * Shows a dialog to start or join a coop game with two (or more?) players.
 * 
 * @author Christian Wichmann
 */
public class CoopStartDialog extends JDialog {

    private static final long serialVersionUID = -8228572015437189814L;

    private Settings settings;
    private ButtonGroup group = new ButtonGroup();
    private JLabel labelGameMode;
    private JComboBox gameModes;
    private JList list;
    private JLabel labelChooser;

    public CoopStartDialog(Settings settings) {

        this.settings = settings;

        initialize();

        addKeyBindings();
    }

    private void initialize() {

        // setSize(500, 500);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setTitle("Coop start...");
        getContentPane().setBackground(settings.getColorModel().getTopColor());
        getContentPane().setForeground(
                settings.getColorModel().getBottomColor());
        ((JPanel) getContentPane()).setBorder(BorderFactory
                .createEtchedBorder());

        // set layout manager
        GridBagLayout layout = new GridBagLayout();
        getContentPane().setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 20, 20, 20);

        // add radio button and labels
        JRadioButton chooseNewGame = new JRadioButton("Start new coop game...");
        chooseNewGame.setSelected(true);
        group.add(chooseNewGame);
        chooseNewGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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

        gameModes = new JComboBox(GameModeType.values());
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(gameModes, c);

        // add radio button and labels
        JRadioButton chooseEnterGame = new JRadioButton(
                "Enter existing coop game...");
        group.add(chooseEnterGame);
        chooseEnterGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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

        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        add(chooseEnterGame, c);

        labelChooser = new JLabel("Choose existing game...", JLabel.CENTER);
        labelChooser.setEnabled(false);
        c.gridx = 0;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(labelChooser, c);

        DefaultListModel listModel;
        listModel = new DefaultListModel();
        listModel.addElement("Jane Doe");
        listModel.addElement("John Smith");
        listModel.addElement("Kathy Green");
        list = new JList(listModel);
        list.setEnabled(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(list, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        add(getButtonPanel(), c);

        pack();
    }

    private void addKeyBindings() {

        JComponent rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESC"), "QuitCoopDialog");
        rootPane.getActionMap().put("QuitCoopDialog", new AbstractAction() {

            private static final long serialVersionUID = 653149778238948695L;

            public void actionPerformed(ActionEvent e) {

                setVisible(false);
            }
        });
    }

    private JPanel getButtonPanel() {

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                setVisible(false);
            }
        });
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }
}
