// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * XXX
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Atom {

    public final String name;
    public final Property<Double> electronegativity;

    public Atom( String name, double electronegativity ) {
        this.name = name;
        this.electronegativity = new Property<Double>( electronegativity );
    }
}
