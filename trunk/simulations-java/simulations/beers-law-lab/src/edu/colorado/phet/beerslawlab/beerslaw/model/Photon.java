// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of a photon.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Photon {

    public final Property<ImmutableVector2D> location; // cm
    public final double wavelength; // nm
    public final double diameter; // cm

    public Photon( ImmutableVector2D location, double wavelength, double diameter ) {
        this.location = new Property<ImmutableVector2D>( location );
        this.wavelength = wavelength;
        this.diameter = diameter;
    }
}
