// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom;
import edu.colorado.phet.balancingchemicalequations.model.Atom.P;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

/**
 * Atoms look like shaded spheres.
 * Origin is at geometric center of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends ShadedSphereNode {

    private static final Function1<Double, Double> RADIUS_SCALING_FUNCTION = new Function1<Double,Double>() {

        private final double RATE_OF_CHANGE = 0.5; // >0 and <1, increase this to make small atoms appear smaller
        private final double maxRadius = new P().getRadius();
        private final double MODEL_TO_VIEW_SCALE = 0.11;

        public Double apply( Double radius ) {
            final double adjustedRadius = ( maxRadius - RATE_OF_CHANGE * ( maxRadius - radius ) );
            return MODEL_TO_VIEW_SCALE * adjustedRadius;
        }
    };

    public AtomNode( Atom atom ) {
        super( 2 * RADIUS_SCALING_FUNCTION.apply( atom.getRadius() ), atom.getColor() );
    }
}