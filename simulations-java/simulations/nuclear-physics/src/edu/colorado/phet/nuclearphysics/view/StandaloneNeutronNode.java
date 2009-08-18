
package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;

/**
 * Representation of a neutron that doesn't track to anything in the model.
 * This is intended primarily for use on control panels.
 * 
 * @author John Blanco
 */
public class StandaloneNeutronNode extends SphericalNode {

    public StandaloneNeutronNode( ) {
        super( NuclearPhysicsConstants.NUCLEON_DIAMETER, NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT, false );
    }
}