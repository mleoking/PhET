/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.dialog;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JDialog;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.dialog.ColorChooserFactory.Listener;
import edu.colorado.phet.faraday.module.ICompassGridModule;


/**
 * BackgroundColorDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BackgroundColorDialog implements ColorChooserFactory.Listener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetApplication _app;
    private JDialog _dialog;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param app the application
     */
    public BackgroundColorDialog( PhetApplication app ) {
        super();
        _app = app;
        String title = SimStrings.get( "BackgroundColorDialog.title" );
        Component parent = app.getPhetFrame();
        
        // Start with the active module's background color.
        Color initialColor = app.getModuleManager().getActiveModule().getApparatusPanel().getBackground();
        
        _dialog = ColorChooserFactory.createDialog( title, parent, initialColor, this );
    }
    
    //----------------------------------------------------------------------------
    // Visibility controls
    //----------------------------------------------------------------------------
    
    /** 
     * Shows the dialog.
     */
    public void show() {
        _dialog.show();
    }
    
    /**
     * Hides the dialog.
     */
    public void hide() {
        _dialog.hide();
    }

    //----------------------------------------------------------------------------
    // ColorChooserFactory.Listener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.control.ColorChooserFactory.Listener#colorChanged(java.awt.Color)
     */
    public void colorChanged( Color color ) {
        handleColorChange( color ); 
    }

    /*
     * @see edu.colorado.phet.faraday.control.ColorChooserFactory.Listener#ok(java.awt.Color)
     */
    public void ok( Color color ) {
        handleColorChange( color );  
    }

    /*
     * @see edu.colorado.phet.faraday.control.ColorChooserFactory.Listener#cancelled(java.awt.Color)
     */
    public void cancelled( Color originalColor ) {
        handleColorChange( originalColor );
    }
    
    /*
     * Sets the background color for all apparatus panels in all modules.
     * If the module has a compass grid, sets whether it uses alpha.
     * 
     * @param color the color
     */
    private void handleColorChange( Color color ) {
        ModuleManager moduleManager = _app.getModuleManager();
        int numberOfModules = moduleManager.numModules();
        for ( int i = 0; i < numberOfModules; i++ ) {
            Module module = moduleManager.moduleAt( i );
            moduleManager.moduleAt( i ).getApparatusPanel().setBackground( color );
            if ( module instanceof ICompassGridModule ) {
                // If the color is black, don't use alpha.
                boolean alphaEnabled = ( ! color.equals( Color.BLACK ) );
                ( (ICompassGridModule) module ).setAlphaEnabled( alphaEnabled );
            }
        }
        _app.getPhetFrame().repaint();
    }
}
