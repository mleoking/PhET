/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.phetcommon.view.util;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;


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
        for( int i = 0; i < menuBar.getMenuCount(); i++ ) {
            if( i == index ) {
                menuList.add( newMenu );
            }
            menuList.add( menuBar.getMenu( i ) );
        }
        menuBar.removeAll();
        //        menuBar = new JMenuBar();
        for( int i = 0; i < menuList.size(); i++ ) {
            JMenu menu = (JMenu)menuList.get( i );
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
        if( !( lm instanceof GridBagLayout ) ) {
            throw new AWTException( "Invalid layout: " + lm );
        }
        else {
            GridBagConstraints gbc = getGridBagConstraints( gridX, gridY,
                                                            gridWidth, gridHeight, fill, anchor );
            gbc.insets = insets;
            ( (GridBagLayout)lm ).setConstraints( component, gbc );
            container.add( component );
        }
    }

    public static void centerWindowOnScreen( Window window ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        window.setLocation( (int)( screenSize.getWidth() / 2 - window.getWidth() / 2 ),
                            (int)( screenSize.getHeight() / 2 - window.getHeight() / 2 ) );
    }

    /**
     * Sets the bounds for a dialog so it is centered over a frame
     *
     * @param dialog
     */
    public static void centerDialogInParent( JDialog dialog ) {
        Rectangle frameBounds = dialog.getParent().getBounds();
        Rectangle dialogBounds = new Rectangle( (int)( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int)( frameBounds.getMinY() + frameBounds.getHeight() / 2 - dialog.getHeight() / 2 ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    // This method returns the selected radio button in a button group
    // Taken from The Java Developer's Almanac, 1.4
    public static JRadioButton getSelection( ButtonGroup group ) {
        for( Enumeration e = group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if( b.getModel() == group.getSelection() ) {
                return b;
            }
        }
        return null;
    }

    /**
     * Fixes button opacity issues. 
     * This is particularly a problem with JButton on Macintosh;
     * when the background color of its container is changed,
     * a button will appear to be sitting on a gray rectangle.
     * 
     * @param button
     */
    public static void fixButtonOpacity( JButton button ) {
        // If the LAF isn't Metal or a derivative, setOpaque( false ) for the button
        if( !( UIManager.getLookAndFeel() instanceof MetalLookAndFeel ) ) {
            button.setOpaque( false );
        }
    }
    
    /**
     * Forces an immediate repaint of a component and all of its children.
     * 
     * @param component
     */
    public static void paintImmediately( Component component ) {
        
        // Paint the component
        if ( component instanceof JComponent ) {
            JComponent jcomponent = (JComponent)component;
            jcomponent.paintImmediately( jcomponent.getBounds() );
        }
        
        // Recursively paint children
        if ( component instanceof Container ) {
            Container container = (Container)component;
            int numberOfChildren = container.getComponentCount();
            for ( int i = 0; i < numberOfChildren; i++ ) {
                Component child = container.getComponent( i );
                paintImmediately( child );
            }
        }
    }


    /**
     * Determine the maximum size for a 2-state button with the specified text and icons.
     * This can be used to make sure that a button doesn't resize during state change.
     *
     * @param button  the UI of this JButton is used for size determination
     * @param string1 text for 1st mode
     * @param icon1   icon for 1st mode
     * @param string2 text for 2nd mode
     * @param icon2   icon for 2nd mode
     * @return the smallest Dimension that contains both modes for the button.
     */
    public static Dimension getMaxDimension( JButton button, String string1, ImageIcon icon1, String string2, ImageIcon icon2 ) {
        // Get dimensions for "Play" state
        button.setText( string1 );
        button.setIcon( icon1 );
        Dimension playSize = button.getUI().getPreferredSize( button );

        // Get dimensions for "Pause" state
        button.setText( string2 );
        button.setIcon( icon2 );
        Dimension pauseSize = button.getUI().getPreferredSize( button );

        // Set max dimensions
        int maxWidth = (int)Math.max( playSize.getWidth(), pauseSize.getWidth() );
        int maxHeight = (int)Math.max( playSize.getHeight(), pauseSize.getHeight() );
        return new Dimension( maxWidth, maxHeight );
    }
}
