// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * Model of a simple light.
 * Origin is at the center of the lens.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Light {

    public static enum LightRepresentation {BEAM, PHOTONS}

    public final ImmutableVector2D location;
    public final Property<Boolean> on;
    public final Property<LightRepresentation> representation;
    public final Property<Double> wavelength; // nm
    public final double lensDiameter; // cm

    public Light( ImmutableVector2D location, boolean on, LightRepresentation representation, double lensDiameter, final Property<BeersLawSolution> solution ) {
        this.location = location;
        this.on = new Property<Boolean>( on );
        this.representation = new Property<LightRepresentation>( representation );
        this.wavelength = new Property<Double>( solution.get().lambdaMax );
        this.lensDiameter = lensDiameter;

        // when the solution changes, set the light to the solution's lambdaMax wavelength
        solution.addObserver( new VoidFunction1<BeersLawSolution>() {
            public void apply( BeersLawSolution solution ) {
                Light.this.wavelength.set( solution.lambdaMax );
            }
        });
    }
}
