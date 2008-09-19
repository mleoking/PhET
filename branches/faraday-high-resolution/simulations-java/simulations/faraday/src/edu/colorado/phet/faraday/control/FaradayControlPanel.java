/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.module.FaradayModule;


/**
 * FaradayControlPanel is the control panel for all Faraday modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Default amount of vertical space, see addVerticalSpace
    public static final int DEFAULT_VERTICAL_SPACE = 8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FaradayModule _module; // module that this control panel is associated with
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public FaradayControlPanel( FaradayModule module ) {
        super( module );
        _module = module;
        
        // Set the control panel's minimum width.
        int width = FaradayResources.getInt( "ControlPanel.width", 225 );
        setMinimumWidth( width );
    }
}
