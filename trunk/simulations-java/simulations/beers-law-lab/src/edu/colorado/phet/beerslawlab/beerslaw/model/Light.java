// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of a simple light
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Light {

    public static enum LightRepresentation {BEAM, PHOTONS}

    public final Property<Boolean> on;
    public final Property<LightRepresentation> representation;
    public final Property<Double> wavelength;

    public Light( boolean on, LightRepresentation representation, double wavelength ) {
        this.on = new Property<Boolean>( on );
        this.representation = new Property<LightRepresentation>( representation );
        this.wavelength = new Property<Double>( wavelength );
    }
}
