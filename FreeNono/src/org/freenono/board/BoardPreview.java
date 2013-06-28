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
package org.freenono.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.Nonogram;

/**
 * Builds a preview image of the running game represented by the Game object. At
 * changes on the board, the image is rebuild by calling the refreshPreview()
 * method. This class is Cloneable, so that it can be used in more than one gui
 * component at a time.
 * 
 * (More information on scaling of images:
 * http://today.java.net/pub/a/today/2007
 * /04/03/perils-of-image-getscaledinstance.html)
 * 
 */
public class BoardPreview extends JComponent implements Cloneable {

    private static final long serialVersionUID = -7154680728413126386L;

    private static Logger logger = Logger.getLogger(BoardPreview.class);
    
    private Nonogram pattern;
    private GameEventHelper eventHelper;

    private int boardWidth;
    private int boardHeight;

    private static final int PREVIEW_WIDTH = 75;
    private static final int PREVIEW_HEIGHT = 75;

    private double newWidth;
    private double newHeight;

    private double offsetWidth;
    private double offsetHeight;

    private static final int COLOR_DARK = 78;
    private static final int COLOR_UNDEF = 202;
    private static final int COLOR_LIGHT = 230;

    private byte[] pixelsAsByte = null;
    private BufferedImage previewImage = null;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void stateChanged(final StateChangeEvent e) {
            switch (e.getNewState()) {
            case gameOver:
                // solveNonogram();
                refreshPreview();
                break;
            case solved:
                solveNonogram();
                refreshPreview();
                break;
            default:
                break;
            }
        }

        @Override
        public void fieldOccupied(final FieldControlEvent e) {

            pixelsAsByte[(e.getFieldRow() * boardWidth) + e.getFieldColumn()] = (byte) COLOR_DARK;

            refreshPreview();
        }

        @Override
        public void fieldUnoccupied(final FieldControlEvent e) {

            pixelsAsByte[(e.getFieldRow() * boardWidth) + e.getFieldColumn()] = (byte) COLOR_LIGHT;

            refreshPreview();
        }
    };

    /**
     * Default constructor that stores the nonogram locally and initializes the
     * preview image.
     * @param pattern
     *            Nonogram to preview
     */
    public BoardPreview(final Nonogram pattern) {

        this.pattern = pattern;
        this.boardWidth = pattern.width();
        this.boardHeight = pattern.height();

        pixelsAsByte = new byte[boardWidth * boardHeight];
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                pixelsAsByte[(y * boardWidth) + x] = (byte) COLOR_LIGHT;
            }
        }

        // Border border = new BevelBorder(BevelBorder.RAISED);
        // this.setBorder(border);

        refreshPreview();
    }

    /**
     * Refresh the image of the preview.
     */
    public final void refreshPreview() {

        renderImage();

        calculateBorders();

        repaint();
    }

    /**
     * Fill the preview so the solved nonogram is visible.
     */
    public final void solveNonogram() {

        byte pixelColor = (byte) COLOR_LIGHT;

        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {

                pixelColor = (byte) COLOR_LIGHT;
                if (pattern.getFieldValue(x, y)) {
                    pixelColor = COLOR_DARK;
                }
                pixelsAsByte[(y * boardWidth) + x] = pixelColor;
            }
        }

    }

    /**
     * Render the internally used byte array to BufferedImage to be shown.
     */
    private void renderImage() {

        // get image object and fill it with the stored pixel values
        BufferedImage image = new BufferedImage(boardWidth, boardHeight,
                BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        raster.setDataElements(0, 0, boardWidth, boardHeight, pixelsAsByte);
        this.previewImage = image;

    }

    /**
     * This method calculates possible borders in the preview image, if the
     * nonogram does not have equal height and width.
     */
    private void calculateBorders() {

        if (boardWidth < boardHeight) {
            newHeight = PREVIEW_HEIGHT;
            newWidth = boardWidth * newHeight / boardHeight;
            offsetWidth = (newHeight - newWidth) / 2;
            offsetHeight = 0;
        } else {
            newWidth = PREVIEW_WIDTH;
            newHeight = boardHeight * newWidth / boardWidth;
            offsetWidth = 0;
            offsetHeight = (newWidth - newHeight) / 2;
        }
    }

    /**
     * Paint the compoponent (image) on the given Graphics object. Is iternally
     * called by swing.
     * @param g
     *            Graphics object to paint to.
     */
    @Override
    public final void paintComponent(final Graphics g) {

        super.paintComponent(g);

        g.drawImage(previewImage, (int) offsetWidth, (int) offsetHeight,
                (int) newWidth, (int) newHeight, null);
    }

    /**
     * Return preferred size of this component.
     * @return Preferred size
     */
    @Override
    public final Dimension getPreferredSize() {

        return new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    }

    /**
     * Get minimum size of component.
     * @return Minimum size
     */
    @Override
    public final Dimension getMinimumSize() {

        return new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    }

    /**
     * Get maximum size of component.
     * @return Maximum size
     */
    @Override
    public final Dimension getMaximumSize() {

        return new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    }

    /**
     * Set event helper.
     * @param eventHelper
     *            Event helper
     */
    public final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    /**
     * Remove event helper.
     */
    public final void removeEventHelper() {

        eventHelper.removeGameListener(gameAdapter);
        this.eventHelper = null;
    }

    /**
     * Clone this preview to a new object.
     * @return Cloned BoardPreview
     */
    public final BoardPreview clone() {
        
        Object theClone = null;
        
        try {
            theClone = super.clone();
            
        } catch (CloneNotSupportedException e) {
        
            logger.debug("Board preview could not be cloned.");
        }
        return (BoardPreview) theClone;
    }

    /**
     * Calculates a image object from current preview of the nonogram with 75x75
     * pixels.
     * 
     * @return buffered image object with current preview
     */
    public final BufferedImage getPreviewImage() {

        BufferedImage bufferedPreview = new BufferedImage(PREVIEW_WIDTH,
                PREVIEW_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = bufferedPreview.getGraphics();

        Color undefColor = new Color(COLOR_UNDEF, COLOR_UNDEF, COLOR_UNDEF);
        g.setColor(undefColor);

        g.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        g.drawImage(previewImage, (int) offsetWidth, (int) offsetHeight,
                (int) newWidth, (int) newHeight, null);

        return bufferedPreview;
    }
}
