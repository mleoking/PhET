/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;


/**
 * PhetStartupWindow is an undecorated window that shows progress
 * while an application is starting up. It displays the PhET logo,
 * a message that you supply, and a progress bar.
 * You can control the percentage complete displayed
 * by the progress bar, or you can request that the progress 
 * bar display a continuous animation.
 * <p>
 * Example use:
 * <code>
 * public static void main(...) {
 *    PhetStartupWindow startupWindow = new PhetStartupWindow( "starting simulation..." );
 *    startupWindow.setIndeterminate( true );
 *    startupWindow.setVisible( true );
 *    // initialize Modules and other stuff here
 *    PhetApplication app = new PhetApplication(...);
 *    app.startApplication(...)
 *    startupWindow.setVisible( false );
 * }
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhetStartupWindow extends JWindow {

    /** This value indicates 0% completed. */
    public static int MIN = 0;
    /** This value indicates 100% completed. */
    public static int MAX = 100;
    
    /* The progress bar will be at least this wide. */
    private static int MIN_PROGRESS_BAR_WIDTH = 150;
    
    private JProgressBar _progressBar;
    
    /**
     * Sole constructor.
     * 
     * @param message the message to be displayed in the window
     */
    public PhetStartupWindow( String message ) {
        super();
        
        // PhET logo
        JLabel logoLabel = new JLabel();
        Image logoImage;
        try {
            logoImage = new ImageLoader().loadImage( "images/Phet-Flatirons-logo-3-small.gif" );
            ImageIcon logoIcon = new ImageIcon( logoImage );
            logoLabel.setIcon( logoIcon );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        // The text message
        JLabel messageLabel = new JLabel( message );
        
        // Progress bar, sized to the text message.
        _progressBar = new JProgressBar( MIN, MAX );
        int width = Math.max( MIN_PROGRESS_BAR_WIDTH, messageLabel.getPreferredSize().width );
        int height = _progressBar.getPreferredSize().height;
        _progressBar.setPreferredSize( new Dimension( width, height ) );
        
        // Layout in a panel
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        panel.setLayout( layout );
        layout.addComponent( logoLabel, 0, 0, 1, 2 );
        layout.addComponent( messageLabel, 0, 1 );
        layout.addComponent( _progressBar, 1, 1 );

        // Add the panel to the window
        getContentPane().add( panel );
        
        // Size the window to fit the panel
        setSize( panel.getPreferredSize() );
        
        // Center the window on the screen.
        {
            // Get the size of the screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

            // Determine the new location of the window
            int w = getSize().width;
            int h = getSize().height;
            int x = ( dim.width - w ) / 2;
            int y = ( dim.height - h ) / 2;

            // Move the window
            setLocation( x, y );
        }
    }
    
    /**
     * Sets the indeterminate property of this window's progress bar.
     * An indeterminate progress bar continuously displays animation
     * indicating that an operation of unknown length is occurring.
     * By default, this property is false.  Some look and feels might
     * not support indeterminate progress bars; they will ignore this
     * property.
     * 
     * @param indeterminate true or false
     */
    public void setIndeterminate( boolean indeterminate ) {
        _progressBar.setIndeterminate( indeterminate );
    }
    
    /**
     * Sets the "percent completed" displayed by the progress bar.
     * The progress bar has a minimum range of MIN, and 
     * a maximum range of MAX.  The value should be set
     * to indicate the percent completed.
     * <p>
     * Note that this call has no effect if the progress bar is
     * set to be indeterminate.
     * 
     * @param percentCompleted
     * @throws IllegalArgumentException if value is not in the range MIN-MAX
     */
    public void setPercentCompleted( int percentCompleted ) {
        if ( percentCompleted < MIN || percentCompleted > MAX ) {
            throw new IllegalArgumentException( "percentCompleted is out of range: " + percentCompleted );
        }
        _progressBar.setValue( percentCompleted );
    }
    
    /**
     * Returns a reference to the window's JProgressBar.
     * Use this with discretion for changing properties of the 
     * progress bar that are not exposed by the interface to
     * ProgressWindow.
     * 
     * @return
     */
    public JProgressBar getProgressBar() {
        return _progressBar;
    }
}
