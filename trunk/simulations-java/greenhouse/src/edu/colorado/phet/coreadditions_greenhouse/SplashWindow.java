/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions_greenhouse;

import edu.colorado.phet.common_greenhouse.view.util.SimStrings;
import edu.colorado.phet.common_greenhouse.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

/**
 * SplashWindow
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SplashWindow extends JDialog {
    private JProgressBar progressBar;

    public SplashWindow( Frame owner, String title ) throws HeadlessException {
        super( owner );

        setUndecorated( true );

        // Put a border around the window.
        getRootPane().setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );

        // Splash message
        String labelFormat = SimStrings.get( "StartupDialog.message" );
        Object[] args = {title};
        String labelString = MessageFormat.format( labelFormat, args );
        JLabel label = new JLabel( labelString );

        // Indeterminate progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate( true );

        // Phet logo
        BufferedImage image = null;
        image = ImageLoader.fetchBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" );
        ImageIcon logo = new ImageIcon( image );

        // Layout
        getContentPane().setLayout( new GridBagLayout() );
        final GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                               GridBagConstraints.CENTER,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 20, 0, 10 ), 0, 0 );
        gbc.gridheight = 2;
        getContentPane().add( new JLabel( logo ), gbc );
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets( 20, 10, 10, 20 );
        getContentPane().add( label, gbc );
        gbc.gridy++;
        gbc.insets = new Insets( 10, 10, 20, 20 );
        getContentPane().add( progressBar, gbc );
        pack();

        // Center the window on the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( (int)( screenSize.getWidth() / 2 - getWidth() / 2 ),
                     (int)( screenSize.getHeight() / 2 - getHeight() / 2 ) );
    }

    public void setIndeterminate( boolean indeterminate ) {
        progressBar.setIndeterminate( indeterminate );
    }

    public void setRangeProperties( int value, int extent, int min, int max ) {
        progressBar.getModel().setRangeProperties( value, extent, min, max, true );
    }
}