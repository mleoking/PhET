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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.module.FourierModule;


/**
 * SoundControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SoundControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public SoundControlPanel( FourierModule module ) {
        super( module );
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "SoundControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );
    }
    
    //----------------------------------------------------------------------------
    // FourierControlPanel implementation
    //----------------------------------------------------------------------------

    public void reset() {
        // TODO Auto-generated method stub  
    }
}
