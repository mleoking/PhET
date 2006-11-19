/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglie2DViewStrategy;


class DeBroglieRadialDistanceNode extends AbstractDeBroglie2DViewStrategy {
    
    public DeBroglieRadialDistanceNode( DeBroglieModel atom ) {
        super( atom );
        update();
    }
    
    public void update() {
        //XXX
    }
}