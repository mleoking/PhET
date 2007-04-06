/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import edu.colorado.phet.opticaltweezers.OTConstants;
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
        setMinumumWidth( OTConstants.MIN_CONTROL_PANEL_WIDTH );
        
        // Layout
        {
            addResetButton();
        }
    }

}
