/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;

/**
 * Basic representation of an electron in the view.  Depicts it as a particle
 * and neglects the wave nature of it.
 *
 * @author John Blanco
 */
public class ElectronNode extends SphericalNode {

    public ElectronNode( ) {
        super( NuclearPhysicsConstants.ELECTRON_DIAMETER, NuclearPhysicsConstants.ELECTRON_ROUND_GRADIENT, false );
    }
}