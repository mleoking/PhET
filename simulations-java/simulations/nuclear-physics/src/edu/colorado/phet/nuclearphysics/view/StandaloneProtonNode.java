/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;

/**
 * Representation of a proton that doesn't track to anything in the model.
 * This is intended primarily for use on control panels.
 *
 * @author John Blanco
 */
public class StandaloneProtonNode extends SphericalNode {

    public StandaloneProtonNode( ) {
        super( NuclearPhysicsConstants.NUCLEON_DIAMETER, NuclearPhysicsConstants.PROTON_ROUND_GRADIENT, false );
    }
}