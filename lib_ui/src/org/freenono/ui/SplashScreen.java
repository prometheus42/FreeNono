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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

/**
 * Splash screen to display while loading.
 * @author Christian Wichmann
 */
public class SplashScreen extends JDialog {

    private static final long serialVersionUID = -3675401004092662517L;

    private static final int TIMER_DELAY = 3000;
    private Integer timerDelay = TIMER_DELAY;

    private final Timer timer = new Timer();

    private static final String DEFAULT_RESSOURCE = "/resources/icon/splashscreen.png";
    private String ressource = DEFAULT_RESSOURCE;

    private BufferedImage image = null;

    /**
     * SplashScreen constructor.
     * @param timerDelay
     *            Delay until splash screen is closed
     */
    public SplashScreen(final int timerDelay) {
        super();

        this.setTimerDelay(timerDelay);
        loadImage();
        initialize();
        if (timerDelay != 0) {
            setupTimer();
        }
    }

    /**
     * SplashScreen constructor.
     * @param ressource
     *            Not sure...
     */
    public SplashScreen(final String ressource) {
        super();

        this.ressource = ressource;
        loadImage();
        initialize();
        setupTimer();
    }

    /**
     * Load the image specified by ressource.
     */
    private void loadImage() {
        try {
            image = ImageIO.read(getClass().getResource(ressource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup timer that closes the splash screen.
     */
    private void setupTimer() {
        timer.schedule(new TimerTask() {
            public void run() {
                close();
            }
        }, TIMER_DELAY);
    }

    /**
     * timerDelay getter.
     * @return timerDelay
     */
    public final Integer getTimerDelay() {
        return this.timerDelay;
    }

    /**
     * timerDelay setter.
     * @param timerDelay
     *            Time in seconds to wait before closing screen.
     */
    public final void setTimerDelay(final Integer timerDelay) {
        this.timerDelay = timerDelay;
    }

    /**
     * Initialize splash screen.
     */
    private void initialize() {
        this.setSize(image.getWidth(), image.getHeight());
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setTitle("");
        this.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(final java.awt.event.KeyEvent e) {
                close();
            }

            @Override
            public void keyTyped(final java.awt.event.KeyEvent e) {
                close();
            }

            @Override
            public void keyReleased(final java.awt.event.KeyEvent e) {
                close();
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(final java.awt.event.MouseEvent e) {
                close();
            }

            @Override
            public void mousePressed(final java.awt.event.MouseEvent e) {
                close();
            }

            @Override
            public void mouseReleased(final java.awt.event.MouseEvent e) {
                close();
            }
        });
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);
    }

    /**
     * Method to close the dialog.
     */
    private void close() {
        setVisible(false);
        dispose();
    }

    @Override
    /**
     * Paint the splashscreen by first calling super and then draw the image.
     */
    public final void paint(final Graphics g) {
        super.paintComponents(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }

}
