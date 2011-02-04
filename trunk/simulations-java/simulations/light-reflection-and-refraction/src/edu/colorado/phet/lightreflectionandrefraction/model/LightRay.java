// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class LightRay {
    public final Property<ImmutableVector2D> tip;
    public final Property<ImmutableVector2D> tail;
    public final double indexOfRefraction;
    public final double wavelength; // wavelength in meters

    public LightRay( Property<ImmutableVector2D> tip, Property<ImmutableVector2D> tail, double indexOfRefraction, double wavelength ) {
        this.tip = tip;
        this.tail = tail;
        this.indexOfRefraction = indexOfRefraction;
        this.wavelength = wavelength;
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        tip.addObserver( simpleObserver );
        tail.addObserver( simpleObserver );
    }

    public double getSpeed() {
        return LRRModel.C / indexOfRefraction;
    }

    public void propagate( double dt ) {
        ImmutableVector2D unitDirection = tip.getValue().getSubtractedInstance( tail.getValue() ).getNormalizedInstance();
        ImmutableVector2D delta = unitDirection.getScaledInstance( getSpeed() * dt );
        tip.setValue( tip.getValue().getAddedInstance( delta ) );
        tail.setValue( tail.getValue().getAddedInstance( delta ) );
    }
}
