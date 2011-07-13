// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * XXX
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Atom implements Resettable {

    public final Property<Double> electronegativity;

    private final String name;
    private final double diameter;
    private final Color color;

    public Atom( String name, double diameter, Color color, double electronegativity ) {
        this.name = name;
        this.diameter = diameter;
        this.color = color;
        this.electronegativity = new Property<Double>( electronegativity );
    }

    public void reset() {
        electronegativity.reset();
    }

    public String getName() {
        return name;
    }

    public double getDiameter() {
        return diameter;
    }

    public Color getColor() {
        return color;
    }
}
