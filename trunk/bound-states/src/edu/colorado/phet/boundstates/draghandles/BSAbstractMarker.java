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

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.umd.cs.piccolox.nodes.PComposite;


public abstract class BSAbstractMarker extends PComposite {
    
    public BSAbstractMarker() {
        super();
    }
    
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        updateView();
    }
    
    public abstract void updateView();
    
    public abstract void setColorScheme( BSColorScheme colorScheme );
}
