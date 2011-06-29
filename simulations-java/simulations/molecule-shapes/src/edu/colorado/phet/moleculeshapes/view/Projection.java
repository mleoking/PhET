package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

public class Projection {
    public static ImmutableVector2D project( ImmutableVector3D vector ) {
        return new ImmutableVector2D( vector.getX(), vector.getY() );
    }
}
