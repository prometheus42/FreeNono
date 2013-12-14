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
package org.freenono.ui.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * Generates an image like those produced by the interaggregate screensaver.
 * 
 * Source: http://www.complexification.net/gallery/machines/interAggregate/
 * 
 * @author Christian Wichmann
 */
public class InteraggregatePainter {

    /*
     * Original copyright information:
     * 
     * Intersection Aggregate, {Software} Structures
     * 
     * j.tarbell May, 2004
     * 
     * Albuquerque, New Mexico complexification.net
     * 
     * commissioned by the Whitney ArtPort
     * 
     * collaboration with Casey Reas, Robert Hodgin, William Ngan
     * 
     * Processing 0085 Beta syntax update
     * 
     * j.tarbell April, 2005 Albuquerque, New Mexico
     */

    private static Logger logger = Logger
            .getLogger(InteraggregatePainter.class);

    // dimensions
    private int dim = 500;
    private int num = 100;

    private Disc[] discs;

    private int maxpal = 512;
    private int numpal = 0;
    private Color[] goodcolor = new Color[maxpal];

    private Random random;
    private Image image;
    private Graphics2D graphics;

    /**
     * Instantiates a new painter.
     */
    public InteraggregatePainter() {

        this.random = new Random();

        resetImage();

        setupPainter();
    }

    /**
     * Resets to an empty image.
     */
    public final void resetImage() {

        this.image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);

        // generate Graphics object to paint into image
        Graphics g = image.getGraphics();
        graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        // g2.setComposite(AlphaComposite.getInstance(
        // AlphaComposite.SRC_OVER, 0.5f));

