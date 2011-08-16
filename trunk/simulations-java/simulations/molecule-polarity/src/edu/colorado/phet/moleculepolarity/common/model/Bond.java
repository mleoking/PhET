// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;

/**
 * Model of a bond between 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bond {

    public final Property<Double> deltaElectronegativity; // atom2.electronegativity - atom1.electronegativity   //TODO remove this
    public final Property<ImmutableVector2D> endpoint1, endpoint2; // ends of the bond at atom1 and atom2, respectively
    public final Property<ImmutableVector2D> dipole; // the bond dipole

    public Bond( final Atom atom1, final Atom atom2 ) {

        this.deltaElectronegativity = new Property<Double>( 0d );
        this.endpoint1 = new Property<ImmutableVector2D>( atom1.location.get() );
        this.endpoint2 = new Property<ImmutableVector2D>( atom2.location.get() );
        this.dipole = new Property<ImmutableVector2D>( new ImmutableVector2D() ); // proper initialization occurs below

        RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {

                // adjust endpoints to match atom locations
                endpoint1.set( new ImmutableVector2D( atom1.location.get() ) );
                endpoint2.set( new ImmutableVector2D( atom2.location.get() ) );

                // adjust dipole
                final double deltaEN = atom2.electronegativity.get() - atom1.electronegativity.get();
                double magnitude = Math.abs( deltaEN );
                double angle = getAngle();
                if ( deltaEN < 0 ) {
                    angle += Math.PI;
                }
                dipole.set( ImmutableVector2D.parseAngleAndMagnitude( magnitude, angle ) );

                // adjust partial charges
                //TODO this works, but partial charge is not equivalent to deltaEN. Do we need a more accurate model?
                atom1.partialCharge.set( -deltaEN );
                atom2.partialCharge.set( deltaEN );

                deltaElectronegativity.set( deltaEN ); //TODO remove this
            }
        };
        observer.observe( atom1.location, atom2.location, atom1.electronegativity, atom2.electronegativity );
    }

    // gets the center of the bond, using the midpoint formula
    public ImmutableVector2D getCenter() {
        return new ImmutableVector2D( ( endpoint1.get().getX() + endpoint2.get().getX() ) / 2, ( endpoint1.get().getY() + endpoint2.get().getY() ) / 2 );
    }

    // gets the angle of endpoint2 relative to the horizontal
    public double getAngle() {
        ImmutableVector2D center = getCenter();
        return PolarCartesianConverter.getAngle( endpoint2.get().getX() - center.getX(), endpoint2.get().getY() - center.getY() );
    }
}
