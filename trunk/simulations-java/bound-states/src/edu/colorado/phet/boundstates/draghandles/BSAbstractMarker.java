/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BSAbstractMarker is the base class for all drag handle markers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractMarker extends PComposite {
    
    /**
     * Constructor.
     */
    public BSAbstractMarker() {
        super();
    }
    
    /**
     * Updates the marker when it's made visible.
     * @param visible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        updateView();
    }
    
    /**
     * Updates the marker to match the model. 
     */
    public abstract void updateView();
    
    /**
     * Sets the color scheme used to draw the marker.
     * 
     * @param colorScheme
     */
    public abstract void setColorScheme( BSColorScheme colorScheme );
}
