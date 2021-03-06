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
package org.freenono.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;
import org.freenono.controller.Manager;
import org.freenono.provider.CollectionFromFilesystem;
import org.freenono.provider.CollectionFromSeed;
import org.freenono.provider.CollectionFromServer;
import org.freenono.provider.CollectionProvider;
import org.freenono.provider.CourseProvider;
import org.freenono.provider.NonogramProvider;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;
import org.freenono.ui.YesNoDialog;
import org.freenono.ui.colormodel.ColorModel;

/**
 * Shows a dialog for the user to administer nonogram collections and choose a nonogram to play.
 * (Replacing NonogramChooser class.)
 *
 * @author Christian Wichmann
 */
public class NonogramExplorer extends JPanel {

    private static final long serialVersionUID = 4250625963548539930L;

    private static Logger logger = Logger.getLogger(NonogramExplorer.class);

    private JPanel tabPane;
    private JPanel courseViewPane;
    private int yCoordinateForTabButtons = 0;
    private boolean updatedForFirstTime = true;

    private final List<CollectionProvider> nonogramProvider;
    private final List<CourseProvider> coursesAlreadyAdded;
    private final List<CourseTabButton> tabList;
    private ColorModel colorModel;

    /**
     * Color for nonograms with difficulty 'easiest'.
     */
    public static final Color EASIEST_COLOR = new Color(122, 255, 123);

    /**
     * Color for nonograms with difficulty 'easy'.
     */
    public static final Color EASY_COLOR = new Color(123, 152, 255);

    /**
     * Color for nonograms with difficulty 'normal'.
     */
    public static final Color NORMAL_COLOR = new Color(255, 246, 117);

    /**
     * Color for nonograms with difficulty 'hard'.
     */
    public static final Color HARD_COLOR = new Color(255, 187, 113);

    /**
     * Color for nonograms with difficulty 'hardest'.
     */
    public static final Color HARDEST_COLOR = new Color(255, 113, 113);

    /**
     * Color for nonograms with difficulty 'undefined'.
     */
    public static final Color UNDEFINED_COLOR = new Color(128, 128, 128);

    private JScrollPane scrollPane;

    private final ImageIcon collectionFromFilesystemIcon;
    private final ImageIcon collectionFromServerIcon;
    private final ImageIcon collectionFromSeedIcon;

    /**
     * Initializes a new NonogramExplorer.
     *
     * @param nonogramProvider
     *            list of collections containing nonogram courses
     * @param colorModel
     *            color model given by settings
     */
    public NonogramExplorer(final List<CollectionProvider> nonogramProvider, final ColorModel colorModel) {

        this.nonogramProvider = nonogramProvider;
        this.colorModel = colorModel;

        coursesAlreadyAdded = new ArrayList<CourseProvider>();
        tabList = new ArrayList<CourseTabButton>();

        collectionFromFilesystemIcon = new ImageIcon(getClass().getResource("/resources/icon/CollectionFromFilesystem.png"));
        collectionFromServerIcon = new ImageIcon(getClass().getResource("/resources/icon/CollectionFromServer.png"));
        collectionFromSeedIcon = new ImageIcon(getClass().getResource("/resources/icon/CollectionFromSeed.png"));

        initialize();

        addListeners();
    }

    // @Override
    // protected final void paintComponent(final Graphics g) {
    //
    // final Graphics2D g2 = (Graphics2D) g;
    // BufferedImage cache = null;
    // if (cache == null || cache.getHeight() != getHeight()) {
    // cache = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
    // final Graphics2D g2d = cache.createGraphics();
    //
    // // TODO Check which color should be used here.
    // final GradientPaint paint = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(),
    // colorModel.getTopColor());
    // g2d.setPaint(paint);
    // g2d.fillRect(0, 0, 2, getHeight());
    // g2d.dispose();
    // }
    // g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
    // }

