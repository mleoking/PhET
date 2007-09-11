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

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.module.ICompassGridModule;


/**
 * BackgroundColorHandler displays a color dialog for setting the background color
 * of the apparatus panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BackgroundColorHandler implements ColorChooserFactory.Listener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private NonPiccoloPhetApplication _app;
    private JDialog _dialog;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param app the application
     */
    public BackgroundColorHandler( NonPiccoloPhetApplication app ) {
        super();
        _app = app;
        String title = FaradayResources.getString( "BackgroundColorDialog.title" );
        Component parent = app.getPhetFrame();

        // Start with the active module's background color.
        Color initialColor = app.getActiveModule().getSimulationPanel().getBackground();

        _dialog = ColorChooserFactory.createDialog( title, parent, initialColor, this );
    }

    //----------------------------------------------------------------------------
    // Dialog controls
    //----------------------------------------------------------------------------

    /**
     * Shows the dialog.
     */
    public void showDialog() {
        _dialog.show();
    }

    /**
     * Hides the dialog.
     */
    public void hideDialog() {
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
        int numberOfModules = _app.numModules();
        for ( int i = 0; i < numberOfModules; i++ ) {
            Module module = _app.getModule( i );
            module.getSimulationPanel().setBackground( color );
            if ( module instanceof ICompassGridModule ) {
                ( (ICompassGridModule) module ).setGridBackground( color );
            }
        }
        _app.getPhetFrame().repaint();
    }
}
