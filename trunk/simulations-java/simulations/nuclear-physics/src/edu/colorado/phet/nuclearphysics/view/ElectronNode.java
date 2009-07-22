
package edu.colorado.phet.nuclearphysics.view;


import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Basic representation of a electron in the view.  Depicts it as a particle
 * and neglects the wave nature of it.
 *
 * @author John Blanco
 */
public class ElectronNode extends SphericalNode {

    public ElectronNode( ) {
        super( NuclearPhysicsConstants.ELECTRON_DIAMETER, NuclearPhysicsConstants.ELECTRON_ROUND_GRADIENT, false );
    }
}