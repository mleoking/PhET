/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierApplication;


/**
 * OptionsMenu implements the Options menu that appears in the Fourier menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OptionsMenu extends JMenu {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierApplication _application;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param application
     */
    public OptionsMenu( FourierApplication application ) {
        
        super( SimStrings.get( "Menubar.options" ) );
        
        _application = application;
        
        setMnemonic( SimStrings.get( "Menubar.options.mnemonic" ).charAt( 0 ) );

        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( SimStrings.get( "Menubar.harmonicColors" ) );
        backgroundColorMenuItem.setMnemonic( SimStrings.get( "Menubar.harmonicColors.mnemonic" ).charAt( 0 ) );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleHarmonicColorsMenuItem();
            }
        } );
        add( backgroundColorMenuItem );
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Handles the "Harmonic Colors" menu item.
     * Opens a dialog that contains controls for setting harmonic colors.
     */
    public void handleHarmonicColorsMenuItem() {
        HarmonicColorsDialog dialog = new HarmonicColorsDialog( _application );
        dialog.show();
    }
}
