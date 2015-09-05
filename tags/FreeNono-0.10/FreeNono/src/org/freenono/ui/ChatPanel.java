/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2014 by FreeNono Development Team
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import org.freenono.net.ChatHandler;
import org.freenono.net.NonoWebConnectionManager;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Shows a panel to send and receive chat messages.
 *
 * @author Christian Wichmann
 */
public class ChatPanel extends JPanel {

    private static final long serialVersionUID = 7324602090393053201L;

    private static final String WELCOME_MESSAGE = Messages.getString("MainUI.ChatWelcomeMessage") + "\n";

    private ChatHandler chatHandler;
    private JTextArea receivedMessagesTextArea;
    private JTextField sendMessageTextField;
    private JButton sendButton;

    private final List<String> messageHistory = new ArrayList<>();
    private int currentIndexFromHistory = 0;

    /**
     * Instantiates a new chat panel.
     */
    public ChatPanel() {

        initialize();

        connectToChat();

        addListeners();

        addKeyBindings();
    }

    /**
     * Initializes all components of this chat panel.
     */
    private void initialize() {

        setOpaque(false);
        setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        final int inset = 15;
        constraints.insets = new Insets(inset, inset, inset, inset);
        constraints.fill = GridBagConstraints.NONE;

        receivedMessagesTextArea = new JTextArea(WELCOME_MESSAGE, 20, 25);
        receivedMessagesTextArea.setEditable(false);
        receivedMessagesTextArea.setFocusable(false);
        receivedMessagesTextArea.setLineWrap(true);
        receivedMessagesTextArea.setWrapStyleWord(true);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        final JScrollPane scrollPane = new JScrollPane(receivedMessagesTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, constraints);

        sendMessageTextField = new JTextField(18);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        add(sendMessageTextField, constraints);

        sendButton = new JButton(Messages.getString("MainUI.ChatSendButton"));
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        add(sendButton, constraints);

        /*
         * Set policy for auto scrolling to end of document. After setting the update policy the
         * caret has to be manually set to the current end of the document.
         *
         * Hint: http://tips4java.wordpress.com/2008/10/22/text-area-scrolling/
         */
        final DefaultCaret caret = (DefaultCaret) receivedMessagesTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        receivedMessagesTextArea.setCaretPosition(receivedMessagesTextArea.getDocument().getLength());
    }

    /**
     * Adds listeners for all components.
     */
    private void addListeners() {

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                messageHistory.add(sendMessageTextField.getText());
                currentIndexFromHistory = 0;
                chatHandler.sendMessage(sendMessageTextField.getText());
                sendMessageTextField.setText("");
            }
        });
    }

    /**
     * Adds key bindings for this panel.
     */
    private void addKeyBindings() {

        sendButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "SendMessage");
        sendButton.getActionMap().put("SendMessage", new AbstractAction() {
            private static final long serialVersionUID = 653149778238948695L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                sendButton.doClick();
            }
        });

        sendButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "GoHistoryUp");
        sendButton.getActionMap().put("GoHistoryUp", new AbstractAction() {
            private static final long serialVersionUID = 653149778238948695L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (currentIndexFromHistory < messageHistory.size()) {
                    currentIndexFromHistory++;
                    sendMessageTextField.setText(messageHistory.get(messageHistory.size() - currentIndexFromHistory));
                }
            }
        });

        sendButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "GoHistoryDown");
        sendButton.getActionMap().put("GoHistoryDown", new AbstractAction() {
            private static final long serialVersionUID = 653149778238948695L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (currentIndexFromHistory > 1) {
                    currentIndexFromHistory--;
                    sendMessageTextField.setText(messageHistory.get(messageHistory.size() - currentIndexFromHistory));
                } else if (currentIndexFromHistory == 1) {
                    currentIndexFromHistory--;
                    sendMessageTextField.setText("");
                }
            }
        });
    }

    /**
     * Connects this chat panel to a given chat handler.
     */
    private void connectToChat() {

        chatHandler = NonoWebConnectionManager.getInstance().getChatHandler();
        chatHandler.receiveMessageBy(new MessageListener<String>() {

            @Override
            public void onMessage(final Message<String> message) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String tmp = chatHandler.resolveChatName(message.getPublishingMember().toString());
                        tmp += ": " + message.getMessageObject() + "\n";
                        receivedMessagesTextArea.append(tmp);
                    }
                });
            }
        });
    }

    /**
     * Closes the connection to the chat channel and removes all listeners so that this object can
     * be destroyed.
     */
    public final void closeChatConnection() {

        chatHandler.closeChat();
    }
}
