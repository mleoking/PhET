// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Ruler model, to take advantage of location reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Ruler extends Movable {

    public final int length; // cm

    public Ruler( int length, ImmutableVector2D location, PBounds dragBounds ) {
        super( location, dragBounds );
        this.length = length;
    }
}
