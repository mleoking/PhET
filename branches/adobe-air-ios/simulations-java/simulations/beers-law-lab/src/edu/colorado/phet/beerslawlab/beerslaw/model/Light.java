// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * Model of a simple light.
 * Origin is at the center of the lens.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Light implements Resettable {

    public final ImmutableVector2D location; // cm
    public final Property<Boolean> on;
    public final Property<Double> wavelength; // nm
    public final double lensDiameter; // cm

    public Light( ImmutableVector2D location, boolean on, double lensDiameter, final Property<BeersLawSolution> solution ) {
        this.location = location;
        this.on = new Property<Boolean>( on );
        this.wavelength = new Property<Double>( solution.get().molarAbsorptivityData.getLambdaMax() );
        this.lensDiameter = lensDiameter;

        // when the solution changes, set the light to the solution's lambdaMax wavelength
        solution.addObserver( new VoidFunction1<BeersLawSolution>() {
            public void apply( BeersLawSolution solution ) {
                Light.this.wavelength.set( solution.molarAbsorptivityData.getLambdaMax() );
            }
        });
    }

    public double getMinY() {
        return location.getY() - ( lensDiameter / 2 );
    }

    public double getMaxY() {
        return location.getY() + ( lensDiameter / 2 );
    }

    public void reset() {
        on.reset();
    }
}
