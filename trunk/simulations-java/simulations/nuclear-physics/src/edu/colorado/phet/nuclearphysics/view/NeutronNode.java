
package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;

/**
 * Basic representation of a neutron in the view.
 *
 * @author John Blanco
 */
public class NeutronNode extends SphericalNode {

    public NeutronNode( ) {
        super( NuclearPhysicsConstants.NUCLEON_DIAMETER, NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT, false );
    }
}