/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.module.MotorsModule;

/**
 * MotorsControlPanel is the control panel for MotorsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private MotorsModule _module;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public MotorsControlPanel( MotorsModule module ) {
        super( module );

        _module = module;

        // Set the control panel's minimum width.
        String widthString = SimStrings.getInstance().getString( "width.controlPanel" );
        if ( widthString != null ) {
            int width = Integer.parseInt( widthString );
            setMinumumWidth( width );
        }
        
        // Layout
        {
            addResetButton();
        }
    }

}
