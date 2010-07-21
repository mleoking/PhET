/* Copyright 2005-2010, University of Colorado */

package edu.colorado.phet.faraday.control;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.faraday.FaradayResources;


/**
 * FaradayControlPanel is the control panel for all Faraday modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayControlPanel extends ControlPanel {

    // a default amount of vertical space 
    private static final int DEFAULT_VERTICAL_SPACE = 8;
    
    public FaradayControlPanel() {
        super();
        
        // Set the control panel's minimum width.
        int width = FaradayResources.getInt( "ControlPanel.width", 225 );
        setMinimumWidth( width );
    }
    
    public void addDefaultVerticalSpace() {
        addVerticalSpace( FaradayControlPanel.DEFAULT_VERTICAL_SPACE );
    }
}
