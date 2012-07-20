// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Ruler model, to take advantage of location reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Ruler extends Movable {

    public final int length; // cm, must be integer units
    public final double insets; // cm, the horizontal insets at the ends of the ruler
    public final double height; //cm

    public Ruler( int length, double insets, double height, Vector2D location, PBounds dragBounds ) {
        super( location, dragBounds );
        this.length = length;
        this.insets = insets;
        this.height = height;
    }
}
