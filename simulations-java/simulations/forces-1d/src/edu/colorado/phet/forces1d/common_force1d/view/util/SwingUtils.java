/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.forces1d.common_force1d.view.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.*;


/**
 * SwingUtils is a collection of utilities related to Java Swing.
 *
 * @author various
 * @version $Revision$
 */
public class SwingUtils {

    /**
     * Not intended for instantiation.
     */
    private SwingUtils() {
    }

    public static void addMenuAt( JFrame frame, JMenu newMenu, int index ) {
        frame.setJMenuBar( addMenuAt( newMenu, frame.getJMenuBar(), index ) );
    }

    /**
     * @param newMenu
     * @param menuBar
     * @param index
     * @return The same JMenuBar, for cascading.
     *         todo See if the same thing can be done with Container.add( component, index )
     */
    public static JMenuBar addMenuAt( JMenu newMenu, JMenuBar menuBar, int index ) {

        ArrayList menuList = new ArrayList();
        for ( int i = 0; i < menuBar.getMenuCount(); i++ ) {
            if ( i == index ) {
                menuList.add( newMenu );
            }
            menuList.add( menuBar.getMenu( i ) );
        }
        menuBar.removeAll();
        //        menuBar = new JMenuBar();
        for ( int i = 0; i < menuList.size(); i++ ) {
            JMenu menu = (JMenu) menuList.get( i );
            menuBar.add( menu );
        }
        return menuBar;
    }

    public static GridBagConstraints getGridBagConstraints( int gridX, int gridY,
                                                            int gridWidth, int gridHeight,
                                                            int fill, int anchor ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.fill = fill;
        gbc.anchor = anchor;

        return gbc;
    }

    /**
     * Add a component to a container with a GridBagLayout, creating
     * GridBagConstraints for it.
     *
     * @param container
     * @param component
     * @param gridX
     * @param gridY
     * @param gridWidth
     * @param gridHeight
     * @param fill
     * @param anchor
     * @throws java.awt.AWTException
     */
    public static void addGridBagComponent( Container container,
                                            Component component,
                                            int gridX, int gridY,
                                            int gridWidth, int gridHeight,
                                            int fill, int anchor )

            throws AWTException {
        addGridBagComponent( container, component, gridX, gridY,
                             gridWidth, gridHeight, fill, anchor, new Insets( 0, 0, 0, 0 ) );
    }

    public static void addGridBagComponent( Container container,
                                            Component component,
                                            int gridX, int gridY,
                                            int gridWidth, int gridHeight,
                                            int fill, int anchor, Insets insets )
            throws AWTException {
        LayoutManager lm = container.getLayout();
        if ( !( lm instanceof GridBagLayout ) ) {
            throw new AWTException( "Invalid layout: " + lm );
        }
        else {
            GridBagConstraints gbc = getGridBagConstraints( gridX, gridY,
                                                            gridWidth, gridHeight, fill, anchor );
            gbc.insets = insets;
            ( (GridBagLayout) lm ).setConstraints( component, gbc );
            container.add( component );
        }
    }

    public static void centerWindowOnScreen( Window window ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        window.setLocation( (int) ( screenSize.getWidth() / 2 - window.getWidth() / 2 ),
                            (int) ( screenSize.getHeight() / 2 - window.getHeight() / 2 ) );
    }

    /**
     * Sets the bounds for a dialog so it is centered over a frame
     *
     * @param dialog
     */
    public static void centerDialogInParent( JDialog dialog ) {
        Rectangle frameBounds = dialog.getParent().getBounds();
        Rectangle dialogBounds = new Rectangle( (int) ( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int) ( frameBounds.getMinY() + frameBounds.getHeight() / 2 - dialog.getHeight() / 2 ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    // This method returns the selected radio button in a button group
    // Taken from The Java Developer's Almanac, 1.4
    public static JRadioButton getSelection( ButtonGroup group ) {
        for ( Enumeration e = group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if ( b.getModel() == group.getSelection() ) {
                return b;
            }
        }
        return null;
    }
}
