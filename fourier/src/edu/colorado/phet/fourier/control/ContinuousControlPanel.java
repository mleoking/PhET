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
 * ContinuousControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ContinuousControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public ContinuousControlPanel( FourierModule module ) {
        super( module );
       
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "ContinuousControlPanel.width" );
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
