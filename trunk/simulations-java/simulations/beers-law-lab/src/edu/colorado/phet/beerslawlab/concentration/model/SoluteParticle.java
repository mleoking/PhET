// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for solute particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SoluteParticle {

    private final Color color;
    private final double size; // particles are square, this is the length of one side
    private final Property<ImmutableVector2D> location; // location of the particle in the coordinate frame of the beaker
    private final double orientation; // rotation angle, in radians

    public SoluteParticle( Color color, double size, ImmutableVector2D location, double orientation ) {
        this.color = color;
        this.size = size;
        this.location = new Property<ImmutableVector2D>( location );
        this.orientation = orientation;
    }

    public Color getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }

    public ImmutableVector2D getLocation() {
        return location.get();
    }

    protected void setLocation( ImmutableVector2D location ) {
        this.location.set( location );
    }

    public void addLocationObserver( VoidFunction1<ImmutableVector2D> observer ) {
        location.addObserver( observer );
    }

    public void removeLocationObserver( VoidFunction1<ImmutableVector2D> observer ) {
        location.removeObserver( observer );
    }

    public double getOrientation() {
        return orientation;
    }
}
