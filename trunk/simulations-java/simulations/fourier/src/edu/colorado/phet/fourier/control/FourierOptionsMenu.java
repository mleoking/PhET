/* Copyright 2005, University of Colorado */

package edu.colorado.phet.fourier.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.fourier.FourierApplication;
import edu.colorado.phet.fourier.FourierResources;


/**
 * OptionsMenu implements the Options menu that appears in the menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierOptionsMenu extends OptionsMenu {
    
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
    public FourierOptionsMenu( FourierApplication application ) {
        super();
        
        _application = application;
        
        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( FourierResources.getString( "Menubar.harmonicColors" ) );
        backgroundColorMenuItem.setMnemonic( FourierResources.getChar( "Menubar.harmonicColors.mnemonic", 'H' ) );
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
        dialog.setVisible( true );
    }
}
