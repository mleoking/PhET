// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A make-believe atom whose electronegativity is mutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Atom implements Resettable {

    public final Property<Double> electronegativity;
    public final Property<Vector2D> location; // location in world coordinates
    public final Property<Double> partialCharge;

    private final String name;
    private final double diameter;
    private final Color color;

    // Atom with a default location of (0,0)
    public Atom( String name, double diameter, Color color, double electronegativity ) {
        this( name, diameter, color, electronegativity, new Vector2D() );
    }

    protected Atom( String name, double diameter, Color color, double electronegativity, Vector2D location ) {
        this.name = name;
        this.diameter = diameter;
        this.color = color;
        this.electronegativity = new Property<Double>( electronegativity );
        this.location = new Property<Vector2D>( location );
        this.partialCharge = new Property<Double>( 0d ); // partial charge is zero until this atom participates in a bond
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