        // paint background
        graphics.setColor(new Color(255, 255, 255));
        graphics.fillRect(0, 0, dim, dim);
    }

    /**
     * Calculate and paint a number of iterations changing the current image.
     * 
     * @param iterations
     *            number of iterations to be calculated
     */
    public final void doIterations(final int iterations) {

        for (int i = 0; i < iterations; i++) {
            draw();
        }
    }

    /**
     * Saves current image to png file.
     * 
     * @param filename
     *            string containing filename with path
     */
    public final void saveToFile(final String filename) {

        try {
            ImageIO.write((RenderedImage) image, "png", new File(filename));
        } catch (IOException e) {
            logger.warn("Could not write to image file.");
        }
    }

    /**
     * Returns a copy of the current image that has been calculated. When the
     * image is calculated further, returned copy do not change with it.
     * 
     * @return copy of the current image
     */
    public final BufferedImage getImage() {

        BufferedImage bufferedPreview = new BufferedImage(dim, dim,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedPreview.getGraphics();

        g.drawImage(image, 0, 0, dim, dim, null);

        return bufferedPreview;
    }

    /**
     * Sets up color palette and Disc data structures.
     */
    private void setupPainter() {

        setColorPalette();

        discs = new Disc[num];

        for (int i = 0; i < num; i++) {
            float x = random.nextFloat() * dim;
            float y = random.nextFloat() * dim;
            float fy = 0;
            float fx = (float) (random.nextFloat() * 2.4 - 1.2);
            float r = 5 + random.nextFloat() * 55;
            discs[i] = new Disc(i, x, y, fx, fy, r);
        }
    }

    /**
     * Draws next frame by moving discs and rendering them on the image.
     */
    private void draw() {

        // move discs
        for (int c = 0; c < num; c++) {
            discs[c].move();
            discs[c].render();
        }
    }

    /**
     * Represents a disc and paints itself onto an image.
     */
    private class Disc {

        private int id;
        private float x, y;
        private float r;
        private float dr;
        private float vx, vy;

        // sand painters
        private int numsands = 3;
        private SandPainter[] sands = new SandPainter[numsands];

        /**
         * Initializes a new Disc with its coordinates and velocity.
         * 
         * @param Id
         *            index identifier
         * @param X
         *            position x
         * @param Y
         *            position y
         * @param Vx
         *            velocity x
         * @param Vy
         *            velocity y
         * @param R
         *            destination radius
         */
        public Disc(final int Id, final float X, final float Y, final float Vx,
                final float Vy, final float R) {

            reset(Id, X, Y, Vx, Vy, R);

            // create sand painters
            for (int n = 0; n < numsands; n++) {
                sands[n] = new SandPainter();
            }

            draw();
        }

        /**
         * Resets all values.
         * 
         * @param Id
         *            index identifier
         * @param X
         *            position x
         * @param Y
         *            position y
         * @param Vx
         *            velocity x
         * @param Vy
         *            velocity y
         * @param R
         *            destination radius
         */
        void reset(int Id, float X, float Y, float Vx, float Vy, float R) {
            // construct
            id = Id;
            x = X;
            y = Y;
            vx = Vx;
            vy = Vy;
            r = 0;
            dr = R;
        }

        /**
         * Draws a ellipse. Why???
         */
        void draw() {
            graphics.setColor(new Color(0, 0, 0, 50));
            graphics.drawOval((int) (x + r / 2), (int) (y + r / 2),
                    (int) (r * 2), (int) (r * 2));
        }

        /**
         * Calculate disc and render via SandPainter to image.
         */
        void render() {

            // find intersecting points with all ascending discs
            for (int n = id + 1; n < num; n++) {
                // find distance to other disc
                float dx = discs[n].x - x;
                float dy = discs[n].y - y;
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                // intersection test
                if (d < (discs[n].r + r)) {
                    // complete containment test
                    if (d > Math.abs(discs[n].r - r)) {
                        // find solutions
                        float a = (r * r - discs[n].r * discs[n].r + d * d)
                                / (2 * d);

                        float p2x = x + a * (discs[n].x - x) / d;
                        float p2y = y + a * (discs[n].y - y) / d;

                        float h = (float) Math.sqrt(r * r - a * a);

                        float p3ax = p2x + h * (discs[n].y - y) / d;
                        float p3ay = p2y - h * (discs[n].x - x) / d;

                        float p3bx = p2x - h * (discs[n].y - y) / d;
                        float p3by = p2y + h * (discs[n].x - x) / d;

                        for (int s = 0; s < numsands; s++) {
                            sands[s].render(p3ax, p3ay, p3bx, p3by);
                        }
                    }
                }
            }
        }

        /**
         * Moves a single Disc and checks boundaries.
         */
        void move() {
            // add velocity to position
            x += vx;
            y += vy;
            // grow to destination radius
            if (r < dr) {
                r += 0.1;
            }
            // bound check
            if (x + r < 0) {
                x += dim + r + r;
            }
            if (x - r > dim) {
                x -= dim + r + r;
            }
            if (y + r < 0) {
                y += dim + r + r;
            }
            if (y - r > dim) {
                y -= dim + r + r;
            }
        }
    }

    /**
     * Stores a sand painter???
     * 
     * @author Christian Wichmann
     */
    private class SandPainter {

        private float p;
        private Color c;
        private float g;

        /**
         * Initializes a SandPainter instance.
         */
        public SandPainter() {

            p = random.nextFloat();
            c = somecolor();
            g = (float) (random.nextFloat() / 1.0 * 0.09 + 0.01);
        }

        /**
         * Draws painting sweeps.
         * 
         * @param x
         *            x-coordinate
         * @param y
         *            y-coordinate
         * @param ox
         *            offset x
         * @param oy
         *            offset y
         */
        void render(final float x, final float y, final float ox, final float oy) {

            g += random.nextFloat() / 10 - 0.050;
            float maxg = 0.22f;
            if (g < -maxg) {
                g = -maxg;
            }
            if (g > maxg) {
                g = maxg;
            }
            p += random.nextFloat() / 10 - 0.050;
            if (p < 0) {
                p = 0;
            }
            if (p > 1.0) {
                p = 1.0f;
            }

            float w = g / 10.0f;
            for (int i = 0; i < 11; i++) {
                float a = 0.1f - i / 110;
                float[] colorComponents = c.getRGBComponents(null);
                graphics.setColor(new Color(colorComponents[0],
                        colorComponents[1], colorComponents[2], a));
                graphics.fillRect(
                        (int) (ox + (x - ox) * Math.sin(p + Math.sin(i * w))),
                        (int) (oy + (y - oy) * Math.sin(p + Math.sin(i * w))),
                        1, 1);
                graphics.fillRect(
                        (int) (ox + (x - ox) * Math.sin(p - Math.sin(i * w))),
                        (int) (oy + (y - oy) * Math.sin(p - Math.sin(i * w))),
                        1, 1);
            }
        }
    }

    /*
     * COLORING ROUTINES
     */

    /**
     * Pick some random good color.
     * 
     * @return some random good color
     */
    private Color somecolor() {

        return goodcolor[random.nextInt(numpal)];
    }

    /**
     * Sets up the color palette.
     */
    private void setColorPalette() {

        /* defining colors from xscreensaver */
        goodcolor[numpal++] = new Color(255, 255, 255); /* white */
        goodcolor[numpal++] = new Color(255, 255, 255); /* more black */
        goodcolor[numpal++] = new Color(255, 255, 255); /* more black */
        goodcolor[numpal++] = Color.decode("#4e3e2e"); /* olive */
        goodcolor[numpal++] = Color.decode("#694d35"); /* camel */
        goodcolor[numpal++] = Color.decode("#b0a085"); /* tan */
        goodcolor[numpal++] = Color.decode("#e6d3ae");
    }
}
