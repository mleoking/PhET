package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A single molecule of sugar.
 *
 * @author Sam Reid
 */
public class SugarMolecule extends Particle {
    public SugarMolecule( ImmutableVector2D position ) {
        super( position );
    }

    //TODO: fix the sugar shape
    @Override public Shape getShape() {
        return new Rectangle( 0, 0, 10, 10 );
    }
}
