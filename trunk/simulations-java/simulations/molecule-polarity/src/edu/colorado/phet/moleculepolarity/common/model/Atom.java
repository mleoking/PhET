// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A make-believe atom whose electronegativity is mutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Atom implements Resettable {

    public final Property<Double> electronegativity;
    public final Property<ImmutableVector2D> location; // global location

    private final String name;
    private final double diameter;
    private final Color color;

    public Atom( String name, double diameter, Color color, double electronegativity ) {
        this( name, diameter, color, electronegativity, new ImmutableVector2D() );
    }

    protected Atom( String name, double diameter, Color color, double electronegativity, ImmutableVector2D location ) {
        this.name = name;
        this.diameter = diameter;
        this.color = color;
        this.electronegativity = new Property<Double>( electronegativity );
        this.location = new Property<ImmutableVector2D>( location );
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
