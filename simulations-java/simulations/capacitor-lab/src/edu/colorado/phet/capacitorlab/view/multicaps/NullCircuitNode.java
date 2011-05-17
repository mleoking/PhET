// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.ICircuit;

//TODO delete this when all circuits are implemented

/**
 * Node for a null circuit, a placeholder for circuits that haven't been implemented.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NullCircuitNode extends AbstractCircuitNode {

    public NullCircuitNode( ICircuit circuit, CLModelViewTransform3D mvt ) {
        super( circuit, mvt );
    }
}
