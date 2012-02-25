// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Immutable class representing a movable fraction in the matching game
 *
 * @author Sam Reid
 */
@Data public class Fraction {
    public final ImmutableVector2D position;
    public final int numerator;
    public final int denominator;
    public final boolean dragging;

    public Fraction dragging( boolean dragging ) { return new Fraction( position, numerator, denominator, dragging );}

    public Fraction translate( double dx, double dy ) { return position( position.plus( dx, dy ) ); }

    private Fraction position( ImmutableVector2D position ) { return new Fraction( position, numerator, denominator, dragging );}
}
