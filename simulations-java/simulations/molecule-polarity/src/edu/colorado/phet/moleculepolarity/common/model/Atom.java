// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * XXX
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Atom {

    public final Property<Double> electronegativity;

    private final String name;
    private final Color color;

    public Atom( String name, Color color, double electronegativity ) {
        this.name = name;
        this.color = color;
        this.electronegativity = new Property<Double>( electronegativity );
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
