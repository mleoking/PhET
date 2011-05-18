// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.chemistry.molecules;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

import static edu.colorado.phet.chemistry.model.Element.P;

/**
 * Atoms look like shaded spheres.
 * Origin is at geometric center of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends ShadedSphereNode {

    private static final double RATE_OF_CHANGE = 0.75; // >0 and <1, increase this to make small atoms appear smaller
    private static final double MAX_RADIUS = P.getRadius();
    private static final double MODEL_TO_VIEW_SCALE = 0.11;

    /*
     * There is a large difference between the radii of the smallest and largest atoms.
     * This function adjusts scaling so that the difference is still noticeable, but not as large.
     */
    private static final Function1<Double, Double> RADIUS_SCALING_FUNCTION = new Function1<Double, Double>() {
        public Double apply( Double radius ) {
            final double adjustedRadius = ( MAX_RADIUS - RATE_OF_CHANGE * ( MAX_RADIUS - radius ) );
            return MODEL_TO_VIEW_SCALE * adjustedRadius;
        }
    };

    public AtomNode( Element element ) {
        super( 2 * RADIUS_SCALING_FUNCTION.apply( element.getRadius() ), element.getColor() );
    }

    public AtomNode( Atom atom ) {
        super( 2 * RADIUS_SCALING_FUNCTION.apply( atom.getRadius() ), atom.getColor() );
    }
}