    /**
     * Initializes a dialog to chose and administer collections and courses.
     */
    private void initialize() {

        setBackground(colorModel.getTopColor());
        setForeground(colorModel.getBottomColor());
        setBorder(BorderFactory.createEtchedBorder());

        // set layout manager
        final GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        final GridBagConstraints c = new GridBagConstraints();
        final int inset = 10;
        c.insets = new Insets(inset, inset, inset, inset);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.VERTICAL;
        add(buildTabbedPane(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(buildButtonPanel(), c);

        // set course view pane to empty panel
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.BOTH;
        courseViewPane = new JPanel();
        courseViewPane.setLayout(new GridBagLayout());
        courseViewPane.add(new JLabel(Messages.getString("NonogramChooserUI.ClickLeft")));
        courseViewPane.setOpaque(false);
        add(courseViewPane, c);
    }

    /**
     * Adds listener for mouse wheel events.
     */
    private void addListeners() {

        tabPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {

                final int dir = e.getWheelRotation();
                final int scrollWidth = CourseTabButton.TAB_HEIGHT_DEFAULT;

                final Point p = scrollPane.getViewport().getViewPosition();

                p.y = dir < 0 ? p.y - scrollWidth : p.y + scrollWidth;
                p.y = p.y < 0 ? 0 : p.y;

                scrollPane.getViewport().setViewPosition(p);
            }
        });

        /**
         * Action class for context menu in tabbed pane.
         *
         * Source: http://www.jroller.com/pago/entry/ improving_jtabbedpanes_mouse_support_like
         */
        // class SelectTabAction extends AbstractAction {
        //
        // private static final long serialVersionUID = -3652726963206234089L;
        //
        // private JTabbedPane tabPane;
        // private int index;
        //
        // /**
        // * Default constructor.
        // *
        // * @param tabPane
        // * tabbed pane on which context menu is shown
        // * @param index
        // * index of this action in context menu
        // */
        // public SelectTabAction(final JTabbedPane tabPane, final int index) {
        // super(tabPane.getTitleAt(index), tabPane.getIconAt(index));
        //
        // this.tabPane = tabPane;
        // this.index = index;
        // }
        //
        // @Override
        // public void actionPerformed(final ActionEvent e) {
        // tabPane.setSelectedIndex(index);
        // }
        // }
        //
        // tabPane.addMouseListener(new MouseAdapter() {
        //
        // public void mouseClicked(final MouseEvent e) {
        // // we only look at the right button
        // if (SwingUtilities.isRightMouseButton(e)) {
        // JTabbedPane tabPane = (JTabbedPane) e.getSource();
        // JPopupMenu menu = new JPopupMenu();
        //
        // int tabCount = tabPane.getTabCount();
        // for (int i = 0; i < tabCount; i++) {
        // menu.add(new SelectTabAction(tabPane, i));
        // }
        //
        // menu.show(tabPane, e.getX(), e.getY());
        // }
        // }
        // });

        /*
         * Add listener to react to clicks on the course buttons and show nonograms from the
         * selected course.
         */
        CourseTabButton.addCourseTabListener(new CourseTabListener() {

            @Override
            public void courseTabChanged() {

                buildCoursePane(CourseTabButton.getSelected());
            }
        });
    }

