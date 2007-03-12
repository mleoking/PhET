/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.module.MotorsModule;


public class MotorsControlPanel extends AbstractControlPanel {

    private MotorsModule _module;
    
    public MotorsControlPanel( MotorsModule module ) {
        super( module );

        _module = module;

        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "width.controlPanel" );
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
