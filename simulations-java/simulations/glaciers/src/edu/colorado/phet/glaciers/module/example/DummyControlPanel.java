/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.module.GlaciersAbstractControlPanel;

/**
 * DummyControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DummyControlPanel extends GlaciersAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DummyCanvas _canvas;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public DummyControlPanel( DummyModule module) {
        super( module );
        
        _canvas = module.getDummyCanvas();

        // Set the control panel's minimum width.
        int minimumWidth = GlaciersResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        //XXX create sub-panels
        
        //XXX yayout
        {
//            addControlFullWidth( XXX );
//            addSeparator();
            addResetButton();
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------

}