    /**
     * Builds a tab pane that will display the course views later.
     *
     * @return tab pane
     */
    private JScrollPane buildTabbedPane() {

        /*
         * TODO Write separate CourseTabButtonPane class to hold listener methods and to make course
         * tabs reusable.
         */
        logger.debug("Building tab panel for courses...");

        final int border = 10;

        tabPane = new JPanel();
        tabPane.setLayout(new GridBagLayout());
        tabPane.setOpaque(false);

        for (final CollectionProvider collection : nonogramProvider) {
            addCollectionTab(collection);
        }

        scrollPane =
                new JScrollPane(tabPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(300, 500));
        scrollPane.setMinimumSize(new Dimension(300, 500));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    /**
     * Adds tabs for all courses in a collection.
     *
     * @param collection
     *            collection to be added
     */
    private void addCollectionTab(final CollectionProvider collection) {

        final int inset = 0;

        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(inset, inset, inset, inset);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 0.1;

        // get image dependent on the collection type
        final ImageIcon icon;
        if (collection instanceof CollectionFromFilesystem) {
            icon = collectionFromFilesystemIcon;
        } else if (collection instanceof CollectionFromServer) {
            icon = collectionFromServerIcon;
        } else if (collection instanceof CollectionFromSeed) {
            icon = collectionFromSeedIcon;
        } else {
            icon = null;
        }

        // add tabs for all courses that were not already added...
        for (final CourseProvider course : collection.getCourseProvider()) {
            boolean skipCourse = false;
            for (final CourseProvider tempCourse : coursesAlreadyAdded) {
                if (tempCourse.getCourseName().equals(course.getCourseName())) {
                    skipCourse = true;
                    break;
                }
            }

            if (!skipCourse) {
                final CourseTabButton newTab = new CourseTabButton(course, icon);
                c.gridy = yCoordinateForTabButtons++;
                tabPane.add(newTab, c);
                tabList.add(newTab);
                coursesAlreadyAdded.add(course);
            }
        }
    }

    /**
     * Builds a pane containing the course view for a specific given course.
     *
     * @param course
     *            course to be shown in pane
     */
    private void buildCoursePane(final CourseProvider course) {

        // remove old course view pane...
        remove(courseViewPane);
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.BOTH;
        // ...and add new one to nonogram explorer
        courseViewPane = new CourseViewPane(course, colorModel, calculateButtonColumns());
        add(courseViewPane, c);
        validate();
    }

    /**
     * Calculates how many columns of nonogram buttons can be display in the window.
     *
     * @return number of columns that should be shown in course view pane
     */
    private int calculateButtonColumns() {

        // TODO call this method when resizing and use it for resizing course view pane
        final int windowWidth = (int) getTopLevelAncestor().getSize().getWidth();

        return (windowWidth - 300) / 100;
    }

    /**
     * Returns a panel containing all buttons on the lower margin of this dialog.
     *
     * @return panel with all buttons
     */
    private JPanel buildButtonPanel() {

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setOpaque(false);

        final JButton maintenanceButton = new JButton(new ImageIcon(getClass().getResource("/resources/icon/CollectionMaintenance.png")));
        maintenanceButton.setEnabled(false);
        maintenanceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                new MaintenanceDialog((JFrame) getTopLevelAncestor(), colorModel.getTopColor(), colorModel.getBottomColor(),
                        nonogramProvider);
            }
        });
        buttonPanel.add(maintenanceButton, BorderLayout.WEST);

        /*
         * Create new panel with FlowLayout to display both buttons (reset and close buttons) on the
         * right side of this dialog. The new panel is then added to the button panel.
         */
        final JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        rightButtonPanel.setOpaque(false);
        final JButton resetPreviewsButton = new JButton(Messages.getString("NonogramChooserUI.ResetPreviewButton"));
        resetPreviewsButton.setToolTipText(Messages.getString("NonogramChooserUI.ResetPreviewTooltip"));
        resetPreviewsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {

                /*
                 * For resetting the preview images all files in a given path are deleted. Thumbnail
                 * path is defined by the Manager class. If directories are present at this path,
                 * nothing will be done with them.
                 */

                final YesNoDialog askResetDialog =
                        new YesNoDialog((JFrame) getTopLevelAncestor(), Messages.getString("NonogramChooserUI.ResetPreviewDialogTitle"),
                                colorModel.getTopColor(), colorModel.getBottomColor(), Messages
                                        .getString("NonogramChooserUI.ResetPreviewDialogQuestion"));
                ((MainUI) getTopLevelAncestor()).centerWindowOnMainScreen(askResetDialog, 0, 0);
                askResetDialog.setVisible(true);

                if (askResetDialog.userChoseYes()) {
                    // delete all thumbnails in designated path
                    // TODO Should this code be in Manager or some tool class?
                    final File thumbDir = new File(Manager.DEFAULT_THUMBNAILS_PATH);
                    if (thumbDir.exists() && thumbDir.isDirectory()) {
                        final File[] listOfThumbnails = thumbDir.listFiles();
                        if (listOfThumbnails != null) {
                            for (final File child : listOfThumbnails) {
                                if (child.isFile()) {
                                    child.delete();
                                }
                            }
                        }
                    }
                }

                updateCourseData();
            }
        });
        resetPreviewsButton.setActionCommand("ResetPreview");
        rightButtonPanel.add(resetPreviewsButton);

        final JButton cancelButton = new JButton(Messages.getString("Cancel"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                ((MainUI) getTopLevelAncestor()).finishStartThroughCancel();
            }
        });
        rightButtonPanel.add(cancelButton);
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

        final int borderWidth = 15;
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, borderWidth, borderWidth, borderWidth));

        return buttonPanel;
    }

    /**
     * Updates course data in all components including tab buttons and course view pane.
     */
    public final void updateCourseData() {

        for (final CourseTabButton courseButton : tabList) {
            courseButton.updateCourseData();
        }
        /*
         * Do not build course view pane when calling this method the first time ever. At the first
         * showing of this nonogram explorer the right panel contains an information about what to
         * do (e.g. click on the course on the left side).
         */
        final CourseProvider lastSelectedCourse = CourseTabButton.getSelected();
        if (!updatedForFirstTime && lastSelectedCourse != null) {
            buildCoursePane(lastSelectedCourse);
        } else {
            updatedForFirstTime = false;
        }
    }

    /**
     * Updates the color model for this nonogram explorer instance.
     *
     * @param colorModel
     *            color model to be set
     */
    public final void updateColorModel(final ColorModel colorModel) {

        this.colorModel = colorModel;
        setBackground(colorModel.getTopColor());
        setForeground(colorModel.getBottomColor());
        courseViewPane.setBackground(colorModel.getTopColor());
        courseViewPane.setForeground(colorModel.getBottomColor());
    }

    /**
     * Returns chosen nonogram pattern.
     *
     * @return chosen nonogram pattern
     */
    public final NonogramProvider getChosenNonogram() {

        logger.debug("Get chosen nonogram from nonogram explorer.");

        if (courseViewPane instanceof CourseViewPane) {
            return ((CourseViewPane) courseViewPane).getChosenNonogram();
        } else {
            return null;
        }
    }
}
