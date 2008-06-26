/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;

/**
 * Basic representation of a proton in the view.
 *
 * @author John Blanco
 */
public class ProtonNode extends SphericalNode {

    public ProtonNode( ) {
        super( NuclearPhysicsConstants.NUCLEON_DIAMETER, NuclearPhysicsConstants.PROTON_ROUND_GRADIENT, false );
    }
}